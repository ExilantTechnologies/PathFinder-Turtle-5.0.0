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

class SqlParameter extends Parameter
{
    SqlParameterType parameterType;
    boolean toUpperCase;
    boolean doNotFormat;
    boolean justLeaveMeAlone;
    String gridName;
    int index;
    String basedOnColumnName;
    String basedOnColumnValue;
    
    SqlParameter() {
        this.parameterType = SqlParameterType.NORMAL;
        this.toUpperCase = false;
        this.doNotFormat = false;
        this.justLeaveMeAlone = false;
        this.gridName = null;
        this.index = 0;
        this.basedOnColumnName = null;
        this.basedOnColumnValue = null;
    }
    
    String getValue(final DataCollection dc, final String fieldName, final Grid grid, final int idx) {
        final String fieldNameToUse = (fieldName == null) ? this.name : fieldName;
        String val = null;
        switch (this.parameterType) {
            case FILTER: {
                val = SqlUtil.getFilterCondition(dc, fieldNameToUse, this.getValueType());
                break;
            }
            case LIST: {
                val = this.getList(dc, fieldNameToUse, grid);
                break;
            }
            case COMBINED: {
                val = this.getCombined(dc, fieldNameToUse);
                break;
            }
            default: {
                val = this.getNoramlValue(dc, fieldNameToUse, grid, idx);
                break;
            }
        }
        if (val != null && val.length() != 0) {
            if (this.toUpperCase) {
                val = val.toUpperCase();
            }
            return val;
        }
        if (this.defaultValue == null || this.defaultValue.length() == 0) {
            return null;
        }
        return this.format(this.defaultValue);
    }
    
    private String getNoramlValue(final DataCollection dc, final String fieldNameToUse, final Grid grid, final int idx) {
        Grid gridToUse = grid;
        String val = null;
        if (grid == null && this.gridName != null) {
            gridToUse = dc.getGrid(this.gridName);
        }
        if (gridToUse != null) {
            final ValueList list = gridToUse.getColumn(fieldNameToUse);
            if (list != null && list.length() >= idx) {
                val = list.getTextValue(idx);
            }
        }
        else {
            final Value value = dc.getValue(fieldNameToUse);
            if (value != null && !value.isNull()) {
                val = value.getTextValue();
            }
        }
        return this.format(val);
    }
    
    private String format(final String val) {
        if (val == null || this.justLeaveMeAlone) {
            return val;
        }
        final DataValueType vt = this.getValueType();
        if (this.doNotFormat && vt == DataValueType.TEXT) {
            return val.replaceAll("'", "''");
        }
        if (val.length() > 0 || AP.enforceNullPolicyForText) {
            return SqlUtil.formatValue(val, this.getValueType());
        }
        return val;
    }
    
    private String getList(final DataCollection dc, final String fieldNameToUse, final Grid grid) {
        String[] vals = null;
        if (grid != null) {
            vals = grid.getColumnAsTextArray(fieldNameToUse);
        }
        else {
            vals = dc.getTextValueList(this.gridName, fieldNameToUse);
        }
        if (vals == null) {
            return null;
        }
        return SqlUtil.formatList(vals, this.getValueType());
    }
    
    private String getCombined(final DataCollection dc, final String fieldName) {
        String val = dc.getTextValue(fieldName, null);
        if (val == null) {
            return "";
        }
        final int len = val.length();
        if (len == 0) {
            return val;
        }
        final char firstChar = val.charAt(0);
        final String newVal = val.substring(1).trim();
        if (firstChar == '=' || firstChar == '>' || firstChar == '<') {
            return String.valueOf(firstChar) + SqlUtil.formatValue(newVal, this.getValueType()) + ' ';
        }
        final char lastChar = val.charAt(len - 1);
        if (lastChar != '%') {
            val = String.valueOf(val) + '%';
        }
        return " LIKE " + SqlUtil.formatValue(val, this.getValueType()) + ' ';
    }
    
    void putTestValues(final DataCollection dc) {
        if (this.isOptional) {
            return;
        }
        switch (this.parameterType) {
            case NORMAL: {
                dc.addValue(this.name, Value.getTestValue(this.dataValueType));
            }
            case FILTER: {
                dc.addValue(this.name, Value.getTestValue(this.dataValueType));
                dc.addValue(String.valueOf(this.name) + "Operator", Value.newValue(1L));
            }
            case LIST: {
                dc.addValueList(this.name, ValueList.getTestList(this.dataValueType));
            }
            case COMBINED: {
                dc.addValue(this.name, Value.newValue("=a"));
            }
            default: {}
        }
    }
}
