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

public class GetFolders implements CustomCodeInterface
{
    static final String RESOURCE_TYPE = "resourceType";
    static final String FOLDER_NAME = "folderName";
    private static final String[] HEADER;
    private static final String[] ROOT;
    
    static {
        HEADER = new String[] { "internal", "name" };
        ROOT = new String[] { ".", "root folder" };
    }
    
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        String rootFolderName = dc.getTextValue("resourceType", "page");
        if (rootFolderName.equals("dataDictionary")) {
            rootFolderName = "dictionary";
        }
        final String[] folderNames = ResourceManager.getResourceFolders(rootFolderName);
        final int n = folderNames.length;
        final String[][] grid = new String[n + 2][];
        grid[0] = GetFolders.HEADER;
        grid[1] = GetFolders.ROOT;
        int i = 2;
        String[] array;
        for (int length = (array = folderNames).length, j = 0; j < length; ++j) {
            final String fn = array[j];
            final String[] row = { fn, fn };
            grid[i] = row;
            ++i;
        }
        dc.addGrid(gridName, grid);
        return folderNames.length;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
