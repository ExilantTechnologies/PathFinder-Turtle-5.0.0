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

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

public abstract class CodeGenUtils
{
    public static final String NEXT_LINE = "\n\t";
    public static final String INDENT1 = "\t";
    public static final String INDENT2 = "\t\t";
    public static final String INDENT3 = "\t\t\t";
    protected static final int MAX_CHARS_PER_LINE = 80;
    
    public static String newLine() {
        return System.getProperty("line.separator");
    }
    
    public static String indent(final int level) {
        final StringBuffer sb = new StringBuffer();
        for (int i = level; i > 0; --i) {
            sb.append('\t');
        }
        return sb.toString();
    }
    
    public static String indentClassAttribute() {
        return indent(1);
    }
    
    public static String indentClassMethod() {
        return indent(1);
    }
    
    public static String indentClassMethodSrc() {
        return indent(2);
    }
    
    public static String indentClassMethodSrcWithinCondition() {
        return indent(3);
    }
    
    public static String indentClassMethodSrcWithinTryCatch() {
        return indent(3);
    }
    
    public static String indentClassMethodSrcWithinConditionWithinTryCatch() {
        return indent(4);
    }
    
    public static String toCamelCase(final String input) {
        if (input == null || input.trim().length() == 0) {
            return input;
        }
        String camel = input.trim();
        if (camel.length() <= 3) {
            return camel.toLowerCase();
        }
        camel = String.valueOf(camel.substring(0, 1).toLowerCase()) + camel.substring(1);
        return camel;
    }
    
    public static String toCamelCase(final String inputString, final Collection<String> dic) {
        if (inputString == null || inputString.trim().length() == 0) {
            return inputString;
        }
        String input = inputString.trim();
        if (input.length() <= 3) {
            return input.toLowerCase();
        }
        input = String.valueOf(input.substring(0, 1).toLowerCase()) + input.substring(1);
        String beginPart = "";
        String matchPart = "";
        String remainingPart = "";
        if (dic != null) {
            for (final String word : dic) {
                final int startIdx = input.trim().toLowerCase().indexOf(word.trim().toLowerCase(), 1);
                if (startIdx < 0) {
                    continue;
                }
                beginPart = input.substring(0, startIdx);
                matchPart = input.substring(startIdx, startIdx + 1).toUpperCase();
                remainingPart = input.substring(startIdx + 1);
                input = String.valueOf(beginPart) + matchPart + remainingPart;
            }
        }
        return input;
    }
    
    public static String toPascalCase(final String name) {
        final String className = name.trim();
        if (className.length() <= 2) {
            return className.toUpperCase();
        }
        return String.valueOf(className.substring(0, 1).toUpperCase()) + className.substring(1);
    }
    
    public static String toPascalCase(final String inputString, final Collection<String> dic) {
        if (inputString == null || inputString.trim().length() == 0) {
            return inputString;
        }
        String input = inputString.trim();
        if (input.length() == 1) {
            return input.toUpperCase();
        }
        input = String.valueOf(input.substring(0, 1).toUpperCase()) + input.substring(1);
        String beginPart = "";
        String matchPart = "";
        String remainingPart = "";
        if (dic != null) {
            for (final String word : dic) {
                final int startIdx = input.trim().toLowerCase().indexOf(word.trim().toLowerCase(), 1);
                if (startIdx < 0) {
                    continue;
                }
                beginPart = input.substring(0, startIdx);
                matchPart = input.substring(startIdx, startIdx + 1).toUpperCase();
                remainingPart = input.substring(startIdx + 1);
                input = String.valueOf(beginPart) + matchPart + remainingPart;
            }
        }
        return input;
    }
    
    public static Collection<String> wrap(final String inputString, final int maxChars) {
        String part = "";
        final ArrayList<String> parts = new ArrayList<String>();
        int idx = 0;
        String input = (inputString == null) ? "" : inputString.trim();
        if (input.length() < maxChars) {
            parts.add(input);
            return parts;
        }
        while (true) {
            while (input.length() >= maxChars) {
                idx = input.indexOf(" ", maxChars);
                if (idx >= 0) {
                    part = input.substring(0, idx + 1);
                    parts.add(part);
                    input = input.substring(idx + 1);
                }
                if (idx < 0) {
                    parts.add(input);
                    return parts;
                }
            }
            continue;
        }
    }
    
    public static String loadCodeTemplate(final String templatePath) throws Exception {
        final InputStream inputStream = CodeGenUtils.class.getResourceAsStream(templatePath);
        final Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        char[] buffer = "".toCharArray();
        final StringBuilder sb = new StringBuilder();
        while (reader.read(buffer) != -1) {
            sb.append(buffer);
            buffer = null;
            buffer = new char[1024];
            for (int i = 0; i < buffer.length; ++i) {
                buffer[i] = ' ';
            }
        }
        inputStream.close();
        return sb.toString();
    }
    
    public static String substituteStub(final String template, final String stubName, final String stubValue) throws ExilityException {
        if (template == null || template.trim().length() == 0) {
            return template;
        }
        if (stubName == null || stubName.trim().length() == 0) {
            return template;
        }
        if (stubValue == null) {
            return template;
        }
        final String stubStartStr = "/*begin_" + stubName + "*/";
        final String stubEndStr = "/*end_" + stubName + "*/";
        int stubStartIdx = template.indexOf(stubStartStr, 0);
        if (stubStartIdx < 0) {
            return template;
        }
        int stubEndIdx = -1;
        String stubPlaceholderStr = "";
        String textToReturn;
        for (textToReturn = template; stubStartIdx >= 0; stubStartIdx = textToReturn.indexOf(stubStartStr, stubEndIdx + stubEndStr.length())) {
            stubEndIdx = textToReturn.indexOf(stubEndStr, stubStartIdx + 1);
            if (stubEndIdx < 0) {
                throw new ExilityException("Cannot find end for the stub named " + stubName);
            }
            stubPlaceholderStr = textToReturn.substring(stubStartIdx, stubEndIdx + stubEndStr.length());
            textToReturn = textToReturn.replace(stubPlaceholderStr, stubValue);
        }
        return textToReturn;
    }
    
    public static String getDataType(final DataValueType valueType) {
        switch (valueType) {
            case BOOLEAN: {
                return "boolean";
            }
            case TEXT: {
                return "String";
            }
            case INTEGRAL: {
                return "long";
            }
            case DECIMAL: {
                return "double";
            }
            case DATE: {
                return "Date";
            }
            case TIMESTAMP: {
                return "Date";
            }
            case NULL: {
                return "String";
            }
            default: {
                return "String";
            }
        }
    }
}
