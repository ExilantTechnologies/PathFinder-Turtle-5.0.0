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

class ButtonPanel extends DisplayPanel
{
    RenderingOption renderingOption;
    
    ButtonPanel() {
        this.renderingOption = RenderingOption.nextToEachOther;
    }
    
    @Override
    void panelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.renderingOption == RenderingOption.nextToEachOther) {
            this.renderNextToEachOther(sbf, pageContext);
        }
        else if (this.renderingOption == RenderingOption.inADropDownList) {
            this.renderDropDown(sbf, pageContext);
        }
        else {
            Spit.out("Button panel " + this.name + " has an invalid rendering option = " + this.renderingOption.toString());
        }
    }
    
    private void renderNextToEachOther(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"buttonpanel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        final String layoutType = pageContext.getLayoutType();
        sbf.append('>');
        if (layoutType.equals("3") || layoutType.equals("5")) {
            sbf.append("<table class=\"buttontable\"><tr>");
        }
        if (this.elements != null) {
            AbstractElement[] elements;
            for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
                final AbstractElement element = elements[i];
                if (element != null) {
                    if (!element.markedAsComment) {
                        if (element instanceof ButtonElement) {
                            if (element.inError) {
                                pageContext.reportError("");
                            }
                            final ButtonElement be = (ButtonElement)element;
                            be.numberOfUnitsToUse = 0;
                            if (this.tableName != null) {
                                be.hidden = true;
                            }
                            if (layoutType.equals("3") || layoutType.equals("5")) {
                                sbf.append("<td>");
                            }
                            be.toHtml(sbf, pageContext);
                            if (layoutType.equals("3") || layoutType.equals("5")) {
                                sbf.append("</td>");
                            }
                        }
                        else {
                            if (layoutType.equals("3") || layoutType.equals("5")) {
                                sbf.append("<td>");
                            }
                            element.toHtml(sbf, pageContext);
                            if (layoutType.equals("3") || layoutType.equals("5")) {
                                sbf.append("</td>");
                            }
                        }
                    }
                }
            }
        }
        if (layoutType.equals("3") || layoutType.equals("5")) {
            sbf.append("</tr></table>");
        }
        sbf.append("</div>");
    }
    
    private void renderDropDown(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<span ");
        if (this.cssClassName == null) {
            sbf.append("class=\"buttondropdown\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        if (this.name == null) {
            final String err = "ERROR : Button panel that is to be rendered as drop down must have a name for it";
            Spit.out(err);
            pageContext.reportError(err);
        }
        sbf.append('>');
        final String selectionName = String.valueOf(this.tableName) + "ButtonList";
        sbf.append("\n<select id=\"").append(selectionName).append("\">");
        sbf.append("\n<option value=\"\" selected=\"selected\"></option>");
        if (this.tableName != null && this.elements != null) {
            AbstractElement[] elements;
            for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
                final AbstractElement element = elements[i];
                if (element != null) {
                    if (!element.markedAsComment) {
                        if (element.inError) {
                            pageContext.reportError("");
                        }
                        sbf.append("\n<option value=\"").append(element.onClickActionName).append("\" >").append(element.label).append("</option>");
                    }
                }
            }
        }
        sbf.append("\n</select>&nbsp;&nbsp;");
        final ButtonElement be = new ButtonElement();
        if (this.buttonLabel != null) {
            be.label = this.buttonLabel;
        }
        else {
            be.label = "GO";
        }
        be.htmlAttributes = " onclick=\"P2.goButtonClicked('" + selectionName + "');\" ";
        be.toHtml(sbf, pageContext);
        sbf.append("\n</span>");
    }
    
    @Override
    void toJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        if (this.tableName == null) {
            return;
        }
        js.append("\n\n/* MetaData for Panel :").append(this.name).append(" with table name = ").append(this.tableName).append("*/");
        js.append('\n').append("ele").append(" = new PM.ButtonPanel();");
        js.append('\n').append("ele").append(".name = '").append(this.tableName).append("';");
        js.append('\n').append("ele").append(".renderingOption = '").append(this.renderingOption.toString()).append("';");
        js.append("\nP2.addTable(").append("ele").append(");");
        AbstractElement[] elements;
        for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
            final AbstractElement ele = elements[i];
            if (ele instanceof ButtonElement) {
                if (!ele.markedAsComment) {
                    if (ele.inError) {
                        pageContext.reportError("");
                    }
                    js.append('\n').append("ele").append(".buttons['").append(ele.name).append("'] = new PM.ButtonField('").append(ele.name).append("','").append(ele.label).append("','").append(ele.onClickActionName).append("');");
                }
            }
        }
    }
    
    @Override
    void panelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        this.panelToHtml(sbf, pageContext);
    }
}
