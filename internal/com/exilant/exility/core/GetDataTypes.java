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

import java.util.Map;

public class GetDataTypes implements CustomCodeInterface
{
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        final String fullName = this.getFileName(dc);
        final DataTypes types = this.readFile(dc, fullName);
        if (types == null) {
            return 0;
        }
        final String[][] data = ObjectManager.getAttributes(types.dataTypes);
        final Grid grid = new Grid(gridName);
        try {
            grid.setRawData(data);
        }
        catch (ExilityException e) {
            dc.addMessage("exilError", "Error while parsing dataTypes : " + e.getMessage());
            return 0;
        }
        grid.resetRawData();
        dc.addGrid(gridName, grid);
        return data.length - 1;
    }
    
    protected DataTypes readFile(final DataCollection dc, final String fullName) {
        final Object obj = ResourceManager.loadResource(fullName, DataTypes.class);
        if (obj != null && obj instanceof DataTypes) {
            return (DataTypes)obj;
        }
        dc.addMessage("exilityError", String.valueOf(fullName) + " could not be read and interpreted as DataTypes");
        return null;
    }
    
    public String getFileName(final DataCollection dc) {
        final String fileName = dc.getTextValue("fileName", "");
        String fullName = "dataTypes";
        if (fileName.length() > 0) {
            fullName = "dataType/" + fileName;
        }
        return fullName;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
