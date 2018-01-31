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

import java.util.List;
import java.util.ArrayList;

class SplitRows implements GridProcessorInterface
{
    String fromGridName;
    String toGridName;
    String[] columnNamesToCopy;
    String columnNameToSplit;
    String splitColumnName;
    String rowSeparator;
    
    SplitRows() {
        this.fromGridName = null;
        this.toGridName = null;
        this.columnNamesToCopy = null;
        this.columnNameToSplit = null;
        this.splitColumnName = null;
        this.rowSeparator = ";";
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid fromGrid = dc.getGrid(this.fromGridName);
        if (fromGrid == null) {
            dc.addMessage("exilNoGrid", this.fromGridName);
            Spit.out("input grid  " + this.fromGridName + " is ot found in dc");
            return 0;
        }
        final Grid toGrid = new Grid(this.toGridName);
        final String[] splitArray = fromGrid.getColumn(this.columnNameToSplit).getTextList();
        final int nbrRows = splitArray.length;
        final List<String> splitVals = new ArrayList<String>();
        final int[] counts = new int[nbrRows];
        int newNbrRows = 0;
        for (int i = 0; i < splitArray.length; ++i) {
            final String[] vals = splitArray[i].split(this.rowSeparator);
            counts[i] = vals.length;
            newNbrRows += vals.length;
            String[] array;
            for (int length = (array = vals).length, k = 0; k < length; ++k) {
                final String val = array[k];
                splitVals.add(val);
            }
        }
        try {
            String[] columnNamesToCopy;
            for (int length2 = (columnNamesToCopy = this.columnNamesToCopy).length, l = 0; l < length2; ++l) {
                final String columnName = columnNamesToCopy[l];
                final ValueList existingColumn = fromGrid.getColumn(columnName);
                final ValueList newColumn = ValueList.newList(existingColumn.getValueType(), newNbrRows);
                int newIdx = 0;
                for (int j = 0; j < nbrRows; ++j) {
                    final Value val2 = existingColumn.getValue(j);
                    newColumn.setValue(val2, newIdx);
                    ++newIdx;
                    for (int n = counts[j]; n > 1; --n) {
                        newColumn.setValue(val2, newIdx);
                        ++newIdx;
                    }
                }
                toGrid.addColumn(columnName, newColumn);
            }
            final String[] arr = splitVals.toArray(new String[0]);
            final ValueList lst = ValueList.newList(arr);
            toGrid.addColumn(this.splitColumnName, lst);
        }
        catch (ExilityException e) {
            dc.addMessage("exilError", e.getMessage(), " stacktrace=" + e.getStackTrace());
            Spit.out("stacktrace=" + e.getStackTrace());
            return 0;
        }
        dc.addGrid(this.toGridName, toGrid);
        return nbrRows;
    }
}
