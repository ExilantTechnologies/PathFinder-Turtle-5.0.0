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

class StaticImageElement extends AbstractElement
{
    String src;
    
    StaticImageElement() {
        this.src = null;
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<img alt=\"\" ");
        super.addMyAttributes(sbf, pageContext);
        String str = "";
        if (this.height != null) {
            str = String.valueOf(str) + "height:" + this.height + "; ";
        }
        if (this.width != null) {
            str = String.valueOf(str) + "width:" + this.width + "; ";
        }
        if (this.border != null) {
            str = String.valueOf(str) + "border-width:" + this.border + "; ";
        }
        if (str.length() > 0) {
            sbf.append("style=\"").append(str).append("\" ");
        }
        sbf.append(" src=\"").append(this.src).append("\" ");
        if (this.name != null) {
            sbf.append("id=\"").append(this.name).append("\" ");
        }
        sbf.append("/>");
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<div id=\"").append(pageContext.getName(this.name)).append("\" ");
        super.addMyAttributes(sbf, pageContext);
        sbf.append("/>");
    }
}
