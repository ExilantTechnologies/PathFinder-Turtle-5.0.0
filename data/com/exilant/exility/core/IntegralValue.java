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

public class IntegralValue extends Value
{
    private long value;
    
    IntegralValue(final long integralValue) {
        this.value = integralValue;
    }
    
    IntegralValue(final String str) {
        if (str == null || str.length() == 0) {
            this.value = 0L;
            return;
        }
        try {
            this.value = Long.parseLong(str);
        }
        catch (NumberFormatException e) {
            try {
                this.value = (long)Double.parseDouble(str);
            }
            catch (NumberFormatException e2) {
                Spit.out("ERROR: |" + str + "| is not a valid integer value. 0 is assumed");
            }
        }
    }
    
    IntegralValue(final double decimalValue) {
        this.value = (long)decimalValue;
    }
    
    @Override
    public long getIntegralValue() {
        return this.value;
    }
    
    @Override
    public double getDecimalValue() {
        return this.value;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.INTEGRAL;
    }
    
    protected String format() {
        return this.textValue = Long.toString(this.value);
    }
    
    @Override
    Value add(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value + val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value + val2.getDecimalValue());
        }
        return super.add(val2);
    }
    
    @Override
    Value subtract(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value - val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value - val2.getDecimalValue());
        }
        return super.subtract(val2);
    }
    
    @Override
    Value multiply(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value * val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value * val2.getDecimalValue());
        }
        return super.multiply(val2);
    }
    
    @Override
    Value divide(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value / val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value / val2.getDecimalValue());
        }
        return super.divide(val2);
    }
    
    @Override
    Value remainder(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value % val2.getIntegralValue());
        }
        return super.remainder(val2);
    }
    
    @Override
    Value power(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(Math.pow(this.value, val2.getIntegralValue()));
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(Math.pow(this.value, val2.getDecimalValue()));
        }
        return super.power(val2);
    }
    
    @Override
    Value equal(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value == val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            final long v1 = Math.round(this.value * DecimalValue.PRECISION_MULTIPLIER);
            final long v2 = Math.round(val2.getDecimalValue() * DecimalValue.PRECISION_MULTIPLIER);
            return Value.newValue(v1 == v2);
        }
        return super.equal(val2);
    }
    
    @Override
    Value notEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value != val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            final long v1 = Math.round(this.value * DecimalValue.PRECISION_MULTIPLIER);
            final long v2 = Math.round(val2.getDecimalValue() * DecimalValue.PRECISION_MULTIPLIER);
            return Value.newValue(v1 != v2);
        }
        return super.notEqual(val2);
    }
    
    @Override
    Value greaterThan(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value > val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value > val2.getDecimalValue());
        }
        return super.greaterThan(val2);
    }
    
    @Override
    Value greaterThanOrEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value >= val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value >= val2.getDecimalValue());
        }
        return super.greaterThanOrEqual(val2);
    }
    
    @Override
    Value lessThan(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value < val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value < val2.getDecimalValue());
        }
        return super.lessThan(val2);
    }
    
    @Override
    Value lessThanOrEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(this.value <= val2.getIntegralValue());
        }
        if (val2.getValueType() == DataValueType.DECIMAL) {
            return Value.newValue(this.value <= val2.getDecimalValue());
        }
        return super.lessThanOrEqual(val2);
    }
    
    @Override
    String getQuotedValue() {
        return this.toString();
    }
    
    @Override
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        if (this.isNull()) {
            statement.setNull(idx, 4);
            return;
        }
        statement.setLong(idx, this.value);
    }
}
