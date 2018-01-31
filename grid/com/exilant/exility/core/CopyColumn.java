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

public class CopyColumn implements GridProcessorInterface
{
    String gridName;
    String columnName;
    String newColumnName;
    
    public CopyColumn() {
        this.gridName = null;
        this.columnName = null;
        this.newColumnName = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            dc.addMessage("exilNoGrid", this.gridName);
            Spit.out("input grid  " + this.gridName + " is not found in dc");
            return 0;
        }
        final ValueList column = grid.getColumn(this.columnName);
        if (column == null) {
            dc.addMessage("exilNoColum", this.columnName, this.gridName);
            Spit.out("Column " + this.columnName + " is not found in grid  " + this.gridName);
            return 0;
        }
        final ValueList newColumn = column.clone();
        try {
            grid.addColumn(this.newColumnName, newColumn);
        }
        catch (ExilityException exilEx) {
            dc.addMessage("exilError", exilEx.getMessage());
            Spit.out(exilEx.getMessage());
            return 0;
        }
        return 1;
    }
}
