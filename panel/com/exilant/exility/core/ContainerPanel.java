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

class ContainerPanel extends AbstractPanel
{
    boolean useTableLayout;
    
    private void toTableHtml(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean for5) {
        sbf.append("<table class=\"simpleContainer\" ");
        super.addMyAttributes(sbf, pageContext);
        if (this.onClickActionName != null) {
            sbf.append(" onclick=\"P2.act(this, '").append(this.name).append("','").append(this.onClickActionName).append("');\" ");
        }
        sbf.append("><tbody><tr>");
        if (this.elements != null && this.elements.length > 0) {
            for (int i = 0; i < this.elements.length; ++i) {
                final AbstractElement ele = this.elements[i];
                sbf.append("<td id=\"").append(this.name).append("Cell").append(i + 1).append("\">");
                if (for5) {
                    ele.toHtml5(sbf, pageContext);
                }
                else {
                    ele.toHtml(sbf, pageContext);
                }
                sbf.append("</td>");
            }
        }
        sbf.append("\n</tr></tbody></table>");
    }
    
    private void toDivHtml(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean for5) {
        sbf.append("<div class=\"simpleContainer\" ");
        super.addMyAttributes(sbf, pageContext);
        if (this.onClickActionName != null) {
            sbf.append(" onclick=\"P2.act(this, '").append(this.name).append("','").append(this.onClickActionName).append("');\" ");
        }
        sbf.append(">");
        if (this.elements != null && this.elements.length > 0) {
            AbstractElement[] elements;
            for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
                final AbstractElement ele = elements[i];
                if (for5) {
                    ele.toHtml5(sbf, pageContext);
                }
                else {
                    ele.toHtml(sbf, pageContext);
                }
            }
        }
        sbf.append("\n</div>");
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.useTableLayout) {
            this.toTableHtml(sbf, pageContext, false);
        }
        else {
            this.toDivHtml(sbf, pageContext, false);
        }
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.useTableLayout) {
            this.toTableHtml(sbf, pageContext, true);
        }
        else {
            this.toDivHtml(sbf, pageContext, true);
        }
    }
    
    @Override
    void panelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String errorText = String.valueOf(this.name) + " is a contianer panel. There is an internal error because of which panelToHtml() is onvoked. Report this to exility support team.";
        pageContext.reportError(errorText);
        sbf.append(errorText);
        Spit.out(errorText);
    }
    
    @Override
    void panelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String errorText = String.valueOf(this.name) + " is a contianer panel. There is an internal error because of which panelToHtml() is onvoked. Report this to exility support team.";
        pageContext.reportError(errorText);
        sbf.append(errorText);
        Spit.out(errorText);
    }
}
