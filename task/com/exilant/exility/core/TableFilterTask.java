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

public class TableFilterTask extends ExilityTask
{
    Condition[] selectionCriterion;
    String columnNames;
    String sortByColumns;
    String sortOrder;
    boolean eliminateDuplicates;
    boolean migratedFromLookUp;
    
    public TableFilterTask() {
        this.selectionCriterion = new Condition[0];
        this.columnNames = null;
        this.sortByColumns = null;
        this.sortOrder = "asc";
        this.eliminateDuplicates = false;
        this.migratedFromLookUp = false;
    }
    
    @Override
    public int executeTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final TableInterface table = Tables.getTable(this.taskName, dc);
        String namesToSelect = this.columnNames;
        if (this.migratedFromLookUp && namesToSelect == null) {
            if (!(table instanceof Table)) {
                dc.raiseException("exilityError", "migratedFromLookUp directive can be used only for xml based table definition, but not for generated table)");
                return 0;
            }
            namesToSelect = ((Table)table).getAllFieldNamesForLookup();
        }
        return table.filter(dc, handle, this.gridName, this.selectionCriterion, namesToSelect, this.sortByColumns, this.sortOrder, null, this.eliminateDuplicates);
    }
    
    @Override
    public int executeBulkTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        dc.raiseException("exilityError", "tableFilterTask can not be executed for each row in a grid(bulk mode)");
        return 0;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.READONLY;
    }
}
