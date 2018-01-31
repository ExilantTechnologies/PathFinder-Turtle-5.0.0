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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table extends AbstractTable implements ToBeInitializedInterface
{
    String module;
    String description;
    boolean okToDelete;
    String[] keyPrefixFieldNames;
    int keyColumnWidth;
    String createdTimestampName;
    String modifiedTimestampName;
    String createdUserName;
    String modifiedUserName;
    String createdTimestampColumnName;
    String modifiedTimestampColumnName;
    String createdUserColumnName;
    String modifiedUserColumnName;
    Column[] columns;
    String parentTableName;
    RelatedTable[] relatedTables;
    String activeField;
    private final Map<String, DataValueType> valueTypes;
    
    public Table() {
        this.module = null;
        this.description = null;
        this.okToDelete = false;
        this.keyPrefixFieldNames = null;
        this.keyColumnWidth = 0;
        this.createdTimestampName = null;
        this.modifiedTimestampName = null;
        this.createdUserName = null;
        this.modifiedUserName = null;
        this.createdTimestampColumnName = null;
        this.modifiedTimestampColumnName = null;
        this.createdUserColumnName = null;
        this.modifiedUserColumnName = null;
        this.columns = new Column[0];
        this.activeField = null;
        this.valueTypes = new HashMap<String, DataValueType>();
    }
    
    @Override
    public void initialize() {
        int nbrKeys = 0;
        final StringBuilder all = new StringBuilder();
        boolean firstOne = true;
        Column[] columns;
        for (int length = (columns = this.columns).length, j = 0; j < length; ++j) {
            final Column column = columns[j];
            if (column.columnName == null) {
                column.columnName = column.name;
            }
            if (column.isKeyColumn) {
                ++nbrKeys;
            }
            this.valueTypes.put(column.columnName, column.getValueType());
            if (firstOne) {
                firstOne = false;
            }
            else {
                all.append(", ");
            }
            all.append(column.columnName).append(" as \"").append(column.name).append("\" ");
        }
        this.keyFieldNames = new String[nbrKeys];
        this.keyColumnNames = new String[nbrKeys];
        if (nbrKeys > 0) {
            int i = 0;
            Column[] columns2;
            for (int length2 = (columns2 = this.columns).length, k = 0; k < length2; ++k) {
                final Column column2 = columns2[k];
                if (column2.isKeyColumn) {
                    this.keyColumnNames[i] = column2.columnName;
                    this.keyFieldNames[i] = column2.name;
                    ++i;
                    if (nbrKeys == i) {
                        break;
                    }
                }
            }
        }
        this.checkStandardFields(all);
        this.allFieldNames = all.toString();
    }
    
    private void checkStandardFields(final StringBuilder all) {
        if (this.modifiedTimestampName != null || this.modifiedTimestampColumnName != null) {
            if (this.modifiedTimestampName == null) {
                this.modifiedTimestampName = this.modifiedTimestampColumnName;
            }
            else if (this.modifiedTimestampColumnName == null) {
                this.modifiedTimestampColumnName = this.modifiedTimestampName;
            }
            if (this.valueTypes.containsKey(this.modifiedTimestampName)) {
                Spit.out("ERROR: timestamp column should not be included in the list of columns. Column is deleted by the loader.");
                this.deleteColumnDefinition(this.modifiedTimestampName);
            }
            all.append(", ").append(this.modifiedTimestampColumnName).append(" as \"").append(this.modifiedTimestampName).append("\" ");
            this.valueTypes.put(this.modifiedTimestampName, DataValueType.TIMESTAMP);
        }
        final DataValueType userType = AP.loggedInUserDataTypeIsInteger ? DataValueType.INTEGRAL : DataValueType.TEXT;
        if (this.modifiedUserName != null || this.modifiedUserColumnName != null) {
            if (this.modifiedUserColumnName == null) {
                this.modifiedUserColumnName = this.modifiedUserName;
            }
            else if (this.modifiedUserName == null) {
                this.modifiedUserName = this.modifiedUserColumnName;
            }
            if (this.valueTypes.containsKey(this.modifiedUserName)) {
                Spit.out("ERROR: modified user name column should not be included in the list of columns. Column is deleted by the loader.");
                this.deleteColumnDefinition(this.modifiedUserName);
            }
            all.append(", ").append(this.modifiedUserColumnName).append(" as \"").append(this.modifiedUserName).append("\" ");
            this.valueTypes.put(this.modifiedUserName, userType);
        }
        if (this.createdTimestampName != null || this.createdTimestampColumnName != null) {
            if (this.createdTimestampName == null) {
                this.createdTimestampName = this.createdTimestampColumnName;
            }
            else if (this.createdTimestampColumnName == null) {
                this.createdTimestampColumnName = this.createdTimestampName;
            }
            if (this.valueTypes.containsKey(this.createdTimestampName)) {
                Spit.out("ERROR: created timestamp column should not be included in the list of columns. Column is deleted by the loader.");
                this.deleteColumnDefinition(this.createdTimestampName);
            }
            all.append(", ").append(this.createdTimestampColumnName).append(" as \"").append(this.createdTimestampName).append("\" ");
            this.valueTypes.put(this.createdTimestampName, DataValueType.TIMESTAMP);
        }
        if (this.createdUserName != null || this.createdUserColumnName != null) {
            if (this.createdUserName == null) {
                this.createdUserName = this.createdUserColumnName;
            }
            else if (this.createdUserColumnName == null) {
                this.createdUserColumnName = this.createdUserName;
            }
            if (this.valueTypes.containsKey(this.createdUserName)) {
                Spit.out("ERROR: createdUsaerName column should not be included in the list of columns. Column is deleted by the loader.");
                this.deleteColumnDefinition(this.createdUserName);
            }
            all.append(", ").append(this.createdUserColumnName).append(" as \"").append(this.createdUserName).append("\" ");
            this.valueTypes.put(this.createdUserName, userType);
        }
    }
    
    String getAllFieldNamesForLookup() {
        final StringBuilder all = new StringBuilder();
        boolean firstOne = true;
        Column[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final Column column = columns[i];
            if (firstOne) {
                firstOne = false;
            }
            else {
                all.append(", ");
            }
            all.append(column.columnName);
        }
        return all.toString();
    }
    
    private void deleteColumnDefinition(final String fieldName) {
        if (this.valueTypes.remove(fieldName) == null) {
            Spit.out("Internal error : " + fieldName + " can not be removed as it is not a column");
            return;
        }
        final int nbrCols = this.columns.length - 1;
        final Column[] cols = this.columns;
        this.columns = new Column[nbrCols];
        int i = 0;
        Column[] array;
        for (int length = (array = cols).length, j = 0; j < length; ++j) {
            final Column col = array[j];
            if (!fieldName.equals(col.name)) {
                this.columns[i] = col;
                ++i;
            }
        }
    }
    
    @Override
    public int insert(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final List<String> names = new ArrayList<String>();
        final List<Value> values = new ArrayList<Value>();
        if (this.keyToBeGenerated) {
            if (this.keyFieldNames.length == 0) {
                dc.raiseException("exilKeysNotDefined", this.name);
            }
            this.generateKey(dc);
            Spit.out("Key generated for table " + this.tableName);
        }
        final int n = this.getColumnsAndValues(dc, names, values, false);
        Spit.out("table insert for : " + this.name + " :" + n + " columns will be inserted.");
        if (n == 0) {
            dc.raiseException("nothingToInsert", this.name);
        }
        StringBuilder sbf = new StringBuilder();
        this.getInsertStatement(sbf, names, this.tableName, dc);
        final int rowsAffected = handle.executePreparedStatement(sbf.toString(), values, false);
        if (rowsAffected > 0 && this.toBeAudited()) {
            values.add(Value.newValue("insert"));
            names.add("action");
            sbf = new StringBuilder();
            this.getInsertStatement(sbf, names, String.valueOf(this.tableName) + AP.audittableSuffix, dc);
            handle.executePreparedStatement(sbf.toString(), values, true);
        }
        return rowsAffected;
    }
    
    public int insertFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid, final boolean[] rowSelector) throws ExilityException {
        final List<String> names = new ArrayList<String>();
        final List<ValueList> values = new ArrayList<ValueList>();
        if (this.keyToBeGenerated) {
            if (this.keyFieldNames.length == 0) {
                dc.raiseException("exilKeysNotDefined", this.name);
            }
            this.generateKeysForGrid(dc, grid, rowSelector);
        }
        final int n = this.getColumnsAndValuesFromGrid(dc, names, values, grid, false);
        if (n == 0) {
            dc.raiseException("nothingToInsert", this.name);
        }
        StringBuilder sbf = new StringBuilder();
        this.getInsertStatement(sbf, names, this.tableName, dc);
        final int rowsAffected = handle.executePreparedStatementBatch(sbf.toString(), values, false, rowSelector);
        if (rowsAffected > 0 && this.toBeAudited()) {
            values.add(this.getValueList(Value.newValue("insert"), grid.getNumberOfRows()));
            names.add("action");
            sbf = new StringBuilder();
            this.getInsertStatement(sbf, names, String.valueOf(this.tableName) + AP.audittableSuffix, dc);
            handle.executePreparedStatementBatch(sbf.toString(), values, true, rowSelector);
        }
        return rowsAffected;
    }
    
    @Override
    public int update(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.keyFieldNames.length == 0) {
            dc.raiseException("exilKeysNotDefined", this.name);
        }
        final List<String> names = new ArrayList<String>();
        final List<Value> values = new ArrayList<Value>();
        final int n = this.getColumnsAndValues(dc, names, values, true);
        if (n == 0) {
            dc.raiseException("nothingToUpate", this.name);
        }
        StringBuilder sbf = new StringBuilder();
        String[] keyFieldNames;
        for (int length = (keyFieldNames = this.keyFieldNames).length, i = 0; i < length; ++i) {
            final String keyFieldName = keyFieldNames[i];
            values.add(dc.getValue(keyFieldName));
        }
        boolean useTimeStamp = false;
        if (this.modifiedTimestampName != null && dc.hasValue(this.modifiedTimestampName)) {
            values.add(dc.getValue(this.modifiedTimestampName));
            useTimeStamp = true;
        }
        this.getUpdateStatement(sbf, names, this.tableName, dc, useTimeStamp);
        final int rowsAffected = handle.executePreparedStatement(sbf.toString(), values, false);
        if (rowsAffected > 0 && this.toBeAudited()) {
            values.add(Value.newValue("modify"));
            names.add("action");
            sbf = new StringBuilder();
            this.getInsertStatement(sbf, names, String.valueOf(this.tableName) + AP.audittableSuffix, dc);
            handle.executePreparedStatement(sbf.toString(), values, true);
        }
        return rowsAffected;
    }
    
    public int updateFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid, final boolean[] rowSelector) throws ExilityException {
        if (this.keyFieldNames.length == 0) {
            dc.raiseException("exilKeysNotDefined", this.name);
        }
        final List<String> names = new ArrayList<String>();
        final List<ValueList> values = new ArrayList<ValueList>();
        final int n = this.getColumnsAndValuesFromGrid(dc, names, values, grid, true);
        if (n == 0) {
            dc.raiseException("nothingToUpate", this.name);
        }
        String[] keyFieldNames;
        for (int length = (keyFieldNames = this.keyFieldNames).length, i = 0; i < length; ++i) {
            final String keyName = keyFieldNames[i];
            final ValueList list = grid.getColumn(keyName);
            if (list == null) {
                dc.raiseException("exilError", "Key column " + keyName + " not found for table " + this.name);
                return 0;
            }
            values.add(list);
        }
        StringBuilder sbf = new StringBuilder();
        boolean useTimeStamp = false;
        if (this.modifiedTimestampName != null && grid.hasColumn(this.modifiedTimestampName)) {
            values.add(grid.getColumn(this.modifiedTimestampName));
            useTimeStamp = true;
        }
        this.getUpdateStatement(sbf, names, this.tableName, dc, useTimeStamp);
        final int rowsAffected = handle.executePreparedStatementBatch(sbf.toString(), values, false, rowSelector);
        if (rowsAffected > 0 && this.toBeAudited()) {
            values.add(this.getValueList(Value.newValue("modify"), grid.getNumberOfRows()));
            names.add("action");
            sbf = new StringBuilder();
            this.getInsertStatement(sbf, names, String.valueOf(this.tableName) + AP.audittableSuffix, dc);
            handle.executePreparedStatementBatch(sbf.toString(), values, true, rowSelector);
        }
        return rowsAffected;
    }
    
    @Override
    public int delete(final DataCollection dc, final DbHandle handle) throws ExilityException {
        return this.deleteHelper(dc, handle, null, null);
    }
    
    public int deleteFromGrid(final DataCollection dc, final DbHandle handle, final Grid grid, final boolean[] selectors) throws ExilityException {
        return this.deleteHelper(dc, handle, grid, selectors);
    }
    
    private int deleteHelper(final DataCollection dc, final DbHandle handle, final Grid grid, final boolean[] selectors) throws ExilityException {
        if (this.keyFieldNames.length == 0) {
            dc.raiseException("exilKeysNotDefined", this.name);
        }
        final List<ValueList> valuesList = new ArrayList<ValueList>();
        final List<Value> values = new ArrayList<Value>();
        final List<String> names = new ArrayList<String>();
        StringBuilder sbf = new StringBuilder();
        if (this.okToDelete) {
            sbf.append("DELETE FROM ").append(this.tableName).append(" WHERE ");
        }
        else if (this.activeField == null) {
            dc.raiseException("exilDeleteNotAllowed", this.tableName);
        }
        else {
            sbf.append("UPDATE ").append(this.tableName).append(" SET ").append(this.activeField).append(" = 'N' ");
            if (this.modifiedTimestampName != null) {
                sbf.append(this.modifiedTimestampColumnName).append(" = ").append(AP.systemDateFunction).append(" ");
            }
            if (this.modifiedUserName != null) {
                sbf.append(this.modifiedUserColumnName).append(" = ").append(SqlUtil.formatValue(dc.getValue(AP.loggedInUserFieldName)));
            }
            sbf.append(" WHERE ");
        }
        String prefix = "";
        for (int i = 0; i < this.keyFieldNames.length; ++i) {
            if (grid == null) {
                final Value keyValue = dc.getValue(this.keyFieldNames[i]);
                if (keyValue == null || keyValue.isNull()) {
                    dc.raiseException("exilNoKeyForDelete", this.tableName, this.keyFieldNames[i]);
                }
                values.add(keyValue);
            }
            else {
                final ValueList keyList = grid.getColumn(this.keyFieldNames[i]);
                if (keyList == null) {
                    dc.raiseException("exilNoKeyForDelete", this.tableName, this.keyFieldNames[i]);
                }
                valuesList.add(keyList);
            }
            sbf.append(prefix).append(this.keyColumnNames[i]).append(" = ? ");
            names.add(this.keyFieldNames[i]);
            prefix = " AND ";
        }
        int rowsAffected;
        if (grid == null) {
            rowsAffected = handle.executePreparedStatement(sbf.toString(), values, false);
        }
        else {
            rowsAffected = handle.executePreparedStatementBatch(sbf.toString(), valuesList, false, selectors);
        }
        if (rowsAffected > 0 && this.toBeAudited()) {
            sbf = new StringBuilder();
            names.add("operation");
            values.add(Value.newValue("delete"));
            this.getInsertStatement(sbf, names, String.valueOf(this.name) + AP.audittableSuffix, dc);
            if (grid == null) {
                handle.executePreparedStatement(sbf.toString(), values, true);
            }
            else {
                handle.executePreparedStatementBatch(sbf.toString(), valuesList, true, null);
            }
        }
        return rowsAffected;
    }
    
    private int getColumnsAndValues(final DataCollection dc, final List<String> names, final List<Value> values, final boolean forUpdate) {
        int nbrCols = 0;
        Column[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final Column column = columns[i];
            if (!column.isKeyColumn || !forUpdate) {
                Value value = dc.getValue(column.name);
                if (column.dataSource != null && column.dataSource.length() > 0) {
                    if (value == null) {
                        value = dc.getValue(column.dataSource);
                        Spit.out(String.valueOf(column.name) + " with dataSource=" + column.dataSource + " got a value of " + value);
                    }
                    else if (AP.treatZeroAndEmptyStringAsNullForDataSource) {
                        final String textValue = value.toString();
                        if (textValue.length() == 0 || textValue.equals("0")) {
                            value = dc.getValue(column.dataSource);
                            Spit.out(String.valueOf(column.name) + " with dataSource=" + column.dataSource + " got a value of " + value);
                        }
                    }
                }
                if (value == null) {
                    if (forUpdate) {
                        continue;
                    }
                    if (column.defaultValue != null && column.defaultValue.length() > 0) {
                        value = column.getDataType().parseValue(column.name, column.defaultValue, null, dc);
                    }
                    if (!column.isNullable) {
                        if (value == null) {
                            continue;
                        }
                        if (value.isNull()) {
                            continue;
                        }
                    }
                    Spit.out("Table=" + this.name + " column=" + column.name + " is going to be inserted with its default value " + column.defaultValue);
                }
                values.add(value);
                names.add(column.columnName);
                ++nbrCols;
            }
        }
        return nbrCols;
    }
    
    private int getColumnsAndValuesFromGrid(final DataCollection dc, final List<String> names, final List<ValueList> values, final Grid grid, final boolean forUpdate) throws ExilityException {
        int nbrCols = 0;
        final int nbrRows = grid.getNumberOfRows();
        Column[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final Column column = columns[i];
            if (!column.isKeyColumn || !forUpdate) {
                ValueList list = null;
                if (column.dataSource != null && dc.hasValue(column.dataSource)) {
                    list = this.getValueList(dc.getValue(column.dataSource), nbrRows);
                    grid.addColumn(column.name, list);
                }
                else if (grid.hasColumn(column.name)) {
                    list = grid.getColumn(column.name);
                }
                else {
                    if (forUpdate) {
                        Spit.out("No value found for column " + column.name + ". It will not be updated.");
                        continue;
                    }
                    Value value = null;
                    if (column.defaultValue != null && column.defaultValue.length() > 0) {
                        value = column.getDataType().parseValue(column.name, column.defaultValue, null, dc);
                    }
                    if (value == null || value.isNull()) {
                        Spit.out("No value found for column " + column.name + " nor is a default specified for it. It will not be in serted.");
                        continue;
                    }
                    list = this.getValueList(value, nbrRows);
                    grid.addColumn(column.name, list);
                }
                names.add(column.columnName);
                values.add(list);
                ++nbrCols;
            }
        }
        return nbrCols;
    }
    
    private void getInsertStatement(final StringBuilder sbf, final List<String> names, final String tableNameToUse, final DataCollection dc) {
        sbf.append("INSERT INTO ").append(tableNameToUse).append(" (");
        final StringBuilder ibf = new StringBuilder();
        for (final String columnName : names) {
            sbf.append(columnName).append(',');
            ibf.append("?,");
        }
        sbf.deleteCharAt(sbf.length() - 1);
        ibf.deleteCharAt(ibf.length() - 1);
        if (this.modifiedTimestampName != null) {
            sbf.append(',').append(this.modifiedTimestampColumnName);
            ibf.append(',').append(AP.systemDateFunction);
        }
        if (this.modifiedUserName != null) {
            sbf.append(',').append(this.modifiedUserColumnName);
            ibf.append(',').append(SqlUtil.formatValue(dc.getValue(AP.loggedInUserFieldName)));
        }
        if (this.createdTimestampName != null) {
            sbf.append(',').append(this.createdTimestampColumnName);
            ibf.append(',').append(AP.systemDateFunction);
        }
        if (this.createdUserName != null) {
            sbf.append(',').append(this.createdUserColumnName);
            ibf.append(',').append(SqlUtil.formatValue(dc.getValue(AP.loggedInUserFieldName)));
        }
        sbf.append(") VALUES (").append(ibf.toString()).append(")");
    }
    
    private void getUpdateStatement(final StringBuilder sbf, final List<String> names, final String tableNameToUse, final DataCollection dc, final boolean useTimestampCheck) {
        sbf.append("UPDATE ").append(tableNameToUse).append(" SET ");
        for (final String nam : names) {
            sbf.append(nam).append(" = ?,");
        }
        sbf.deleteCharAt(sbf.length() - 1);
        if (this.modifiedTimestampName != null) {
            sbf.append(',').append(this.modifiedTimestampColumnName).append(" = ").append(AP.systemDateFunction).append(" ");
        }
        if (this.modifiedUserName != null) {
            sbf.append(',').append(this.modifiedUserColumnName).append(" = ").append(SqlUtil.formatValue(dc.getValue(AP.loggedInUserFieldName)));
        }
        sbf.append(" WHERE ");
        String[] keyColumnNames;
        for (int length = (keyColumnNames = this.keyColumnNames).length, i = 0; i < length; ++i) {
            final String keyColumnName = keyColumnNames[i];
            sbf.append(keyColumnName).append(" = ? AND ");
        }
        if (useTimestampCheck) {
            sbf.append(this.modifiedTimestampName).append(" = ?");
        }
        else {
            final int len = sbf.length();
            sbf.delete(len - 4, len);
        }
    }
    
    public void toScript(final StringBuilder sbf) {
        Spit.out("Table.toScript(sbf) is not yet implemented");
    }
    
    @Override
    public int readbasedOnKey(final DataCollection dc, final DbHandle handle) throws ExilityException {
        return this.read(dc, handle, null, null);
    }
    
    public boolean createFromDb(final String name, final String tableName) {
        this.name = name;
        try {
            final Grid dbColumns = DbHandle.getAllColumns(tableName);
            final int n = dbColumns.getNumberOfRows();
            if (n == 0) {
                return false;
            }
            this.tableName = tableName;
            this.columns = new Column[n];
            for (int i = 0; i < n; ++i) {}
        }
        catch (ExilityException e) {
            return false;
        }
        return true;
    }
    
    private Map<String, DbColumn> getDbColumns() {
        final Map<String, DbColumn> dbColumns = new HashMap<String, DbColumn>();
        try {
            final Grid grid = DbHandle.getAllColumns(this.tableName);
            grid.getNumberOfRows();
        }
        catch (ExilityException ex) {}
        return dbColumns;
    }
}
