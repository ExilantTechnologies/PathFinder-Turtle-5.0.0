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

class Parameter implements ToBeInitializedInterface
{
    String name;
    String dataElementName;
    String label;
    boolean isOptional;
    String defaultValue;
    String description;
    private AbstractDataType dataType;
    protected DataValueType dataValueType;
    private DataElement dataElement;
    
    Parameter() {
        this.name = null;
        this.dataElementName = null;
        this.label = null;
        this.isOptional = false;
        this.defaultValue = null;
        this.description = null;
        this.dataType = null;
        this.dataValueType = DataValueType.TEXT;
        this.dataElement = null;
    }
    
    public final AbstractDataType getDataType() {
        return this.dataType;
    }
    
    public final DataValueType getValueType() {
        return this.dataValueType;
    }
    
    public final DataElement getDataElement() {
        return this.dataElement;
    }
    
    @Override
    public void initialize() {
        final String nam = (this.dataElementName != null) ? this.dataElementName : this.name;
        this.dataElement = DataDictionary.getElement(nam);
        if (this.dataElement == null) {
            if (this.dataElementName != null) {
                Spit.out(String.valueOf(this.name) + " is using " + this.dataElementName + " as a data element name, but this is not found in data dictionary. Default text element is assumed.");
            }
            this.dataElement = DataDictionary.getDefaultElement(nam);
        }
        final AbstractDataType dt = DataTypes.getDataType(this.dataElement.dataType, null);
        this.dataValueType = dt.getValueType();
        this.dataType = dt;
    }
    
    public Value getDefaultValue() {
        if (this.defaultValue == null || this.defaultValue.length() == 0) {
            return null;
        }
        return this.dataType.parseValue(this.name, this.defaultValue, null, null);
    }
}
