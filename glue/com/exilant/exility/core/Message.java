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

public class Message
{
    public static final String[] ALL_ATTRIBUTES;
    public static final String EXILITY_ERROR = "exilityError";
    public static final String EXILITY_WARNING = "exilityWarning";
    public static final String EXILITY_INFO = "exilityInfo";
    String name;
    String text;
    boolean forClient;
    MessageSeverity severity;
    
    static {
        ALL_ATTRIBUTES = new String[] { "name", "text", "severity", "forClient" };
    }
    
    public Message() {
        this.forClient = false;
        this.severity = MessageSeverity.UNDEFINED;
    }
    
    Message getFormattedMessage(final String[] args) {
        final Message msg = new Message();
        msg.name = this.name;
        msg.severity = this.severity;
        String str = this.text;
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i] != null) {
                    str = str.replaceAll("@" + (i + 1), args[i]);
                }
            }
        }
        msg.text = str;
        return msg;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public int getNumberOfParameters() {
        for (char i = '1'; i <= '9'; ++i) {
            if (this.text.indexOf("@" + i) < 0) {
                return i - '1';
            }
        }
        return 9;
    }
    
    void markForClient() {
        this.forClient = true;
    }
    
    public String getName() {
        return this.name;
    }
}
