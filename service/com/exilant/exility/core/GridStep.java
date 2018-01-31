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

class GridStep extends AbstractStep
{
    String name;
    String columns;
    
    GridStep() {
        this.name = null;
        this.columns = null;
        this.stepType = StepType.GRIDSTEP;
    }
    
    @Override
    String executeStep(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.name == null || this.name.equals("")) {
            dc.addMessage("exilityError", "The variable name not specified to create the list");
            return "next";
        }
        final String[] names = this.columns.split(",");
        final Grid grid = new Grid(this.name);
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String curName = array[i];
            grid.addColumn(curName, dc.getValueList(curName));
        }
        dc.addGrid(this.name, grid);
        return "next";
    }
}
