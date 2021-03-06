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

@Deprecated
class ValidationStatement extends AbstractLogicStatement
{
    String messageName;
    String[] messageParameters;
    
    ValidationStatement() {
        this.messageName = null;
        this.messageParameters = new String[0];
    }
    
    @Override
    Value execute(final DataCollection dc, final Variables variants) throws ExilityException {
        if (this.messageName != null) {
            if (this.messageParameters.length == 0) {
                dc.addMessage(this.messageName, this.messageParameters);
            }
            else {
                final String[] parms = new String[this.messageParameters.length];
                int i = 0;
                String[] messageParameters;
                for (int length = (messageParameters = this.messageParameters).length, j = 0; j < length; ++j) {
                    final String parm = messageParameters[j];
                    parms[i] = dc.getTextValue(parm, parm);
                    ++i;
                }
                dc.addMessage(this.messageName, parms);
            }
        }
        return null;
    }
}
