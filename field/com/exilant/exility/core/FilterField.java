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

class FilterField extends AbstractInputField
{
    private static final String STRING_OPERATORS = "1,Equals;2,Starts with;3,Contains";
    private static final String NON_STRING_OPERATORS = "1,Equals;4,Greater than;5,Less than;6,Between";
    int size;
    CamparisonType defaultComparisonType;
    
    FilterField() {
        this.size = 20;
        this.defaultComparisonType = CamparisonType.notSpecified;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final SelectionField df = this.getSelectionField();
        df.fieldToHtml(sbf, pageContext);
        sbf.append("&nbsp;&nbsp;");
        TextInputField inf = this.getFirstField();
        inf.fieldToHtml(sbf, pageContext);
        if (this.valueType != DataValueType.TEXT) {
            sbf.append("&nbsp;&nbsp;");
            sbf.append("\n<span id=\"" + this.name + "ToDiv\" ");
            if (this.defaultComparisonType != CamparisonType.between) {
                sbf.append(" style=\"display:none;\" ");
            }
            sbf.append("> <label>and</label> ");
            inf = this.getSecondField();
            inf.fieldToHtml(sbf, pageContext);
            sbf.append("</span>");
        }
    }
    
    private TextInputField getFirstField() {
        final TextInputField inf = new TextInputField();
        inf.name = this.name;
        inf.labelPosition = LabelPosition.hide;
        inf.dataElementName = this.dataElementName;
        inf.defaultValue = this.defaultValue;
        inf.isRequired = this.isRequired;
        inf.size = this.size;
        inf.formatter = this.formatter;
        inf.onUserChangeActionName = this.onUserChangeActionName;
        inf.initialize();
        inf.setFilterField();
        return inf;
    }
    
    private TextInputField getSecondField() {
        final TextInputField inf = new TextInputField();
        inf.name = String.valueOf(this.name) + "To";
        inf.labelPosition = LabelPosition.hide;
        inf.dataElementName = ((this.dataElementName != null) ? this.dataElementName : this.name);
        inf.isRequired = false;
        inf.size = this.size;
        inf.fromField = this.name;
        inf.hidden = this.hidden;
        inf.formatter = this.formatter;
        inf.onUserChangeActionName = this.onUserChangeActionName;
        inf.initialize();
        return inf;
    }
    
    private SelectionField getSelectionField() {
        final SelectionField sf = new SelectionField();
        sf.name = String.valueOf(this.name) + "Operator";
        sf.label = this.label;
        sf.dataElementName = DataDictionary.DEFAULT_ELEMENT_NAME;
        sf.onUserChangeActionName = this.onUserChangeActionName;
        int ct;
        if (this.defaultComparisonType == CamparisonType.notSpecified) {
            if (this.valueType == DataValueType.TEXT) {
                ct = CamparisonType.contains.ordinal();
            }
            else {
                ct = CamparisonType.equals.ordinal();
            }
        }
        else {
            ct = this.defaultComparisonType.ordinal();
        }
        sf.defaultValue = Integer.toString(ct);
        sf.isRequired = true;
        sf.isFilterOperator = true;
        if (this.valueType == DataValueType.TEXT) {
            sf.valueList = "1,Equals;2,Starts with;3,Contains";
        }
        else {
            sf.valueList = "1,Equals;4,Greater than;5,Less than;6,Between";
        }
        sf.hidden = this.hidden;
        sf.initialize();
        return sf;
    }
    
    @Override
    void toJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        final SelectionField sf = this.getSelectionField();
        sf.toJs(js, pageContext);
        TextInputField inf = this.getFirstField();
        inf.toJs(js, pageContext);
        if (this.valueType != DataValueType.TEXT) {
            inf = this.getSecondField();
            inf.toJs(js, pageContext);
        }
    }
}
