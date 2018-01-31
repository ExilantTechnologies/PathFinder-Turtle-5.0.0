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

class DecimalDataType extends AbstractDataType
{
    int numberOfDecimals;
    boolean allowNegativeValue;
    double minValue;
    double maxValue;
    private String formatterString;
    
    DecimalDataType() {
        this.numberOfDecimals = 2;
        this.allowNegativeValue = false;
        this.minValue = Double.MIN_VALUE;
        this.maxValue = Double.MAX_VALUE;
        this.formatterString = "0.00";
    }
    
    @Override
    DataValueType getValueType() {
        return DataValueType.DECIMAL;
    }
    
    @Override
    boolean isValid(final Value value) {
        return this.isValid(value.getDecimalValue());
    }
    
    private boolean isValid(final double number) {
        final boolean result = number >= this.minValue && number <= this.maxValue;
        if (!result) {
            Spit.out("Decimal validation failed for value = " + number + " because max value = " + this.maxValue + " and minValue=" + this.minValue);
        }
        return true;
    }
    
    @Override
    boolean isValidList(final ValueList valueList, final boolean isOptional) {
        try {
            final double[] values = valueList.getDecimalList();
            for (int i = 0; i < values.length; ++i) {
                if (valueList.isNull(i)) {
                    if (!isOptional) {
                        return false;
                    }
                }
                else if (!this.isValid(valueList.getDecimalValue(i))) {
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
    protected String formatValue(final Value value) {
        if (value.getValueType() != DataValueType.DECIMAL) {
            return value.toString();
        }
        return String.format(String.valueOf(value.getDecimalValue()), this.formatterString);
    }
    
    @Override
    protected String[] formatValueList(final ValueList valueList) {
        String[] returnList = null;
        try {
            returnList = new String[valueList.length()];
            for (int i = 0; i < valueList.length(); ++i) {
                if (valueList.isNull(i) || valueList.getValue(i).getTextValue().equals("")) {
                    returnList[i] = valueList.getValue(i).toString();
                }
                else {
                    returnList[i] = String.format(String.valueOf(valueList.getValue(i)), this.formatterString);
                }
            }
        }
        catch (Exception ex) {}
        return returnList;
    }
    
    @Override
    int getMaxLength() {
        if (this.maxValue == Double.MAX_VALUE) {
            return 17;
        }
        int i = 1;
        for (int val = 10; val < this.maxValue && i < 17; val *= 10, ++i) {}
        return i + this.numberOfDecimals + 1;
    }
    
    @Override
    void addInputToStoredProcedure(final CallableStatement statement, final int idx, final Value value) throws SQLException {
        statement.setDouble(idx, value.getDecimalValue());
    }
    
    @Override
    void addOutputToStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        statement.registerOutParameter(idx, 8);
    }
    
    @Override
    public void initialize() {
        if (!this.allowNegativeValue && this.minValue < 0.0) {
            this.minValue = 0.0;
        }
        if (this.numberOfDecimals != 2 && this.numberOfDecimals > 0) {
            this.formatterString = "0.";
            for (int i = this.numberOfDecimals; i > 0; --i) {
                this.formatterString = String.valueOf(this.formatterString) + '0';
            }
        }
        super.initialize();
    }
    
    @Override
    Value extractFromStoredProcedure(final CallableStatement statement, final int idx) throws SQLException {
        return Value.newValue(statement.getDouble(idx));
    }
    
    @Override
    public String sqlFormat(final String value) {
        return value;
    }
}
