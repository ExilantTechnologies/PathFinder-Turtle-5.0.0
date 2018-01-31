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

class Column extends Parameter
{
    static final String[] ALL_ATTRIBUTES;
    String dataSource;
    String columnName;
    boolean isKeyColumn;
    boolean isClobColumn;
    String listServiceName;
    String[] enumuration;
    String enumType;
    String basedOnColumnName;
    String basedOnColumnValue;
    String otherColumnName;
    boolean isNullable;
    String keyFromTable;
    
    static {
        ALL_ATTRIBUTES = new String[] { "name", "columnName", "dataElementName", "label", "description", "isOptional", "defaultValue", "defaultValue", "isKeyColumn", "listServiceName", "enumeration", "enumerationType", "dependantOnColumnName" };
    }
    
    Column() {
        this.dataSource = null;
        this.columnName = null;
        this.isKeyColumn = false;
        this.isClobColumn = false;
        this.listServiceName = null;
        this.enumuration = null;
        this.enumType = null;
        this.basedOnColumnName = null;
        this.basedOnColumnValue = null;
        this.otherColumnName = null;
        this.isNullable = false;
        this.keyFromTable = null;
    }
    
    void validate(final Value value, final DataCollection dc) throws ExilityException {
        if (value == null || value.isNull() || value.getTextValue().length() == 0) {
            if (this.defaultValue == null && !this.isOptional) {
                dc.raiseException("exilValueIsRequired", this.name);
            }
            if (this.basedOnColumnName != null) {
                final Value otherValue = dc.getValue(this.basedOnColumnName);
                if (otherValue != null && !otherValue.isNull()) {
                    if (this.basedOnColumnValue == null) {
                        dc.raiseException("exilValueIsRequired", this.name);
                    }
                    else if (otherValue.getTextValue().equals(this.basedOnColumnValue)) {
                        dc.raiseException("exilValueIsRequired", this.name);
                    }
                }
            }
            if (this.otherColumnName != null) {
                final Value otherValue = dc.getValue(this.otherColumnName);
                if (otherValue == null || otherValue.isNull() || otherValue.getTextValue().length() == 0) {
                    dc.raiseException("exilValueIsRequired", this.name);
                }
            }
        }
    }
}
