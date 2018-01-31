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

class ButtonElement extends AbstractElement
{
    String imageName;
    boolean isDefaultButton;
    WhatToDoOnFormChange whatToDoOnFormChange;
    String iconImage;
    
    ButtonElement() {
        this.imageName = null;
        this.isDefaultButton = false;
        this.whatToDoOnFormChange = WhatToDoOnFormChange.LEAVEMEALONE;
        this.iconImage = null;
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String fieldName = pageContext.getName(this.name);
        if (AP.projectName.equals("Celebrus")) {
            sbf.append("\n");
            sbf.append("<table");
            sbf.append(" id=\"" + fieldName + "\"");
            sbf.append(" class=\"buttonelementtable\" cellpadding=\"0\" cellspacing=\"0\"");
            if (this.onClickActionName != null) {
                String script = "P2.act(this, '" + fieldName + "', '" + this.onClickActionName + "');";
                if (pageContext.isInsideGrid) {
                    script = "P2.rowSelected('" + pageContext.getTableName() + "', this);" + script;
                }
                sbf.append(" onclick=\"").append(script).append("\" ");
            }
            if (this.htmlAttributes != null) {
                sbf.append(' ').append(this.htmlAttributes).append(' ');
            }
            String style = "";
            if (this.onClickActionName != null) {
                style = String.valueOf(style) + "cursor:pointer;";
            }
            if (this.width != null) {
                style = String.valueOf(style) + "width:" + this.width + ';';
            }
            if (this.height != null) {
                style = String.valueOf(style) + "overflow:auto; height:" + this.height + ';';
            }
            if (this.hidden) {
                style = String.valueOf(style) + "display:none;";
            }
            if (style.length() > 0) {
                sbf.append(" style=\"").append(style).append("\"");
            }
            sbf.append(" onmouseover=\"ButtonEffect(this)\" onmousedown=\"ButtonDown(this)\" >");
            sbf.append("\n");
            sbf.append("<tr>\n");
            sbf.append("<td class=\"ButtonLeftCSS\">&nbsp;</td>\n");
            sbf.append("<td class=\"ButtonMiddleCSS\">\n");
            if (this.iconImage != null) {
                sbf.append("<img src=\"../../exilityImages/" + this.iconImage + "\" class=\"ButtonIconCSS\" />\n");
            }
            sbf.append("<font class=\"ButtonFontCSS\">" + this.label + "</font>\n");
            sbf.append("</td>\n");
            sbf.append("<td class=\"ButtonRightCSS\">&nbsp;</td>\n");
            sbf.append("</tr>\n");
            sbf.append("</table>\n");
            return;
        }
        final String layoutType = pageContext.getLayoutType();
        if (layoutType.equals("3") || layoutType.equals("5")) {
            if (this.imageName == null) {
                sbf.append("\n<input type=");
                if (this.isDefaultButton) {
                    sbf.append("\"submit\" ");
                }
                else {
                    sbf.append("\"button\" ");
                }
                if (this.cssClassName == null) {
                    sbf.append(" class=\"button\" ");
                }
                sbf.append("value=\"").append(this.label).append("\" ");
                if (this.name != null) {
                    sbf.append("id=\"").append(fieldName).append("\" ");
                }
                super.addMyAttributes(sbf, pageContext);
                if (this.whatToDoOnFormChange == WhatToDoOnFormChange.ENABLE) {
                    sbf.append("disabled=\"disabled\" ");
                }
                sbf.append(" />");
            }
            else if (!this.imageName.equals("default")) {
                sbf.append("\n<input type=");
                sbf.append("\"image\" ");
                if (this.cssClassName == null) {
                    sbf.append(" class=\"imagebutton\" ");
                }
                sbf.append(" src=\"").append(PageGeneratorContext.imageFolderName).append(this.imageName).append("\" ");
                if (this.name != null) {
                    sbf.append("id=\"").append(fieldName).append("\" ");
                }
                super.addMyAttributes(sbf, pageContext);
                if (this.whatToDoOnFormChange == WhatToDoOnFormChange.ENABLE) {
                    sbf.append("disabled=\"disabled\" ");
                }
                sbf.append(" />");
            }
            else {
                sbf.append("\n<table class=\"buttonelementtable\" cellpadding=\"0\" cellspacing=\"0\" ><tr>");
                sbf.append("<td id=\"").append(String.valueOf(fieldName) + "Left").append("\"");
                if (this.hidden) {
                    sbf.append(" style=\"background-image:url('../../exilityImages/buttonLeft.gif'); background-repeat:no-repeat; width:10px; height:20px; display:none;\">&nbsp;</td>");
                }
                else {
                    sbf.append(" style=\"background-image:url('../../exilityImages/buttonLeft.gif'); background-repeat:no-repeat; width:10px; height:20px; \">&nbsp;</td>");
                }
                sbf.append("<td id=\"").append(String.valueOf(fieldName) + "Middle").append("\"");
                if (this.hidden) {
                    sbf.append(" style=\"background-image:url('../../exilityImages/buttonMiddle.gif'); background-repeat:repeat-x; height:20px; vertical-align: middle; display:none;\">");
                }
                else {
                    sbf.append(" style=\"background-image:url('../../exilityImages/buttonMiddle.gif'); background-repeat:repeat-x; height:20px; vertical-align: middle; \">");
                }
                if (this.iconImage != null) {
                    sbf.append("<img src=\"../../exilityImages/" + this.iconImage + "\"/>");
                }
                sbf.append("<font");
                if (this.cssClassName == null) {
                    sbf.append(" class=\"imagebutton\" ");
                }
                if (this.name != null) {
                    sbf.append(" id=\"").append(fieldName).append("\" ");
                }
                super.addMyAttributes(sbf, pageContext);
                sbf.append(">");
                sbf.append(this.label);
                sbf.append("</font></td>");
                sbf.append("<td id=\"").append(String.valueOf(fieldName) + "Right").append("\"");
                if (this.hidden) {
                    sbf.append("style=\"background-image:url('../../exilityImages/buttonRight.gif'); background-repeat:no-repeat; width:10px; height:20px; display:none;\">&nbsp;</td>");
                }
                else {
                    sbf.append("style=\"background-image:url('../../exilityImages/buttonRight.gif'); background-repeat:no-repeat; width:10px; height:20px; \">&nbsp;</td>");
                }
                sbf.append("</tr></table>\n");
            }
        }
        else {
            sbf.append("\n<input type=");
            if (this.imageName == null) {
                if (this.isDefaultButton) {
                    sbf.append("\"submit\" ");
                }
                else {
                    sbf.append("\"button\" ");
                }
                if (this.cssClassName == null) {
                    sbf.append(" class=\"button\" ");
                }
                sbf.append("value=\"").append(this.label).append("\" ");
            }
            else {
                sbf.append("\"image\" ");
                if (this.cssClassName == null) {
                    sbf.append(" class=\"imagebutton\" ");
                }
                sbf.append(" src=\"").append(PageGeneratorContext.imageFolderName).append(this.imageName).append("\" ");
            }
            if (this.name != null) {
                sbf.append("id=\"").append(fieldName).append("\" ");
            }
            super.addMyAttributes(sbf, pageContext);
            if (this.whatToDoOnFormChange == WhatToDoOnFormChange.ENABLE) {
                sbf.append("disabled=\"disabled\" ");
            }
            sbf.append(" />");
        }
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<input type=");
        if (this.isDefaultButton) {
            sbf.append("\"submit\" ");
        }
        else {
            sbf.append("\"button\" ");
        }
        sbf.append("value=\"").append(this.label).append("\" ");
        if (this.name != null) {
            sbf.append("id=\"").append(pageContext.getName(this.name)).append("\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        if (this.whatToDoOnFormChange == WhatToDoOnFormChange.ENABLE) {
            sbf.append("disabled=\"disabled\" ");
        }
        sbf.append(" />");
    }
    
    public void toJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        if (this.isDefaultButton) {
            js.append("// this is the default button\nP2.defaultButtonName = '" + this.name + "';\n");
        }
    }
}
