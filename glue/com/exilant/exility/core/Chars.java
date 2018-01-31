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

class Chars
{
    static final char AND = '&';
    static final char OR = '|';
    static final char NOT = '!';
    static final char ADD = '+';
    static final char SUBTRACT = '-';
    static final char MULTIPLY = '*';
    static final char DIVIDE = '/';
    static final char REMAINDER = '%';
    static final char POWER = '^';
    static final char EQUAL = '=';
    static final char GREATER = '>';
    static final char LESS = '<';
    static final char EXISTS = '?';
    static final char SPACE = ' ';
    static final char CARRIAGE_RETURN = '\r';
    static final char TAB = '\t';
    static final char NEW_LINE = '\n';
    static final char BRACKET_OPEN = '(';
    static final char CURLY_OPEN = '{';
    static final char SQUARE_OPEN = '[';
    static final char BRACKET_CLOSE = ')';
    static final char CURLY_CLOSE = '}';
    static final char SQUARE_CLOSE = ']';
    static final char TEXT_QUOTE = '\"';
    static final char TEXT_QUOTE1 = '\u201d';
    static final char[] QUOTES;
    static final char DATE_QUOTE = '\'';
    static final char DIGIT_START = '0';
    static final char DIGIT_END = '9';
    static final char LCHAR_START = 'a';
    static final char LCHAR_END = 'z';
    static final char UCHAR_START = 'A';
    static final char UCHAR_END = 'Z';
    static final char UNDERSCORE = '_';
    static final char DOT = '.';
    
    static {
        QUOTES = new char[] { '\"', '\u201d' };
    }
    
    static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    static boolean isNumeric(final char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }
    
    static boolean isAlphaNumeric(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_';
    }
    
    static boolean isAlpha(final char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '.';
    }
    
    static boolean isOpenBracket(final char c) {
        return c == '(' || c == '[' || c == '{';
    }
    
    static boolean isCloseBracket(final char c) {
        return c == ')' || c == ']' || c == '}';
    }
    
    static boolean isTextQuote(final char c) {
        char[] quotes;
        for (int length = (quotes = Chars.QUOTES).length, i = 0; i < length; ++i) {
            final char ch = quotes[i];
            if (c == ch) {
                return true;
            }
        }
        return false;
    }
    
    public static int getCharIndex(final char[] chars, final int startIdx, final String textToSearchIn) {
        if (textToSearchIn == null || textToSearchIn.length() == 0 || chars.length == 0) {
            return -1;
        }
        for (final char c : chars) {
            final int charIdx = textToSearchIn.indexOf(c, startIdx);
            if (charIdx >= 0) {
                return charIdx;
            }
        }
        return -1;
    }
    
    static boolean isDateQuote(final char c) {
        return c == '\'';
    }
    
    static boolean isOperator(final char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%' || c == '=' || c == '<' || c == '>' || c == '!' || c == '|' || c == '&' || c == '?';
    }
    
    static boolean isWhiteSpace(final char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
    
    static boolean isUnaryOperator(final char c) {
        return c == '!' || c == '-' || c == '?';
    }
}
