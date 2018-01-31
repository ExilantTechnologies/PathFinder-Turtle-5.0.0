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

public class GetTable implements CustomCodeInterface
{
    static String FOLDER_NAME;
    static String NAME;
    
    static {
        GetTable.FOLDER_NAME = "folderName";
        GetTable.NAME = "name";
    }
    
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        String tableName = dc.getTextValue(GetTable.NAME, null);
        final String folderName = dc.getTextValue(GetTable.FOLDER_NAME, "");
        if (folderName.length() == 0) {
            tableName = String.valueOf(folderName) + '.' + tableName;
        }
        try {
            final Table table = (Table)Tables.getTable(tableName, dc);
            ObjectManager.toDc(table, dc);
            return 1;
        }
        catch (ExilityException e) {
            dc.addMessage("exilError", "Unable to load Table definition.\n " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
