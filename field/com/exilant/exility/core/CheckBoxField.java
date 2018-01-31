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

class CheckBoxField extends AbstractInputField
{
    boolean checkedValueIsTheDefault;
    String checkedValue;
    String uncheckedValue;
    boolean isChecked;
    String tableName;
    
    CheckBoxField() {
        this.checkedValueIsTheDefault = false;
        this.checkedValue = null;
        this.uncheckedValue = null;
        this.isChecked = false;
        this.tableName = null;
    }
    
    void setTableName(final String tableName) {
        this.tableName = tableName;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
        if (dt.getValueType() != DataValueType.BOOLEAN && !this.name.endsWith("Delete")) {
            Spit.out("ERROR:" + this.name + " is a check box but its data type is not Boolean. Some features will not work properly.");
        }
        sbf.append("\n<input type=\"checkbox\" ");
        super.addMyAttributes(sbf, pageContext);
        if (this.checkedValue != null) {
            sbf.append(" value = \"").append(this.checkedValue).append("\" ");
        }
        if (this.checkedValueIsTheDefault) {
            sbf.append(" checked = \"checked\" ");
        }
        sbf.append("/>");
        final String lbl = this.getLabelToUse(pageContext);
        final String layoutType = pageContext.getLayoutType();
        if (layoutType != null && layoutType.equals("5") && lbl != null) {
            sbf.append("\n<label for=\"").append(this.name).append("\" ");
            if (this.hoverText != null) {
                sbf.append("title=\"").append(this.hoverText).append("\" ");
            }
            sbf.append(">").append(lbl).append("</label>");
        }
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.checkedValueIsTheDefault) {
            this.isChecked = true;
        }
        BooleanDataType dt = null;
        try {
            dt = (BooleanDataType)DataTypes.getDataType(this.dataType, null);
        }
        catch (Exception e) {
            Spit.out("Field " + this.name + " does not have a valid boolean data type associates with that. Check box WILL NOT be initialized with the rigth default");
        }
        if (this.defaultValue == null) {
            if (this.checkedValueIsTheDefault) {
                if (this.checkedValue != null) {
                    this.defaultValue = this.checkedValue;
                }
                else if (dt != null) {
                    this.defaultValue = dt.trueValue;
                }
            }
            else if (this.uncheckedValue != null) {
                this.defaultValue = this.uncheckedValue;
            }
            else if (dt != null) {
                this.defaultValue = dt.falseValue;
            }
            Spit.out("A default value of " + this.defaultValue + " is set to " + this.name);
        }
    }
}
