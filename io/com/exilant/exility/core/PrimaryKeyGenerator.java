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

public class PrimaryKeyGenerator
{
    private static final String UPDATE_SQL = "update exilPrimaryKey set lastKeyUsed = lastKeyUsed + ";
    private static final String WHERE = " where tableName = '";
    private static final String INSERT_SQL = "insert into exilPrimaryKey (tableName, lastKeyUsed) values ('";
    private static final String TABLE_NAME = "exilPrimaryKey";
    private static final String SELECT_SQL = "select lastKeyUsed from exilPrimaryKey where tableName = '";
    
    public static long getNextKey(final String tableName, final String columnName, final int nbrKeys) throws ExilityException {
        long key = 0L;
        final DbHandle handle = DbHandle.borrowHandle(DataAccessType.READWRITE);
        handle.beginTransaction();
        boolean gotIntoTrouble = false;
        try {
            final StringBuilder sql = new StringBuilder();
            sql.append("update exilPrimaryKey set lastKeyUsed = lastKeyUsed + ").append(nbrKeys).append(" where tableName = '").append(tableName).append('\'');
            final int n = handle.execute(sql.toString(), false);
            if (n > 0) {
                final Object obj = handle.extractSingleField("select lastKeyUsed from exilPrimaryKey where tableName = '" + tableName + '\'');
                if (obj == null) {
                    Spit.out("Row for table " + tableName + " not found in " + "exilPrimaryKey");
                    key = 0L;
                }
                else {
                    key = ((Number)obj).longValue();
                }
            }
            else {
                Spit.out("row for table " + tableName + " not found in " + "exilPrimaryKey" + ". row will be inserted using max(" + columnName + ") from this table.");
                sql.setLength(0);
                sql.append("select max(").append(columnName).append(") from ").append(tableName);
                final Object obj = handle.extractSingleField(sql.toString());
                if (obj == null) {
                    Spit.out("Unable to get max key from this table");
                    key = 0L;
                }
                else {
                    key = ((Number)obj).longValue();
                }
                key += nbrKeys;
                sql.setLength(0);
                sql.append("insert into exilPrimaryKey (tableName, lastKeyUsed) values ('").append(tableName).append("',").append(nbrKeys).append(")");
                handle.execute(sql.toString(), false);
            }
        }
        catch (ExilityException e) {
            Spit.out("Error while generating primary key for table " + tableName + ". " + e.getMessage());
            gotIntoTrouble = true;
        }
        if (gotIntoTrouble) {
            handle.rollback();
        }
        else {
            handle.commit();
        }
        DbHandle.returnHandle(handle);
        key = key - nbrKeys + 1L;
        Spit.out(String.valueOf(key) + " generated for table " + tableName);
        return key;
    }
}
