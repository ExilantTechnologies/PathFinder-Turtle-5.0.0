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

abstract class AbstractPanel extends AbstractElement
{
    private static final String HTML_BEFORE_PANEL;
    private static final String HTML_AFTER_PANEL;
    private static final String HTML_BEFORE_PANEL3;
    private static final String HTML_AFTER_PANEL3;
    private static final String RIGHT_ARROW;
    private static final String LEFT_ARROW;
    static final String[] ALL_META_ATTRIBUTES;
    static final String[] ALL_TABLE_SENSITIVE_ATTRIBUTES;
    boolean requiresGroupOutline;
    boolean isCollapsible;
    boolean noBorder;
    int elementsPerRow;
    String tabLabel;
    String tableName;
    String repeatOnFieldName;
    String labelFieldName;
    String repeatingPanelName;
    String elderBrother;
    String youngerBrother;
    String columnSumCssClassName;
    String buttonLabel;
    SlideEffect slideEffect;
    String tabIconImage;
    String htmlFileName;
    boolean keepItSimple;
    AbstractElement[] elements;
    
    static {
        HTML_BEFORE_PANEL = " border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"10px\" ><img width=\"10px\"  src=\"" + PageGeneratorContext.imageFolderName + "b2TopLeft.gif\"/></td><td background=\"" + PageGeneratorContext.imageFolderName + "b2Top.gif\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2Top.gif\" /></td>" + "<td  width=\"10px\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2TopRight.gif\" /></td></tr>" + "<tr><td background=\"" + PageGeneratorContext.imageFolderName + "b2Left.gif\" valign=\"top\" width=\"10px\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2Left.gif\" width=\"10\" valign=\"top\"/></td>" + "<td bgcolor=\"#f6f6f6\" valign=\"top\">";
        HTML_AFTER_PANEL = "</td><td width=\"10px\" valign=\"top\" background=\"" + PageGeneratorContext.imageFolderName + "b2Right.gif\"><img width=\"10px\" src=\"" + PageGeneratorContext.imageFolderName + "b2Right.gif\"/></td></tr>" + "<tr><td><img width=\"10px\"  src=\"" + PageGeneratorContext.imageFolderName + "b2BottomLeft.gif\"/></td><td background=\"" + PageGeneratorContext.imageFolderName + "b2Bottom.gif\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2Bottom.gif\" /></td>" + "<td  width=\"10px\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2BottomRight.gif\" /></td></tr>" + "</table>";
        HTML_BEFORE_PANEL3 = " border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"10px\" colspan=\"2\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2TopLeft.gif\"/></td><td class=\"b2Top\"></td>" + "<td  width=\"10px\" colspan=\"2\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2TopRight.gif\" /></td></tr>" + "<tr><td class=\"b2Left\" valign=\"top\" width=\"1px\"></td><td class=\"b2Fill\" valign=\"top\" width=\"9px\"></td>" + "<td valign=\"top\" class=\"panelholder\">";
        HTML_AFTER_PANEL3 = "</td><td width=\"9px\" valign=\"top\" class=\"b2Fill\"></td><td width=\"1px\" valign=\"top\" class=\"b2Right\"></td></tr><tr><td width=\"10px\" colspan=\"2\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2BottomLeft.gif\"/></td><td class=\"b2Bottom\"></td>" + "<td  width=\"10px\" colspan=\"2\"><img src=\"" + PageGeneratorContext.imageFolderName + "b2BottomRight.gif\" /></td></tr>" + "</table>";
        RIGHT_ARROW = "<img border=\"0\" src=\"" + PageGeneratorContext.imageFolderName + "right.gif\" >";
        LEFT_ARROW = "<img border=\"0\" src=\"" + PageGeneratorContext.imageFolderName + "left.gif\" >";
        ALL_META_ATTRIBUTES = new String[] { "addSeqNo", "rowsCanBeAdded", "initialNumberOfRows", "multipleSelect", "onClickActionName", "onDblClickActionName", "pageSize", "paginateButtonType", "renderingOption", "rowsCanBeCloned", "showNbrRowsAs", "showHeader", "simulateClickOnFirstRow", "simulateClickOnRow", "sendAffectedRowsOnly", "tableName", "childTableName", "childKeysTableName", "repeatingColumnName", "qtyBySize", "minRows", "maxRows", "elderBrother", "youngerBrother", "repeatingPanelName", "uniqueColumns", "functionBeforeAddRow", "functionAfterAddRow", "functionBeforeDeleteRow", "functionAfterDeleteRow", "actionDisabled", "dataForNewRowToBeClonedFromFirstRow", "dataForNewRowToBeClonedFromRow", "newRowColumnsNotToBePopulatedWithData", "columnNosHavingGroupedRadioButton", "treeViewColumnName", "treeViewKeyColumn", "treeViewParentKeyColumn", "treeViewHasChildColumn", "rowsCanBeDeleted", "frozenColumnIndex", "slideEffect", "allColumnsAreSortable", "allColumnsAreFilterable", "doNotDeleteAppendedRows", "confirmOnRowDelete", "headerGrouping", "isFixedHeight", "childHtmlAttributes", "doNotShowTreeViewCheckBox", "paginationServiceName", "paginationServiceFieldNames", "paginationServiceFieldSources", "paginateCallback", "doNotResize", "rowHelpText", "quickSearchFieldName", "linkedTableName", "rowsCanBeMoved", "keepABlankRow", "mergeWithTableName", "stubNameForMerging", "localPagination", "nestedTableName", "nestOnColumnName", "nestedTableColumnName", "mergeOnColumnName", "markedAsComment", "messageIdForUniqueColumns", "renderAsADisplayPanel", "autoSaveServiceName", "functionABeforeAutoSave", "functionAfterAutoSave", "useTableLayout" };
        ALL_TABLE_SENSITIVE_ATTRIBUTES = new String[] { "actionFieldName", "idFieldName", "keyFieldName", "repeatedFieldName", "repeatOnFieldName", "labelFieldName", "firstFieldName" };
    }
    
    AbstractPanel() {
        this.requiresGroupOutline = false;
        this.isCollapsible = false;
        this.noBorder = false;
        this.elementsPerRow = 1;
        this.tabLabel = null;
        this.tableName = null;
        this.repeatOnFieldName = null;
        this.labelFieldName = null;
        this.repeatingPanelName = null;
        this.elderBrother = null;
        this.youngerBrother = null;
        this.columnSumCssClassName = "field";
        this.buttonLabel = null;
        this.slideEffect = SlideEffect.none;
        this.tabIconImage = null;
        this.htmlFileName = null;
        this.keepItSimple = false;
        this.elements = null;
    }
    
    boolean leaveHeightToMe() {
        return false;
    }
    
    boolean leaveSlidingToMe() {
        return false;
    }
    
    @Override
    void addMyAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.name != null) {
            sbf.append("id=\"").append(this.name).append("\" ");
        }
        if (this.cssClassName != null) {
            sbf.append("class=\"").append(this.cssClassName).append("\" ");
        }
        if (this.htmlAttributes != null) {
            sbf.append(' ').append(this.htmlAttributes).append(' ');
        }
        String style = "";
        if (this.width != null) {
            style = String.valueOf(style) + "width:" + this.width + ';';
        }
        if (this.height != null && !this.leaveHeightToMe()) {
            style = String.valueOf(style) + "overflow-y:auto; height:" + this.height + ';';
        }
        else if (this.slideEffect.equals(SlideEffect.fromLeft)) {
            style = String.valueOf(style) + "overflow:hidden;";
        }
        if (this.hidden) {
            style = String.valueOf(style) + "display:none;";
        }
        if (this.align != null && this.getClass().equals(ButtonPanel.class) && AP.alignPanels) {
            style = String.valueOf(style) + "text-align:" + this.align + ";";
        }
        if (style.length() > 0) {
            sbf.append("style=\"").append(style).append("\" ");
        }
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        boolean panelHasItsName = true;
        if (this.name == null) {
            panelHasItsName = false;
            this.name = pageContext.getPanelName();
        }
        final String lt = pageContext.getLayoutType();
        if (AP.projectName.equals("Celebrus")) {
            this.toHtmlCelebrus(sbf, pageContext);
        }
        else if (lt == null) {
            this.toHtml0(sbf, pageContext);
        }
        else if (lt.equals("1")) {
            this.toHtml1(sbf, pageContext);
        }
        else if (lt.equals("2")) {
            this.toHtml2(sbf, pageContext);
        }
        else if (lt.equals("3") || lt.equals("5")) {
            this.toHtml3(sbf, pageContext);
        }
        else {
            this.toHtml0(sbf, pageContext);
        }
        if (!panelHasItsName) {
            this.name = null;
        }
    }
    
    void toHtml0(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        boolean panelHasItsName = true;
        if (this.name == null) {
            panelHasItsName = false;
            this.name = pageContext.getPanelName();
        }
        String firstTag = "div";
        String secondTag = "span";
        if (this.requiresGroupOutline) {
            firstTag = "fieldset";
            secondTag = "legend";
        }
        String topClass = "expandedfieldset";
        if (this.hidden) {
            topClass = "collapsedfieldset";
        }
        sbf.append('<').append(firstTag).append(' ');
        if (this.name != null) {
            sbf.append(" id=\"").append(this.name).append("Top\" ");
        }
        if (this.width == null && AP.alignPanels && this.align == null) {
            sbf.append(" width=\"100%\" ");
        }
        sbf.append(" class=\"").append(topClass).append("\">");
        if (this.label != null && this.label.length() > 0 && !(this instanceof XMLGridPanel)) {
            sbf.append('<').append(secondTag);
            this.addLabel(sbf);
            sbf.append("</").append(secondTag).append('>');
        }
        final PrePost pp = new PrePost();
        if (this.slideEffect != SlideEffect.none && !this.leaveSlidingToMe()) {
            this.getPrePostForSlider(pp, this.name);
        }
        sbf.append(pp.pre);
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml(sbf, pageContext);
        }
        sbf.append(pp.post);
        sbf.append("</").append(firstTag).append('>');
        if (!panelHasItsName) {
            this.name = null;
        }
    }
    
    private void toHtml1(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<fieldset id=\"").append(this.name).append("Top\" class=\"");
        if (this.requiresGroupOutline) {
            if (this.hidden) {
                sbf.append("collapsedfieldset\"");
            }
            else {
                sbf.append("expandedfieldset\"");
            }
        }
        else {
            sbf.append("fieldsetwithnooutline\"");
            if (this.hidden) {
                sbf.append(" style=\"display:none;\" ");
            }
        }
        if (this.width == null && AP.alignPanels && this.align == null) {
            sbf.append(" width=\"100%\" ");
        }
        sbf.append(">");
        if (this.label != null && this.label.length() > 0 && !(this instanceof XMLGridPanel)) {
            sbf.append("\n<legend ");
            this.addLabel(sbf);
            sbf.append("</legend>");
        }
        final PrePost pp = new PrePost();
        if (this.slideEffect != SlideEffect.none && !this.leaveSlidingToMe()) {
            this.getPrePostForSlider(pp, this.name);
        }
        sbf.append(pp.pre);
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml(sbf, pageContext);
        }
        sbf.append(pp.post);
        sbf.append("\n</fieldset>");
    }
    
    void addLabel(final StringBuilder sbf) {
        if (this.isCollapsible) {
            sbf.append(" class=\"twister\" id=\"").append(this.name).append("Twister\" onclick=\"P2.twist(this, '").append(this.name).append("');");
            if (this.onClickActionName != null && this.tabLabel == null) {
                sbf.append("P2.act('").append(this.onClickActionName).append("');");
            }
            sbf.append("\"> ");
            if (this.hidden) {
                sbf.append(PageGeneratorContext.collapsedImg);
            }
            else {
                sbf.append(PageGeneratorContext.expandedImg);
            }
            sbf.append("<span id=\"").append(this.name).append("Label\">").append(this.label).append("</span>");
        }
        else {
            sbf.append(" id=\"").append(this.name).append("Label\" class=\"tablelabel\" ");
            if (this.onClickActionName != null && this.tabLabel == null) {
                sbf.append(" style=\"cursor:pointer\" onclick=\"P2.act('").append(this.onClickActionName).append("');\" ");
            }
            sbf.append('>').append(this.label);
        }
    }
    
    void toHtml2(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (AP.spanForButtonPanelRequires && this.getClass().equals(ButtonPanel.class)) {
            sbf.append("\n<span id=\"").append(this.name).append("Top\"");
            if (this.width == null && (this.getClass().equals(ButtonPanel.class) || AP.alignPanels) && this.align == null) {
                sbf.append(" style=\"width:100%\" ");
            }
            sbf.append(">");
        }
        else {
            sbf.append("\n<table id=\"").append(this.name).append("Top\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"");
            if (this.width == null && (this.getClass().equals(ButtonPanel.class) || AP.alignPanels) && this.align == null) {
                sbf.append(" width=\"100%\" ");
            }
            sbf.append("> <tr><td");
            if (this.align != null) {
                sbf.append(" align=\"").append(this.align).append("\" ");
            }
            sbf.append(">");
        }
        if (this.label != null && !(this instanceof XMLGridPanel)) {
            if (this.getClass().equals(ButtonPanel.class)) {
                sbf.append("<span ");
            }
            else {
                sbf.append("<div ");
            }
            this.addLabel(sbf);
            if (this.getClass().equals(ButtonPanel.class)) {
                sbf.append("\n</span>");
            }
            else {
                sbf.append("\n</div>");
            }
        }
        if (this.requiresGroupOutline) {
            sbf.append("\n<table class=\"");
            if (this.hidden) {
                sbf.append("collapsedfieldset");
            }
            else {
                sbf.append("expandedfieldset");
            }
            sbf.append("\" ");
            sbf.append(AbstractPanel.HTML_BEFORE_PANEL);
        }
        final PrePost pp = new PrePost();
        if (this.slideEffect != SlideEffect.none && !this.leaveSlidingToMe()) {
            this.getPrePostForSlider(pp, this.name);
        }
        sbf.append(pp.pre);
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml(sbf, pageContext);
        }
        sbf.append(pp.post);
        if (this.requiresGroupOutline) {
            sbf.append(AbstractPanel.HTML_AFTER_PANEL);
        }
        if (AP.spanForButtonPanelRequires && this.getClass().equals(ButtonPanel.class)) {
            sbf.append("</span>");
        }
        else {
            sbf.append("</td></tr></table>");
        }
    }
    
    void toHtml3(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String layoutType = pageContext.getLayoutType();
        sbf.append("\n<table id=\"").append(this.name).append("Top\" class=\"");
        if (this.hidden) {
            sbf.append("collapsedfieldset");
        }
        else {
            sbf.append("expandedfieldset");
        }
        sbf.append("\" ");
        if (this.width == null && (this.getClass().equals(ButtonPanel.class) || AP.alignPanels) && this.align == null) {
            sbf.append(" width=\"100%\" ");
        }
        if (this.requiresGroupOutline) {
            if (layoutType.equals("3") || layoutType.equals("5")) {
                sbf.append(AbstractPanel.HTML_BEFORE_PANEL3);
            }
            else {
                sbf.append(AbstractPanel.HTML_BEFORE_PANEL);
            }
        }
        else {
            sbf.append(" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>");
        }
        if (this.label != null && !(this instanceof XMLGridPanel)) {
            sbf.append("<div ");
            if (this.widthInPixels > 0 && (layoutType.equals("3") || layoutType.equals("5"))) {
                int divWidth = this.widthInPixels;
                if (!(this instanceof TabPanel)) {
                    divWidth += 20;
                }
                sbf.append("style=\"width: " + divWidth + "px;\" ");
            }
            this.addLabel(sbf);
            sbf.append("\n</div>");
        }
        final PrePost pp = new PrePost();
        if (this.slideEffect != SlideEffect.none && !this.leaveSlidingToMe()) {
            this.getPrePostForSlider(pp, this.name);
        }
        sbf.append(pp.pre);
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml(sbf, pageContext);
        }
        sbf.append(pp.post);
        if (this.requiresGroupOutline) {
            if (layoutType.equals("3") || layoutType.equals("5")) {
                sbf.append(AbstractPanel.HTML_AFTER_PANEL3);
            }
            else {
                sbf.append(AbstractPanel.HTML_AFTER_PANEL);
            }
        }
        else {
            sbf.append("</td></tr></table>");
        }
    }
    
    void toHtmlCelebrus(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String layoutType = pageContext.getLayoutType();
        sbf.append("\n<table id=\"").append(this.name).append("Top\" class=\"");
        if (this.hidden) {
            sbf.append("collapsedfieldset");
        }
        else {
            sbf.append("expandedfieldset");
        }
        sbf.append("\" ");
        if (this.width == null && (this.getClass().equals(ButtonPanel.class) || AP.alignPanels) && this.align == null) {
            sbf.append(" width=\"100%\" ");
        }
        sbf.append(" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  onmouseover=\"PannelEffect(this)\">\n");
        if (this.requiresGroupOutline) {
            sbf.append("<tr>\n");
            sbf.append("<td colspan=\"2\" class=\"topLeftCorner\"></td>\n");
            sbf.append("<td class=\"b2Top\"></td>\n");
            sbf.append("<td colspan=\"2\" class=\"topRightCorner\"></td>\n");
            sbf.append("</tr>\n");
            sbf.append("<tr>\n");
            sbf.append("<td class=\"b2Left\"></td>\n");
            sbf.append("<td class=\"b2Fill\"></td>\n");
        }
        else {
            sbf.append("<tr>\n");
        }
        sbf.append("<td class=\"panelholder\">\n");
        if (this.label != null && !(this instanceof XMLGridPanel)) {
            sbf.append("<div ");
            if (this.widthInPixels > 0 && (layoutType.equals("3") || layoutType.equals("5"))) {
                int divWidth = this.widthInPixels;
                if (!(this instanceof TabPanel)) {
                    divWidth += 20;
                }
                sbf.append("style=\"width: " + divWidth + "px;\" ");
            }
            this.addLabel(sbf);
            sbf.append("\n</div>");
        }
        final PrePost pp = new PrePost();
        if (this.slideEffect != SlideEffect.none && !this.leaveSlidingToMe()) {
            this.getPrePostForSlider(pp, this.name);
        }
        sbf.append(pp.pre);
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml(sbf, pageContext);
        }
        sbf.append(pp.post);
        sbf.append("</td>\n");
        if (this.requiresGroupOutline) {
            sbf.append("<td class=\"b2Fill\"></td>\n");
            sbf.append("<td class=\"b2Right\"></td>\n");
            sbf.append("</tr>\n");
            sbf.append("<tr>\n");
            sbf.append("<td colspan=\"2\" class=\"bottomLeftCorner\"></td>\n");
            sbf.append("<td class=\"b2Bottom\"></td>\n");
            sbf.append("<td colspan=\"2\" class=\"bottomRightCorner\">&nbsp;</td>\n");
            sbf.append("</tr>\n");
        }
        else {
            sbf.append("</tr>\n");
        }
        sbf.append("</table>\n");
    }
    
    void getPrePostForSlider(final PrePost pp, final String ename) {
        pp.pre = "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr>";
        pp.post = "</tr></table>";
        if (this.slideEffect == SlideEffect.fromLeft) {
            pp.pre = String.valueOf(pp.pre) + this.getSlider(ename) + "<td>";
            pp.post = "</td >" + pp.post;
        }
        else {
            pp.pre = String.valueOf(pp.pre) + "<td>";
            pp.post = "</td>" + this.getSlider(ename) + pp.post;
        }
    }
    
    private String getSlider(final String ename) {
        final String lr = (this.slideEffect == SlideEffect.fromLeft) ? "left" : "right";
        return "<td class=\"" + lr + "slider\" id=\"" + ename + "Slider\" style=\"height:" + this.height + "; vertical-align:middle;\" title=\"Slide\" onclick=\"P2.slidePanel(this, '" + ename + "', '" + lr + "');\">" + AbstractPanel.LEFT_ARROW + "<br />" + AbstractPanel.RIGHT_ARROW + "</td>";
    }
    
    abstract void panelToHtml(final StringBuilder p0, final PageGeneratorContext p1);
    
    abstract void panelToHtml5(final StringBuilder p0, final PageGeneratorContext p1);
    
    void setChildTableColumns(final StringBuilder js, final PageGeneratorContext pageContext) {
    }
    
    void toJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        if (this.tableName != null) {
            js.append("\n\n/* MetaData for Panel :").append(this.name).append(" with table name = ").append(this.tableName).append("*/");
            final String objectName = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1);
            String pName = this.name;
            if (this.repeatOnFieldName != null && this.repeatingPanelName != null) {
                pName = this.repeatingPanelName;
            }
            js.append('\n').append("ele").append(" = new PM.").append(objectName).append("();");
            js.append('\n').append("ele").append(".name = '").append(this.tableName).append("';");
            js.append('\n').append("ele").append(".panelName = '").append(pName).append("';");
            pageContext.setTableName(this.tableName, false);
            pageContext.setAttributes(this, js, AbstractPanel.ALL_META_ATTRIBUTES);
            pageContext.setTableSensitiveAttributes(this, js, AbstractPanel.ALL_TABLE_SENSITIVE_ATTRIBUTES);
            this.setChildTableColumns(js, pageContext);
            js.append("\nP2.addTable(").append("ele").append(");");
        }
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            final String j = this.getJsForTemplate(html, pageContext);
            if (j != null) {
                js.append(j);
            }
        }
        this.elementsToJs(js, pageContext);
        if (this.tableName != null) {
            pageContext.resetTableName();
        }
    }
    
    protected String getJsForTemplate(final String html, final PageGeneratorContext pageContext) {
        return null;
    }
    
    void elementsToJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        if (this.elements == null) {
            return;
        }
        AbstractElement[] elements;
        for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
            final AbstractElement ele = elements[i];
            if (!ele.markedAsComment) {
                if (ele instanceof AbstractField) {
                    final AbstractField f = (AbstractField)ele;
                    f.toJs(js, pageContext);
                }
                else if (ele instanceof AbstractPanel) {
                    final AbstractPanel p = (AbstractPanel)ele;
                    p.toJs(js, pageContext);
                }
                else if (ele instanceof ButtonElement) {
                    final ButtonElement be = (ButtonElement)ele;
                    be.toJs(js, pageContext);
                }
            }
        }
    }
    
    int parsePixels(final String txt) {
        int n = 0;
        final String textToBeParsed = txt.replace("px", "").replace("PX", "");
        try {
            n = Integer.parseInt(textToBeParsed);
            return n;
        }
        catch (Exception ex) {
            return 0;
        }
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.name == null || this.name.length() == 0) {
            final String newName = pageContext.getPanelName();
            Spit.out("It is now mandatory to provide name for all elements. I have assumed " + newName + " for a panel.");
            this.name = newName;
        }
        if (this.markedAsComment) {
            return;
        }
        if (this.keepItSimple) {
            this.toSimpleHtml(sbf, pageContext);
            return;
        }
        String topClass = "expandedfieldset";
        if (this.hidden) {
            topClass = "collapsedfieldset";
        }
        sbf.append("<div class=\"").append(topClass).append("\" id=\"").append(this.name).append("Top\" >");
        if (this.label != null && this.label.length() > 0) {
            if (this.isCollapsible) {
                sbf.append("<div id=\"").append(this.name).append("Twister\" class=\"").append(this.hidden ? "collapsed" : "expanded").append("Twister\" onclick=\"P2.twist(this, '").append(this.name).append("');\" >");
            }
            sbf.append("<div id=\"").append(this.name).append("Label\" class=\"panelLabel\" >").append(this.label).append("</div>");
            if (this.isCollapsible) {
                sbf.append("</div>");
            }
        }
        if (this.htmlFileName != null) {
            final String html = this.getFileContent(pageContext);
            sbf.append(this.formatHtml(html, pageContext));
        }
        else {
            this.panelToHtml5(sbf, pageContext);
        }
        sbf.append("</div>");
    }
    
    private void toSimpleHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"simplePanel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        if (this.onClickActionName != null) {
            sbf.append(" onclick=\"P2.act(this, '").append(this.name).append("','").append(this.onClickActionName).append("');\" ");
        }
        sbf.append("> ");
        if (this.elements != null && this.elements.length > 0) {
            AbstractElement[] elements;
            for (int length = (elements = this.elements).length, i = 0; i < length; ++i) {
                final AbstractElement ele = elements[i];
                if (ele == null) {
                    final String msg = "Panel " + this.name + " has an invalid element definition";
                    pageContext.reportError(msg);
                    sbf.append(msg);
                }
                else if (ele.inError) {
                    final String msg = "Element " + ele.name + " has invalid defintion within panel " + this.name;
                    pageContext.reportError(msg);
                    sbf.append(msg);
                }
                else if (!ele.markedAsComment) {
                    if (ele instanceof AbstractField) {
                        ((AbstractField)ele).toSimpleHtml(sbf, pageContext);
                    }
                    else {
                        ele.toHtml5(sbf, pageContext);
                    }
                }
            }
        }
        sbf.append(" </div>");
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.widthInPixels == 0 && this.slideEffect != SlideEffect.none) {
            final String msg = "ERROR: For sliding effect for panel " + this.name + " you MUST specify width in pixels";
            Spit.out(msg);
            this.inError = true;
        }
        if (this.isCollapsible && (this.label == null || this.label.length() == 0)) {
            if (this.name != null && this.name.length() > 0) {
                this.label = this.name;
            }
            else {
                this.label = "Label for below panel!!";
            }
        }
        if (this.elements != null && this.elements.length > 0) {
            final AbstractElement[] elements;
            final int length = (elements = this.elements).length;
            int i = 0;
            while (i < length) {
                final AbstractElement ele = elements[i];
                if (!ele.markedAsComment) {
                    if (ele.numberOfUnitsToUse == 0) {
                        ele.numberOfUnitsToUse = 1;
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    private String getFileContent(final PageGeneratorContext context) {
        String txt = null;
        if (this.htmlFileName == null) {
            txt = "templateFileName is not set for html template panel." + this.name;
            Spit.out(txt);
            context.reportError(txt);
        }
        else {
            txt = ResourceManager.readFile("page/" + this.htmlFileName);
            if (txt == null) {
                txt = String.valueOf(this.htmlFileName) + " could not be read. Ensure that this file exists inside your resource/page/ folder. Panel " + this.name + " is not loaded.";
                Spit.out(txt);
                context.reportError(txt);
            }
        }
        return txt;
    }
    
    protected String formatHtml(final String html, final PageGeneratorContext context) {
        return html;
    }
}
