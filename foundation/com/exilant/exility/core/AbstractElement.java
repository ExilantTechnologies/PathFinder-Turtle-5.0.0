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

abstract class AbstractElement implements ToBeInitializedInterface
{
    String name;
    String description;
    String label;
    String footerLabel;
    String width;
    String height;
    boolean hidden;
    String align;
    String htmlAttributes;
    String techNotes;
    String businessValidation;
    String hoverText;
    int numberOfUnitsToUse;
    String cssClassName;
    String onClickActionName;
    boolean isSortable;
    boolean isFilterable;
    boolean inError;
    String border;
    boolean markedAsComment;
    protected int widthInPixels;
    
    AbstractElement() {
        this.name = null;
        this.description = null;
        this.label = null;
        this.footerLabel = null;
        this.width = null;
        this.height = null;
        this.hidden = false;
        this.align = null;
        this.htmlAttributes = null;
        this.techNotes = null;
        this.businessValidation = null;
        this.hoverText = null;
        this.numberOfUnitsToUse = 1;
        this.cssClassName = null;
        this.onClickActionName = null;
        this.isSortable = false;
        this.isFilterable = false;
        this.inError = false;
        this.border = null;
        this.markedAsComment = false;
        this.widthInPixels = 0;
    }
    
    abstract void toHtml(final StringBuilder p0, final PageGeneratorContext p1);
    
    abstract void toHtml5(final StringBuilder p0, final PageGeneratorContext p1);
    
    void addMyAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String myName = pageContext.getName(this.name);
        if (this.cssClassName != null) {
            sbf.append("class=\"").append(this.cssClassName).append("\" ");
        }
        if (this.hoverText != null) {
            sbf.append("title=\"").append(this.hoverText).append("\" ");
        }
        if (this.onClickActionName != null) {
            String script = "P2.act(this, '" + myName + "', '" + this.onClickActionName + "', event);";
            if (pageContext.isInsideGrid) {
                script = "P2.rowSelected('" + pageContext.getTableName() + "', this);" + script;
            }
            sbf.append("onclick=\"").append(script).append("\" ");
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
        if (this.border != null) {
            style = String.valueOf(style) + "border-width:" + this.border + "; ";
        }
        if (style.length() > 0) {
            sbf.append("style=\"").append(style).append("\"");
        }
    }
    
    @Override
    public void initialize() {
        if (this.width == null || this.width.equals("")) {
            return;
        }
        final String txt = this.width.replaceAll("[pP][xX]", "");
        try {
            this.widthInPixels = Integer.parseInt(txt);
        }
        catch (Exception e) {
            return;
        }
        if (!AP.pageLayoutType.equals("5")) {
            final char c = this.width.charAt(this.width.length() - 1);
            if (c >= '0' && c <= '9') {
                this.width = String.valueOf(this.width) + "px";
            }
        }
    }
}
