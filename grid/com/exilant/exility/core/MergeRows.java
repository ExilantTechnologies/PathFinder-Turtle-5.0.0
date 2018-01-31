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

public class MergeRows implements GridProcessorInterface
{
    String fromGridName;
    String toGridName;
    String[] keyColumnNames;
    String columnNameToBeMerged;
    String mergedColumnName;
    String rowSeparator;
    
    public MergeRows() {
        this.fromGridName = null;
        this.toGridName = null;
        this.keyColumnNames = null;
        this.columnNameToBeMerged = null;
        this.mergedColumnName = null;
        this.rowSeparator = ";";
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid fromGrid = dc.getGrid(this.fromGridName);
        if (fromGrid == null) {
            Spit.out(String.valueOf(this.fromGridName) + " not found");
            return 0;
        }
        final int nbrRows = fromGrid.getNumberOfRows();
        if (nbrRows == 0) {
            Spit.out(String.valueOf(this.fromGridName) + " has no data in it");
            return 0;
        }
        final int nbrKeys = this.keyColumnNames.length;
        final String[][] keyColumnTexts = new String[nbrKeys][];
        final ValueList[] keyColumns = new ValueList[nbrKeys];
        for (int i = 0; i < nbrKeys; ++i) {
            final ValueList lst = fromGrid.getColumn(this.keyColumnNames[i]);
            keyColumns[i] = lst;
            keyColumnTexts[i] = lst.getTextList();
        }
        final boolean[] keyChanged = new boolean[nbrRows];
        int newNbrRows = 1;
        for (int row = 1; row < keyChanged.length; ++row) {
            for (int keyCol = 0; keyCol < nbrKeys; ++keyCol) {
                if (!keyColumnTexts[keyCol][row].equals(keyColumnTexts[keyCol][row - 1])) {
                    ++newNbrRows;
                    keyChanged[row] = true;
                    break;
                }
            }
        }
        final Grid toGrid = new Grid(this.toGridName);
        final ValueList[] newColumns = new ValueList[nbrKeys];
        final ValueList columnToMerge = fromGrid.getColumn(this.columnNameToBeMerged);
        final ValueList mergedColumn = ValueList.newList(DataValueType.TEXT, newNbrRows);
        try {
            for (int j = 0; j < nbrKeys; ++j) {
                final ValueList newColumn = ValueList.newList(keyColumns[j].getValueType(), newNbrRows);
                newColumns[j] = newColumn;
                toGrid.addColumn(this.keyColumnNames[j], newColumn);
            }
            toGrid.addColumn(this.mergedColumnName, mergedColumn);
        }
        catch (Exception e) {
            Spit.out(e);
            dc.addError(e.getMessage());
            return 0;
        }
        for (int k = 0; k < newColumns.length; ++k) {
            newColumns[k].setValue(keyColumns[k].getValue(0), 0);
        }
        int newIdx = 0;
        String s = columnToMerge.getTextValue(0);
        for (int l = 1; l < nbrRows; ++l) {
            if (keyChanged[l]) {
                mergedColumn.setTextValue(s, newIdx);
                ++newIdx;
                for (int m = 0; m < newColumns.length; ++m) {
                    newColumns[m].setValue(keyColumns[m].getValue(l), newIdx);
                }
                s = columnToMerge.getTextValue(l);
            }
            else {
                s = String.valueOf(s) + this.rowSeparator + columnToMerge.getTextValue(l);
            }
        }
        mergedColumn.setTextValue(s, newIdx);
        dc.addGrid(this.toGridName, toGrid);
        return newIdx + 1;
    }
}
