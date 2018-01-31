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

class GridPanel extends TablePanel
{
    boolean rowsCanBeAdded;
    boolean rowsCanBeCloned;
    String keyFieldName;
    String actionFieldName;
    String uniqueColumns;
    String messageIdForUniqueColumns;
    int minRows;
    int maxRows;
    boolean sendAffectedRowsOnly;
    String functionBeforeAddRow;
    String functionAfterAddRow;
    String functionBeforeDeleteRow;
    String functionAfterDeleteRow;
    boolean dataForNewRowToBeClonedFromFirstRow;
    rowType dataForNewRowToBeClonedFromRow;
    String newRowColumnsNotToBePopulatedWithData;
    String labelForAddRowButton;
    String hoverForDeleteCheckBox;
    boolean doNotDeleteAppendedRows;
    boolean confirmOnRowDelete;
    boolean mouseOverRequired;
    String autoSaveServiceName;
    String functionBeforeAutoSave;
    String functionAfterAutoSave;
    
    public GridPanel() {
        this.rowsCanBeAdded = false;
        this.rowsCanBeCloned = false;
        this.keyFieldName = null;
        this.actionFieldName = null;
        this.uniqueColumns = null;
        this.messageIdForUniqueColumns = null;
        this.minRows = 0;
        this.maxRows = 0;
        this.sendAffectedRowsOnly = false;
        this.functionBeforeAddRow = null;
        this.functionAfterAddRow = null;
        this.functionBeforeDeleteRow = null;
        this.functionAfterDeleteRow = null;
        this.dataForNewRowToBeClonedFromFirstRow = false;
        this.dataForNewRowToBeClonedFromRow = rowType.none;
        this.newRowColumnsNotToBePopulatedWithData = null;
        this.labelForAddRowButton = null;
        this.hoverForDeleteCheckBox = null;
        this.doNotDeleteAppendedRows = false;
        this.confirmOnRowDelete = false;
        this.mouseOverRequired = false;
        this.autoSaveServiceName = null;
        this.functionBeforeAutoSave = null;
        this.functionAfterAutoSave = null;
        this.tableType = "grid";
    }
    
    @Override
    void addAfterTable(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String layoutType = pageContext.getLayoutType();
        if (layoutType != null && layoutType.equals("5")) {
            sbf.append("<div class=\"addremoveicons\" id=\"").append(this.tableName).append("actiondiv\" >");
            this.addAddRowButton5(sbf);
        }
        else if (this.rowsCanBeAdded) {
            this.addAddRowButton(sbf, pageContext);
        }
        if (this.rowsCanBeCloned) {
            this.addCloneRowButton(sbf, pageContext);
        }
        if (layoutType != null && layoutType.equals("5")) {
            sbf.append("</div>");
        }
    }
    
    @Override
    protected void addSubClassSpecificDataRowAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append(" class=\"gridrow\" ");
        if (this.rowHeight > 0) {
            sbf.append("style=\"height:").append(this.rowHeight).append("px;\" ");
        }
        if (this.mouseOverRequired) {
            sbf.append("onmouseover=\"P2.listMouseOver(this, '").append(this.tableName).append("', event);\" ");
            sbf.append("onmouseout=\"P2.listMouseOut(this, '").append(this.tableName).append("', event);\" ");
        }
    }
    
    private void addAddRowButton(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final ButtonElement btn = new ButtonElement();
        if (this.labelForAddRowButton != null) {
            btn.label = this.labelForAddRowButton;
            if (pageContext.getLayoutType().equals("3")) {
                btn.imageName = "default";
            }
        }
        else {
            btn.label = "Add a Row";
        }
        btn.name = String.valueOf(this.tableName) + "AddRow";
        btn.htmlAttributes = "onclick=\"P2.addTableRow(this, '" + this.tableName + "');\"";
        btn.toHtml(sbf, pageContext);
    }
    
    private void addAddRowButton5(final StringBuilder sbf) {
        if (this.rowsCanBeAdded) {
            sbf.append("<img class=\"addicon\" src=\"").append(PageGeneratorContext.imageFolderName).append("addIcon.png\" border=\"0\" ");
            if (this.labelForAddRowButton != null) {
                sbf.append("alt=\"").append(this.labelForAddRowButton).append("\" ");
            }
            sbf.append("style=\"cursor:pointer;\" id=\"");
            sbf.append(this.tableName).append("AddRow\" height=\"15\" style=\"margin-right: 5px; margin-left: 5px;\" ");
            sbf.append("onclick=\"P2.addTableRow(this, '").append(this.tableName).append("');\" />");
        }
        if (this.rowsCanBeDeleted && this.labelForBulkDeleteCheckBox != null) {
            sbf.append("<img class=\"removeicon\" src=\"").append(PageGeneratorContext.imageFolderName).append("removeIcon.png\" border=\"0\" ");
            sbf.append("style=\"cursor:pointer;\" id=\"");
            sbf.append(this.tableName).append("RemoveRow\" height=\"15\" style=\"margin-right: 5px; margin-left: 5px;\" ");
            sbf.append("onclick=\"P2.removeTableRows(this, '").append(this.tableName).append("');\" />");
        }
    }
    
    private void addCloneRowButton(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final ButtonElement btn = new ButtonElement();
        btn.label = "Clone this Row";
        btn.name = String.valueOf(this.tableName) + "CloneRow";
        btn.htmlAttributes = "onmousedown=\"P2.cloneTableRow(this, '" + this.tableName + "');\"";
        btn.toHtml(sbf, pageContext);
    }
}
