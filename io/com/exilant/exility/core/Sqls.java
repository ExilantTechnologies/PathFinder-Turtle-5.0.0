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

public class Sqls
{
    private static final HashMap<String, SqlInterface> sqls;
    
    static {
        sqls = new HashMap<String, SqlInterface>();
    }
    
    public static SqlInterface getSqlTemplateOrNull(final String name) {
        return getTemplateWorker(name);
    }
    
    public static SqlInterface getTemplate(final String name, final DataCollection dc) throws ExilityException {
        if (name == null || name.length() == 0) {
            return null;
        }
        final SqlInterface sql = getTemplateWorker(name);
        if (sql == null) {
            dc.raiseException("exilNoSuchSql", name);
        }
        return sql;
    }
    
    private static SqlInterface getTemplateWorker(final String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        SqlInterface sql = null;
        if (Sqls.sqls.containsKey(name)) {
            return Sqls.sqls.get(name);
        }
        sql = (Sql)ResourceManager.loadResource("sql." + name, Sql.class);
        if (sql == null || sql.getName() == null) {
            return null;
        }
        if (AP.definitionsToBeCached) {
            Sqls.sqls.put(name, sql);
        }
        return sql;
    }
    
    public static String getSql(final String name, final DataCollection dc) throws ExilityException {
        final SqlInterface sql = getTemplateWorker(name);
        if (sql != null && sql instanceof Sql) {
            return ((Sql)sql).getSql(dc);
        }
        throw new ExilityException("No sql tempalte with name " + name);
    }
    
    static void save(final Sql sql, final DataCollection dc) {
        ResourceManager.saveResource("sql." + sql.name, sql);
    }
    
    public static Sql getSqlOnTheFly(final String sqlText, final boolean toBeExecuted, final String[] fieldNames, final DataValueType[] valueTypes) {
        final Sql sql = new Sql();
        sql.name = "Runtime Generated";
        sql.sql = sqlText;
        sql.toBeExecuted = toBeExecuted;
        sql.setColumnNames(fieldNames);
        sql.setValueTypes(valueTypes);
        return sql;
    }
    
    static void flush() {
        Sqls.sqls.clear();
    }
}
