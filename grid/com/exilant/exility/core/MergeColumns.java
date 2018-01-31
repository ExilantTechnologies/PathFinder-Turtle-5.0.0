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

class MergeColumns implements GridProcessorInterface
{
    String gridName;
    String[] columnNamesToMerge;
    String mergedColumnName;
    String fieldSeparator;
    
    MergeColumns() {
        this.gridName = null;
        this.columnNamesToMerge = null;
        this.mergedColumnName = null;
        this.fieldSeparator = "-";
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            dc.addMessage("exilNoGrid", this.gridName);
            Spit.out("input grid  " + this.gridName + " is ot found in dc");
            return 0;
        }
        final int nbrRows = grid.getNumberOfRows();
        final int nbrFields = this.columnNamesToMerge.length;
        final String[] mergedText = new String[nbrRows];
        final ValueList[] columnToMerge = new ValueList[nbrFields];
        for (int j = 0; j < columnToMerge.length; ++j) {
            columnToMerge[j] = grid.getColumn(this.columnNamesToMerge[j]);
        }
        for (int i = 0; i < mergedText.length; ++i) {
            String s = columnToMerge[0].getTextValue(i);
            for (int k = 1; k < columnToMerge.length; ++k) {
                s = String.valueOf(s) + this.fieldSeparator + columnToMerge[k].getTextValue(i);
            }
            mergedText[i] = s;
        }
        final ValueList lst = ValueList.newList(mergedText);
        try {
            grid.addColumn(this.mergedColumnName, lst);
        }
        catch (ExilityException e) {
            Spit.out(e.getMessage());
            dc.addMessage("exilError", e.getMessage());
            return 0;
        }
        return 1;
    }
}
