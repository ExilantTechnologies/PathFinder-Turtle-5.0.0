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

import java.util.Date;

public class ValueList
{
    protected static final String MSG = "List has a null value and hence can not return any other type of value";
    protected static final String INDEX_OUT_OF_RANGE = "Value list is accessed with an invalid index of ";
    protected static final String INTEGRAL = "integral";
    protected static final String DECIMAL = "decimal";
    protected static final String DATE = "date";
    protected static final String BOOLEAN = "boolean";
    protected static final String TIMESTAMP = "timestamp";
    public String[] textList;
    boolean[] isNullList;
    
    public static IntegralValueList newList(final long[] integralList) {
        return new IntegralValueList(integralList);
    }
    
    public static DecimalValueList newList(final double[] decimalList) {
        return new DecimalValueList(decimalList);
    }
    
    public static DateValueList newList(final Date[] dateList) {
        return new DateValueList(dateList);
    }
    
    public static TimeStampValueList newTimeStampValueList(final Date[] dateList) {
        return new TimeStampValueList(dateList);
    }
    
    public static BooleanValueList newList(final boolean[] booleanList) {
        return new BooleanValueList(booleanList);
    }
    
    public static ValueList newList(final String[] textList) {
        return new ValueList(textList);
    }
    
    public static ValueList newList(final String[] textList, final DataValueType type) {
        switch (type) {
            case NULL: {
                return null;
            }
            case TEXT: {
                return new ValueList(textList);
            }
            case INTEGRAL: {
                return new IntegralValueList(textList);
            }
            case DATE: {
                return new DateValueList(textList);
            }
            case DECIMAL: {
                return new DecimalValueList(textList);
            }
            case BOOLEAN: {
                return new BooleanValueList(textList);
            }
            case TIMESTAMP: {
                return new TimeStampValueList(textList);
            }
            default: {
                Spit.out("ERROR : DataValueType " + type + " is defined but not implemented fully");
                return null;
            }
        }
    }
    
    public static ValueList newList(final DataValueType type, final int length) {
        switch (type) {
            case NULL: {
                return null;
            }
            case TEXT: {
                return new ValueList(length);
            }
            case INTEGRAL: {
                return new IntegralValueList(length);
            }
            case DATE: {
                return new DateValueList(length);
            }
            case DECIMAL: {
                return new DecimalValueList(length);
            }
            case BOOLEAN: {
                return new BooleanValueList(length);
            }
            case TIMESTAMP: {
                return new TimeStampValueList(length);
            }
            default: {
                Spit.out("ERROR : DataValueType " + type + " is defined but not implemented fully");
                return null;
            }
        }
    }
    
    public ValueList() {
        this.textList = null;
        this.isNullList = null;
    }
    
    public ValueList(final String[] textList) {
        this.textList = null;
        this.isNullList = null;
        this.textList = textList;
        this.isNullList = new boolean[textList.length];
        for (int i = 0; i < textList.length; ++i) {
            final String text = textList[i];
            if (text == null || text.length() == 0) {
                this.isNullList[i] = true;
                this.textList[i] = "";
            }
        }
    }
    
    public ValueList(final int length) {
        this.textList = null;
        this.isNullList = null;
        this.textList = new String[length];
        this.isNullList = new boolean[length];
    }
    
    public DataValueType getValueType() {
        return DataValueType.TEXT;
    }
    
    public final int length() {
        if (this.isNullList == null) {
            return 0;
        }
        return this.isNullList.length;
    }
    
    protected final void raiseException(final String expectedType) {
        throw new RuntimeException("Value list is of type " + this.getValueType() + " while " + expectedType + " value is being tried to set/get.");
    }
    
    public long[] getIntegralList() {
        this.raiseException("integral");
        return null;
    }
    
    public long getIntegralValue(final int index) {
        this.raiseException("integral");
        return 0L;
    }
    
    public void setIntegralValue(final long integralValue, final int index) {
        this.raiseException("integral");
    }
    
    public double[] getDecimalList() {
        this.raiseException("decimal");
        return null;
    }
    
    public double getDecimalValue(final int index) {
        this.raiseException("decimal");
        return 0.0;
    }
    
    public void setDecimalValue(final double decimalValue, final int index) {
        this.raiseException("decimal");
    }
    
    public Date[] getDateList() {
        this.raiseException("date");
        return null;
    }
    
    public Date getDateValue(final int index) {
        this.raiseException("date");
        return new Date();
    }
    
    public void setDateValue(final Date dateValue, final int index) {
        this.raiseException("date");
    }
    
    public boolean[] getBooleanList() {
        this.raiseException("boolean");
        return null;
    }
    
    public boolean getBooleanValue(final int index) {
        this.raiseException("boolean");
        return false;
    }
    
    public void setBooleanValue(final boolean booleanValue, final int index) {
        this.raiseException("boolean");
    }
    
    public Date[] getTimeStampList() {
        this.raiseException("timestamp");
        return null;
    }
    
    public Date getTimeStampValue(final int index) {
        this.raiseException("timestamp");
        return new Date();
    }
    
    public void setTimeStampValue(final Date timeStampValue, final int index) {
        this.raiseException("timestamp");
    }
    
    public Value getValue(final int index) {
        if (this.isNullList[index]) {
            return new NullValue(this.getValueType());
        }
        return Value.newValue(this.textList[index]);
    }
    
    public final void setValue(final Value value, final int index) {
        if (value == null || value.isNull()) {
            this.isNullList[index] = true;
            if (this.textList != null) {
                this.textList[index] = "";
            }
            return;
        }
        final DataValueType dtv = value.getValueType();
        if (dtv != this.getValueType()) {
            this.raiseException(dtv.toString());
        }
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = value.getTextValue();
        }
        this.setValueByType(value, index);
    }
    
    protected void setValueByType(final Value value, final int index) {
    }
    
    public final String[] getTextList() {
        if (this.textList == null) {
            this.format();
        }
        return this.textList;
    }
    
    public final String getTextValue(final int index) {
        if (this.textList == null) {
            this.format();
        }
        return this.textList[index];
    }
    
    public void setTextValue(final String textValue, final int index) {
        if (textValue == null || textValue.length() == 0) {
            this.textList[index] = "";
            this.isNullList[index] = true;
            return;
        }
        this.textList[index] = textValue;
        this.isNullList[index] = false;
    }
    
    public final void setNullValue(final int index) {
        if (this.textList != null) {
            this.textList[index] = "";
        }
        this.isNullList[index] = true;
    }
    
    public boolean validateTo(final ValueList toList) {
        if (toList.getValueType() != this.getValueType() || toList.length() != this.length()) {
            return false;
        }
        final String[] toArray = toList.textList;
        for (int i = 0; i < this.textList.length; ++i) {
            if (this.isNullList[i] || toList.isNullList[i]) {
                return false;
            }
            if (this.textList[i].compareTo(toArray[i]) > 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean validateBasedOn(final ValueList basedOnList) {
        if (basedOnList.length() != this.length()) {
            return false;
        }
        final String[] basedOnArray = basedOnList.getTextList();
        for (int i = 0; i < this.textList.length; ++i) {
            if (basedOnArray[i].length() > 0 && this.isNullList[i]) {
                return false;
            }
        }
        return true;
    }
    
    public String[] format() {
        if (this.textList == null) {
            this.textList = new String[0];
        }
        return this.textList;
    }
    
    public Value[] getValueArray() {
        final Value[] values = new Value[this.length()];
        for (int i = 0; i < this.textList.length; ++i) {
            values[i] = (this.isNullList[i] ? new NullValue(this.getValueType()) : Value.newValue(this.textList[i]));
        }
        return values;
    }
    
    public final boolean isNull(final int index) {
        return this.isNullList[index];
    }
    
    public int append(final ValueList lst) {
        if (lst.getValueType() != DataValueType.TEXT) {
            Spit.out("list of type " + lst.getValueType() + " can not be appended to a list of type TEXT");
            return 0;
        }
        int currentLength = this.length();
        final int lengthToAdd = lst.length();
        if (lengthToAdd == 0) {
            return currentLength;
        }
        final int newLength = currentLength + lengthToAdd;
        final String[] newList = new String[newLength];
        final boolean[] newIsNullList = new boolean[newLength];
        for (int i = 0; i < currentLength; ++i) {
            newList[i] = this.textList[i];
            newIsNullList[i] = this.isNullList[i];
        }
        for (int i = 0; i < lengthToAdd; ++i) {
            newList[currentLength] = lst.textList[i];
            newIsNullList[currentLength] = lst.isNullList[i];
            ++currentLength;
        }
        this.textList = newList;
        this.isNullList = newIsNullList;
        return newLength;
    }
    
    public ValueList filter(final int newNumberOfRows, final boolean[] selections) {
        final boolean[] ba = new boolean[newNumberOfRows];
        final String[] ta = new String[newNumberOfRows];
        int j = 0;
        for (int i = 0; i < this.isNullList.length; ++i) {
            if (selections[i]) {
                ba[j] = this.isNullList[i];
                ta[j] = this.textList[i];
                ++j;
            }
        }
        final ValueList l = new ValueList();
        l.isNullList = ba;
        l.textList = ta;
        return l;
    }
    
    public ValueList clone() {
        final boolean[] ba = new boolean[this.length()];
        for (int i = 0; i < this.isNullList.length; ++i) {
            ba[i] = this.isNullList[i];
        }
        final String[] ta = new String[this.length()];
        for (int j = 0; j < this.textList.length; ++j) {
            ta[j] = this.textList[j];
        }
        final ValueList l = new ValueList();
        l.isNullList = ba;
        l.textList = ta;
        return l;
    }
    
    protected final void copyFilteredText(final ValueList filteredList, final int newNumberOfRows, final boolean[] selections) {
        if (this.textList == null) {
            return;
        }
        filteredList.textList = new String[newNumberOfRows];
        int j = 0;
        for (int i = 0; i < this.textList.length; ++i) {
            if (selections[i]) {
                if (j >= newNumberOfRows) {
                    Spit.out("Design error: copyFilteredText() is called with newNumberOfRows = " + newNumberOfRows + " while there are more values in the filter selection array");
                }
                else {
                    filteredList.textList[j] = this.textList[i];
                    ++j;
                }
            }
        }
    }
    
    static ValueList getTestList(final DataValueType valueType) {
        switch (valueType) {
            case TEXT: {
                final String[] a1 = { "a" };
                return newList(a1);
            }
            case INTEGRAL: {
                final long[] a2 = { 1L };
                return newList(a2);
            }
            case DECIMAL: {
                final double[] a3 = { 1.1 };
                return newList(a3);
            }
            case BOOLEAN: {
                final boolean[] a4 = { false };
                return newList(a4);
            }
            case DATE: {
                final Date[] a5 = { new Date() };
                return newList(a5);
            }
            case TIMESTAMP: {
                final Date[] a6 = { new Date() };
                return newList(a6);
            }
            case NULL: {
                return newList(DataValueType.NULL, 1);
            }
            default: {
                Spit.out("Error : ValueList.getTestList() has not implemented its logic for type " + valueType);
                return null;
            }
        }
    }
    
    public boolean equal(final ValueList otherList, final DataCollection dc, final String listName) {
        if (this.length() != otherList.length()) {
            final String msg = String.valueOf(listName) + ": Found " + this.length() + " value sin list while " + otherList.length() + " values were expected.";
            if (dc != null) {
                dc.addMessage("exilityError", msg);
            }
            else {
                Spit.out(msg);
            }
            return false;
        }
        if (this.getValueType() != otherList.getValueType()) {
            final String msg = String.valueOf(listName) + ": Found " + this.getValueType() + " type while " + otherList.getValueType() + " was expected.";
            if (dc != null) {
                dc.addMessage("exilityError", msg);
            }
            else {
                Spit.out(msg);
            }
            return false;
        }
        boolean matched = true;
        final String[] thisTexts = this.getTextList();
        final String[] otherTexts = otherList.getTextList();
        for (int i = 0; i < thisTexts.length; ++i) {
            if (!thisTexts[i].equals(otherTexts[i])) {
                final String msg2 = String.valueOf(listName) + ": row nbr (1 based) - " + (i + 1) + " Found '" + thisTexts[i] + "' while '" + otherTexts[i] + "' was expected.";
                if (dc != null) {
                    dc.addMessage("exilityError", msg2);
                }
                else {
                    Spit.out(msg2);
                }
                matched = false;
            }
        }
        return matched;
    }
}
