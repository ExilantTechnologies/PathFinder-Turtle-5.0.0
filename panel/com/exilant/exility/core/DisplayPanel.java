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

class DisplayPanel extends AbstractPanel
{
    String linkedTableName;
    String[] columnWidths;
    boolean rowsCanBeDeleted;
    String labelForBulkDeleteCheckBox;
    
    DisplayPanel() {
        this.linkedTableName = null;
        this.columnWidths = null;
        this.rowsCanBeDeleted = false;
        this.labelForBulkDeleteCheckBox = null;
    }
    
    @Override
    void panelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"displaypanel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        if (this.onClickActionName != null) {
            sbf.append(" onclick=\"P2.act(this, '").append(this.name).append("','").append(this.onClickActionName).append("');\" ");
        }
        sbf.append("> ");
        if (this.elementsPerRow < 1) {
            this.elementsPerRow = 1;
        }
        if (this.elements != null && this.elements.length > 0) {
            if (this.tableName != null) {
                pageContext.setTableName(this.tableName, false);
            }
            this.elementsToHtml(sbf, pageContext, true);
            if (this.tableName != null) {
                pageContext.resetTableName();
            }
        }
        sbf.append(" </div>");
    }
    
    void elementsToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean setIdToTable) {
        this.setTableTag(sbf, pageContext, setIdToTable);
        final int totalElements = this.elements.length;
        int unitsUsed = 0;
        int eleIdx = 0;
        sbf.append(" <tr>");
        if (this.rowsCanBeDeleted) {
            sbf.append("<td colspan=\"").append(this.elementsPerRow * 2).append("\">");
            this.addDeleteCheckBox(sbf, pageContext, false);
            sbf.append("</td></tr><tr>");
        }
        AbstractElement ele = this.elements[eleIdx];
        int units = ele.numberOfUnitsToUse;
        if (units == 0) {
            sbf.append("<td colspan=\"2\">");
        }
        while (true) {
            if (ele != null && !ele.markedAsComment) {
                if (ele.inError) {
                    pageContext.reportError("");
                }
                if (ele instanceof AbstractField) {
                    final AbstractField field = (AbstractField)ele;
                    field.toHtml(sbf, pageContext);
                }
                else {
                    if (units > 0) {
                        sbf.append(" <td colspan=\"").append(units * 2).append("\" ");
                        if (ele.align != null) {
                            sbf.append(" align=\"").append(ele.align).append("\" ");
                        }
                        sbf.append('>');
                    }
                    ele.toHtml(sbf, pageContext);
                }
            }
            unitsUsed += units;
            if (++eleIdx == totalElements) {
                break;
            }
            ele = this.elements[eleIdx];
            if (ele == null) {
                continue;
            }
            if (ele.markedAsComment) {
                continue;
            }
            units = ele.numberOfUnitsToUse;
            if (units == 0) {
                continue;
            }
            sbf.append("</td>");
            if (unitsUsed < this.elementsPerRow) {
                continue;
            }
            unitsUsed = 0;
            sbf.append(" </tr> <tr>");
        }
        sbf.append("</td> </tr></table>");
    }
    
    void setTableTag(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean setIdToTable) {
        final String layoutType = pageContext.getLayoutType();
        if (layoutType != null && layoutType.equals("2")) {
            sbf.append(" <table class=\"fieldstable\" ");
        }
        else {
            sbf.append(" <table cellpadding=\"0\" cellspacing=\"0\" class=\"fieldstable\" ");
        }
        if (AP.alignPanels) {
            sbf.append(" width=\"100%\" ");
        }
        if (this.tableName != null && setIdToTable) {
            sbf.append("id=\"").append(this.tableName).append("\" >\n");
        }
        else {
            sbf.append(">\n");
        }
        final int nbrTds = this.elementsPerRow * 2;
        boolean assignWidths = false;
        if (this.columnWidths != null) {
            if (nbrTds == this.columnWidths.length) {
                assignWidths = true;
            }
            else {
                final String err = "ERROR: " + this.name + " has columnWidths set with " + this.columnWidths.length + " comma separated values. It should have " + this.elementsPerRow * 2 + " values instead. (this is two times the elementsPerRow value) Alternately, do not use this attribute, but use css for columns in stead.";
                Spit.out(err);
                pageContext.reportError(err);
            }
        }
        if (AP.generateColTags) {
            for (int i = 0; i < nbrTds; ++i) {
                sbf.append("<col id=\"").append(this.name).append("Col").append(i + 1).append("\" ");
                if (assignWidths) {
                    sbf.append("style=\"width:").append(this.columnWidths[i]).append("\" ");
                }
                sbf.append(" />");
            }
        }
        else if (assignWidths) {
            sbf.append("\n<tr>");
            String[] columnWidths;
            for (int length = (columnWidths = this.columnWidths).length, j = 0; j < length; ++j) {
                final String w = columnWidths[j];
                sbf.append("\n<td><img src=\"").append(PageGeneratorContext.imageFolderName).append("space.gif\" height=\"1px\" width=\"" + w + "\"/></td>");
            }
            sbf.append("\n</tr>");
        }
    }
    
    @Override
    void panelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"displayPanel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        if (this.onClickActionName != null) {
            sbf.append(" onclick=\"P2.act(this, '").append(this.name).append("','").append(this.onClickActionName).append("');\" ");
        }
        sbf.append("> ");
        if (this.elements != null && this.elements.length > 0) {
            if (this.tableName != null) {
                pageContext.setTableName(this.tableName, false);
            }
            this.elementsToHtml5(sbf, pageContext, true);
            if (this.tableName != null) {
                pageContext.resetTableName();
            }
        }
        sbf.append(" </div>");
    }
    
    void elementsToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean setIdToTable) {
        sbf.append(" <table cellpadding=\"0\" cellspacing=\"0\" class=\"fieldsTable\" ");
        if (setIdToTable) {
            sbf.append(" id=\"");
            if (this.tableName != null) {
                sbf.append(this.tableName);
            }
            else {
                sbf.append(this.name).append("FieldsTable");
            }
            sbf.append("\" ");
        }
        sbf.append(">\n");
        for (int nbrTds = this.elementsPerRow * 2, i = 0; i < nbrTds; ++i) {
            sbf.append("<col id=\"").append(this.name).append("Col").append(i + 1).append("\" />");
        }
        int unitsUsed = 0;
        sbf.append("<tr>");
        if (this.rowsCanBeDeleted) {
            sbf.append("<td colspan=\"").append(this.elementsPerRow * 2).append("\">");
            this.addDeleteCheckBox(sbf, pageContext, false);
            sbf.append("</td></tr><tr>");
        }
        boolean isFirstTd = true;
        AbstractElement[] elements;
        for (int length = (elements = this.elements).length, j = 0; j < length; ++j) {
            final AbstractElement ele = elements[j];
            if (!ele.markedAsComment) {
                if (ele.numberOfUnitsToUse > 0) {
                    if (isFirstTd) {
                        isFirstTd = false;
                    }
                    else {
                        sbf.append("</td>");
                    }
                    if (unitsUsed >= this.elementsPerRow) {
                        sbf.append("</tr><tr>");
                        unitsUsed = 0;
                    }
                    if (!(ele instanceof AbstractField)) {
                        sbf.append(" <td colspan=\"").append(ele.numberOfUnitsToUse * 2).append("\" >");
                    }
                    unitsUsed += ele.numberOfUnitsToUse;
                }
                ele.toHtml5(sbf, pageContext);
            }
        }
        sbf.append("</td>");
        if (unitsUsed < this.elementsPerRow) {
            sbf.append("<td colspan=\"").append(this.elementsPerRow - unitsUsed).append("\">&nbsp;</td>");
        }
        sbf.append("</tr> </table> ");
    }
    
    void addDeleteCheckBox(final StringBuilder sbf, final PageGeneratorContext pageContext, final boolean useTd) {
        final String layoutType = pageContext.getLayoutType();
        String closeTag;
        if (useTd) {
            closeTag = "</td>";
            if (layoutType.equals("5")) {
                sbf.append("<td style=\"width:21px;\">");
            }
            else {
                sbf.append("<td>");
            }
        }
        else {
            sbf.append("<div class=\"deleteRowButton\">");
            closeTag = "</div>";
        }
        AbstractField chk = null;
        if (layoutType.equals("5") && AP.showIamgeForDeleteOption && this.labelForBulkDeleteCheckBox != null) {
            chk = new CheckBoxField();
            chk.label = "";
        }
        else if (AP.showIamgeForDeleteOption) {
            final ImageField img = new ImageField();
            img.baseSrc = PageGeneratorContext.imageFolderName;
            img.imageExtension = ".gif";
            img.defaultValue = "deleteRow";
            img.cssClassName = "deleteimg";
            chk = img;
        }
        else {
            chk = new CheckBoxField();
        }
        chk.name = String.valueOf(this.tableName) + "Delete";
        chk.dataElementName = "boolean";
        chk.numberOfUnitsToUse = 1;
        if (layoutType.equals("5") && this.labelForBulkDeleteCheckBox != null) {
            chk.htmlAttributes = "onkeydown=\"P2.handleGridNav(this, '" + chk.name + "', event);\" onclick=\"return P2.removeTableRow(event, this, '" + this.tableName + "');\"";
        }
        else {
            chk.htmlAttributes = "onkeydown=\"P2.handleGridNav(this, '" + chk.name + "', event);\" onclick=\"return P2.deleteTableRow(event, this, '" + this.tableName + "');\"";
        }
        if (pageContext.useHtml5) {
            chk.fieldToHtml5(sbf, pageContext);
        }
        else {
            chk.fieldToHtml(sbf, pageContext);
        }
        sbf.append(closeTag);
    }
}
