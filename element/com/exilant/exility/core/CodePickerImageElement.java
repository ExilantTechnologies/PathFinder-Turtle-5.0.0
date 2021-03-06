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

class CodePickerImageElement extends StaticImageElement
{
    String targetName;
    String codePickerImage;
    
    CodePickerImageElement() {
        this.targetName = null;
        this.codePickerImage = "codePicker.gif";
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String myName = pageContext.getName(this.name);
        sbf.append("<span style=\"vertical-align:bottom;\"><img ");
        super.addMyAttributes(sbf, pageContext);
        if (this.hidden) {
            pageContext.addHiddenFieldsToPage(this.targetName);
        }
        if (pageContext.getLayoutType().equals("5")) {
            sbf.append(" id=\"").append(myName).append("\" class=\"codePickerClass\" style=\"border-style:none;cursor:pointer;");
            sbf.append(" vertical-align: middle;");
        }
        else {
            sbf.append(" id=\"").append(myName).append("\" height=\"18px\" width=\"18px\" style=\"border-style:none;cursor:pointer;");
        }
        sbf.append("\" onmousedown=\"P2.createCodePicker(this, event, '");
        sbf.append(this.targetName).append("');\" onclick=\"P2.startCodePicker();\" src=\"");
        sbf.append(PageGeneratorContext.imageFolderName).append(this.codePickerImage).append("\" /></span>");
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<div class=\"codePicker\" onmousedown=\"P2.createCodePicker(this, event, '");
        sbf.append(this.targetName).append("');\" onclick=\"P2.startCodePicker();\" id=\"");
        sbf.append(pageContext.getName(this.name));
        sbf.append("\" ");
        super.addMyAttributes(sbf, pageContext);
        sbf.append(" />");
    }
}
