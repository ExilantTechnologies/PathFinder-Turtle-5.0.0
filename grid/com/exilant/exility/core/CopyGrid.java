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

class CopyGrid implements GridProcessorInterface, ToBeInitializedInterface
{
    String fromGridName;
    String toGridName;
    String[] fromColumns;
    String[] toColumns;
    public String actionFieldName;
    
    CopyGrid() {
        this.fromGridName = null;
        this.toGridName = null;
        this.fromColumns = null;
        this.toColumns = null;
        this.actionFieldName = null;
    }
    
    @Override
    public void initialize() {
        if (this.toColumns == null) {
            this.toColumns = this.fromColumns;
        }
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid fromGrid = dc.getGrid(this.fromGridName);
        if (fromGrid == null) {
            dc.addMessage("exilNoGrid", this.fromGridName);
            Spit.out(fromGrid + " is not found");
            return 0;
        }
        final String[][] fromGridData = fromGrid.getRawData();
        if (this.actionFieldName != null) {
            try {
                final String[] actions = fromGrid.getColumn(this.actionFieldName).getTextList();
                int nbrRows = fromGridData.length;
                nbrRows = nbrRows + 1 - fromGrid.filterRows(this.actionFieldName, Comparator.EQUALTO, "delete").getRawData().length;
                final String[][] toGridData = new String[nbrRows][];
                toGridData[0] = fromGridData[0];
                int i = 1;
                int j = 1;
                while (i < fromGridData.length) {
                    if (!actions[i - 1].equals("delete")) {
                        toGridData[j++] = fromGridData[i];
                    }
                    ++i;
                }
                fromGrid.setRawData(toGridData);
            }
            catch (ExilityException eEx) {
                dc.addMessage("exilError", eEx.getMessage());
                return 0;
            }
        }
        if (this.fromColumns == null) {
            final Grid toGrid = new Grid(this.toGridName);
            try {
                toGrid.setRawData(fromGrid.getRawData().clone());
                fromGrid.setRawData(fromGridData);
                dc.addGrid(this.toGridName, toGrid);
            }
            catch (ExilityException eEx2) {
                dc.addMessage("exilError", eEx2.getMessage());
                return 0;
            }
            return 0;
        }
        final Grid toGrid = new Grid(this.toGridName);
        for (int k = 0; k < this.fromColumns.length; ++k) {
            try {
                toGrid.addColumn(this.toColumns[k], ValueList.newList(fromGrid.getColumnAsTextArray(this.fromColumns[k]).clone(), fromGrid.getColumn(this.fromColumns[k]).getValueType()));
            }
            catch (ExilityException ex) {}
        }
        try {
            fromGrid.setRawData(fromGridData);
            dc.addGrid(this.toGridName, toGrid);
        }
        catch (ExilityException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
