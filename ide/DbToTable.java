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

package com.exilant.exility.ide;

import com.exilant.exility.core.DataAccessType;
import com.exilant.exility.core.ExilityException;
import com.exilant.exility.core.ObjectManager;
import com.exilant.exility.core.Table;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.ServiceInterface;

public class DbToTable implements ServiceInterface
{
    static String NAME;
    static String DB_TABLE_NAME;
    
    static {
        DbToTable.NAME = "tableName";
        DbToTable.DB_TABLE_NAME = "tableName";
    }
    
    @Override
    public void execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final String tableName = dc.getTextValue(DbToTable.DB_TABLE_NAME, null);
        if (tableName == null || tableName.length() == 0) {
            dc.addError(String.valueOf(DbToTable.DB_TABLE_NAME) + " is required for this service.");
            return;
        }
        String defName = dc.getTextValue(DbToTable.NAME, null);
        if (defName == null || defName.length() == 0) {
            defName = tableName;
        }
        final Table table = new Table();
        if (table.createFromDb(defName, tableName)) {
            ObjectManager.toDc(table, dc);
        }
        else {
            dc.addError("Error while getting data base table " + tableName + ". You are encouraged to get to the root of this issue rather we providiing some clues about it :-)");
        }
    }
    
    @Override
    public String getName() {
        return "dbToTable";
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.READONLY;
    }
}
