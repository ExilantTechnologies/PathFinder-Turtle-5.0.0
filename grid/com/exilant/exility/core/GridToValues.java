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

public class GridToValues implements GridProcessorInterface
{
    public String gridName;
    public String prefix;
    public boolean removeGrid;
    public int index;
    
    public GridToValues() {
        this.gridName = null;
        this.prefix = null;
        this.removeGrid = false;
        this.index = 0;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null || grid.getNumberOfRows() == 0) {
            Spit.out("GridToValues: grid " + this.gridName + " not found");
            return 0;
        }
        String[] columnNames;
        for (int length = (columnNames = grid.getColumnNames()).length, i = 0; i < length; ++i) {
            final String columnName = columnNames[i];
            final String nameInGrid = (this.prefix == null) ? columnName : (String.valueOf(this.prefix) + columnName);
            dc.addValue(nameInGrid, grid.getValue(columnName, this.index));
        }
        if (this.removeGrid) {
            dc.removeGrid(this.gridName);
        }
        return 1;
    }
}
