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

public class Tables
{
    private static final HashMap<String, TableInterface> tables;
    
    static {
        tables = new HashMap<String, TableInterface>();
    }
    
    public static TableInterface getTable(final String tableDefinitionName, final DataCollection dc) throws ExilityException {
        final TableInterface table = getTableWorker(tableDefinitionName);
        if (table == null) {
            final String err = "table definition not found for " + tableDefinitionName;
            dc.raiseException(err, new String[0]);
        }
        return table;
    }
    
    public static TableInterface getTableOrNull(final String tableDefinitionName) {
        return getTableWorker(tableDefinitionName);
    }
    
    static void saveTable(final TableInterface table, final DataCollection dc) {
        ResourceManager.saveResource("table." + table.getName(), table);
    }
    
    private static TableInterface getTableWorker(final String tableDefinitionName) {
        if (Tables.tables.containsKey(tableDefinitionName)) {
            return Tables.tables.get(tableDefinitionName);
        }
        TableInterface table = null;
        table = (Table)ResourceManager.loadResource("table." + tableDefinitionName, Table.class);
        if (table == null || table.getName() == null) {
            return null;
        }
        if (AP.definitionsToBeCached) {
            Tables.tables.put(tableDefinitionName, table);
        }
        return table;
    }
    
    static void flush() {
        Tables.tables.clear();
    }
}
