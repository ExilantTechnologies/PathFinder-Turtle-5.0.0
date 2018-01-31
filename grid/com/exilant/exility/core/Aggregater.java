/* *******************************************************************************************************
Copyright (c) 2015 EXILANT Technologies Private Limited
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ******************************************************************************************************** */

package com.exilant.exility.core;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Aggregater implements GridProcessorInterface, ToBeInitializedInterface
{
    String inputGridName;
    String outputGridName;
    AggregaterColumn[] columns;
    private String[] headerRow;
    
    public Aggregater() {
        this.inputGridName = null;
        this.outputGridName = null;
        this.columns = new AggregaterColumn[0];
    }
    
    @Override
    public int process(final DataCollection dc) {
        String[][] inData = null;
        final Grid grid = dc.getGrid(this.inputGridName);
        if (grid == null) {
            Spit.out(String.valueOf(this.inputGridName) + " not found");
            return 0;
        }
        inData = grid.getRawData();
        if (inData == null || inData.length == 0 || inData[0].length == 0) {
            Spit.out(String.valueOf(this.inputGridName) + " has no data in it");
            return 0;
        }
        final Assistant assistant = new Assistant(this.columns, inData[0], this.headerRow.length);
        final ArrayList<String[]> newRows = new ArrayList<String[]>();
        for (int i = 1; i < inData.length; ++i) {
            final String[] newRow = assistant.processARow(inData[i]);
            if (newRow != null) {
                newRows.add(newRow);
            }
        }
        newRows.add(assistant.getLastRow());
        final int n = newRows.size();
        final String[][] outData = new String[n + 1][];
        outData[0] = this.headerRow;
        for (int j = 1; j < outData.length; ++j) {
            outData[j] = newRows.get(j - 1);
        }
        try {
            dc.addGrid(this.outputGridName, outData);
        }
        catch (Exception e) {
            Spit.out(e);
            return 0;
        }
        return 1;
    }
    
    @Override
    public void initialize() {
        final ArrayList<String> list = new ArrayList<String>();
        AggregaterColumn[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final AggregaterColumn column = columns[i];
            if (column.aggregationFunction != AggregationFunctionName.APPEND) {
                list.add(column.outputColumnName);
            }
        }
        this.headerRow = list.toArray(new String[0]);
    }
    
    enum AggregationFunctionName
    {
        KEY("KEY", 0), 
        SUM("SUM", 1), 
        AVERAGE("AVERAGE", 2), 
        MIN("MIN", 3), 
        MAX("MAX", 4), 
        FIRST("FIRST", 5), 
        LAST("LAST", 6), 
        COUNT("COUNT", 7), 
        LIST("LIST", 8), 
        APPEND("APPEND", 9);
        
        private AggregationFunctionName(final String s, final int n) {
        }
    }
    
    class Assistant
    {
        private int numberOfGroupByFields;
        private String[] groupByValues;
        private int numberOfAggregateFields;
        private AggregaterFieldInterface[] aggregaterFields;
        int numberOfOutputColumns;
        private boolean accumulationStarted;
        private int totalFields;
        private int[] dataSourceIndexes;
        
        Assistant(final AggregaterColumn[] fields, final String[] inputHeaderRow, final int nbrCols) {
            this.accumulationStarted = false;
            boolean aggregationFieldFound = false;
            this.totalFields = fields.length;
            this.dataSourceIndexes = new int[this.totalFields];
            this.numberOfOutputColumns = nbrCols;
            this.aggregaterFields = new AggregaterFieldInterface[this.totalFields];
            for (int i = 0; i < this.totalFields; ++i) {
                final AggregaterColumn column = fields[i];
                final String columnName = column.inputColumnName;
                for (int j = 0; j < inputHeaderRow.length; ++j) {
                    if (columnName.equals(inputHeaderRow[j])) {
                        this.dataSourceIndexes[i] = j;
                        break;
                    }
                }
                if (!aggregationFieldFound) {
                    if (column.aggregationFunction == AggregationFunctionName.KEY) {
                        continue;
                    }
                    this.numberOfGroupByFields = i;
                    this.groupByValues = new String[i];
                    this.aggregaterFields = new AggregaterFieldInterface[this.totalFields - i];
                    aggregationFieldFound = true;
                }
                AggregaterFieldInterface newField = null;
                if (column.aggregationFunction == AggregationFunctionName.LIST) {
                    newField = new ListField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.APPEND) {
                    final AppendField ap = new AppendField();
                    ap.setListField((ListField)this.aggregaterFields[this.numberOfAggregateFields - 1]);
                    newField = ap;
                }
                else if (column.aggregationFunction == AggregationFunctionName.SUM) {
                    newField = new SumField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.COUNT) {
                    newField = new CountField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.MAX) {
                    newField = new MaxField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.MIN) {
                    newField = new MinField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.FIRST) {
                    newField = new FirstField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.LAST) {
                    newField = new LastField();
                }
                else if (column.aggregationFunction == AggregationFunctionName.AVERAGE) {
                    newField = new AverageField();
                }
                else {
                    Spit.out("Invalid aggregationfunciton name " + column.aggregationFunction);
                }
                this.aggregaterFields[this.numberOfAggregateFields] = newField;
                ++this.numberOfAggregateFields;
            }
        }
        
        String[] processARow(final String[] rowOfData) {
            String[] rowToReturn = null;
            boolean grouByFieldChanged = false;
            if (this.accumulationStarted) {
                for (int i = 0; i < this.numberOfGroupByFields; ++i) {
                    final int inputIdx = this.dataSourceIndexes[i];
                    if (!this.groupByValues[i].equals(rowOfData[inputIdx])) {
                        rowToReturn = this.getCurrentRow();
                        grouByFieldChanged = true;
                        break;
                    }
                }
            }
            if (grouByFieldChanged || !this.accumulationStarted) {
                for (int i = 0; i < this.numberOfGroupByFields; ++i) {
                    final int inputIdx = this.dataSourceIndexes[i];
                    this.groupByValues[i] = rowOfData[inputIdx];
                }
                this.accumulationStarted = true;
            }
            int idx = 0;
            for (int j = this.numberOfGroupByFields; j < this.totalFields; ++j) {
                final int inputIdx2 = this.dataSourceIndexes[j];
                this.aggregaterFields[idx].accumulate(rowOfData[inputIdx2]);
                ++idx;
            }
            return rowToReturn;
        }
        
        private String[] getCurrentRow() {
            final String[] currentRow = new String[this.numberOfOutputColumns];
            for (int i = 0; i < this.numberOfGroupByFields; ++i) {
                currentRow[i] = this.groupByValues[i];
            }
            int currentRowIdx = this.numberOfGroupByFields;
            for (int j = 0; j < this.numberOfAggregateFields; ++j) {
                final String val = this.aggregaterFields[j].getValue();
                if (val != null) {
                    currentRow[currentRowIdx] = val;
                    ++currentRowIdx;
                }
            }
            return currentRow;
        }
        
        String[] getLastRow() {
            this.accumulationStarted = false;
            return this.getCurrentRow();
        }
    }
    
    class SumField implements AggregaterFieldInterface
    {
        private double value;
        
        SumField() {
            this.value = 0.0;
        }
        
        @Override
        public void accumulate(final String val) {
            this.value += Double.parseDouble(val);
        }
        
        @Override
        public String getValue() {
            final String val = Double.toString(this.value);
            this.value = 0.0;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class CountField implements AggregaterFieldInterface
    {
        private int count;
        
        CountField() {
            this.count = 0;
        }
        
        @Override
        public void accumulate(final String val) {
            ++this.count;
        }
        
        @Override
        public String getValue() {
            final String val = Integer.toString(this.count);
            this.count = 0;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class LastField implements AggregaterFieldInterface
    {
        private String value;
        
        @Override
        public void accumulate(final String val) {
            this.value = val;
        }
        
        @Override
        public String getValue() {
            final String val = this.value;
            this.value = null;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class FirstField implements AggregaterFieldInterface
    {
        private String value;
        
        @Override
        public void accumulate(final String val) {
            if (this.value == null) {
                this.value = val;
            }
        }
        
        @Override
        public String getValue() {
            final String val = this.value;
            this.value = null;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class MaxField implements AggregaterFieldInterface
    {
        private double max;
        
        MaxField() {
            this.max = Double.MIN_VALUE;
        }
        
        @Override
        public void accumulate(final String val) {
            final double d = Double.parseDouble(val);
            if (d > this.max) {
                this.max = d;
            }
        }
        
        @Override
        public String getValue() {
            final String val = Double.toString(this.max);
            this.max = Double.MIN_VALUE;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class MinField implements AggregaterFieldInterface
    {
        private double min;
        
        MinField() {
            this.min = Double.MAX_VALUE;
        }
        
        @Override
        public void accumulate(final String val) {
            final double d = Double.parseDouble(val);
            if (d < this.min) {
                this.min = d;
            }
        }
        
        @Override
        public String getValue() {
            final String val = Double.toString(this.min);
            this.min = Double.MAX_VALUE;
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class AverageField implements AggregaterFieldInterface
    {
        private double value;
        private int count;
        
        AverageField() {
            this.value = 0.0;
            this.count = 0;
        }
        
        @Override
        public void accumulate(final String val) {
            this.value += Double.parseDouble(val);
            ++this.count;
        }
        
        @Override
        public String getValue() {
            if (this.count == 0) {
                return "0";
            }
            final double d = this.value / this.count;
            this.value = 0.0;
            this.count = 0;
            return Double.toString(d);
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class ListField implements AggregaterFieldInterface
    {
        private final StringBuilder value;
        private int count;
        
        ListField() {
            this.value = new StringBuilder();
            this.count = 0;
        }
        
        @Override
        public void accumulate(final String val) {
            if (this.count > 0) {
                this.value.append(',');
            }
            this.value.append(val);
            ++this.count;
        }
        
        void append(final String val) {
            this.value.append('-').append(val);
        }
        
        @Override
        public String getValue() {
            final String val = this.value.toString();
            this.count = 0;
            this.value.setLength(0);
            return val;
        }
        
        @Override
        public boolean requiresColumn() {
            return true;
        }
    }
    
    class AppendField implements AggregaterFieldInterface
    {
        private ListField listField;
        
        void setListField(final ListField listField) {
            this.listField = listField;
        }
        
        @Override
        public void accumulate(final String val) {
            this.listField.append(val);
        }
        
        @Override
        public String getValue() {
            return null;
        }
        
        @Override
        public boolean requiresColumn() {
            return false;
        }
    }
    
    interface AggregaterFieldInterface
    {
        void accumulate(final String p0);
        
        String getValue();
        
        boolean requiresColumn();
    }
}
