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

public class BooleanValueList extends ValueList
{
    boolean[] list;
    
    protected BooleanValueList() {
    }
    
    protected BooleanValueList(final boolean[] list) {
        this.list = list;
        this.isNullList = new boolean[list.length];
    }
    
    protected BooleanValueList(final String[] textList) {
        this.textList = textList;
        this.list = new boolean[textList.length];
        this.isNullList = new boolean[this.list.length];
        for (int i = 0; i < textList.length; ++i) {
            final String text = textList[i];
            if (text == null || text.length() == 0) {
                this.isNullList[i] = true;
            }
            else {
                this.list[i] = BooleanValue.parse(text);
            }
        }
    }
    
    protected BooleanValueList(final int length) {
        this.list = new boolean[length];
        this.isNullList = new boolean[length];
    }
    
    @Override
    public DataValueType getValueType() {
        return DataValueType.BOOLEAN;
    }
    
    @Override
    public boolean[] getBooleanList() {
        return this.list;
    }
    
    @Override
    public boolean getBooleanValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return this.list[index];
    }
    
    @Override
    public Value getValue(final int index) {
        if (this.isNullList[index]) {
            return new NullValue(DataValueType.BOOLEAN);
        }
        return Value.newValue(this.list[index]);
    }
    
    @Override
    protected void setValueByType(final Value value, final int index) {
        this.list[index] = value.getBooleanValue();
    }
    
    @Override
    public void setTextValue(final String textValue, final int index) {
        super.raiseException("boolean");
    }
    
    @Override
    public void setBooleanValue(final boolean booleanValue, final int index) {
        this.list[index] = booleanValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = BooleanValue.toString(booleanValue);
        }
    }
    
    @Override
    public String[] format() {
        if (this.textList == null) {
            this.textList = new String[this.list.length];
            for (int i = 0; i < this.list.length; ++i) {
                this.textList[i] = (this.isNullList[i] ? "" : BooleanValue.toString(this.list[i]));
            }
        }
        return this.textList;
    }
    
    @Override
    public Value[] getValueArray() {
        final Value[] values = new Value[this.length()];
        for (int i = 0; i < this.list.length; ++i) {
            values[i] = (this.isNullList[i] ? new NullValue(DataValueType.BOOLEAN) : Value.newValue(this.list[i]));
        }
        return values;
    }
    
    @Override
    public boolean validateTo(final ValueList toList) {
        return false;
    }
    
    @Override
    public int append(final ValueList listToAppend) {
        if (listToAppend.getValueType() != DataValueType.BOOLEAN) {
            Spit.out("list of type " + listToAppend.getValueType() + "can not be appended to a list of type Boolean.");
            return 0;
        }
        int currentLength = this.length();
        final int lengthToAdd = listToAppend.length();
        if (lengthToAdd == 0) {
            return currentLength;
        }
        this.textList = null;
        final BooleanValueList lst = (BooleanValueList)listToAppend;
        final int newLength = currentLength + lengthToAdd;
        final boolean[] newList = new boolean[newLength];
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
    public BooleanValueList filter(final int newNumberOfRows, final boolean[] selections) {
        final boolean[] ba = new boolean[newNumberOfRows];
        final boolean[] newList = new boolean[newNumberOfRows];
        int j = 0;
        for (int i = 0; i < this.isNullList.length; ++i) {
            if (selections[i]) {
                ba[j] = this.isNullList[i];
                newList[j] = this.list[i];
                ++j;
            }
        }
        final BooleanValueList l = new BooleanValueList();
        l.isNullList = ba;
        l.list = newList;
        if (this.textList != null) {
            super.copyFilteredText(l, newNumberOfRows, selections);
        }
        return l;
    }
    
    @Override
    public BooleanValueList clone() {
        final boolean[] ba = new boolean[this.length()];
        final String[] ta = new String[this.length()];
        final boolean[] a = new boolean[this.length()];
        for (int i = 0; i < this.isNullList.length; ++i) {
            ba[i] = this.isNullList[i];
            ta[i] = this.textList[i];
            a[i] = this.list[i];
        }
        final BooleanValueList l = new BooleanValueList();
        l.isNullList = ba;
        l.textList = ta;
        l.list = a;
        return l;
    }
}
