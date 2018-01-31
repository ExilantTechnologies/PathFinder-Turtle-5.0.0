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

import java.util.HashMap;
import java.util.Map;

public class Condition
{
    private static Comparator[] filterValues;
    private static Map<Comparator, String> sqlOperators;
    String columnName;
    String fieldName;
    Comparator comparator;
    String gridName;
    
    static {
        (Condition.filterValues = new Comparator[9])[0] = null;
        Condition.filterValues[1] = Comparator.EQUALTO;
        Condition.filterValues[2] = Comparator.STARTSWITH;
        Condition.filterValues[3] = Comparator.CONTAINS;
        Condition.filterValues[4] = Comparator.GREATERTHAN;
        Condition.filterValues[5] = Comparator.LESSTHAN;
        Condition.filterValues[6] = Comparator.BETWEEN;
        Condition.filterValues[7] = Comparator.IN;
        Condition.filterValues[8] = Comparator.NOTIN;
        Condition.sqlOperators = new HashMap<Comparator, String>() {
            private static final long serialVersionUID = 1L;
            
            {
                this.put(Comparator.EQUALTO, " = ");
                this.put(Comparator.NOTEQUALTO, " != ");
                this.put(Comparator.GREATERTHAN, " > ");
                this.put(Comparator.GREATERTHANOREQUALTO, " >= ");
                this.put(Comparator.LESSTHAN, " < ");
                this.put(Comparator.LESSTHANOREQUALTO, " <= ");
            }
        };
    }
    
    public Condition() {
        this.columnName = null;
        this.fieldName = null;
        this.comparator = Comparator.EQUALTO;
        this.gridName = null;
    }
    
    public Condition(final String colName, final String fieldName, final Comparator comp) {
        this.columnName = null;
        this.fieldName = null;
        this.comparator = Comparator.EQUALTO;
        this.gridName = null;
        this.columnName = colName;
        this.fieldName = fieldName;
        this.comparator = comp;
    }
    
    public static boolean toSql(final StringBuilder sbf, final Condition[] conditions, final DataCollection dc) throws ExilityException {
        if (conditions == null || conditions.length == 0) {
            return false;
        }
        int countOfConditionsAdded = 0;
        for (final Condition condition : conditions) {
            if (countOfConditionsAdded > 0) {
                sbf.append(" AND ");
            }
            if (condition.appendToSql(sbf, dc)) {
                ++countOfConditionsAdded;
            }
        }
        return countOfConditionsAdded != 0;
    }
    
    public boolean appendToSql(final StringBuilder sbf, final DataCollection dc) throws ExilityException {
        if (this.comparator == Comparator.EXISTS || this.comparator == Comparator.DOESNOTEXIST) {
            throw new ExilityException(String.valueOf(this.comparator.toString()) + " can not be used in a condition for table based opertations.");
        }
        if (this.comparator == Comparator.IN || this.comparator == Comparator.NOTIN) {
            sbf.append(' ').append(this.columnName).append(' ');
            return this.appendListToSql(sbf, dc);
        }
        if (this.comparator == Comparator.FILTER) {
            return this.appendToSqlForFilter(sbf, dc);
        }
        return this.appendToSqlOthers(sbf, dc, this.comparator);
    }
    
    private boolean appendToSqlOthers(final StringBuilder sbf, final DataCollection dc, final Comparator cmpToUse) {
        final Value value = dc.getValue(this.fieldName);
        if (value == null) {
            return false;
        }
        sbf.append(this.columnName);
        if (cmpToUse == Comparator.CONTAINS) {
            sbf.append(" LIKE '%").append(value.getTextValue().replaceAll("'", "''")).append("%'");
        }
        else if (cmpToUse == Comparator.STARTSWITH) {
            sbf.append(" LIKE '").append(value.getTextValue().replaceAll("'", "''")).append("%'");
        }
        else if (cmpToUse == Comparator.BETWEEN) {
            final Value toValue = dc.getValue(String.valueOf(this.fieldName) + "To");
            if (toValue == null) {
                return false;
            }
            sbf.append(" BETWEEN (").append(SqlUtil.formatValue(value)).append(',').append(SqlUtil.formatValue(toValue)).append(")");
        }
        else {
            sbf.append(Condition.sqlOperators.get(cmpToUse)).append(SqlUtil.formatValue(value));
        }
        return true;
    }
    
    private boolean appendToSqlForFilter(final StringBuilder sbf, final DataCollection dc) {
        int filterValue = 0;
        try {
            filterValue = Integer.parseInt(dc.getValue(String.valueOf(this.fieldName) + "Operator").toString());
        }
        catch (Exception e) {
            return false;
        }
        return filterValue >= 1 && filterValue <= Condition.filterValues.length && this.appendToSqlOthers(sbf, dc, Condition.filterValues[filterValue]);
    }
    
    private boolean appendListToSql(final StringBuilder sbf, final DataCollection dc) {
        ValueList values = null;
        final int dotAt = this.fieldName.indexOf(46);
        if (dotAt == -1) {
            values = dc.getValueList(this.fieldName);
        }
        else {
            final String tableName = this.fieldName.substring(0, dotAt);
            final String listName = this.fieldName.substring(dotAt + 1);
            final Grid grid = dc.getGrid(tableName);
            if (grid != null) {
                values = grid.getColumn(listName);
            }
        }
        if (values == null || values.length() == 0) {
            return false;
        }
        if (this.comparator == Comparator.NOTIN) {
            sbf.append(" NOT ");
        }
        sbf.append(" IN (").append(SqlUtil.formatList(values)).append(')');
        return true;
    }
    
    static String render(final Condition[] conditions) {
        final StringBuilder sbf = new StringBuilder();
        for (final Condition condition : conditions) {
            sbf.append(condition.columnName).append(' ').append(condition.comparator.toString()).append(' ').append(condition.fieldName).append('\n');
        }
        return sbf.toString();
    }
}
