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

public enum Comparator
{
    EXISTS("EXISTS", 0), 
    DOESNOTEXIST("DOESNOTEXIST", 1), 
    EQUALTO("EQUALTO", 2), 
    NOTEQUALTO("NOTEQUALTO", 3), 
    LESSTHAN("LESSTHAN", 4), 
    LESSTHANOREQUALTO("LESSTHANOREQUALTO", 5), 
    GREATERTHAN("GREATERTHAN", 6), 
    GREATERTHANOREQUALTO("GREATERTHANOREQUALTO", 7), 
    CONTAINS("CONTAINS", 8), 
    STARTSWITH("STARTSWITH", 9), 
    FILTER("FILTER", 10), 
    IN("IN", 11), 
    NOTIN("NOTIN", 12), 
    BETWEEN("BETWEEN", 13);
    
    private Comparator(final String s, final int n) {
    }
}
