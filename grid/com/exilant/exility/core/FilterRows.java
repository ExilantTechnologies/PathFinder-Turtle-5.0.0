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

class FilterRows implements GridProcessorInterface
{
    String fromGridName;
    String toGridName;
    String columnName;
    Comparator comparator;
    String value;
    
    FilterRows() {
        this.fromGridName = null;
        this.toGridName = null;
        this.columnName = null;
        this.comparator = Comparator.EXISTS;
        this.value = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid fromGrid = dc.getGrid(this.fromGridName);
        if (fromGrid == null) {
            dc.addMessage("exilNoGrid", this.fromGridName);
            Spit.out("input grid for Filter rows " + this.fromGridName + " is ot found in dc");
            return 0;
        }
        try {
            if (this.fromGridName.equals(this.toGridName)) {
                fromGrid.filter(this.columnName, this.comparator, this.value);
                return 1;
            }
            final Grid toGrid = fromGrid.filterRows(this.columnName, this.comparator, this.value);
            dc.addGrid(this.toGridName, toGrid);
        }
        catch (ExilityException e) {
            return 0;
        }
        return 1;
    }
}
