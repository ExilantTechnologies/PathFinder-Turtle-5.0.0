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

public class IntegralValueList extends ValueList
{
    private long[] list;
    
    protected IntegralValueList() {
    }
    
    protected IntegralValueList(final long[] list) {
        this.list = list;
        this.isNullList = new boolean[list.length];
    }
    
    protected IntegralValueList(final String[] textList) {
        this.textList = textList;
        this.list = new long[textList.length];
        this.isNullList = new boolean[textList.length];
        for (int i = 0; i < textList.length; ++i) {
            final String text = textList[i];
            if (text == null || text.length() == 0) {
                this.isNullList[i] = true;
            }
            else {
                try {
                    this.list[i] = Long.parseLong(text);
                }
                catch (Exception e) {
                    if (text.indexOf(46) < 0) {
                        if (text.indexOf(69) <= 0) {
                            continue;
                        }
                    }
                    try {
                        final long lval = (long)Double.parseDouble(text);
                        this.list[i] = lval;
                        this.textList[i] = new StringBuilder().append(lval).toString();
                    }
                    catch (Exception e2) {
                        Spit.out(String.valueOf(text) + " is not an integer.");
                    }
                }
            }
        }
    }
    
    protected IntegralValueList(final double[] decimalList) {
        this.list = new long[decimalList.length];
        this.isNullList = new boolean[this.textList.length];
        for (int i = 0; i < decimalList.length; ++i) {
            this.list[i] = (long)decimalList[i];
        }
    }
    
    protected IntegralValueList(final int length) {
        this.list = new long[length];
        this.isNullList = new boolean[length];
    }
    
    @Override
    public DataValueType getValueType() {
        return DataValueType.INTEGRAL;
    }
    
    @Override
    public void setTextValue(final String textValue, final int index) {
        this.raiseException("integral");
    }
    
    @Override
    public long[] getIntegralList() {
        return this.list;
    }
    
    @Override
    public double[] getDecimalList() {
        final double[] decimalList = new double[this.list.length];
        for (int i = 0; i < this.list.length; ++i) {
            decimalList[i] = this.list[i];
        }
        return decimalList;
    }
    
    @Override
    public void setIntegralValue(final long integralValue, final int index) {
        this.list[index] = integralValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = Long.toString(integralValue);
        }
    }
    
    @Override
    public long getIntegralValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return this.list[index];
    }
    
    @Override
    public void setDecimalValue(final double decimalValue, final int index) {
        final long longValue = (long)decimalValue;
        this.list[index] = longValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = Long.toString(longValue);
        }
    }
    
    @Override
    public double getDecimalValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return this.list[index];
    }
    
    @Override
    public String[] format() {
        if (this.textList == null) {
            this.textList = new String[this.list.length];
            for (int i = 0; i < this.list.length; ++i) {
                this.textList[i] = (this.isNullList[i] ? "" : Long.toString(this.list[i]));
            }
        }
        return this.textList;
    }
    
    @Override
    public Value[] getValueArray() {
        final Value[] values = new Value[this.length()];
        for (int i = 0; i < this.list.length; ++i) {
            values[i] = (this.isNullList[i] ? new NullValue(DataValueType.INTEGRAL) : Value.newValue(this.list[i]));
        }
        return values;
    }
    
    @Override
    public boolean validateTo(final ValueList toList) {
        if (toList.getValueType() != DataValueType.INTEGRAL || toList.length() != this.length()) {
            return false;
        }
        final long[] toArray = ((IntegralValueList)toList).list;
        for (int i = 0; i < this.textList.length; ++i) {
            if (this.isNullList[i] || toList.isNullList[i] || this.list[i] > toArray[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Value getValue(final int index) {
        if (this.isNullList[index]) {
            return new NullValue(DataValueType.INTEGRAL);
        }
        return new IntegralValue(this.list[index]);
    }
    
    @Override
    protected void setValueByType(final Value value, final int index) {
        this.list[index] = value.getIntegralValue();
    }
    
    @Override
    public int append(final ValueList listToAppend) {
        if (listToAppend.getValueType() != DataValueType.INTEGRAL) {
            Spit.out("list of type " + listToAppend.getValueType() + "can not be appended to a list of type INtegral.");
            return 0;
        }
        int currentLength = this.length();
        final int lengthToAdd = listToAppend.length();
        if (lengthToAdd == 0) {
            return currentLength;
        }
        this.textList = null;
        final IntegralValueList lst = (IntegralValueList)listToAppend;
        final int newLength = currentLength + lengthToAdd;
        final long[] newList = new long[newLength];
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
    public IntegralValueList filter(final int newNumberOfRows, final boolean[] selections) {
        final boolean[] ba = new boolean[newNumberOfRows];
        final long[] newList = new long[newNumberOfRows];
        int j = 0;
        for (int i = 0; i < this.isNullList.length; ++i) {
            if (selections[i]) {
                ba[j] = this.isNullList[i];
                newList[j] = this.list[i];
                ++j;
            }
        }
        final IntegralValueList l = new IntegralValueList();
        l.isNullList = ba;
        l.list = newList;
        if (this.textList != null) {
            super.copyFilteredText(l, newNumberOfRows, selections);
        }
        return l;
    }
    
    @Override
    public IntegralValueList clone() {
        final boolean[] ba = new boolean[this.length()];
        final String[] ta = new String[this.length()];
        final long[] a = new long[this.length()];
        for (int i = 0; i < this.isNullList.length; ++i) {
            ba[i] = this.isNullList[i];
            ta[i] = this.textList[i];
            a[i] = this.list[i];
        }
        final IntegralValueList l = new IntegralValueList();
        l.isNullList = ba;
        l.textList = ta;
        l.list = a;
        return l;
    }
}
