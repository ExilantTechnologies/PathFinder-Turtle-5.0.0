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

class AddColumnStep extends AbstractStep
{
    String gridName;
    String columnName;
    Expression expression;
    
    AddColumnStep() {
        this.gridName = null;
        this.columnName = null;
        this.expression = null;
        this.stepType = StepType.ADDCOLUMNSTEP;
    }
    
    @Override
    String executeStep(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.gridName == null || this.gridName.equals("")) {
            dc.addMessage("exilityError", "Grid name not specified to add the column");
            return "next";
        }
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            dc.addMessage("exilityError", String.valueOf(this.gridName) + " not found in dc. You have used this grid name in an AddColumn Step.");
            return "next";
        }
        final Value val = this.expression.evaluate(dc);
        final ValueList valueList = new ValueList(grid.getNumberOfRows());
        for (int i = 0; i < valueList.length(); ++i) {
            valueList.setValue(val, i);
        }
        grid.addColumn(this.columnName, valueList);
        dc.addGrid(this.gridName, grid);
        return "next";
    }
}
