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

class ListPanel extends TablePanel
{
    public static final String LINEAR = "linear";
    public static final String DROP_DOWN = "dropDown";
    String showNbrRowsAs;
    int pageSize;
    boolean actionDisabled;
    boolean multipleSelect;
    String onDblClickActionName;
    String messageNameOnNoData;
    String paginateButtonType;
    String paginationServiceName;
    String paginationServiceFieldNames;
    String paginationServiceFieldSources;
    String paginateCallback;
    
    public ListPanel() {
        this.showNbrRowsAs = null;
        this.pageSize = AP.defaultPaginationSize;
        this.actionDisabled = false;
        this.multipleSelect = false;
        this.onDblClickActionName = null;
        this.messageNameOnNoData = null;
        this.paginateButtonType = AP.paginateButtonType;
        this.paginationServiceName = null;
        this.paginationServiceFieldNames = null;
        this.paginationServiceFieldSources = null;
        this.paginateCallback = null;
        this.tableType = "list";
    }
    
    @Override
    void addBeforePanel(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.messageNameOnNoData != null) {
            String text = "&nbsp;";
            if (this.messageNameOnNoData.length() > 0) {
                text = Messages.getMessageText(this.messageNameOnNoData);
            }
            sbf.append("\n<div id=\"").append(this.tableName).append("NoData\" class=\"nodatamessage\" style=\"display:none;\" >").append(text).append("</div>");
        }
    }
    
    @Override
    void addBeforeTable(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String layoutType = pageContext.getLayoutType();
        if (this.pageSize > 0) {
            if (layoutType.equals("5")) {
                sbf.append("<div id=\"").append(this.tableName).append("PaginationData\" class=\"paginationdatadiv\" >");
                sbf.append("\n<span id=\"").append(this.tableName).append("TotalRows\"></span><span>&nbsp;").append(this.paginationLabel).append("</span>");
                sbf.append("\n</div>");
                sbf.append("\n<div id=\"").append(this.tableName).append("Pagination\" style=\"display:none;\" class=\"paginationdiv\" >");
                sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("FirstPage\" onclick=\"P2.paginate('").append(this.tableName).append("','first');\"><img src=\"../../images/arrowstart.png\" style=\"margin-bottom: -3px\"></span>&nbsp;&nbsp;");
                sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("PrevPage\" onclick=\"P2.paginate('").append(this.tableName).append("','prev');\"><img src=\"../../images/arrowleft.png\" style=\"margin-bottom: -3px\"></span>&nbsp;&nbsp;");
                sbf.append("<span id=\"").append(this.tableName).append("PageCount\"> Page <input type=\"text\" size=\"2\" onkeydown=\"P2.checkPageValue(this, event);\" id=\"").append(this.tableName).append("CurrentPage\" />&nbsp;of&nbsp;<span id=\"").append(this.tableName).append("TotalPages\"></span>&nbsp;&nbsp;");
                sbf.append("</span>");
                sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("NextPage\" onclick=\"P2.paginate('").append(this.tableName).append("','next');\"><img src=\"../../images/arrowright.png\" style=\"margin-bottom: -3px\"></span>&nbsp;&nbsp;");
                sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("LastPage\" onclick=\"P2.paginate('").append(this.tableName).append("','last');\"><img src=\"../../images/arrowend.png\" style=\"margin-bottom: -3px\"></span>");
                sbf.append("\n</div>");
            }
            else {
                sbf.append("<div id=\"").append(this.tableName).append("Pagination\" style=\"display:none;\" class=\"paginationdiv\" >");
                sbf.append("\n<span id=\"").append(this.tableName).append("TotalRows\"></span><span>&nbsp;").append(this.paginationLabel).append("</span>").append("<span id=\"").append(this.tableName).append("PageCount\"> Page <input type=\"text\" size=\"2\" onkeydown=\"P2.checkPageValue(this, event);\" id=\"").append(this.tableName).append("CurrentPage\" />&nbsp;of&nbsp;<span id=\"").append(this.tableName).append("TotalPages\"></span>&nbsp;&nbsp;");
                if (this.paginateButtonType == "dropDown") {
                    this.addDropDownPaginateButtons(sbf, pageContext);
                }
                else {
                    this.addLinearPaginateButtons(sbf);
                }
                sbf.append("</span>");
                sbf.append("\n</div>");
            }
        }
    }
    
    private void addLinearPaginateButtons(final StringBuilder sbf) {
        sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("FirstPage\" onclick=\"P2.paginate('").append(this.tableName).append("','first');\">&lt;&lt;First</span>&nbsp;&nbsp;");
        sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("PrevPage\" onclick=\"P2.paginate('").append(this.tableName).append("','prev');\">&lt;Prev</span>&nbsp;&nbsp;");
        sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("NextPage\" onclick=\"P2.paginate('").append(this.tableName).append("','next');\">Next&gt;</span>&nbsp;&nbsp;");
        sbf.append("<span class=\"activepaginationbutton\" id=\"").append(this.tableName).append("LastPage\" onclick=\"P2.paginate('").append(this.tableName).append("','last');\">Last&gt;&gt;</span>");
    }
    
    private void addDropDownPaginateButtons(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<span>Pages: </span><select id=\"").append(this.tableName).append("PageSelection\"><option value=\"\"></option><option value=\"first\">First</option><option value=\"prev\">Prev</option><option value=\"next\">Next</option><option value=\"last\">Last</option></select>");
        final ButtonElement te = new ButtonElement();
        te.label = "&nbsp;Go&nbsp;";
        te.name = String.valueOf(this.tableName) + "GoButton";
        te.htmlAttributes = "onclick=\"P2.paginate('" + this.tableName + "', 'go');\" ";
        te.cssClassName = "paginategobutton";
        te.toHtml(sbf, pageContext);
    }
    
    @Override
    protected void addSubClassSpecificDataRowAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        String style = "";
        if (!this.actionDisabled && (this.multipleSelect || this.onClickActionName != null || this.onDblClickActionName != null)) {
            style = "cursor:pointer;";
        }
        if (this.rowHeight > 0) {
            style = String.valueOf(style) + "height:" + this.rowHeight + "px;";
        }
        if (style.length() > 0) {
            sbf.append("style=\"").append(style).append("\" ");
        }
        sbf.append("onmouseover=\"P2.listMouseOver(this, '").append(this.tableName).append("', event);\" ");
        sbf.append("onmouseout=\"P2.listMouseOut(this, '").append(this.tableName).append("', event);\" ");
        if (this.onDblClickActionName != null) {
            sbf.append("ondblclick=\"P2.listDblClicked(this, '").append(this.tableName).append("', '").append(this.onDblClickActionName).append("');\" ");
        }
    }
}
