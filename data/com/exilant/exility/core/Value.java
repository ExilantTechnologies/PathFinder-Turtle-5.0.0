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

public class Value
{
    static final String NULL_VALUE = "";
    protected String textValue;
    
    public static Value newValue(final long integralValue) {
        return new IntegralValue(integralValue);
    }
    
    public static Value newValue(final boolean booleanValue) {
        if (booleanValue) {
            return BooleanValue.TRUE_VALUE;
        }
        return BooleanValue.FALSE_VALUE;
    }
    
    public static Value newValue(final Date dateValue) {
        return new DateValue(dateValue);
    }
    
    public static Value newTimeStampValue(final Date dateValue) {
        return new TimeStampValue(dateValue);
    }
    
    public static Value newTimeStampValue(final Timestamp dateValue) {
        return new TimeStampValue(dateValue);
    }
    
    public static Value newValue(final double decimalValue) {
        return new DecimalValue(decimalValue);
    }
    
    public static Value newValue(final String textValue) {
        if (textValue == null) {
            return new NullValue(DataValueType.TEXT);
        }
        return new Value(textValue);
    }
    
    public static Value newValue(final String fieldValue, final DataValueType type) {
        if (fieldValue == null || (DataValueType.TEXT != type && fieldValue.length() == 0)) {
            return new NullValue(type);
        }
        try {
            Value value = null;
            switch (type) {
                case NULL: {
                    return new NullValue(DataValueType.TEXT);
                }
                case TEXT: {
                    return new Value(fieldValue);
                }
                case INTEGRAL: {
                    value = new IntegralValue(fieldValue);
                    value.textValue = fieldValue;
                    return value;
                }
                case BOOLEAN: {
                    value = new BooleanValue(fieldValue);
                    value.textValue = fieldValue;
                    return value;
                }
                case DECIMAL: {
                    value = new DecimalValue(fieldValue);
                    value.textValue = fieldValue;
                    return value;
                }
                case DATE: {
                    value = new DateValue(fieldValue);
                    value.textValue = fieldValue;
                    return value;
                }
                case TIMESTAMP: {
                    value = new TimeStampValue(fieldValue);
                    value.textValue = fieldValue;
                    return value;
                }
                default: {
                    Spit.out("Value.newValue() does not handle value type " + type);
                    break;
                }
            }
        }
        catch (Exception e) {
            Spit.out("Error: " + fieldValue + " could not be parsed as a " + type);
            Spit.out(e);
        }
        return null;
    }
    
    protected Value() {
    }
    
    public boolean isNull() {
        return false;
    }
    
    public boolean isSpecified() {
        return !this.isNull() && this.toString().length() != 0;
    }
    
    private Value(final String textValue) {
        this.textValue = textValue;
    }
    
    public boolean getBooleanValue() {
        return BooleanValue.parse(this.textValue);
    }
    
    public Date getDateValue() {
        return DateUtility.parseDate(this.textValue);
    }
    
    Date getTimeStampValue() {
        return DateUtility.parseDateTime(this.textValue);
    }
    
    public long getIntegralValue() {
        try {
            return Long.parseLong(this.textValue);
        }
        catch (Exception e) {
            return 0L;
        }
    }
    
    public double getDecimalValue() {
        return Double.parseDouble(this.textValue);
    }
    
    public String getTextValue() {
        return this.toString();
    }
    
    DataValueType getValueType() {
        return DataValueType.TEXT;
    }
    
    @Override
    public String toString() {
        return this.format();
    }
    
    protected void raiseException(final String oper, final Value val2) {
        throw new RuntimeException("Value of type " + this.getValueType().toString() + " with value " + this.getTextValue() + "  can not be " + oper + " a value of type " + val2.getValueType() + " with value " + val2.getTextValue());
    }
    
    String format() {
        return this.textValue;
    }
    
    Value add(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(String.valueOf(this.toString()) + val2.toString());
        }
        this.raiseException("added to", val2);
        return null;
    }
    
    Value subtract(final Value val2) {
        this.raiseException("subtracted from", val2);
        return null;
    }
    
    Value multiply(final Value val2) {
        this.raiseException("multiplied to", val2);
        return null;
    }
    
    Value divide(final Value val2) {
        this.raiseException("divided by", val2);
        return null;
    }
    
    Value remainder(final Value val2) {
        this.raiseException("divided by", val2);
        return null;
    }
    
    Value power(final Value val2) {
        this.raiseException("raised to power", val2);
        return null;
    }
    
    Value equal(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(this.toString().equalsIgnoreCase(val2.toString()));
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value notEqual(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(!this.toString().equalsIgnoreCase(val2.toString()));
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value equal(final NullValue val2) {
        final Value val3 = val2.getNonNullValue();
        if (val3 == null) {
            this.raiseException("compared to", val2);
            return BooleanValue.FALSE_VALUE;
        }
        return val3.equal(this);
    }
    
    Value notEqual(final NullValue val2) {
        final Value val3 = val2.getNonNullValue();
        if (val3 == null) {
            this.raiseException("compared to", val2);
            return BooleanValue.FALSE_VALUE;
        }
        return val3.notEqual(this);
    }
    
    Value lessThan(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(this.toString().compareToIgnoreCase(val2.toString()) < 0);
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value lessThanOrEqual(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(this.toString().compareToIgnoreCase(val2.toString()) <= 0);
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value greaterThan(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(this.toString().compareToIgnoreCase(val2.toString()) > 0);
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value greaterThanOrEqual(final Value val2) {
        if (this.getValueType() == DataValueType.TEXT || val2.getValueType() == DataValueType.TEXT) {
            return newValue(this.toString().compareToIgnoreCase(val2.toString()) >= 0);
        }
        if (this.getValueType() == DataValueType.NULL || val2.getValueType() == DataValueType.NULL) {
            return newValue(false);
        }
        this.raiseException("compared to", val2);
        return null;
    }
    
    Value and(final Value val2) {
        this.raiseException("logically operated with", val2);
        return null;
    }
    
    Value or(final Value val2) {
        this.raiseException("logically operated with", val2);
        return null;
    }
    
    String getQuotedValue() {
        return String.valueOf('\"') + this.getTextValue().replaceAll("\"", "\"\"") + '\"';
    }
    
    boolean compare(final Comparator comparator, final Value value) {
        if (value == null) {
            return false;
        }
        switch (comparator) {
            case EQUALTO: {
                return this.equal(value).getBooleanValue();
            }
            case NOTEQUALTO: {
                return this.notEqual(value).getBooleanValue();
            }
            case GREATERTHAN: {
                return this.greaterThan(value).getBooleanValue();
            }
            case GREATERTHANOREQUALTO: {
                return this.greaterThanOrEqual(value).getBooleanValue();
            }
            case LESSTHAN: {
                return this.lessThan(value).getBooleanValue();
            }
            case LESSTHANOREQUALTO: {
                return this.lessThanOrEqual(value).getBooleanValue();
            }
            case DOESNOTEXIST: {
                return false;
            }
            case EXISTS: {
                return true;
            }
            case CONTAINS: {
                return this.textValue.contains(value.toString());
            }
            case FILTER: {
                return false;
            }
            case STARTSWITH: {
                return this.textValue.startsWith(value.toString());
            }
            default: {
                Spit.out("Value.compare() has not implemented comparator " + comparator);
                return false;
            }
        }
    }
    
    public void addToPrepearedStatement(final PreparedStatement statement, final int idx) throws SQLException {
        if (this.isNull()) {
            statement.setNull(idx, 12);
            return;
        }
        statement.setString(idx, this.textValue);
    }
    
    public static Value getTestValue(final DataValueType valueType) {
        switch (valueType) {
            case TEXT: {
                return newValue("a");
            }
            case INTEGRAL: {
                return newValue(1L);
            }
            case DECIMAL: {
                return newValue(1.0);
            }
            case BOOLEAN: {
                return newValue(false);
            }
            case DATE: {
                return newValue(new Date());
            }
            case TIMESTAMP: {
                return newTimeStampValue(new Date());
            }
            case NULL: {
                return new NullValue(DataValueType.TEXT);
            }
            default: {
                Spit.out("Value.getTestValue() has not implemented value type " + valueType);
                return null;
            }
        }
    }
    
    public static String getXlType(final DataValueType valueType) {
        switch (valueType) {
            case INTEGRAL:
            case DECIMAL: {
                return "Number";
            }
            case BOOLEAN: {
                return "Boolean";
            }
            case DATE:
            case TIMESTAMP: {
                return "DateTime";
            }
            default: {
                return "String";
            }
        }
    }
    
    public static DataValueType getTypeFromXl(final String type) {
        if (type.equals("String")) {
            return DataValueType.TEXT;
        }
        if (type.equals("Number")) {
            return DataValueType.DECIMAL;
        }
        if (type.equals("DateTime")) {
            return DataValueType.DATE;
        }
        if (type.equals("Boolean")) {
            return DataValueType.BOOLEAN;
        }
        return DataValueType.TEXT;
    }
}
