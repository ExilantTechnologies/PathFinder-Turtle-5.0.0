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
import java.util.Date;

class DateDataType extends AbstractDataType
{
    int maxDaysBeforeToday;
    int maxDaysAfterToday;
    boolean includesTime;
    
    DateDataType() {
        this.maxDaysBeforeToday = Integer.MAX_VALUE;
        this.maxDaysAfterToday = Integer.MAX_VALUE;
        this.includesTime = false;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.DATE;
    }
    
    @Override
    boolean isValid(final Value value) {
        return this.isValid(value.getDateValue());
    }
    
    private boolean isValid(final Date dat) {
        if (this.maxDaysAfterToday == Integer.MAX_VALUE && this.maxDaysBeforeToday == Integer.MAX_VALUE) {
            return true;
        }
        final Date today = DateUtility.getToday();
        int diff = DateUtility.subtractDates(dat, today);
        if (this.maxDaysAfterToday != Integer.MAX_VALUE && diff > this.maxDaysAfterToday) {
            return false;
        }
        if (diff >= 0) {
            return true;
        }
        diff = -diff;
        return this.maxDaysBeforeToday == Integer.MAX_VALUE || diff <= this.maxDaysBeforeToday;
    }
    
    @Override
    boolean isValidList(final ValueList valueList, final boolean isOptional) {
        try {
            final Date[] values = valueList.getDateList();
            for (int i = 0; i < values.length; ++i) {
                if (valueList.isNull(i)) {
                    if (!isOptional) {
                        return false;
                    }
                }
                else if (!this.isValid(valueList.getDateValue(i))) {
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    @Override
    int getMaxLength() {
        if (this.includesTime) {
            return 21;
        }
        return 12;
    }
    
    @Override
    void addInputToStoredProcedure(final CallableStatement statement, final int idx, final Value value) throws SQLException {
        statement.setDate(idx, new java.sql.Date(value.getDateValue().getTime()));
    }
    
    @Override
    void addOutputToStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        statement.registerOutParameter(idx, 91);
    }
    
    @Override
    Value extractFromStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        return Value.newValue(statement.getDate(idx));
    }
    	
    @Override
    public String sqlFormat(final String value) {
        if (this.includesTime) {
            return String.valueOf(AP.dateTimeFormattingPrefix) + value + AP.dateTimeFormattingPostfix;
        }
        return String.valueOf(AP.dateFormattingPrefix) + value + AP.dateFormattingPostfix;
    }
}
