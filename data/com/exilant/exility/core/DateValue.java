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
import java.util.Date;

public class DateValue extends Value
{
    protected Date value;
    
    DateValue() {
    }
    
    DateValue(final Date dateValue) {
        this.value = dateValue;
    }
    
    DateValue(final String str) {
        this.value = DateUtility.parseDate(str);
    }
    
    @Override
    public Date getDateValue() {
        return this.value;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.DATE;
    }
    
    protected String format() {
        if (this.value == null) {
            this.textValue = "";
        }
        return this.textValue = DateUtility.formatDate(this.value);
    }
    
    @Override
    Value add(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(DateUtility.addDays(this.value, val2.getIntegralValue()));
        }
        return super.add(val2);
    }
    
    @Override
    Value subtract(final Value val2) {
        if (val2.getValueType() == DataValueType.INTEGRAL) {
            return Value.newValue(DateUtility.addDays(this.value, -val2.getIntegralValue()));
        }
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(DateUtility.subtractDates(this.value, val2.getDateValue()));
        }
        return super.subtract(val2);
    }
    
    @Override
    Value equal(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(this.value.equals(val2.getDateValue()));
        }
        return super.equal(val2);
    }
    
    @Override
    Value notEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(!this.value.equals(val2.getDateValue()));
        }
        return super.notEqual(val2);
    }
    
    @Override
    Value greaterThan(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(this.value.after(val2.getDateValue()));
        }
        return super.greaterThan(val2);
    }
    
    @Override
    Value greaterThanOrEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(!this.value.before(val2.getDateValue()));
        }
        return super.greaterThanOrEqual(val2);
    }
    
    @Override
    Value lessThan(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(this.value.before(val2.getDateValue()));
        }
        return super.lessThan(val2);
    }
    
    @Override
    Value lessThanOrEqual(final Value val2) {
        if (val2.getValueType() == DataValueType.DATE) {
            return Value.newValue(!this.value.after(val2.getDateValue()));
        }
        return super.lessThanOrEqual(val2);
    }
    
    @Override
    String getQuotedValue() {
        return String.valueOf('\'') + this.toString() + '\'';
    }
    
    @Override
    public boolean isNull() {
        return this.value == null;
    }
    
    @Override
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        if (this.isNull()) {
            statement.setNull(idx, 91);
            return;
        }
        statement.setDate(idx, new java.sql.Date(this.value.getTime()));
    }
}
