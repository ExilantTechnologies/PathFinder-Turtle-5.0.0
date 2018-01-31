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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class Messages
{
    private static final String[] HEADER_ROW;
    private static Messages instance;
    Map<String, Message> messages;
    
    static {
        HEADER_ROW = new String[] { "name", "text", "severity" };
        Messages.instance = new Messages();
    }
    
    static Messages getInstance() {
        return Messages.instance;
    }
    
    public Messages() {
        this.messages = new HashMap<String, Message>();
    }
    
    public static Message getMessage(final String messageName) {
        return Messages.instance.messages.get(messageName);
    }
    
    public Map<String, Message> getMessages() {
        return this.messages;
    }
    
    public static void setMessage(final Message message) {
        Messages.instance.messages.remove(message.name);
        Messages.instance.messages.put(message.name, message);
    }
    
    public static void removeMessage(final Message message) {
        Messages.instance.messages.remove(message.name);
    }
    
    public static Message getMessage(final String code, final String[] parameters) {
        Message messageToReturn = null;
        if (Messages.instance.messages.containsKey(code)) {
            return Messages.instance.messages.get(code).getFormattedMessage(parameters);
        }
        messageToReturn = new Message();
        messageToReturn.name = code;
        messageToReturn.severity = MessageSeverity.ERROR;
        String txt = String.valueOf(code) + ". Description of this message is not available.";
        if (parameters != null) {
            txt = String.valueOf(txt) + " This message was called with additional data:";
            for (int i = 0; i < parameters.length; ++i) {
                txt = String.valueOf(txt) + (i + 1) + "." + parameters[i] + '\t';
            }
            messageToReturn.text = txt;
        }
        return messageToReturn;
    }
    
    static void reload(final boolean removeExistingMessages) {
        if (removeExistingMessages || Messages.instance == null) {
            Messages.instance = new Messages();
        }
        load();
    }
    
    static synchronized void load() {
        try {
            final Map<String, Object> msgs = ResourceManager.loadFromFileOrFolder("messages", "message");
            for (final String fileName : msgs.keySet()) {
                final Object obj = msgs.get(fileName);
                if (!(obj instanceof Messages)) {
                    Spit.out("message folder contains an xml that is not messages. File ignored.");
                }
                else {
                    final Messages msg = (Messages)obj;
                    Messages.instance.copyFrom(msg);
                }
            }
            Spit.out(String.valueOf(Messages.instance.messages.size()) + " messages loaded.");
        }
        catch (Exception e) {
            Spit.out("Unable to load messages. Error : " + e.getMessage());
            Spit.out(e);
        }
    }
    
    private void copyFrom(final Messages msgs) {
        for (final Message m : msgs.messages.values()) {
            if (this.messages.containsKey(m.name)) {
                Spit.out("Error : message " + m.name + " is defined more than once");
            }
            else {
                this.messages.put(m.name, m);
            }
        }
    }
    
    static String getMessageText(final String code) {
        final Message message = Messages.instance.messages.get(code);
        if (message == null) {
            return String.valueOf(code) + " is not defined.";
        }
        return message.text;
    }
    
    static MessageSeverity getSeverity(final String code) {
        final Message message = Messages.instance.messages.get(code);
        if (message == null) {
            return MessageSeverity.UNDEFINED;
        }
        return message.severity;
    }
    
    public static String[][] getAllInGrid() {
        final String[] names = Messages.instance.messages.keySet().toArray(new String[0]);
        Arrays.sort(names);
        final String[][] rows = new String[names.length + 1][];
        rows[0] = Messages.HEADER_ROW;
        int i = 1;
        String[] array;
        for (int length = (array = names).length, j = 0; j < length; ++j) {
            final String aname = array[j];
            final String[] arow = { aname, null, null };
            final Message msg = Messages.instance.messages.get(aname);
            arow[1] = msg.text;
            arow[2] = msg.severity.toString();
            rows[i] = arow;
            ++i;
        }
        return rows;
    }
    
    static String[][] getMatchingMessages(final String startngName) {
        final String stringToMatch = startngName.toLowerCase();
        final String[] names = Messages.instance.messages.keySet().toArray(new String[0]);
        int nbr = 0;
        for (int i = 0; i < names.length; ++i) {
            if (names[i].toLowerCase().startsWith(stringToMatch)) {
                names[nbr] = names[i];
                ++nbr;
            }
        }
        final String[] filteredNames = new String[nbr];
        for (int j = 0; j < filteredNames.length; ++j) {
            filteredNames[j] = names[j];
        }
        Arrays.sort(filteredNames);
        final String[][] rows = new String[filteredNames.length + 1][];
        rows[0] = Messages.HEADER_ROW;
        int k = 1;
        String[] array;
        for (int length = (array = filteredNames).length, l = 0; l < length; ++l) {
            final String aname = array[l];
            final String[] arow = { aname, null, null };
            final Message msg = Messages.instance.messages.get(aname);
            arow[1] = msg.text;
            arow[2] = msg.severity.toString();
            rows[k] = arow;
            ++k;
        }
        return rows;
    }
    
    static Collection<Message> getClientMessages() {
        final Collection<Message> msgs = new ArrayList<Message>();
        for (final Message msg : Messages.instance.messages.values()) {
            if (msg.forClient) {
                msgs.add(msg);
            }
        }
        return msgs;
    }
}
