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

public class CopyRows implements GridProcessorInterface
{
    public String fromGridName;
    public String toGridName;
    public String actionFieldName;
    
    public CopyRows() {
        this.fromGridName = null;
        this.toGridName = null;
        this.actionFieldName = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final String[][] fromGrid = dc.getGrid(this.fromGridName).getRawData();
        if (fromGrid.length == 0 || fromGrid[0].length == 0) {
            dc.addMessage("exilNoGrid", this.fromGridName);
            Spit.out("input grid for append rows " + this.fromGridName + " is ot found in dc");
            return 0;
        }
        int nbrRows = fromGrid.length;
        final String[] header = fromGrid[0];
        final int nbrCols = header.length;
        int startAt = 1;
        String[] actions = null;
        if (this.actionFieldName != null) {
            try {
                actions = dc.getGrid(this.fromGridName).getColumn(this.actionFieldName).getTextList();
                nbrRows = nbrRows + 1 - dc.getGrid(this.fromGridName).filterRows(this.actionFieldName, Comparator.EQUALTO, "delete").getRawData().length;
            }
            catch (ExilityException eEx) {
                dc.addMessage("exilError", eEx.getMessage());
                return 0;
            }
        }
        String[][] toGrid = new String[0][];
        if (dc.hasGrid(this.toGridName)) {
            toGrid = dc.getGrid(this.toGridName).getRawData();
        }
        if (toGrid.length == 0 || toGrid[0].length == 0) {
            toGrid = new String[nbrRows][];
            toGrid[0] = fromGrid[0];
        }
        else {
            if (nbrCols != toGrid[0].length) {
                dc.addMessage("exilNoGrid", this.toGridName);
                Spit.out(" grid  " + this.toGridName + " is ot found in dc");
                return 0;
            }
            final String[][] tempGrid = new String[nbrRows + toGrid.length - 1][];
            for (int i = 0; i < toGrid.length; ++i) {
                tempGrid[i] = toGrid[i];
            }
            startAt = toGrid.length;
            toGrid = tempGrid;
        }
        for (int j = 1; j < fromGrid.length; ++j) {
            if (this.actionFieldName != null && actions != null) {
                final String action = actions[j - 1];
                if (action.equals("delete")) {
                    continue;
                }
            }
            toGrid[startAt] = fromGrid[j];
            ++startAt;
        }
        dc.addGrid(this.toGridName, toGrid);
        return 1;
    }
}
