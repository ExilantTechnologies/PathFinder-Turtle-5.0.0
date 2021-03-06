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

class AddColumn implements GridProcessorInterface
{
    String gridName;
    String columnName;
    DataValueType columnType;
    Expression expression;
    
    AddColumn() {
        this.gridName = null;
        this.columnName = null;
        this.columnType = DataValueType.TEXT;
        this.expression = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            dc.addMessage("exilNoGrid", this.gridName);
            Spit.out("input grid  " + this.gridName + " is not found in dc");
            return 0;
        }
        try {
            final ValueList col = ValueList.newList(this.columnType, grid.getNumberOfRows());
            grid.addColumn(this.columnName, col);
            this.expression.evaluateColumn(grid, dc, this.columnName);
        }
        catch (ExilityException e) {
            dc.addMessage("exilError", e.getMessage());
        }
        return 1;
    }
}
