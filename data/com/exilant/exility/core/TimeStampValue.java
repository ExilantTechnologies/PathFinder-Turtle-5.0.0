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
import java.sql.Timestamp;
import java.util.Date;

public class TimeStampValue extends DateValue
{
    private String milliSeconds;
    
    TimeStampValue(final Date dateValue) {
        this.value = dateValue;
    }
    
    TimeStampValue(final String str) {
        this.milliSeconds = str;
        this.value = DateUtility.parseDateTime(str);
    }
    
    TimeStampValue(final Timestamp dateValue) {
        this.value = dateValue;
        if (dateValue == null) {
            Spit.out("ERROR:  null is not a valid TimeStamp. You may get null related issues in your transactions.");
            return;
        }
        this.milliSeconds = dateValue.toString();
    }
    
    @Override
    Date getTimeStampValue() {
        return this.value;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.TIMESTAMP;
    }
    
    @Override
    protected String format() {
        if (this.value == null) {
            this.textValue = "";
        }
        return String.valueOf(this.milliSeconds);
    }
    
    @Override
    String getQuotedValue() {
        return String.valueOf('\'') + this.toString() + '\'';
    }
    
    @Override
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        if (this.isNull()) {
            statement.setNull(idx, 93);
            return;
        }
        statement.setTimestamp(idx, Timestamp.valueOf(this.milliSeconds));
    }
}
