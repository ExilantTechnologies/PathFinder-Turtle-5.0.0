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

public class SaveMessages extends GetMessages
{
    @Override
    public void execute(final DataCollection dc, final DbHandle dbHandle) {
        final Grid messagesGrid = dc.getGrid("messages");
        if (messagesGrid == null) {
            dc.addMessage("exilityError", "Data for messages is expected in 'messages' table. It is missing.");
            return;
        }
        final int n = messagesGrid.getNumberOfRows();
        final Message[] messagesInAnArray = new Message[n];
        final String[] actions = new String[n];
        boolean gotError = false;
        Message message = null;
        String action = null;
        for (int i = 0; i < n; ++i) {
            messagesGrid.copyRowToDc(i, null, dc);
            message = new Message();
            ObjectManager.fromDc(message, dc);
            messagesInAnArray[i] = message;
            final String[] array = actions;
            final int n2 = i;
            final String textValue = dc.getTextValue("bulkAction", null);
            array[n2] = textValue;
            action = textValue;
            final Message existingMessage = Messages.getMessage(message.name);
            if (action.equals("add")) {
                if (existingMessage != null) {
                    dc.addMessage("error", String.valueOf(message.name) + " (row " + (i + 1) + ") already exists with severity " + message.severity + " and text \"" + message.text + "\".");
                    gotError = true;
                }
            }
            else if (existingMessage == null) {
                dc.addMessage("error", String.valueOf(message.name) + " (row " + (i + 1) + ") not found.");
                gotError = true;
            }
        }
        if (gotError) {
            return;
        }
        final Messages messages = new Messages();
        final Map<String, Message> msgs = messages.messages;
        int nbrSaved = 0;
        for (int j = 0; j < n; ++j) {
            action = actions[j];
            message = messagesInAnArray[j];
            if (!action.equals("add")) {
                Messages.removeMessage(message);
            }
            if (!action.equals("delete")) {
                Messages.setMessage(message);
                msgs.put(message.name, message);
                ++nbrSaved;
            }
        }
        final String fullName = this.getFileName(dc);
        ResourceManager.saveResource(fullName, messages);
        dc.addMessage("info", String.valueOf(nbrSaved) + " messages saved to resource " + fullName);
    }
    
    @Override
    public String getName() {
        return "saveMessage";
    }
}
