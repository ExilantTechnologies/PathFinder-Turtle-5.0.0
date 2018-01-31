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

public class DecimalValueList extends ValueList
{
    double[] list;
    
    protected DecimalValueList() {
    }
    
    protected DecimalValueList(final double[] list) {
        this.list = list;
        this.isNullList = new boolean[list.length];
    }
    
    protected DecimalValueList(final String[] textList) {
        this.textList = textList;
        this.list = new double[textList.length];
        this.isNullList = new boolean[textList.length];
        for (int i = 0; i < textList.length; ++i) {
            final String text = textList[i];
            if (text == null || text.length() == 0) {
                this.isNullList[i] = true;
            }
            else {
                this.list[i] = Double.parseDouble(text);
            }
        }
    }
    
    protected DecimalValueList(final long[] integralList) {
        this.list = new double[integralList.length];
        this.isNullList = new boolean[integralList.length];
        for (int i = 0; i < integralList.length; ++i) {
            this.list[i] = integralList[i];
        }
    }
    
    protected DecimalValueList(final int length) {
        this.list = new double[length];
        this.isNullList = new boolean[length];
    }
    
    @Override
    public DataValueType getValueType() {
        return DataValueType.DECIMAL;
    }
    
    @Override
    public double[] getDecimalList() {
        return this.list;
    }
    
    @Override
    public long[] getIntegralList() {
        final long[] l = new long[this.list.length];
        for (int i = 0; i < this.list.length; ++i) {
            l[i] = (long)this.list[i];
        }
        return l;
    }
    
    @Override
    public double getDecimalValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return this.list[index];
    }
    
    @Override
    public void setDecimalValue(final double decimalValue, final int index) {
        this.list[index] = decimalValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = DecimalValue.decimalFormatter.format(decimalValue);
        }
    }
    
    @Override
    public void setTextValue(final String textValue, final int index) {
        throw new RuntimeException("decimal");
    }
    
    @Override
    public long getIntegralValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return (long)this.list[index];
    }
    
    @Override
    public void setIntegralValue(final long integralValue, final int index) {
        final double decimalValue = integralValue;
        this.list[index] = decimalValue;
        if (this.textList != null) {
            this.textList[index] = DecimalValue.decimalFormatter.format(decimalValue);
        }
        this.isNullList[index] = false;
    }
    
    @Override
    public String[] format() {
        this.textList = new String[this.list.length];
        for (int i = 0; i < this.list.length; ++i) {
            this.textList[i] = (this.isNullList[i] ? "" : DecimalValue.toString(this.list[i]));
        }
        return this.textList;
    }
    
    @Override
    public Value[] getValueArray() {
        final Value[] values = new Value[this.length()];
        for (int i = 0; i < this.list.length; ++i) {
            values[i] = (this.isNullList[i] ? new NullValue(DataValueType.DECIMAL) : Value.newValue(this.list[i]));
        }
        return values;
    }
    
    @Override
    public boolean validateTo(final ValueList toList) {
        if (toList.getValueType() != this.getValueType() || toList.length() != this.length()) {
            return false;
        }
        final double[] toArray = ((DecimalValueList)toList).list;
        for (int i = 0; i < this.textList.length; ++i) {
            if (this.isNullList[i] || toList.isNullList[i] || this.list[i] > toArray[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected void setValueByType(final Value value, final int index) {
        this.list[index] = value.getDecimalValue();
    }
    
    @Override
    public Value getValue(final int index) {
        if (this.isNullList[index]) {
            return new NullValue(DataValueType.DECIMAL);
        }
        return Value.newValue(this.list[index]);
    }
    
    @Override
    public int append(final ValueList listToAppend) {
        if (listToAppend.getValueType() != DataValueType.DECIMAL) {
            Spit.out("list of type " + listToAppend.getValueType() + "can not be appended to a list of type Decimal.");
            return 0;
        }
        int currentLength = this.length();
        final int lengthToAdd = listToAppend.length();
        if (lengthToAdd == 0) {
            return currentLength;
        }
        this.textList = null;
        final DecimalValueList lst = (DecimalValueList)listToAppend;
        final int newLength = currentLength + lengthToAdd;
        final double[] newList = new double[newLength];
        final boolean[] newIsNullList = new boolean[newLength];
        for (int i = 0; i < currentLength; ++i) {
            newList[i] = this.list[i];
            newIsNullList[i] = this.isNullList[i];
        }
        for (int i = 0; i < lengthToAdd; ++i) {
            newList[currentLength] = lst.list[i];
            newIsNullList[currentLength] = lst.isNullList[i];
            ++currentLength;
        }
        return newLength;
    }
    
    @Override
    public DecimalValueList filter(final int newNumberOfRows, final boolean[] selections) {
        final boolean[] ba = new boolean[newNumberOfRows];
        final double[] newList = new double[newNumberOfRows];
        int j = 0;
        for (int i = 0; i < this.isNullList.length; ++i) {
            if (selections[i]) {
                ba[j] = this.isNullList[i];
                newList[j] = this.list[i];
                ++j;
            }
        }
        final DecimalValueList l = new DecimalValueList();
        l.isNullList = ba;
        l.list = newList;
        if (this.textList != null) {
            super.copyFilteredText(l, newNumberOfRows, selections);
        }
        return l;
    }
    
    @Override
    public DecimalValueList clone() {
        final boolean[] ba = new boolean[this.length()];
        final String[] ta = new String[this.length()];
        final double[] a = new double[this.length()];
        for (int i = 0; i < this.isNullList.length; ++i) {
            ba[i] = this.isNullList[i];
            ta[i] = this.textList[i];
            a[i] = this.list[i];
        }
        final DecimalValueList l = new DecimalValueList();
        l.isNullList = ba;
        l.textList = ta;
        l.list = a;
        return l;
    }
}
