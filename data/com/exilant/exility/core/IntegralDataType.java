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

class IntegralDataType extends AbstractDataType
{
    boolean allowNegativeValue;
    long minValue;
    long maxValue;
    
    IntegralDataType() {
        this.allowNegativeValue = false;
        this.minValue = Long.MIN_VALUE;
        this.maxValue = Long.MAX_VALUE;
    }
    
    @Override
    boolean isValid(final Value value) {
        return this.isValid(value.getIntegralValue());
    }
    
    @Override
    boolean isValidList(final ValueList valueList, final boolean isOptional) {
        try {
            final long[] values = valueList.getIntegralList();
            for (int i = 0; i < values.length; ++i) {
                if (valueList.isNull(i)) {
                    if (!isOptional) {
                        return false;
                    }
                }
                else if (!this.isValid(valueList.getIntegralValue(i))) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    private boolean isValid(final long value) {
        return value <= this.maxValue && value >= this.minValue;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.INTEGRAL;
    }
    
    @Override
    int getMaxLength() {
        if (this.maxValue == Long.MAX_VALUE) {
            return 17;
        }
        final int l1 = Long.toString(this.maxValue).length();
        final int l2 = Long.toString(this.minValue).length();
        System.out.println(String.valueOf(this.minValue) + " is min with length = " + l2 + " and " + this.maxValue + " is max value with length = " + l1);
        return (l1 > l2) ? l1 : l2;
    }
    
    @Override
    void addInputToStoredProcedure(final CallableStatement statement, final int idx, final Value value) throws SQLException {
        statement.setLong(idx, value.getIntegralValue());
    }
    
    @Override
    void addOutputToStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        statement.registerOutParameter(idx, 4);
    }
    
    @Override
    public void initialize() {
        if (!this.allowNegativeValue) {
            if (this.minValue < 0L) {
                Spit.out(String.valueOf(this.name) + " does not allow negative values, but has fixed a negative value as min value. min value is ignored.");
                this.minValue = 0L;
            }
            if (this.maxValue < 0L) {
                Spit.out(String.valueOf(this.name) + " does not allow negative values, but has fixed a negative value as max value. max value is ignored.");
                this.maxValue = Long.MAX_VALUE;
            }
        }
        super.initialize();
    }
    
    @Override
    Value extractFromStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        return Value.newValue(statement.getInt(idx));
    }
    
    @Override
    public String sqlFormat(final String value) {
        return value;
    }
}
