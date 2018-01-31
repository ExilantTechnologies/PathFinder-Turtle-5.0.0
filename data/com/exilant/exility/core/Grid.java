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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Grid
{
    private static final int UNKNOWN_ROWS = -1;
    private int numberOfRows;
    private int numberOfColumns;
    private String name;
    private String[][] rawData;
    private Map<String, Integer> columnIndexes;
    Map<String, ValueList> columnValues;
    
    public Grid() {
        this.numberOfRows = -1;
        this.numberOfColumns = 0;
        this.name = "noName";
        this.rawData = null;
        this.columnIndexes = new HashMap<String, Integer>();
        this.columnValues = new HashMap<String, ValueList>();
    }
    
    public Grid(final String name) {
        this.numberOfRows = -1;
        this.numberOfColumns = 0;
        this.name = "noName";
        this.rawData = null;
        this.columnIndexes = new HashMap<String, Integer>();
        this.columnValues = new HashMap<String, ValueList>();
        this.name = name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setRawData(final String[][] rawData) throws ExilityException {
        this.setRawData(rawData, null);
    }
    
    public void setRawData(final String[][] rawData, final DataValueType[] types) throws ExilityException {
        if (rawData == null || rawData.length == 0) {
            throw new ExilityException("raw data for a grid must have a header row");
        }
        this.rawData = rawData;
        this.columnIndexes = new HashMap<String, Integer>();
        this.columnValues = new HashMap<String, ValueList>();
        this.numberOfColumns = rawData[0].length;
        this.numberOfRows = rawData.length - 1;
        final String[] columnNames = rawData[0];
        for (int j = 0; j < columnNames.length; ++j) {
            String columnName = columnNames[j];
            if (columnName == null) {
                columnName = "";
            }
            this.columnIndexes.put(columnName, new Integer(j));
            final String[] list = new String[this.numberOfRows];
            for (int i = 0; i < list.length; ++i) {
                list[i] = this.rawData[i + 1][j];
            }
            final DataValueType vt = (types == null) ? DataDictionary.getValueType(columnName) : types[j];
            this.columnValues.put(columnName, ValueList.newList(list, vt));
        }
    }
    
    public void resetRawData() {
        this.rawData = null;
    }
    
    public void addColumn(final String columnName, final ValueList column) throws ExilityException {
        final int nrows = column.length();
        if (this.numberOfRows == -1) {
            this.numberOfRows = nrows;
        }
        else if (nrows != this.numberOfRows) {
            throw new ExilityException("exilWrongRowMismatch. Existing grid has " + this.numberOfRows + " while a column is being added with " + nrows + " rows.");
        }
        if (this.columnIndexes.containsKey(columnName)) {
            this.columnValues.remove(columnName);
        }
        else {
            this.columnIndexes.put(columnName, new Integer(this.numberOfColumns));
            ++this.numberOfColumns;
        }
        this.columnValues.put(columnName, column);
        this.rawData = null;
    }
    
    public void setValues(final String[] columnNames, final DataValueType[] valueTypes, final List<Value[]> values, final DataCollection dc) {
        this.columnIndexes = new HashMap<String, Integer>();
        this.columnValues = new HashMap<String, ValueList>();
        this.numberOfColumns = columnNames.length;
        this.numberOfRows = values.size();
        for (int j = 0; j < columnNames.length; ++j) {
            String columnName = columnNames[j];
            if (this.columnIndexes.containsKey(columnName)) {
                final String msg = "Error : " + this.name + "a grid has duplicate colum name " + columnName;
                Spit.Out(msg);
                if (dc != null) {
                    dc.addMessage("exilError", msg);
                }
                columnName = String.valueOf(columnName) + j;
            }
            this.columnIndexes.put(columnName, new Integer(j));
            final ValueList list = ValueList.newList(valueTypes[j], this.numberOfRows);
            this.columnValues.put(columnName, list);
            for (int i = 0; i < this.numberOfRows; ++i) {
                list.setValue(values.get(i)[j], i);
            }
        }
    }
    
    public void appendValues(final List<Value[]> values) {
        final int n = values.size();
        if (n == 0) {
            return;
        }
        final Value[] firstRow = values.get(0);
        final int m = firstRow.length;
        if (m != this.getNumberOfColumns()) {
            Spit.out("Internal design error: invalid attempt to append rows to grid " + this.name + " Number of columns mismatch.");
            return;
        }
        for (final String columnName : this.columnIndexes.keySet()) {
            final int j = this.columnIndexes.get(columnName);
            final ValueList list = this.columnValues.get(columnName);
            final Value aValue = firstRow[j];
            final ValueList newList = ValueList.newList(aValue.getValueType(), n);
            for (int i = 0; i < n; ++i) {
                final Value[] row = values.get(i);
                newList.setValue(row[j], i);
            }
            list.append(newList);
        }
    }
    
    public int getNumberOfRows() {
        return this.numberOfRows;
    }
    
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }
    
    public ValueList getColumn(final String columnName) {
        final ValueList lst = this.columnValues.get(columnName);
        if (lst == null) {
            final String s = "Information: column " + columnName + " is not found in the grid. It may be added automatically, or may result in an error.";
            Spit.out(s);
        }
        return lst;
    }
    
    public boolean hasColumn(final String columnName) {
        return this.columnValues.containsKey(columnName);
    }
    
    public String getValueAsText(final String columnName, final int index, final String defaultValue) {
        final ValueList column = this.columnValues.get(columnName);
        if (column != null) {
            final String val = column.getTextValue(index);
            if (val != null) {
                return val;
            }
        }
        return defaultValue;
    }
    
    public String[] getColumnAsTextArray(final String columnName) {
        final ValueList column = this.columnValues.get(columnName);
        if (column != null) {
            return column.format();
        }
        return null;
    }
    
    public Value getValue(final String columnName, final int idx) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return null;
        }
        return column.getValue(idx);
    }
    
    public void setValue(final String columnName, final Value value, final int idx) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setValue(value, idx);
    }
    
    public void setTextValue(final String columnName, final int index, final String textValue) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setTextValue(textValue, index);
    }
    
    public void setIntegralValue(final String columnName, final int index, final long value) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setIntegralValue(value, index);
    }
    
    public void setBooleanValue(final String columnName, final int index, final boolean value) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setBooleanValue(value, index);
    }
    
    public void setDateValue(final String columnName, final int index, final Date value) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setDateValue(value, index);
    }
    
    public void setDecimalValue(final String columnName, final int index, final double value) {
        final ValueList column = this.columnValues.get(columnName);
        if (column == null) {
            return;
        }
        this.rawData = null;
        column.setDecimalValue(value, index);
    }
    
    public String[][] getRawData() {
        if (this.rawData == null) {
            this.createRawData();
        }
        return this.rawData;
    }
    
    public String[][] getRawData(final Parameter[] columns) {
        if (this.rawData == null) {
            this.createRawData(columns);
        }
        final String[][] data = this.rawData;
        this.rawData = null;
        return data;
    }
    
    public String[][] getRawData(final String[] columnNames) {
        if (this.rawData == null) {
            this.createRawData(columnNames);
        }
        final String[][] data = this.rawData;
        this.rawData = null;
        return data;
    }
    
    private void createRawData() {
        final String[][] data = new String[this.numberOfRows + 1][this.numberOfColumns];
        final String[] headerRow = data[0];
        for (final String nam : this.columnIndexes.keySet()) {
            final int i = this.columnIndexes.get(nam);
            headerRow[i] = nam;
            final ValueList list = this.getColumn(nam);
            if (list == null) {
                continue;
            }
            final String[] vals = list.format();
            for (int j = 0; j < vals.length; ++j) {
                data[j + 1][i] = vals[j];
            }
        }
        this.rawData = data;
    }
    
    private void createRawData(final Parameter[] columns) {
        final String[][] data = new String[this.numberOfRows + 1][this.numberOfColumns];
        for (int i = 0; i < columns.length; ++i) {
            final Parameter column = columns[i];
            data[0][i] = column.name;
            final ValueList list = this.getColumn(column.name);
            String[] vals = null;
            final AbstractDataType dt = column.getDataType();
            vals = dt.format(list);
            for (int j = 0; j < vals.length; ++j) {
                data[j + 1][i] = vals[j];
            }
        }
        this.rawData = data;
    }
    
    private void createRawData(final String[] columns) {
        final String[][] data = new String[this.numberOfRows + 1][this.numberOfColumns];
        for (int i = 0; i < columns.length; ++i) {
            final String nam = columns[i];
            data[0][i] = nam;
            final ValueList list = this.getColumn(nam);
            if (list != null) {
                final String[] vals = list.format();
                for (int j = 0; j < vals.length; ++j) {
                    data[j + 1][i] = vals[j];
                }
            }
        }
        this.rawData = data;
    }
    
    public String[] getColumnNames() {
        final String[] columnNames = new String[this.numberOfColumns];
        for (final Map.Entry<String, Integer> entry : this.columnIndexes.entrySet()) {
            columnNames[entry.getValue()] = entry.getKey();
        }
        return columnNames;
    }
    
    public void renameColumn(final String fromName, final String toName) {
        if (!this.columnIndexes.containsKey(fromName)) {
            return;
        }
        final int colIdx = this.columnIndexes.get(fromName);
        this.columnIndexes.put(toName, new Integer(colIdx));
        this.columnIndexes.remove(fromName);
        this.columnValues.put(toName, this.columnValues.get(fromName));
        this.columnValues.remove(fromName);
        this.rawData = null;
    }
    
    public void filter(final String columnName, final Comparator comparator, final String value) throws ExilityException {
        final boolean[] selections = new boolean[this.numberOfRows];
        final int nbrSelectedRows = this.getSelectionFlags(columnName, comparator, value, selections);
        if (nbrSelectedRows == this.numberOfRows) {
            return;
        }
        for (final String colName : this.columnIndexes.keySet()) {
            this.columnValues.put(colName, this.columnValues.get(colName).filter(nbrSelectedRows, selections));
        }
        this.rawData = null;
    }
    
    public Grid filterRows(final String columnName, final Comparator comparator, final String value) throws ExilityException {
        final boolean[] selections = new boolean[this.numberOfRows];
        final int nbrSelectedRows = this.getSelectionFlags(columnName, comparator, value, selections);
        final Grid newGrid = new Grid(this.name);
        for (final String colName : this.columnIndexes.keySet()) {
            newGrid.addColumn(colName, this.columnValues.get(colName).filter(nbrSelectedRows, selections));
        }
        return newGrid;
    }
    
    private int getSelectionFlags(final String columnName, final Comparator comparator, final String value, final boolean[] selections) throws ExilityException {
        final ValueList list = this.columnValues.get(columnName);
        if (list == null) {
            final String msg = String.valueOf(columnName) + " is not a column Name. Filter will not work";
            Spit.out(msg);
            throw new ExilityException(msg);
        }
        if (value == null || value.length() == 0) {
            for (int i = 0; i < selections.length; ++i) {
                selections[i] = true;
            }
            return selections.length;
        }
        int nbrRowsSelected = 0;
        final Value val = Value.newValue(value, list.getValueType());
        for (int j = 0; j < selections.length; ++j) {
            boolean selected = false;
            if (comparator == Comparator.EXISTS) {
                selected = true;
            }
            else {
                final Value v = list.isNull(j) ? Value.newValue("") : list.getValue(j);
                selected = v.compare(comparator, val);
            }
            if (selected) {
                ++nbrRowsSelected;
                selections[j] = true;
            }
        }
        return nbrRowsSelected;
    }
    
    public boolean copyRowToDc(final int index, final String prefix, final DataCollection dc) {
        final String prefixToUse = (prefix != null) ? prefix : "";
        if (index >= this.numberOfRows) {
            for (final String columnName : this.columnValues.keySet()) {
                dc.removeValue(String.valueOf(prefixToUse) + columnName);
            }
            return false;
        }
        for (final String columnName : this.columnValues.keySet()) {
            final ValueList column = this.columnValues.get(columnName);
            dc.addValue(String.valueOf(prefixToUse) + columnName, column.getValue(index));
        }
        return true;
    }
    
    public String toHtml() {
        return HtmlUtil.rowsToTable(this.getRawData());
    }
    
    public String getName() {
        return (this.name == null) ? "" : this.name;
    }
    
    public void toSpreadSheetXml(final StringBuilder sbf, final String sheetName) {
        sbf.append("\n<Worksheet ss:Name=\"").append(sheetName).append("\">\n<Table>\n<Row>");
        final String[][] data = this.getRawData();
        final String[] header = data[0];
        final String[] types = new String[this.numberOfColumns];
        final boolean[] isDate = new boolean[this.numberOfColumns];
        for (int j = 0; j < this.numberOfColumns; ++j) {
            final String colName = header[j];
            final DataValueType type = this.columnValues.get(colName).getValueType();
            isDate[j] = (type == DataValueType.DATE);
            types[j] = Value.getXlType(type);
            sbf.append("\n<Cell><Data ss:Type=\"String\">").append(colName).append("</Data></Cell>");
        }
        sbf.append("\n</Row>");
        for (int i = 1; i < data.length; ++i) {
            final String[] row = data[i];
            sbf.append("\n<Row>");
            for (int k = 0; k < row.length; ++k) {
                sbf.append("\n<Cell");
                String value = row[k];
                if (isDate[k] && value.length() > 0) {
                    sbf.append(" ss:StyleID=\"").append("dateStyle").append('\"');
                    value = String.valueOf(value.substring(0, 10)) + 'T' + value.substring(11);
                }
                else if (types[k].equals("Boolean")) {
                    value = ((value.toUpperCase().equals("TRUE") || value.equals("1")) ? "1" : "0");
                }
                sbf.append("><Data ss:Type=\"").append(types[k]).append("\">").append(value).append("</Data></Cell>");
            }
            sbf.append("\n</Row>");
        }
        sbf.append("\n</Table>\n</Worksheet>");
    }
    
    public boolean equal(final Grid otherGrid, final DataCollection dc) {
        boolean matched = true;
        final int thisNbrRows = this.numberOfRows;
        final int otherNbrRows = otherGrid.numberOfRows;
        if (thisNbrRows != otherNbrRows) {
            final String msg = "Found " + thisNbrRows + " rows in the grid " + this.name + " while " + otherNbrRows + " rows are expected.";
            if (dc != null) {
                dc.addMessage("exilityError", msg);
            }
            else {
                Spit.out(msg);
            }
            return false;
        }
        final int thisNbrCols = this.numberOfColumns;
        final int otherNbrCols = otherGrid.numberOfColumns;
        if (thisNbrCols < otherNbrCols) {
            final String msg2 = "Found " + thisNbrCols + " columns in the grid " + this.name + " while at least " + otherNbrCols + " columns are expected.";
            if (dc != null) {
                dc.addMessage("exilityError", msg2);
            }
            else {
                Spit.out(msg2);
            }
            return false;
        }
        final String[] otherColumnNames = otherGrid.getColumnNames();
        String[] array;
        for (int length = (array = otherColumnNames).length, i = 0; i < length; ++i) {
            final String colName = array[i];
            if (!this.columnIndexes.containsKey(colName)) {
                final String msg3 = "Column " + colName + " not found in in the grid " + this.name;
                if (dc != null) {
                    dc.addMessage("exilityError", msg3);
                }
                else {
                    Spit.out(msg3);
                }
                matched = false;
            }
        }
        if (!matched) {
            return false;
        }
        String[] array2;
        for (int length2 = (array2 = otherColumnNames).length, j = 0; j < length2; ++j) {
            final String colName = array2[j];
            final ValueList thisColumn = this.columnValues.get(colName);
            final ValueList otherColumn = otherGrid.columnValues.get(colName);
            if (!thisColumn.equal(otherColumn, dc, colName)) {
                matched = false;
            }
        }
        return matched;
    }
    
    public void removeColumn(final String columnName) {
        if (this.numberOfRows == -1) {
            Spit.out("The Grid Does not have any data yet. Hence can't perform any operation");
            return;
        }
        if (this.columnIndexes.containsKey(columnName)) {
            this.columnValues.remove(columnName);
            this.columnIndexes.remove(columnName);
            int colIdx = 0;
            for (final String nam : this.columnIndexes.keySet()) {
                this.columnIndexes.put(nam, new Integer(colIdx));
                ++colIdx;
            }
            --this.numberOfColumns;
            if (this.numberOfColumns == 0) {
                this.numberOfRows = -1;
            }
            this.resetRawData();
            return;
        }
        Spit.out("The column name: " + columnName + " does not exist. Hence no column deleted.");
    }
    
    public void purgeGrid() {
        if (this.numberOfRows == -1) {
            Spit.out("The Grid \"" + this.name + "\" Does not have any data. Cannot purge anything.");
        }
        for (final String nam : this.columnValues.keySet()) {
            this.columnValues.put(nam, null);
        }
        this.resetRawData();
        this.numberOfRows = 0;
    }
    
    public String[] getFilteredList(final String columnName, final String basedOnColumnName, final String filterValue) {
        final ValueList list = this.getColumn(columnName);
        if (list == null) {
            Spit.out("Column " + columnName + " not found in grid " + this.name);
            return null;
        }
        final String[] vals = list.getTextList();
        if (basedOnColumnName == null) {
            return vals;
        }
        final ValueList otherColumn = this.getColumn(basedOnColumnName);
        if (otherColumn == null) {
            Spit.out("based on column " + basedOnColumnName + " not found in grid " + this.name + ". values are not filtered.");
            return vals;
        }
        final String[] valsToCompare = otherColumn.getTextList();
        int n = 0;
        final boolean[] toInclude = new boolean[valsToCompare.length];
        for (int i = 0; i < valsToCompare.length; ++i) {
            if (!valsToCompare[i].equals(filterValue)) {
                ++n;
                toInclude[i] = true;
            }
        }
        final String[] newVals = new String[n];
        n = 0;
        for (int j = 0; j < toInclude.length; ++j) {
            if (toInclude[j]) {
                newVals[n] = vals[j];
                ++n;
            }
        }
        return newVals;
    }
}
