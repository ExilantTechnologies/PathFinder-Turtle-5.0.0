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

class PasswordField extends AbstractInputField
{
    int size;
    
    PasswordField() {
        this.size = 20;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<input type=\"password\" ");
        if (this.cssClassName == null) {
            sbf.append("class=\"inputfield\" ");
        }
        if (this.size > 0) {
            sbf.append(" size=\"").append(this.size).append("\" ");
        }
        if (this.defaultValue != null) {
            sbf.append(" value=\"").append(this.defaultValue).append("\" ");
        }
        final int len = DataTypes.getDataType(this.dataType, null).getMaxLength();
        if (len > 0) {
            sbf.append(" maxlength=\"").append(len).append("\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        sbf.append("/>");
    }
}
