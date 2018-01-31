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

import java.sql.SQLException;
import java.sql.CallableStatement;

public abstract class AbstractDataType implements ToBeInitializedInterface
{
    static final String[] ALL_ATTRIBUTES;
    protected static final int MAX_DIGITS = 17;
    public static final String DEFAULT_TYPE = "text";
    String name;
    String messageName;
    String description;
    String formatter;
    String sqlType;
    
    static {
        ALL_ATTRIBUTES = new String[] { "name", "messageName", "description", "formatter", "minLength", "maxLength", "regex", "minValue", "maxValue", "allowNegativeValue", "numberOfDecimals", "maxDaysBeforeToday", "maxDaysAfterToday", "includesTime", "trueValue", "falseValue" };
    }
    
    public AbstractDataType() {
        this.name = null;
        this.messageName = null;
        this.description = null;
        this.formatter = null;
        this.sqlType = null;
    }
    
    Value parseValue(final String fieldName, final String fieldValue, final String[] validValues, final DataCollection dc) {
        if (fieldValue == null || (fieldValue.length() == 0 && this.getValueType() != DataValueType.TEXT)) {
            return new NullValue(this.getValueType());
        }
        if (validValues != null) {
            for (final String fval : validValues) {
                if (fval.equalsIgnoreCase(fieldValue)) {
                    return Value.newValue(fieldValue, this.getValueType());
                }
            }
        }
        else {
            final Value value = Value.newValue(fieldValue, this.getValueType());
            if (value != null && this.isValid(value)) {
                return value;
            }
        }
        final String localFieldName = (fieldName == null) ? "field" : fieldName;
        if (dc != null) {
            dc.addMessage(this.messageName, localFieldName, fieldValue);
        }
        Spit.out(String.valueOf(fieldValue) + " is invalid for " + localFieldName + " that is of datatype" + this.name);
        return null;
    }
    
    ValueList getValueList(final String fieldName, final String[] fieldValues, final boolean isOptional, final DataCollection dc) {
        final ValueList valueList = ValueList.newList(fieldValues, this.getValueType());
        if (valueList != null && this.isValidList(valueList, isOptional)) {
            return valueList;
        }
        final String localFieldName = (fieldName == null) ? "field" : fieldName;
        final StringBuilder sbf = new StringBuilder(fieldValues[0]);
        for (int i = 1; i < fieldValues.length; ++i) {
            sbf.append(',').append(fieldValues[i]);
        }
        if (dc != null) {
            dc.addMessage(this.messageName, localFieldName, sbf.toString());
        }
        Spit.out(String.valueOf(sbf.toString()) + " is invalid for " + localFieldName + " that is of datatype" + this.name);
        return null;
    }
    
    abstract boolean isValid(final Value p0);
    
    abstract boolean isValidList(final ValueList p0, final boolean p1);
    
    abstract DataValueType getValueType();
    
    String format(final Value value) {
        Value val = value;
        final DataValueType t1 = value.getValueType();
        final DataValueType t2 = this.getValueType();
        if (t1 != t2) {
            val = Value.newValue(value.toString(), t2);
        }
        return this.formatValue(val);
    }
    
    String[] format(final ValueList valueList) {
        final DataValueType t1 = valueList.getValueType();
        final DataValueType t2 = this.getValueType();
        if (t1 != t2) {
            Spit.out("Incompatible value type provided for formatting " + t1 + " and " + t2);
            return valueList.format();
        }
        return this.formatValueList(valueList);
    }
    
    protected String formatValue(final Value value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
    
    protected String[] formatValueList(final ValueList valueList) {
        return valueList.format();
    }
    
    abstract int getMaxLength();
    
    abstract void addInputToStoredProcedure(final CallableStatement p0, final int p1, final Value p2) throws SQLException;
    
    abstract void addOutputToStoredProcedure(final CallableStatement p0, final int p1) throws SQLException;
    
    abstract Value extractFromStoredProcedure(final CallableStatement p0, final int p1) throws SQLException;
    
    public String getSqlType() {
        return this.sqlType;
    }
    
    @Override
    public void initialize() {
        if (this.messageName == null || this.messageName.length() == 0) {
            this.messageName = "invalid" + this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        }
    }
    
    public abstract String sqlFormat(final String p0);
}
