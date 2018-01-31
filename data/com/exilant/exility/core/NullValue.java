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

public class NullValue extends Value
{
    private static final String MSG = "No operations allowed when value is NULL";
    private final DataValueType valueType;
    
    protected NullValue(final DataValueType valueType) {
        this.valueType = valueType;
        this.textValue = "";
    }
    
    @Override
    public boolean isNull() {
        return true;
    }
    
    @Override
    DataValueType getValueType() {
        return this.valueType;
    }
    
    @Override
    Value add(final Value val2) {
        throw new RuntimeException("No operations allowed when value is NULL");
    }
    
    @Override
    String getQuotedValue() {
        return "null";
    }
    
    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        switch (this.valueType) {
            case BOOLEAN: {
                statement.setNull(idx, 16);
                break;
            }
            case DATE: {
                statement.setNull(idx, 91);
                break;
            }
            case DECIMAL: {
                statement.setNull(idx, 8);
                break;
            }
            case INTEGRAL: {
                statement.setNull(idx, 4);
                break;
            }
            case TEXT: {
                statement.setNull(idx, 12);
                break;
            }
            case TIMESTAMP: {
                statement.setNull(idx, 91);
                break;
            }
            default: {
                statement.setNull(idx, 12);
                break;
            }
        }
    }
    
    @Override
    Value equal(final Value val2) {
        final Value val3 = this.getNonNullValue();
        if (val3 == null) {
            return BooleanValue.FALSE_VALUE;
        }
        return val3.equal(val2);
    }
    
    @Override
    Value notEqual(final Value val2) {
        final Value val3 = this.getNonNullValue();
        if (val3 == null) {
            return BooleanValue.FALSE_VALUE;
        }
        return val3.notEqual(val2);
    }
    
    Value getNonNullValue() {
        switch (this.valueType) {
            case INTEGRAL: {
                return Value.newValue(0L);
            }
            case DECIMAL: {
                return Value.newValue(0.0);
            }
            case TEXT: {
                return Value.newValue("");
            }
            case BOOLEAN: {
                return BooleanValue.FALSE_VALUE;
            }
            default: {
                return null;
            }
        }
    }
}
