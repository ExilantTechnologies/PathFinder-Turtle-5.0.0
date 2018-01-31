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
import java.util.regex.Pattern;

class TextDataType extends AbstractDataType
{
    int minLength;
    int maxLength;
    Pattern regex;
    
    TextDataType() {
        this.minLength = 0;
        this.maxLength = Integer.MAX_VALUE;
        this.regex = null;
        this.name = "text";
    }
    
    @Override
    boolean isValid(final Value value) {
        return this.isValid(value.getTextValue());
    }
    
    private boolean isValid(final String val) {
        final int len = val.length();
        return len >= this.minLength && len <= this.maxLength && (this.regex == null || this.regex.matcher(val).matches());
    }
    
    @Override
    boolean isValidList(final ValueList valueList, final boolean isOptional) {
        final String[] values = valueList.getTextList();
        for (int i = 0; i < values.length; ++i) {
            if (valueList.isNull(i)) {
                if (!isOptional) {
                    return false;
                }
            }
            else if (!this.isValid(valueList.getTextValue(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    int getMaxLength() {
        return this.maxLength;
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.TEXT;
    }
    
    @Override
    void addInputToStoredProcedure(final CallableStatement statement, final int idx, final Value value) throws SQLException {
        statement.setString(idx, value.getTextValue());
    }
    
    @Override
    void addOutputToStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        statement.registerOutParameter(idx, 12);
    }
    
    @Override
    Value extractFromStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        return Value.newValue(statement.getString(idx));
    }
    
    @Override
    public String sqlFormat(final String value) {
        return String.valueOf('\'') + value.replace("'", "''") + '\'';
    }
}
