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

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Spit
{
    private static ThreadLocal<StringBuilder> bufferHolder;
    private static final Logger logger;
    
    static {
        Spit.bufferHolder = new ThreadLocal<StringBuilder>();
        logger = LoggerFactory.getLogger("trace");
    }
    
    public static String startWriter() {
        final StringBuilder sbf = Spit.bufferHolder.get();
        Spit.bufferHolder.set(new StringBuilder());
        return (sbf == null) ? null : sbf.toString();
    }
    
    public static void out(final String textToBeLogged) {
        final String trace = String.valueOf('\n') + textToBeLogged;
        final StringBuilder sbf = Spit.bufferHolder.get();
        if (sbf == null) {
            Spit.logger.info(trace);
        }
        else {
            sbf.append(trace);
        }
    }
    
    public static void out(final Exception e) {
        final StringWriter writer = new StringWriter();
        final PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        final String trace = writer.getBuffer().toString();
        out(trace);
    }
    
    public static String stopWriter() {
        final StringBuilder sbf = Spit.bufferHolder.get();
        Spit.bufferHolder.set(null);
        if (sbf != null) {
            return sbf.toString();
        }
        return "";
    }
    
    public static void writeServiceLog(final String textToBeLogged, final String forUser, final String forService) {
        Spit.logger.info("<serviceLog userId=\"" + forUser + "\" serviceId=\"" + forService + "\"> " + textToBeLogged + "</serviceLog>");
    }
    
    @Deprecated
    public static void out(final Object obj) {
        out(obj.toString());
    }
    
    @Deprecated
    public static void Out(final Object obj) {
        out(obj.toString());
    }
    
    public static void Out(final String text) {
        out(text);
    }
    
    public static void out(final Object caller, final Object obj) {
        out(String.valueOf((caller == null) ? "" : caller.toString()) + obj.toString());
    }
}
