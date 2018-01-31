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

public class GetFiles implements CustomCodeInterface
{
    static String RESOURCE_TYPE;
    static String FOLDER_NAME;
    static String FILE_NAME;
    
    static {
        GetFiles.RESOURCE_TYPE = "resourceType";
        GetFiles.FOLDER_NAME = "folderName";
        GetFiles.FILE_NAME = "fileName";
    }
    
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        final String resourceTypeName = dc.getTextValue(GetFiles.RESOURCE_TYPE, null);
        String folderName = dc.getTextValue(GetFiles.FOLDER_NAME, "");
        if (folderName.length() == 0) {
            folderName = resourceTypeName;
        }
        else {
            folderName = String.valueOf(resourceTypeName) + '/' + folderName.replace('.', '/');
        }
        final String[] fileNames = ResourceManager.getResourceList(folderName);
        dc.addGrid(gridName, Util.namesToGrid(fileNames, GetFiles.FILE_NAME));
        return fileNames.length;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
