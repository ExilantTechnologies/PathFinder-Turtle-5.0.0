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

public class BulkTask extends AbstractInsertTask implements ToBeInitializedInterface
{
    public String actionFieldName;
    
    public BulkTask() {
        this.actionFieldName = "bulkAction";
    }
    
    @Override
    public int executeBulkTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final TableInterface table = Tables.getTable(this.taskName, dc);
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            return 0;
        }
        final int nbrRows = grid.getNumberOfRows();
        if (nbrRows == 0) {
            return 0;
        }
        if (this.parentTableName != null) {
            this.copyGeneratedKeys(grid, dc);
        }
        return table.bulkAction(dc, handle, grid, this.actionFieldName);
    }
    
    @Override
    public int executeTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        throw new ExilityException("Bulk task can not be executed as a single task");
    }
    
    @Override
    public void initialize() {
        this.repeatForRowsInGrid = true;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.READWRITE;
    }
}
