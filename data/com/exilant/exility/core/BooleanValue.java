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
import java.sql.PreparedStatement;

class BooleanValue extends Value
{
    private final boolean value;
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    static final BooleanValue TRUE_VALUE;
    static final BooleanValue FALSE_VALUE;
    
    static {
        TRUE_VALUE = new BooleanValue(true);
        FALSE_VALUE = new BooleanValue(false);
    }
    
    public static String toString(final boolean booleanValue) {
        if (booleanValue) {
            return "true";
        }
        return "false";
    }
    
    public static boolean parse(final String str) {
        if (str.equalsIgnoreCase("true") || str.equals("1")) {
            return true;
        }
        if (str.equalsIgnoreCase("false") || str.equals("0")) {
            return false;
        }
        Spit.out(String.valueOf(str) + " is not a valid boolean value. It has to be either " + "true" + " or " + "false" + ". False value assumed");
        return false;
    }
    
    protected BooleanValue(final boolean booleanValue) {
        this.value = booleanValue;
        if (booleanValue) {
            this.textValue = "true";
        }
        else {
            this.textValue = "false";
        }
    }
    
    protected BooleanValue(final String str) {
        this.value = parse(str);
        if (this.value) {
            this.textValue = "true";
        }
        else {
            this.textValue = "false";
        }
    }
    
    @Override
    public boolean getBooleanValue() {
        return this.value;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.BOOLEAN;
    }
    
    @Override
    Value and(final Value val2) {
        if (val2.getValueType() != DataValueType.BOOLEAN) {
            this.raiseException("logically operated with", val2);
            return BooleanValue.FALSE_VALUE;
        }
        if (!this.getBooleanValue()) {
            return BooleanValue.FALSE_VALUE;
        }
        if (val2.getBooleanValue()) {
            return BooleanValue.TRUE_VALUE;
        }
        return BooleanValue.FALSE_VALUE;
    }
    
    @Override
    Value or(final Value val2) {
        if (val2.getValueType() != DataValueType.BOOLEAN) {
            this.raiseException("logically operated with", val2);
            return BooleanValue.FALSE_VALUE;
        }
        if (this.getBooleanValue()) {
            return BooleanValue.TRUE_VALUE;
        }
        if (val2.getBooleanValue()) {
            return BooleanValue.TRUE_VALUE;
        }
        return BooleanValue.FALSE_VALUE;
    }
    
    @Override
    Value equal(final Value val2) {
        if (val2.getValueType() != DataValueType.BOOLEAN) {
            return super.equal(val2);
        }
        if (this.getBooleanValue()) {
            if (val2.getBooleanValue()) {
                return BooleanValue.TRUE_VALUE;
            }
            return BooleanValue.FALSE_VALUE;
        }
        else {
            if (val2.getBooleanValue()) {
                return BooleanValue.FALSE_VALUE;
            }
            return BooleanValue.TRUE_VALUE;
        }
    }
    
    @Override
    Value notEqual(final Value val2) {
        if (val2.getValueType() != DataValueType.BOOLEAN) {
            return super.notEqual(val2);
        }
        if (this.getBooleanValue()) {
            if (val2.getBooleanValue()) {
                return BooleanValue.FALSE_VALUE;
            }
            return BooleanValue.TRUE_VALUE;
        }
        else {
            if (val2.getBooleanValue()) {
                return BooleanValue.TRUE_VALUE;
            }
            return BooleanValue.FALSE_VALUE;
        }
    }
    
    @Override
    String getQuotedValue() {
        return this.toString();
    }
    
    @Override
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        statement.setBoolean(idx, this.value);
    }
}
