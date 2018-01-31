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
import java.sql.Date;
import java.sql.CallableStatement;

class TimeStampDataType extends AbstractDataType
{
    @Override
    DataValueType getValueType() {
        return DataValueType.TIMESTAMP;
    }
    
    @Override
    boolean isValid(final Value value) {
        return true;
    }
    
    @Override
    boolean isValidList(final ValueList valueList, final boolean isOptional) {
        if (isOptional) {
            return true;
        }
        for (int len = valueList.length(), i = 0; i < len; ++i) {
            if (valueList.isNull(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    int getMaxLength() {
        return 20;
    }
    
    @Override
    void addInputToStoredProcedure(final CallableStatement statement, final int idx, final Value value) throws SQLException {
        statement.setDate(idx, new Date(value.getDateValue().getTime()));
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
        return value;
    }
}
