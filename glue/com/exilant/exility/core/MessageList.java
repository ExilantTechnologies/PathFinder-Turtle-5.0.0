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

import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

public class MessageList
{
    private final ArrayList<Message> messages;
    private MessageSeverity highestSeverity;
    private final Set<String> messageIds;
    
    public MessageList() {
        this.highestSeverity = MessageSeverity.IGNORE;
        this.messages = new ArrayList<Message>();
        this.messageIds = new HashSet<String>();
    }
    
    public MessageSeverity getSevirity() {
        return this.highestSeverity;
    }
    
    MessageSeverity addMessage(final String messageName, final String[] parameters) {
        final Message msg = Messages.getMessage(messageName, parameters);
        Spit.out(msg.text);
        if (msg.severity != MessageSeverity.IGNORE) {
            this.messages.add(msg);
            this.messageIds.add(messageName);
            if (msg.severity.compareTo(this.highestSeverity) > 0) {
                this.highestSeverity = msg.severity;
            }
        }
        return msg.severity;
    }
    
    public boolean hasError() {
        return this.highestSeverity == MessageSeverity.ERROR;
    }
    
    public boolean hasMessage(final String messageId) {
        return this.messageIds.contains(messageId);
    }
    
    Message get(final int idx) {
        try {
            return this.messages.get(idx);
        }
        catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    MessageSeverity getLastSeverity() {
        final int n = this.messages.size();
        if (n == 0) {
            return MessageSeverity.IGNORE;
        }
        return this.messages.get(n - 1).severity;
    }
    
    public String[] getMessageTexts() {
        final String[] texts = new String[this.messages.size()];
        int i = 0;
        for (final Message msg : this.messages) {
            texts[i] = msg.text;
            ++i;
        }
        return texts;
    }
    
    public String[] getWarningTexts() {
        final List<String> texts = new ArrayList<String>();
        for (final Message msg : this.messages) {
            if (msg.severity == MessageSeverity.WARNING || msg.severity == MessageSeverity.UNDEFINED) {
                texts.add(msg.text);
            }
        }
        return texts.toArray(new String[0]);
    }
    
    public String[] getInfoTexts() {
        final List<String> texts = new ArrayList<String>();
        for (final Message msg : this.messages) {
            if (msg.severity == MessageSeverity.INFO) {
                texts.add(msg.text);
            }
        }
        return texts.toArray(new String[0]);
    }
    
    public String[] getErrorTexts() {
        final List<String> texts = new ArrayList<String>();
        for (final Message msg : this.messages) {
            if (msg.severity == MessageSeverity.ERROR) {
                texts.add(msg.text);
            }
        }
        return texts.toArray(new String[0]);
    }
    
    public int size() {
        return this.messages.size();
    }
    
    public String[][] toGrid() {
        final int n = this.messages.size() + 1;
        final String[][] grid = new String[n][];
        final String[] hdr = { "severity", "text" };
        grid[0] = hdr;
        for (int i = 1; i < n; ++i) {
            final Message msg = this.messages.get(i - 1);
            final String[] row = { msg.severity.toString(), msg.text };
            grid[i] = row;
        }
        return grid;
    }
}
