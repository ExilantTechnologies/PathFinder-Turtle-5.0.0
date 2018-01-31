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
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Sql implements SqlInterface, ToBeInitializedInterface
{
    public String name;
    public String sql;
    int minParameters;
    String module;
    String description;
    boolean toBeExecuted;
    SqlType sqlType;
    SqlParameter[] inputParameters;
    Parameter[] outputParameters;
    String storedProcedureName;
    private String[] columnNames;
    private DataValueType[] valueTypes;
    
    public Sql() {
        this.name = null;
        this.sql = null;
        this.minParameters = 0;
        this.module = null;
        this.description = null;
        this.toBeExecuted = false;
        this.sqlType = SqlType.dynamicSql;
        this.inputParameters = null;
        this.outputParameters = null;
        this.storedProcedureName = null;
        this.columnNames = null;
        this.valueTypes = null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void initialize() {
        if (this.outputParameters != null && this.outputParameters.length > 0) {
            final int n = this.outputParameters.length;
            this.columnNames = new String[n];
            this.valueTypes = new DataValueType[n];
            for (int i = 0; i < n; ++i) {
                final Parameter p = this.outputParameters[i];
                this.columnNames[i] = p.name;
                this.valueTypes[i] = p.getValueType();
            }
        }
        if (this.sql != null && this.storedProcedureName != null) {
            Spit.out("Sql " + this.name + " has provided stored procedure name as well as sql. storedProcedure will be ignored and this will be treated as a sql.");
            this.storedProcedureName = null;
        }
    }
    
    String[] getColumnNames() {
        return this.columnNames;
    }
    
    DataValueType[] getValueTypes() {
        return this.valueTypes;
    }
    
    void setColumnNames(final String[] columnNames) {
        this.columnNames = columnNames;
    }
    
    public void setValueTypes(final DataValueType[] valueTypes) {
        this.valueTypes = valueTypes;
    }
    
    String getSql(final DataCollection dc) throws ExilityException {
        return this.getSql(dc, null, 0);
    }
    
    String getSql(final DataCollection dc, final Grid grid, final int index) throws ExilityException {
        if (this.inputParameters == null || this.inputParameters.length == 0) {
            return this.sql;
        }
        final StringBuilder sbf = new StringBuilder(this.sql);
        int paramCount = 0;
        for (int k = this.inputParameters.length - 1; k >= 0; --k) {
            final SqlParameter param = this.inputParameters[k];
            final String val = param.getValue(dc, null, grid, index);
            if (val != null && val.length() > 0) {
                ++paramCount;
            }
            else if (!param.isOptional) {
                dc.raiseException("exilNoValueForParameter", param.name, this.name);
            }
            final String p = "@" + (k + 1);
            if (!param.isOptional) {
                this.substituteRequired(p, sbf, val);
            }
            else {
                this.substituteOptional(p, sbf, val, dc);
            }
        }
        boolean removeText = false;
        if (paramCount == 0) {
            removeText = true;
        }
        this.removeResidualBraces(sbf, dc, removeText);
        if (this.minParameters > 0 && paramCount < this.minParameters) {
            dc.raiseException("exilNotEnoughParameters", this.name, String.valueOf(this.minParameters), String.valueOf(paramCount));
        }
        return sbf.toString();
    }
    
    private void substituteOptional(final String param, final StringBuilder sbf, final String val, final DataCollection dc) throws ExilityException {
        final int paramLength = param.length();
        final String op = "{";
        final String cl = "}";
        for (int paramAt = sbf.indexOf(param); paramAt >= 0; paramAt = sbf.indexOf(param)) {
            final int openBracketAt = sbf.lastIndexOf(op, paramAt);
            final int closeBracketAt = sbf.indexOf(cl, paramAt);
            if (openBracketAt < 0 || closeBracketAt < 0) {
                dc.raiseException("exilBracesNotFound", this.name, param);
            }
            if (val == null || val.length() == 0) {
                sbf.delete(openBracketAt, closeBracketAt + 1);
            }
            else {
                sbf.deleteCharAt(closeBracketAt);
                sbf.replace(paramAt, paramAt + paramLength, val);
                sbf.deleteCharAt(openBracketAt);
            }
        }
    }
    
    private void substituteRequired(final String param, final StringBuilder sbf, final String val) {
        final int paramLength = param.length();
        int i = sbf.length();
        while (true) {
            i = sbf.lastIndexOf(param, i);
            if (i < 0) {
                break;
            }
            sbf.replace(i, i + paramLength, val);
        }
    }
    
    private void removeResidualBraces(final StringBuilder sbf, final DataCollection dc, final boolean removeText) throws ExilityException {
        final int openBracketAt = sbf.lastIndexOf("{");
        final int closeBracketAt = sbf.indexOf("}");
        if (openBracketAt < 0 && closeBracketAt < 0) {
            return;
        }
        if (openBracketAt < 0 || closeBracketAt < 0 || openBracketAt > closeBracketAt) {
            Spit.out("SQl after substituion has invalid braces in that\n" + sbf.toString());
            dc.raiseException("exilUnmatchedBraces", this.name);
        }
        if (removeText) {
            sbf.delete(openBracketAt, closeBracketAt + 1);
        }
        else {
            sbf.deleteCharAt(closeBracketAt);
            sbf.deleteCharAt(openBracketAt);
        }
    }
    
    private int executePs(final DataCollection dc, final DbHandle handle, final String gridName) throws ExilityException {
        final String sqlText = this.getSql(dc);
        if (gridName == null) {
            final List<Value> values = this.getValueList(dc);
            if (values == null) {
                return 0;
            }
            return handle.executePreparedStatement(sqlText, values, false);
        }
        else {
            final List<ValueList> values2 = this.getValuesList(dc, gridName);
            if (values2 == null) {
                return 0;
            }
            return handle.executePreparedStatementBatch(sqlText, values2, false, null);
        }
    }
    
    private int extractPs(final DataCollection dc, final DbHandle handle, final String gridName, final String prefix) throws ExilityException {
        final String sqlText = this.getSql(dc);
        final List<Value> values = this.getValueList(dc);
        if (values == null) {
            return 0;
        }
        return handle.extractFromPreparedStatement(sqlText, values, this.columnNames, prefix, this.valueTypes, dc, gridName, this);
    }
    
    private List<ValueList> getValuesList(final DataCollection dc, final String gridName) {
        final Grid grid = dc.getGrid(gridName);
        if (grid == null) {
            dc.addMessage("exilError", "Grid " + gridName + " is not found.");
            return null;
        }
        final List<ValueList> values = new ArrayList<ValueList>();
        SqlParameter[] inputParameters;
        for (int length = (inputParameters = this.inputParameters).length, i = 0; i < length; ++i) {
            final Parameter parm = inputParameters[i];
            final ValueList list = grid.getColumn(parm.name);
            if (list == null) {
                dc.addMessage("exilError", "Column " + parm.name + " not found in grid " + gridName + ".");
                return null;
            }
            values.add(list);
        }
        return values;
    }
    
    private List<Value> getValueList(final DataCollection dc) {
        final List<Value> values = new ArrayList<Value>();
        SqlParameter[] inputParameters;
        for (int length = (inputParameters = this.inputParameters).length, i = 0; i < length; ++i) {
            final Parameter parm = inputParameters[i];
            final Value value = dc.getValue(parm.name);
            Spit.out("parameter " + parm.name + " has value " + value);
            if (value == null) {
                dc.addMessage("exilFieldIsRequired", parm.name);
                return null;
            }
            values.add(value);
        }
        return values;
    }
    
    @Override
    public int execute(final DataCollection dc, final DbHandle handle, final String gridName, final String prefix) throws ExilityException {
        if (this.storedProcedureName != null) {
            return handle.executeSP(this.storedProcedureName, this.inputParameters, null, this.outputParameters, dc);
        }
        final String sqlText = this.getSql(dc);
        if (this.sqlType == SqlType.preparedStatement) {
            if (this.toBeExecuted) {
                return this.executePs(dc, handle, gridName);
            }
            return this.extractPs(dc, handle, gridName, prefix);
        }
        else {
            if (this.toBeExecuted) {
                return handle.execute(sqlText, false);
            }
            if (gridName == null) {
                return handle.extractSingleRow(sqlText, dc.values, this, prefix);
            }
            final Grid grid = handle.extractToGrid(sqlText, this, gridName, null);
            if (grid == null) {
                return 0;
            }
            dc.addGrid(gridName, grid);
            return grid.getNumberOfRows();
        }
    }
    
    @Override
    public int executeBulkTask(final DataCollection dc, final DbHandle handle, final String gridName, final boolean forValidation) throws ExilityException {
        final Grid grid = dc.getGrid(gridName);
        if (grid == null) {
            return 0;
        }
        final int nbrRows = grid.getNumberOfRows();
        if (nbrRows == 0) {
            return 0;
        }
        final String[] sqlText = new String[nbrRows];
        for (int i = 0; i < sqlText.length; ++i) {
            sqlText[i] = this.getSql(dc, grid, i);
        }
        if (this.toBeExecuted) {
            return handle.executeBatch(sqlText, false);
        }
        final Map<String, Value> values = new HashMap<String, Value>();
        int nbrRowsExtracted = 0;
        for (int j = 0; j < sqlText.length; ++j) {
            final int n = handle.extractSingleRow(sqlText[j], values, this, null);
            if (n != 0) {
                if (nbrRowsExtracted == 0) {
                    this.addColumnsToGrid(grid, forValidation ? gridName : null);
                }
                ++nbrRowsExtracted;
                for (final String nm : values.keySet()) {
                    grid.setValue(nm, values.get(nm), j);
                }
            }
        }
        if (nbrRowsExtracted == 0) {
            this.addColumnsToGrid(grid, forValidation ? gridName : null);
        }
        return nbrRowsExtracted;
    }
    
    private void addColumnsToGrid(final Grid grid, final String gridName) throws ExilityException {
        if (this.columnNames == null) {
            return;
        }
        final int nbrRows = grid.getNumberOfRows();
        for (int i = 0; i < this.columnNames.length; ++i) {
            final String columnName = this.columnNames[i];
            if (!grid.hasColumn(columnName)) {
                grid.addColumn(columnName, ValueList.newList(this.valueTypes[i], nbrRows));
            }
        }
        if (gridName != null) {
            grid.addColumn(String.valueOf(gridName) + "KeyFound", ValueList.newList(DataValueType.BOOLEAN, nbrRows));
        }
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        if (this.toBeExecuted) {
            return DataAccessType.READWRITE;
        }
        return DataAccessType.READWRITE;
    }
    
    void toJavaPsBlock(final StringBuilder sbf) {
        final List<SqlPart> parts = this.getSqlParts();
        if (parts == null) {
            Spit.out(String.valueOf(this.name) + " can not be converted");
            return;
        }
        final String lineText = "\n\t\t";
        sbf.append(lineText).append("StringBuilder sbf = new StringBuilder();");
        sbf.append(lineText).append("List<Value> values = new ArrayList<Value>();");
        sbf.append('\n').append(lineText).append("// Let us construct sql based on input parameters");
        final String startingSql = parts.get(0).fixedText;
        parts.get(0).fixedText = "";
        sbf.append(lineText).append("sbf.append(\"").append(startingSql).append("\");");
        for (final SqlPart p : parts) {
            p.toJava(sbf, lineText);
        }
    }
    
    public String getASamplePs() {
        final StringBuilder sbf = new StringBuilder();
        final List<SqlPart> parts = this.getSqlParts();
        for (final SqlPart p : parts) {
            sbf.append(p.fixedText).append(p.conditionalText);
        }
        sbf.append("\n parameters in order are \n");
        for (final SqlPart p : parts) {
            sbf.append(p.parm.name).append("  ");
        }
        return sbf.toString();
    }
    
    private List<SqlPart> getSqlParts() {
        final List<SqlPart> parts = new ArrayList<SqlPart>();
        final String s = this.sql;
        int startAt = 0;
        String nextStr = "";
        final boolean firstTextAssigned = false;
        for (int foundAt = s.indexOf(64, startAt); foundAt >= 0; foundAt = s.indexOf(64, startAt)) {
            char a = s.charAt(foundAt + 1);
            if (a < '0' && a > '9') {
                nextStr = String.valueOf(nextStr) + s.substring(startAt, foundAt);
                startAt = foundAt + 1;
            }
            else {
                final SqlPart part = new SqlPart();
                parts.add(part);
                int width = 2;
                int idx = a - '0';
                a = s.charAt(foundAt + 2);
                if (a >= '0' && a <= '9') {
                    width = 3;
                    idx = 10 * idx + (a - '0');
                }
                if (idx >= this.inputParameters.length) {
                    Spit.out("Invalid @i found in sql " + this.name);
                    return null;
                }
                part.parm = this.inputParameters[idx];
                if (part.parm.isOptional) {
                    final int openAt = s.lastIndexOf(123, foundAt);
                    final int closeAt = s.indexOf(125, foundAt);
                    if (openAt == -1 || closeAt == -1) {
                        Spit.out(String.valueOf(this.name) + " does not have mathcing braces for input parameter " + part.parm.name);
                        return null;
                    }
                    if (openAt < startAt) {
                        Spit.out(String.valueOf(this.name) + " has overlapped braces for parameter " + part.parm.name);
                        return null;
                    }
                    part.fixedText = (String.valueOf(nextStr) + s.substring(startAt, openAt - 1)).replaceAll("\"", "\\\"");
                    if (!firstTextAssigned && parts.size() > 1) {
                        parts.get(0).fixedText = part.fixedText;
                        part.fixedText = "";
                    }
                    part.conditionalText = String.valueOf(s.substring(openAt + 1, foundAt - 1)) + " ? " + s.substring(foundAt + width, closeAt - 1);
                    part.conditionalText = part.conditionalText.replaceAll("\"", "\\\"");
                    nextStr = "";
                    startAt = closeAt + 1;
                }
                else {
                    nextStr = String.valueOf(nextStr) + s.substring(startAt, foundAt - 1) + " ? ";
                    startAt = foundAt + width;
                }
            }
        }
        return parts;
    }
    
    class SqlPart
    {
        SqlParameter parm;
        String fixedText;
        String conditionalText;
        
        SqlPart() {
            this.fixedText = "";
            this.conditionalText = "";
        }
        
        void toJava(final StringBuilder sbf, final String lineText) {
            if (this.fixedText.length() > 0) {
                sbf.append(lineText).append("sql.append(\"").append(this.fixedText).append("\");");
            }
            if (this.conditionalText.length() > 0) {
                sbf.append('\n').append(lineText).append("if (this.").append(this.parm.name).append(" != null )");
                sbf.append(lineText).append('{');
                sbf.append(lineText).append('\t').append("sql.append(\"").append(this.conditionalText).append("\");");
                sbf.append(lineText).append('\t').append("values.add(Value.newValue(this.").append(this.parm.name).append(");");
                sbf.append(lineText).append('}');
            }
            else {
                sbf.append('\n').append(lineText).append("values.add(Value.newValue(this.").append(this.parm.name).append(");");
            }
        }
    }
}
