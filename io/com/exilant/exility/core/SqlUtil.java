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

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class SqlUtil
{
    public static final int OPERATOR_ANY = 0;
    public static final int OPERATOR_EQUAL = 1;
    public static final int OPERATOR_STARTS_WITH = 2;
    public static final int OPERATOR_CONTAINS = 3;
    public static final int OPERATOR_GREATER_THAN = 4;
    public static final int OPERATOR_LESS_THAN = 5;
    public static final int OPERATOR_BETWEEN = 6;
    public static final int OPERATOR_IN = 7;
    public static final int OPERATOR_NOT_IN = 8;
    private static final String[] OPERATOR_VERBS;
    
    static {
        OPERATOR_VERBS = new String[] { "", " = ", " LIKE ", " LIKE ", " > ", " < ", " BETWEEN ", " IN ", " NOT IN " };
    }
    
    private static String getOperatorText(final int operator) {
        if (operator < 0 || operator > 0) {
            return "";
        }
        return SqlUtil.OPERATOR_VERBS[operator];
    }
    
    public static String formatValue(final String val, final DataValueType valueType) {
        if (val.length() == 0) {
            if (valueType != DataValueType.TEXT) {
                return null;
            }
            if (AP.useNullForEmptyString) {
                return "NULL";
            }
            return "''";
        }
        else {
            switch (valueType) {
                case TEXT: {
                    return String.valueOf('\'') + val.replace("'", "''") + '\'';
                }
                case DATE: {
                    if (val.equals("systemDate")) {
                        return AP.systemDateFunction;
                    }
                    return String.valueOf(AP.dateFormattingPrefix) + val + AP.dateFormattingPostfix;
                }
                case TIMESTAMP: {
                    if (val.equals("systemDate")) {
                        return AP.systemDateFunction;
                    }
                    return String.valueOf(AP.dateTimeFormattingPrefix) + val + AP.dateTimeFormattingPostfix;
                }
                default: {
                    return val;
                }
            }
        }
    }
    
    public static String formatValue(final Value value) {
        String val = null;
        if (value != null) {
            switch (value.getValueType()) {
                case TEXT: {
                    val = value.getTextValue();
                    if (val != null) {
                        val = String.valueOf('\'') + val.replace("'", "''") + '\'';
                        break;
                    }
                    break;
                }
                case DATE: {
                    final Date dat = value.getDateValue();
                    val = String.valueOf(AP.dateFormattingPrefix) + DateUtility.formatDate(dat) + AP.dateFormattingPostfix;
                    break;
                }
                case TIMESTAMP: {
                    final Date dat = value.getTimeStampValue();
                    val = String.valueOf(AP.dateTimeFormattingPrefix) + DateUtility.formatDate(dat) + AP.dateTimeFormattingPostfix;
                    break;
                }
                default: {
                    return value.getTextValue();
                }
            }
        }
        if (val != null) {
            return val;
        }
        if (AP.useNullForEmptyString) {
            return " NULL ";
        }
        return "''";
    }
    
    public static String formatUser(final DataCollection dc) {
        return String.valueOf('\'') + dc.getValue(AP.loggedInUserFieldName).toString() + '\'';
    }
    
    public static String formatTimeStamp() {
        return AP.systemDateFunction;
    }
    
    public static String formatList(final ValueList values) {
        if (values == null) {
            return "";
        }
        final String[] textValues = values.getTextList();
        return formatList(textValues, values.getValueType());
    }
    
    public static String formatList(final String[] values, final DataValueType valueType) {
        final StringBuilder sbf = new StringBuilder();
        for (String val : values) {
            if (val != null) {
                if (val.length() != 0) {
                    val = formatValue(val, valueType);
                    if (val != null) {
                        sbf.append(val).append(',');
                    }
                }
            }
        }
        if (sbf.length() > 0) {
            sbf.deleteCharAt(sbf.length() - 1);
        }
        return sbf.toString();
    }
    
    public static String getFilterCondition(final DataCollection dc, final String fieldName, final DataValueType valueType) {
        final String operatorFieldName = String.valueOf(fieldName) + "Operator";
        final Value v = dc.getValue(operatorFieldName);
        if (v == null) {
            return "";
        }
        int op;
        if (v.getValueType() == DataValueType.INTEGRAL) {
            op = (int)v.getIntegralValue();
        }
        else {
            op = Integer.parseInt(v.getTextValue());
        }
        if (op == 0) {
            return "";
        }
        String val = dc.getTextValue(fieldName, null);
        if (val == null || val.length() == 0) {
            return "";
        }
        final StringBuilder sbf = new StringBuilder();
        sbf.append(" ").append(getOperatorText(op));
        if (op == 3) {
            val = String.valueOf('%') + val + '%';
        }
        else if (op == 2) {
            val = String.valueOf(val) + '%';
        }
        sbf.append(formatValue(val, valueType));
        if (op == 6) {
            final String val2 = dc.getTextValue(String.valueOf(fieldName) + "To", "");
            sbf.append(" AND ");
            sbf.append(formatValue(val2, valueType));
        }
        sbf.append(' ');
        return sbf.toString();
    }
}
