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

public class TimeStampValueList extends DateValueList
{
    TimeStampValueList() {
    }
    
    protected TimeStampValueList(final String[] textList) {
        this.textList = textList;
        this.list = new Date[textList.length];
        this.isNullList = new boolean[textList.length];
        for (int i = 0; i < textList.length; ++i) {
            final String text = textList[i];
            if (text == null || text.length() == 0) {
                this.isNullList[i] = true;
            }
            else {
                this.list[i] = DateUtility.parseDateTime(textList[i]);
            }
        }
    }
    
    protected TimeStampValueList(final Date[] list) {
        this.list = list;
        this.isNullList = new boolean[list.length];
    }
    
    protected TimeStampValueList(final int length) {
        this.list = new Date[length];
        this.isNullList = new boolean[length];
    }
    
    @Override
    public DataValueType getValueType() {
        return DataValueType.TIMESTAMP;
    }
    
    @Override
    public void setTimeStampValue(final Date timeStampValue, final int index) {
        this.list[index] = timeStampValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = DateUtility.formatDateTime(timeStampValue);
        }
    }
    
    @Override
    public void setDateValue(final Date dateValue, final int index) {
        this.list[index] = dateValue;
        this.isNullList[index] = false;
        if (this.textList != null) {
            this.textList[index] = DateUtility.formatDateTime(dateValue);
        }
    }
    
    @Override
    public Date getTimeStampValue(final int index) {
        if (this.isNullList[index]) {
            throw new RuntimeException("List has a null value and hence can not return any other type of value");
        }
        return this.list[index];
    }
    
    @Override
    public Date[] getTimeStampList() {
        return this.list;
    }
    
    @Override
    public Date[] getDateList() {
        return this.list;
    }
    
    @Override
    public String[] format() {
        if (this.textList == null) {
            this.textList = new String[this.list.length];
            for (int i = 0; i < this.list.length; ++i) {
                this.textList[i] = (this.isNullList[i] ? "" : DateUtility.formatDateTime(this.list[i]));
            }
        }
        return this.textList;
    }
    
    @Override
    public Value[] getValueArray() {
        final Value[] values = new Value[this.length()];
        for (int i = 0; i < this.list.length; ++i) {
            values[i] = (this.isNullList[i] ? new NullValue(DataValueType.TIMESTAMP) : Value.newTimeStampValue(this.list[i]));
        }
        return values;
    }
    
    @Override
    public boolean validateTo(final ValueList toList) {
        if (toList.getClass() != this.getClass() || toList.length() != this.length()) {
            return false;
        }
        final TimeStampValueList tl = (TimeStampValueList)toList;
        final Date[] toArray = tl.list;
        for (int i = 0; i < this.textList.length; ++i) {
            if (this.isNullList[i] || tl.isNullList[i] || this.list[i].compareTo(toArray[i]) > 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Value getValue(final int index) {
        if (this.isNullList[index]) {
            return new NullValue(DataValueType.TIMESTAMP);
        }
        return new TimeStampValue(this.list[index]);
    }
    
    @Override
    protected void setValueByType(final Value value, final int index) {
        this.list[index] = value.getTimeStampValue();
    }
    
    @Override
    public TimeStampValueList clone() {
        final boolean[] ba = new boolean[this.length()];
        for (int i = 0; i < this.isNullList.length; ++i) {
            ba[i] = this.isNullList[i];
        }
        final String[] ta = new String[this.length()];
        for (int j = 0; j < this.textList.length; ++j) {
            ta[j] = this.textList[j];
        }
        final TimeStampValueList l = new TimeStampValueList();
        l.isNullList = ba;
        l.textList = ta;
        final Date[] a = new Date[this.length()];
        for (int k = 0; k < this.list.length; ++k) {
            a[k] = this.list[k];
        }
        l.list = a;
        return l;
    }
}
