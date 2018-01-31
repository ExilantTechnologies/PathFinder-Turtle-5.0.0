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

public class DummyLogin implements CustomCodeInterface
{
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        if (AP.globalServerDataNames != null) {
            String[] globalServerDataNames;
            for (int length = (globalServerDataNames = AP.globalServerDataNames).length, i = 0; i < length; ++i) {
                final String name = globalServerDataNames[i];
                dc.addTextValue(name, "420");
            }
        }
        dc.addTextValue(AP.loggedInUserFieldName, "420");
        if (AP.setCookies != null) {
            String[] setCookies;
            for (int length2 = (setCookies = AP.setCookies).length, j = 0; j < length2; ++j) {
                final String name = setCookies[j];
                dc.addTextValue(name, "420");
            }
        }
        dc.addTextValue("folderName", ResourceManager.getResourceFolder());
        return 1;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
