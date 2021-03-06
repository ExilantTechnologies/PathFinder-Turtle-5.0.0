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

public class GetMessages implements ServiceInterface
{
    @Override
    public void execute(final DataCollection dc, final DbHandle dbHandle) throws ExilityException {
        String[][] data = null;
        if (dc.hasValue("fileName")) {
            final String fullName = this.getFileName(dc);
            final Messages msgs = this.readMessageFile(dc, fullName);
            if (msgs != null) {
                data = ObjectManager.getAttributes(msgs.messages);
            }
        }
        else {
            data = Messages.getAllInGrid();
        }
        if (data != null) {
            dc.addGrid("messages", data);
        }
    }
    
    protected Messages readMessageFile(final DataCollection dc, final String fullName) {
        final Object obj = ResourceManager.loadResource(fullName, Messages.class);
        if (obj != null && obj instanceof Messages) {
            return (Messages)obj;
        }
        dc.addMessage("exilityError", String.valueOf(fullName) + " could not be read and interpreted as MessageList");
        return null;
    }
    
    protected String getFileName(final DataCollection dc) {
        final String fileName = dc.getTextValue("fileName", "");
        String fullName = "messages";
        if (fileName.length() > 0) {
            fullName = "message/" + fileName;
        }
        return fullName;
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.NONE;
    }
    
    @Override
    public String getName() {
        return "getMessages";
    }
}
