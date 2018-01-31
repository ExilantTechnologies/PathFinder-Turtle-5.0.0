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

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class AbstractTable implements TableInterface
{
    private static final String WHERE = " WHERE ";
    protected String name;
    protected String tableName;
    protected String[] keyFieldNames;
    protected String[] keyColumnNames;
    protected boolean isAudited;
    protected boolean keyToBeGenerated;
    protected String allFieldNames;
    
    public AbstractTable() {
        this.tableName = "AbstractTable";
        this.keyFieldNames = null;
        this.keyColumnNames = null;
        this.isAudited = false;
        this.keyToBeGenerated = false;
        this.allFieldNames = null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public boolean toBeAudited() {
        return AP.enableAuditForAll || this.isAudited;
    }
    
    public boolean keyToBeGenerated() {
        return this.keyToBeGenerated;
    }
    
    protected abstract int insertFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2, final boolean[] p3) throws ExilityException;
    
    @Override
    public int insertFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid) throws ExilityException {
        return this.insertFromGrid(dc, handle, grid, null);
    }
    
    @Override
    public int updateFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid) throws ExilityException {
        return this.updateFromGrid(dc, handle, grid, null);
    }
    
    protected abstract int updateFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2, final boolean[] p3) throws ExilityException;
    
    @Override
    public int deleteFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid) throws ExilityException {
        return this.deleteFromGrid(dc, handle, grid, null);
    }
    
    protected abstract int deleteFromGrid(final DataCollection p0, final DbHandle p1, final Grid p2, final boolean[] p3) throws ExilityException;
    
    @Override
    public int read(final DataCollection dc, final DbHandle handle, final String gridName, final String prefix) throws ExilityException {
        return this.filter(dc, handle, gridName, null, null, null, null, prefix, false);
    }
    
    @Override
    public int massUpdate(final DataCollection dc, final DbHandle handle, final Condition[] conditions, final String columnNames, final String columnValues) throws ExilityException {
        final String[] names = columnNames.split(",");
        final String[] values = columnValues.split(",");
        if (names.length != values.length) {
            dc.addMessage("exilError", "Column names do not have matching values to be set to in mass update step for " + this.tableName);
            return 0;
        }
        final StringBuilder sb = new StringBuilder();
        boolean addedAtLeastOne = false;
        sb.append("UPDATE ").append(this.tableName).append(" SET ");
        for (int i = 0; i < names.length; ++i) {
            if (addedAtLeastOne) {
                sb.append(", ");
            }
            sb.append(names[i]).append(" = ").append(SqlUtil.formatValue(Value.newValue(values[i])));
            addedAtLeastOne = true;
        }
        sb.append(" WHERE ");
        final boolean gotAdded = Condition.toSql(sb, conditions, dc);
        if (!gotAdded) {
            dc.raiseException("exilUpdateWithNoConditions", this.tableName);
            return 0;
        }
        return handle.execute(sb.toString(), false);
    }
    
    @Override
    public int massDelete(final DataCollection dc, final DbHandle handle, final Condition[] conditions) throws ExilityException {
        final StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(this.tableName).append(" WHERE ");
        final boolean gotAdded = Condition.toSql(sb, conditions, dc);
        if (!gotAdded) {
            dc.raiseException("exilUpdateWithNoConditions", this.getName());
            return 0;
        }
        return handle.execute(sb.toString(), false);
    }
    
    @Override
    public int persist(final DataCollection dc, final DbHandle handle, final String saveOperation) throws ExilityException {
        if (saveOperation.equals("add")) {
            return this.insert(dc, handle);
        }
        if (saveOperation.equals("modify")) {
            return this.update(dc, handle);
        }
        if (saveOperation.equals("delete")) {
            return this.delete(dc, handle);
        }
        if (saveOperation.equals("save")) {
            return this.save(dc, handle);
        }
        return 0;
    }
    
    @Override
    public int save(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.keyFieldNames == null || this.keyFieldNames.length != 1) {
            throw new ExilityException("Save operaiton requires that a unique key field be defined for the table. Not valid for table " + this.name);
        }
        final Value value = dc.getValue(this.keyFieldNames[0]);
        boolean keyFound = false;
        if (value != null && !value.isNull()) {
            if (value.getValueType() == DataValueType.INTEGRAL) {
                if (value.getIntegralValue() > 0L) {
                    keyFound = true;
                }
            }
            else if (value.getTextValue().length() > 0) {
                keyFound = true;
            }
        }
        if (keyFound) {
            return this.update(dc, handle);
        }
        return this.insert(dc, handle);
    }
    
    @Override
    public int saveFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid) throws ExilityException {
        if (this.keyFieldNames == null || this.keyFieldNames.length != 1) {
            throw new ExilityException("Save operaiton requires that a unique key field be defined for the table. Not valid for table " + this.name);
        }
        final long[] keys = grid.getColumn(this.keyFieldNames[0]).getIntegralList();
        final int nbrRows = keys.length;
        final boolean[] inserts = new boolean[nbrRows];
        final boolean[] updates = new boolean[nbrRows];
        int nbrInserts = 0;
        for (int i = 0; i < keys.length; ++i) {
            if (keys[i] > 0L) {
                updates[i] = true;
            }
            else {
                inserts[i] = true;
                ++nbrInserts;
            }
        }
        int totalAffected = 0;
        if (nbrInserts > 0) {
            totalAffected = this.insertFromGrid(dc, handle, grid, inserts);
        }
        if (nbrInserts < nbrRows) {
            totalAffected += this.updateFromGrid(dc, handle, grid, updates);
        }
        return totalAffected;
    }
    
    @Override
    public int bulkAction(final DataCollection dc, final DbHandle handle, final Grid grid, final String actionColumnName) throws ExilityException {
        final String[] actions = grid.getColumnAsTextArray(actionColumnName);
        final int nbrRows = actions.length;
        final boolean[] inserts = new boolean[nbrRows];
        final boolean[] updates = new boolean[nbrRows];
        final boolean[] deletes = new boolean[nbrRows];
        int nbrInserts = 0;
        int nbrUpdates = 0;
        int nbrDeletes = 0;
        for (int i = 0; i < actions.length; ++i) {
            final String action = actions[i];
            if (action.equals("add") || action.equals("insert")) {
                inserts[i] = true;
                ++nbrInserts;
            }
            else if (action.equals("modify")) {
                updates[i] = true;
                ++nbrUpdates;
            }
            else if (action.equals("delete")) {
                deletes[i] = true;
                ++nbrDeletes;
            }
        }
        Spit.out("Total Rows=" + nbrRows + ", Nbr Inserts=" + nbrInserts + ", Nbr Updates=" + nbrUpdates + ", Nbr Deletes=" + nbrDeletes);
        int totalAffected = 0;
        if (nbrInserts > 0) {
            totalAffected = this.insertFromGrid(dc, handle, grid, inserts);
        }
        if (nbrUpdates > 0) {
            totalAffected += this.updateFromGrid(dc, handle, grid, updates);
        }
        if (nbrDeletes > 0) {
            totalAffected += this.deleteFromGrid(dc, handle, grid, deletes);
        }
        return totalAffected;
    }
    
    protected long generateKey(final DataCollection dc) throws ExilityException {
        final long key = PrimaryKeyGenerator.getNextKey(this.tableName, this.keyColumnNames[0], 1);
        long existingKey = dc.getIntegralValue(this.keyFieldNames[0], 0L);
        if (existingKey > 0L) {
            existingKey = 0L;
        }
        dc.addGeneratedKey(this.tableName, existingKey, key);
        dc.addValue(this.keyFieldNames[0], Value.newValue(key));
        return key;
    }
    
    protected long generateKeysForGrid(final DataCollection dc, final Grid grid, final boolean[] rowSelector) throws ExilityException {
        int nbrKeysRequired;
        final int nbrRows = nbrKeysRequired = grid.getNumberOfRows();
        if (rowSelector != null) {
            if (nbrRows != rowSelector.length) {
                throw new ExilityException("AbstarctTable.generateKeysForGrid is called with a selector of length " + rowSelector.length + " while the corresponding grid has " + nbrRows + " rows.");
            }
            nbrKeysRequired = 0;
            for (final boolean toSelect : rowSelector) {
                if (toSelect) {
                    ++nbrKeysRequired;
                }
            }
        }
        if (nbrKeysRequired == 0) {
            Spit.out("Nothing to insert into table " + this.name);
            return 0L;
        }
        if (this.keyFieldNames == null || this.keyFieldNames.length != 1) {
            throw new ExilityException("Save operaiton requires that a unique key field be defined for the table. Not valid for table " + this.name);
        }
        final String keyColumnName = this.keyColumnNames[0];
        final String keyFieldName = this.keyFieldNames[0];
        final long keyToReturn;
        long key = keyToReturn = PrimaryKeyGenerator.getNextKey(this.tableName, keyColumnName, nbrKeysRequired);
        long[] existingKeys = null;
        if (grid.hasColumn(keyFieldName)) {
            existingKeys = grid.getColumn(keyFieldName).getIntegralList();
        }
        else {
            final ValueList list = ValueList.newList(DataValueType.INTEGRAL, nbrRows);
            grid.addColumn(keyFieldName, list);
            existingKeys = new long[nbrRows];
        }
        for (int i = 0; i < existingKeys.length; ++i) {
            if (rowSelector == null || rowSelector[i]) {
                dc.addGeneratedKey(this.tableName, existingKeys[i], key);
                grid.setIntegralValue(keyFieldName, i, key);
                ++key;
            }
        }
        return keyToReturn;
    }
    
    protected ValueList getValueList(final Value value, final int nbrValues) {
        final ValueList list = ValueList.newList(value.getValueType(), nbrValues);
        for (int i = 0; i < nbrValues; ++i) {
            list.setValue(value, i);
        }
        return list;
    }
    
    @Override
    public int massRead(final DataCollection dc, final DbHandle handle, final String gridName, final Grid grid) throws ExilityException {
        final StringBuilder sbf = new StringBuilder();
        final int n = grid.getNumberOfRows();
        if (n == 0) {
            return 0;
        }
        Grid outGrid = null;
        for (int i = 1; i <= n; ++i) {
            String[] keyFieldNames;
            for (int length = (keyFieldNames = this.keyFieldNames).length, j = 0; j < length; ++j) {
                final String keyField = keyFieldNames[j];
                dc.addValue(keyField, grid.getValue(keyField, i));
            }
            this.getSelectStatement(sbf, dc, null, null, null, null, null, false);
            outGrid = handle.extractToGrid(sbf.toString(), gridName, outGrid);
            sbf.setLength(0);
        }
        if (outGrid == null) {
            return 0;
        }
        dc.addGrid(gridName, outGrid);
        return outGrid.getNumberOfRows();
    }
    
    @Override
    public int filter(final DataCollection dc, final DbHandle handle, final String gridName, final Condition[] conditions, final String columnsToSelect, final String columnsToSort, final String sortOrder, final String prefix, final boolean eliminateDuplicates) throws ExilityException {
        final StringBuilder sbf = new StringBuilder();
        this.getSelectStatement(sbf, dc, conditions, columnsToSelect, columnsToSort, sortOrder, prefix, eliminateDuplicates);
        if (gridName == null) {
            return handle.extractSingleRow(sbf.toString(), dc.values, prefix);
        }
        final Grid grid = handle.extractToGrid(sbf.toString(), gridName, null);
        dc.addGrid(gridName, grid);
        return grid.getNumberOfRows();
    }
    
    protected void getSelectStatement(final StringBuilder sbf, final DataCollection dc, final Condition[] conditions, final String columnsToSelect, final String columnsToSort, final String sortOrder, final String prefix, final boolean eliminateDuplicates) throws ExilityException {
        sbf.append("SELECT ");
        if (eliminateDuplicates) {
            sbf.append(" DISTINCT ");
        }
        if (columnsToSelect == null) {
            sbf.append(this.allFieldNames);
        }
        else {
            sbf.append(columnsToSelect);
        }
        sbf.append(" FROM ").append(this.tableName).append(" WHERE ");
        if (conditions == null) {
            sbf.append(this.getPrimaryKeyClause(dc));
        }
        else {
            final boolean gotAdded = Condition.toSql(sbf, conditions, dc);
            if (!gotAdded) {
                Spit.out(String.valueOf(this.name) + " filtering: No filtering criterion. Going to read all rows.");
                sbf.setLength(sbf.length() - " WHERE ".length());
            }
        }
        if (columnsToSort != null) {
            sbf.append(" ORDER BY ");
            if (sortOrder.equalsIgnoreCase("asc")) {
                sbf.append(columnsToSort);
            }
            else {
                String[] split;
                for (int length = (split = columnsToSort.split(",")).length, i = 0; i < length; ++i) {
                    final String columnName = split[i];
                    sbf.append(columnName).append(" ").append(sortOrder).append(",");
                }
                sbf.deleteCharAt(sbf.length() - 1);
            }
        }
    }
    
    protected String getPrimaryKeyClause(final DataCollection dc) {
        final StringBuilder sbf = new StringBuilder();
        for (int i = 0; i < this.keyFieldNames.length; ++i) {
            if (i > 0) {
                sbf.append(" AND ");
            }
            final Value keyValue = dc.getValue(this.keyFieldNames[i]);
            sbf.append(this.keyColumnNames[i]);
            if (keyValue == null) {
                sbf.append(" is null ");
            }
            else {
                sbf.append(" = ").append(SqlUtil.formatValue(keyValue));
            }
        }
        return sbf.toString();
    }
}
