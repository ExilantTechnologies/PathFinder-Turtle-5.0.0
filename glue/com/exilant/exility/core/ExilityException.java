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

public class ExilityException extends Exception
{
    private static final long serialVersionUID = 1L;
    boolean messageToBeAdded;
    
    public ExilityException() {
        this.messageToBeAdded = false;
    }
    
    public ExilityException(final String message) {
        super(message);
        this.messageToBeAdded = false;
        this.messageToBeAdded = true;
        Spit.out(message);
    }
    
    public ExilityException(final Exception e) {
        super(e.getMessage());
        this.messageToBeAdded = false;
        this.messageToBeAdded = true;
        this.setStackTrace(e.getStackTrace());
        Spit.out(e.getMessage());
    }
    
    public ExilityException(final String message, final Exception e) {
        super(String.valueOf(message) + "\n" + e.getMessage());
        this.messageToBeAdded = false;
        this.messageToBeAdded = true;
        this.setStackTrace(e.getStackTrace());
        Spit.out(String.valueOf(message) + "\n" + e.getMessage());
    }
}
