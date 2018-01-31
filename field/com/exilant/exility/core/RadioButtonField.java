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

class RadioButtonField extends AbstractInputField
{
    String valueList;
    String listServiceId;
    String keyValue;
    boolean noAutoLoad;
    
    RadioButtonField() {
        this.valueList = null;
        this.listServiceId = null;
        this.keyValue = null;
        this.noAutoLoad = false;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.listServiceId != null) {
            this.fieldToHtmlRevised(sbf, pageContext);
            return;
        }
        String[] list = null;
        if (this.valueList != null && this.valueList.length() > 0) {
            list = this.valueList.split(";");
        }
        else {
            final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
            if (dt == null || !(dt instanceof BooleanDataType)) {
                final String message = "Invalid value list for radio button " + this.name;
                Spit.out(message);
                return;
            }
            final BooleanDataType bd = (BooleanDataType)dt;
            list = new String[] { "0," + bd.falseValue, "1," + bd.trueValue };
        }
        final int n = list.length;
        if (n < 2) {
            final String message = "ERROR: Radio button field " + this.name + " with label " + this.label + " has been supplied with a value list = " + this.valueList + ". It should have at least two values in it. e.g. 0,Any Color; 1,Red; 2, Blue..";
            Spit.out(message);
            pageContext.reportError(message);
            return;
        }
        final String myName = pageContext.getName(this.name);
        sbf.append("<span class=\"radiogroup\" ");
        super.addMyParentsAttributes(sbf, pageContext);
        sbf.append(">");
        for (int i = 0; i < n; ++i) {
            final String[] entry = list[i].split(",");
            sbf.append("\n<input type=\"radio\" name=\"").append(myName).append("Radio\" id=\"").append(this.name).append('_').append(entry[0]).append("\" ");
            super.addMyAttributesOnly(sbf, pageContext);
            if (this.htmlAttributes != null) {
                sbf.append(' ').append(this.htmlAttributes).append(' ');
            }
            sbf.append(" value=\"").append(entry[0]).append("\" ");
            if (entry[0].equals(this.defaultValue)) {
                sbf.append(" checked=\"checked\" ");
            }
            sbf.append("/><label>");
            if (entry.length > 1) {
                sbf.append(entry[1]);
            }
            else {
                sbf.append(entry[0]);
            }
            sbf.append("</label>");
            sbf.append("&nbsp;&nbsp;");
        }
        sbf.append("</span>");
    }
    
    private void fieldToHtmlRevised(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<div class=\"radiogroup\" ");
        super.addMyParentsAttributes(sbf, pageContext);
        sbf.append(" style=\"visibility:hidden;\" ><div class=\"radio\"><input type=\"radio\" name=\"");
        sbf.append(pageContext.getName(this.name));
        sbf.append("Radio\" ");
        super.addMyAttributesOnly(sbf, pageContext);
        sbf.append(" value=\"---\" /><label>---</label></div></div>");
    }
}
