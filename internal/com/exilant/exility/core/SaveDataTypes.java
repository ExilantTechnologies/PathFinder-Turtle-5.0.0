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

import java.util.Iterator;

public class SaveDataTypes implements CustomCodeInterface
{
    static final String GRID_NAME = "dataTypes";
    static final String BULK_ACTION_NAME = "bulkAction";
    static final String DELETE_ACTION = "delete";
    
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        final Grid types = dc.getGrid("dataTypes");
        if (types == null) {
            dc.addMessage("exilError", "Invalid data: grid with name dataTypes not found in dc.");
            return 0;
        }
        final int nbrTypes = types.getNumberOfRows();
        final DataTypes dts = new DataTypes();
        final AbstractDataType[] toBedeleted = new AbstractDataType[nbrTypes];
        int nbrToBeDeleted = 0;
        int nbrTypesAdded = 0;
        boolean errorEncountered = false;
        for (int i = 0; i < nbrTypes; ++i) {
            types.copyRowToDc(i, null, dc);
            final Object obj = ObjectManager.createFromDc(dc);
            if (obj == null || !(obj instanceof AbstractDataType)) {
                dc.addMessage("exilError", "Unable to convert contents of row " + (i + 1) + " into a valid data type. Probably " + "_type" + " is not a valid data type in this.");
                errorEncountered = true;
            }
            else {
                final AbstractDataType dt = (AbstractDataType)obj;
                final String action = dc.getTextValue("bulkAction", "");
                if (action.equals("delete")) {
                    toBedeleted[nbrToBeDeleted] = dt;
                    ++nbrToBeDeleted;
                }
                else {
                    dts.dataTypes.put(dt.name, dt);
                    ++nbrTypesAdded;
                }
            }
        }
        if (errorEncountered) {
            return 0;
        }
        final GetDataTypes getter = new GetDataTypes();
        final String fullName = getter.getFileName(dc);
        dc.removeGrid("dataTypes");
        ResourceManager.saveResource(fullName, dts);
        dc.addMessage("exilInfo", String.valueOf(fullName) + " saved with " + nbrTypesAdded + " messages.");
        if (nbrToBeDeleted > 0) {
            AbstractDataType[] array;
            for (int length = (array = toBedeleted).length, j = 0; j < length; ++j) {
                final AbstractDataType dt = array[j];
                DataTypes.removeDataType(dt);
            }
        }
        for (final String name : dts.dataTypes.keySet()) {
            DataTypes.addDataType(DataTypes.getDataType(name, dc));
        }
        getter.execute(dc, dbHandle, gridName, parameters);
        return nbrTypesAdded;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
