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

import java.util.ArrayList;
import java.util.Stack;
import java.util.List;

abstract class TablePanel extends DisplayPanel
{
    private static final int ONLY_TABLE = 0;
    private static final int LEFT_TABLE = 1;
    private static final int RIGHT_TABLE = 2;
    private static final int SCROLL_TABLE = 3;
    private static final String SPACE = "&nbsp;";
    private static final String SPACER_IMAGE = "<td><img src=\"../../exilityImages/space.gif\" style=\"width:1px; height:1px;\" /></td>";
    String tableType;
    String idFieldName;
    String childTableName;
    String childKeysTableName;
    String repeatingColumnName;
    String rowHtmlAttributes;
    String headerRowHtmlAttributes;
    boolean allColumnsAreSortable;
    boolean allColumnsAreFilterable;
    int rowHeight;
    int headerHeight;
    String freezeColumn;
    int leftPanelWidth;
    int rightPanelWidth;
    int frozenColumnIndex;
    private int rightPanelWidthStartIndex;
    boolean showHeader;
    boolean isFixedHeight;
    String treeViewColumnName;
    String treeViewKeyColumn;
    String treeViewParentKeyColumn;
    String treeViewHasChildColumn;
    boolean needFooter;
    int heightNumber;
    String firstFieldName;
    boolean addSeqNo;
    String headerGrouping;
    String[] headerLevels;
    int noOfHeaderLevels;
    boolean doNotShowTreeViewCheckBox;
    boolean doNotResize;
    int initialNumberOfRows;
    rowType simulateClickOnRow;
    boolean simulateClickOnFirstRow;
    String rowHelpText;
    boolean rowsCanBeMoved;
    boolean keepABlankRow;
    String mergeWithTableName;
    String stubNameForMerging;
    String quickSearchFieldName;
    String paginationLabel;
    boolean localPagination;
    String nestedTableName;
    String nestOnColumnName;
    String nestedTableColumnName;
    String mergeOnColumnName;
    boolean renderAsADisplayPanel;
    static final String BEGIN_TABLE = "begin-table-";
    static final String END_TABLE = "end-table-";
    static final String BEGIN_TOKEN = "{{";
    static final String END_TOKEN = "}}";
    private static final String BEGIN_NO_DATA = "begin-no-data";
    private static final String END_NO_DATA = "end-no-data";
    private static final String NO_DATA = "no-data";
    private static final int TYPE_INITIAL = 0;
    private static final int TYPE_BEGIN_NO_DATA = 1;
    private static final int TYPE_END_NO_DATA = 2;
    private static final int TYPE_BEGIN_TABLE = 3;
    private static final int TYPE_END_TABLE = 4;
    private static final int TYPE_COLUMN = 5;
    private static final String JS_NAME_FOR_FRAGMENTS = "htmlFragments";
    
    TablePanel() {
        this.tableType = null;
        this.idFieldName = null;
        this.childTableName = null;
        this.childKeysTableName = null;
        this.repeatingColumnName = null;
        this.rowHtmlAttributes = null;
        this.headerRowHtmlAttributes = null;
        this.allColumnsAreSortable = false;
        this.allColumnsAreFilterable = false;
        this.rowHeight = 0;
        this.headerHeight = 0;
        this.freezeColumn = null;
        this.leftPanelWidth = 0;
        this.rightPanelWidth = 0;
        this.frozenColumnIndex = 0;
        this.rightPanelWidthStartIndex = 0;
        this.showHeader = true;
        this.isFixedHeight = false;
        this.treeViewColumnName = null;
        this.treeViewKeyColumn = null;
        this.treeViewParentKeyColumn = null;
        this.treeViewHasChildColumn = null;
        this.needFooter = false;
        this.heightNumber = 0;
        this.firstFieldName = null;
        this.addSeqNo = false;
        this.headerGrouping = null;
        this.headerLevels = null;
        this.noOfHeaderLevels = 0;
        this.doNotShowTreeViewCheckBox = false;
        this.doNotResize = false;
        this.initialNumberOfRows = 0;
        this.simulateClickOnRow = rowType.none;
        this.simulateClickOnFirstRow = false;
        this.rowHelpText = null;
        this.rowsCanBeMoved = false;
        this.keepABlankRow = false;
        this.mergeWithTableName = null;
        this.stubNameForMerging = null;
        this.quickSearchFieldName = null;
        this.paginationLabel = "rows selected.";
        this.localPagination = false;
        this.nestedTableName = null;
        this.nestOnColumnName = null;
        this.nestedTableColumnName = null;
        this.mergeOnColumnName = null;
        this.renderAsADisplayPanel = false;
    }
    
    @Override
    boolean leaveHeightToMe() {
        return true;
    }
    
    @Override
    boolean leaveSlidingToMe() {
        return this.frozenColumnIndex > 0;
    }
    
    @Override
    void panelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.name.equals(this.tableName)) {
            final String err = "ERROR : Table name of a " + this.tableType + " Panel should be different from its name. Please change " + this.tableType + "  panel " + this.name + ".";
            Spit.out(err);
            pageContext.reportError(err);
            return;
        }
        if (this.elements == null || this.elements.length == 0) {
            final String err = "ERROR : Panel " + this.name + " has no child elements.";
            Spit.out(err);
            pageContext.reportError(err);
            return;
        }
        this.addBeforePanel(sbf, pageContext);
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"").append(this.tableType).append("panel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        sbf.append('>');
        final String layoutType = pageContext.getLayoutType();
        this.addBeforeTable(sbf, pageContext);
        if (layoutType != null && layoutType.equals("5")) {
            sbf.append("\n<div id=\"").append(this.tableName).append("div\" ");
            if (this.cssClassName == null) {
                sbf.append("class=\"").append(this.tableType).append("paneldiv\" ");
            }
            sbf.append('>');
        }
        if (this.renderAsADisplayPanel) {
            pageContext.setTableName(this.tableName, false);
            sbf.append("\n<div id=\"").append(this.tableName).append("\" class=\"tableTop\" >");
            this.addDataRow(sbf, pageContext, 0, this.elements.length, null);
            sbf.append("</div>");
        }
        else {
            pageContext.setTableName(this.tableName, true);
            if (this.frozenColumnIndex > 0) {
                this.generateFreezeColumnPanel(sbf, pageContext);
            }
            else if (this.isFixedHeight) {
                this.fixedHeightPanel(sbf, pageContext, 0, this.elements.length, 0);
            }
            else {
                this.expandingHeightPanel(sbf, pageContext, 0, this.elements.length, 0);
            }
        }
        pageContext.resetTableName();
        if (layoutType != null && layoutType.equals("5")) {
            sbf.append("\n</div>");
        }
        this.addAfterTable(sbf, pageContext);
        sbf.append("</div>");
        this.addAfterPanel(sbf, pageContext);
    }
    
    private void generateFreezeColumnPanel(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<table border=\"0\" id=\"").append(this.tableName).append("ContainerTable\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"overflow:hidden;\"><tr>");
        sbf.append("<td style=\"vertical-align:top;\">");
        int rightWidthNumber = this.widthInPixels - this.leftPanelWidth - 2;
        if (this.heightNumber > 0) {
            System.out.println("Going to generate fixed header scrolled-tables for " + this.name);
            this.fixedHeightPanel(sbf, pageContext, 0, this.frozenColumnIndex, 1);
            rightWidthNumber -= 15;
        }
        else {
            System.out.println("Going to generate expandable-tables for " + this.name);
            this.expandingHeightPanel(sbf, pageContext, 0, this.frozenColumnIndex, 1);
        }
        sbf.append("</td><td style=\"vertical-align:top;\">");
        sbf.append("<div id=\"").append(this.tableName).append("RightContainer\" style=\"overflow-x:scroll; overflow-y:hidden; width:");
        sbf.append(rightWidthNumber).append("px;\">");
        if (this.heightNumber > 0) {
            this.fixedHeightPanel(sbf, pageContext, this.frozenColumnIndex, this.elements.length, 2);
        }
        else {
            this.expandingHeightPanel(sbf, pageContext, this.frozenColumnIndex, this.elements.length, 2);
        }
        sbf.append("</div></td>");
        if (this.heightNumber > 0) {
            sbf.append("<td style=\"vertical-align:top;\">");
            this.fixedHeightPanel(sbf, pageContext, 0, 0, 3);
            sbf.append("</td>");
        }
        sbf.append("</tr></table>");
    }
    
    void addBeforePanel(final StringBuilder sbf, final PageGeneratorContext pageContext) {
    }
    
    void addBeforeTable(final StringBuilder sbf, final PageGeneratorContext pageContext) {
    }
    
    void addAfterTable(final StringBuilder sbf, final PageGeneratorContext pageContext) {
    }
    
    void addAfterPanel(final StringBuilder sbf, final PageGeneratorContext pageContext) {
    }
    
    protected void expandingHeightPanel(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex, final int forTable) {
        int widthToUse = 0;
        String tableNameToUse = this.tableName;
        if (forTable == 2) {
            tableNameToUse = String.valueOf(tableNameToUse) + "Right";
            widthToUse = this.rightPanelWidth;
        }
        else if (forTable == 1) {
            widthToUse = this.leftPanelWidth;
        }
        sbf.append("\n<table id=\"").append(tableNameToUse);
        sbf.append("\" class=\"").append(this.tableType).append("table\" ");
        this.addTableBorderEtc(sbf, pageContext.getLayoutType());
        if (widthToUse != 0) {
            sbf.append(" style=\" width:").append(widthToUse).append("px;\" ");
        }
        sbf.append(">");
        this.addTableHeader(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? tableNameToUse : null);
        this.addDataRow(sbf, pageContext, startIndex, endIndex, null);
        this.addFooterRow(sbf, pageContext, startIndex, endIndex);
        sbf.append("</table>");
    }
    
    private void addTableBorderEtc(final StringBuilder sbf, final String layoutType) {
        if (layoutType.equals("2") || layoutType.equals("5") || (layoutType.equals("3") && this.tableType.equals("grid"))) {
            sbf.append(" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" ");
        }
        else if (this.tableType.equals("grid")) {
            sbf.append(" border=\"1\" ");
        }
        else {
            sbf.append(" border=\"0\" ");
        }
    }
    
    protected void fixedHeightPanel(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex, final int forTable) {
        if (this.inError) {
            return;
        }
        String nameToUse = this.tableName;
        String widthToUse = this.width;
        String scrollToUse = "auto";
        boolean addOnScroll = false;
        if (forTable != 0) {
            if (forTable == 1) {
                widthToUse = String.valueOf(this.leftPanelWidth) + "px";
                scrollToUse = "hidden";
            }
            else if (forTable == 2) {
                nameToUse = String.valueOf(nameToUse) + "Right";
                widthToUse = String.valueOf(this.rightPanelWidth) + "px";
                scrollToUse = "hidden";
            }
            else if (forTable == 3) {
                nameToUse = String.valueOf(nameToUse) + "Scroll";
                widthToUse = "1px";
                scrollToUse = "scroll";
                addOnScroll = true;
            }
        }
        sbf.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" id=\"").append(nameToUse).append("\" class=\"").append(this.tableType).append("table\" ");
        if (widthToUse != null) {
            sbf.append("style=\"width:").append(widthToUse).append(";\" ");
        }
        sbf.append("><tr><td><table ");
        this.addTableBorderEtc(sbf, pageContext.getLayoutType());
        if (forTable != 0) {
            sbf.append(" style=\"width:").append(widthToUse).append("\" ");
        }
        sbf.append(" class=\"").append(this.tableType).append("tableheader\" ");
        final String tableNameToUse = String.valueOf(nameToUse) + "Header";
        sbf.append("id=\"").append(tableNameToUse).append("\" >");
        this.addTableHeader(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? tableNameToUse : null);
        sbf.append("</table></td></tr><tr><td><div id=\"").append(nameToUse);
        sbf.append("BodyWrapper\" style=\"height:").append(this.heightNumber - this.headerHeight);
        sbf.append("px; overflow-x: hidden; overflow-y:").append(scrollToUse).append("\" ");
        if (addOnScroll) {
            sbf.append(" onscroll=\"document.getElementById('").append(this.tableName).append("BodyWrapper').scrollTop = this.scrollTop; document.getElementById('").append(this.tableName).append("RightBodyWrapper').scrollTop = this.scrollTop;\" ");
        }
        sbf.append("><table ");
        this.addTableBorderEtc(sbf, pageContext.getLayoutType());
        sbf.append(" class=\"").append(this.tableType).append("tablebody\" ");
        if (widthToUse != null) {
            sbf.append("style=\"width:").append(widthToUse).append(";\" ");
        }
        final String tableId = String.valueOf(nameToUse) + "Body";
        sbf.append(" id=\"").append(tableId).append("\">");
        this.addDataRow(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? tableId : null);
        this.addFooterRow(sbf, pageContext, startIndex, endIndex);
        sbf.append("</table></div></td></tr></table>");
    }
    
    protected void expandingHeightFreezePanel(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex) {
        final String layoutType = pageContext.getLayoutType();
        sbf.append("<div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightWrapper\" ");
        }
        else {
            sbf.append("LeftWrapper\" ");
        }
        if (this.slideEffect.equals(SlideEffect.fromLeft) || startIndex > 0) {
            sbf.append("style=\"margin: 0px; overflow: hidden; position: static;\">");
        }
        else {
            sbf.append("style=\"margin: 0px;  position: static;\">");
        }
        sbf.append("<div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightContainer\" style=\"width:").append(this.rightPanelWidth).append("px; overflow-x:auto; overflow-y:hidden;\">");
        }
        else {
            sbf.append("LeftContainer\" style=\"position:relative; width:").append(this.leftPanelWidth).append("px;\">");
        }
        sbf.append("\n<table id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("Right\" width=\"").append(this.width).append("\"");
        }
        else {
            sbf.append("Left\"");
        }
        sbf.append("\" class=\"").append(this.tableType).append("table\" ");
        if (this.tableType.equals("list")) {
            if (layoutType.equals("2") || layoutType.equals("5")) {
                sbf.append(" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
            }
            else {
                sbf.append(" border=\"0\">");
            }
        }
        else if (layoutType != null && (layoutType.equals("2") || layoutType.equals("3") || layoutType.equals("5"))) {
            sbf.append(" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        }
        else {
            sbf.append(" border=\"1\">");
        }
        final String tableNameToUse = String.valueOf(this.tableName) + ((startIndex > 0) ? "RightHeader" : "LeftHeader");
        sbf.append("<tr><td><table border=\"1\" id=\"").append(tableNameToUse).append("\" >");
        this.addTableHeader(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? tableNameToUse : null);
        sbf.append("</table>");
        sbf.append("</td></tr><tr><td><div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightBodyWrapper\"").append("style=\"height:").append(this.heightNumber - this.headerHeight).append("px; overflow: hidden;\">");
        }
        else {
            sbf.append("LeftBodyWrapper\"").append("style=\"height:").append(this.heightNumber - this.headerHeight).append("px; overflow-x: hidden; overflow-y: auto;\" ").append("onscroll=\"document.getElementById('").append(this.tableName).append("RightBodyWrapper').scrollTop = this.scrollTop;\">");
        }
        final String thisTableName = String.valueOf(this.tableName) + ((startIndex > 0) ? "RightBody" : "LeftBody");
        sbf.append("<table border=\"1\" id=\"").append(thisTableName).append("\" >");
        this.addDataRow(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? thisTableName : null);
        this.addFooterRow(sbf, pageContext, startIndex, endIndex);
        sbf.append("</table></div></td></tr></table></div></div>");
    }
    
    protected void fixedHeightFreezePanel(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex) {
        if (this.headerHeight == 0) {
            final String msg = "ERROR: You have asked for height of " + this.height + " for list panel " + this.name + " with table name as " + this.tableName + ". To implement this properly, you need to specify headerHeight as well. headerHeight would be the height of the header row. You can get it based on teh css you have used. If you are not sure, start with 21 and then adjust absed on the behaviour of your rendered table";
            Spit.out(msg);
            pageContext.reportError(msg);
            return;
        }
        sbf.append("<div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightWrapper\" ");
        }
        else {
            sbf.append("LeftWrapper\" ");
        }
        if (this.slideEffect.equals(SlideEffect.fromLeft) || startIndex > 0) {
            sbf.append("style=\"margin: 0px; overflow: hidden;  position: static;\">");
        }
        else {
            sbf.append("style=\"margin: 0px; position: static;\">");
        }
        sbf.append("<div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightContainer\" style=\"width:").append(this.rightPanelWidth).append("; \">");
        }
        else {
            sbf.append("LeftContainer\" style=\"position:relative; width:").append(this.leftPanelWidth).append(";\">");
        }
        sbf.append("<table border=\"1\" id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("Right\" width=\"").append(this.width).append("\">");
        }
        else {
            sbf.append("Left\">");
        }
        final String tableNameToUse = String.valueOf(this.tableName) + ((startIndex > 0) ? "RightHeader" : "LeftHeader");
        sbf.append("<tr><td><table border=\"1\" id=\"").append(tableNameToUse).append("\" >");
        this.addTableHeader(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? tableNameToUse : null);
        sbf.append("</table>");
        sbf.append("</td></tr><tr><td><div id=\"").append(this.tableName);
        if (startIndex > 0) {
            sbf.append("RightBodyWrapper\"").append("style=\"height:").append(this.heightNumber - this.headerHeight).append("px; overflow: hidden;\">");
        }
        else {
            sbf.append("LeftBodyWrapper\"").append("style=\"height:").append(this.heightNumber - this.headerHeight).append("px; overflow-x: hidden; overflow-y: auto;\" ").append("onscroll=\"document.getElementById('").append(this.tableName).append("RightBodyWrapper').scrollTop = this.scrollTop;\">");
        }
        final String thisTableName = String.valueOf(this.tableName) + ((startIndex > 0) ? "RightBody" : "LeftBody");
        sbf.append("<table border=\"1\" id=\"").append(thisTableName).append("\" >");
        this.addDataRow(sbf, pageContext, startIndex, endIndex, AP.generateColTags ? thisTableName : null);
        this.addFooterRow(sbf, pageContext, startIndex, endIndex);
        sbf.append("</table></div></td></tr></table></div></div>");
    }
    
    protected void addTableHeader(final StringBuilder sbf, final PageGeneratorContext pc, final int startIndex, final int endIndex, final String colTagPrefix) {
        String headerStyle = "";
        String cellStyle = "";
        if (this.headerHeight > 0) {
            if (this.noOfHeaderLevels > 0) {
                headerStyle = String.valueOf(headerStyle) + "height: " + this.headerHeight / (this.noOfHeaderLevels + 1) + "px; ";
            }
            else {
                headerStyle = String.valueOf(headerStyle) + "height: " + this.headerHeight + "px; ";
            }
        }
        if (!this.showHeader) {
            headerStyle = String.valueOf(headerStyle) + "display:none; ";
        }
        if (headerStyle.length() > 0) {
            headerStyle = "style=\"" + headerStyle + "\" ";
        }
        if (endIndex == 0) {
            int n = this.noOfHeaderLevels;
            sbf.append("<thead><tr ").append(headerStyle).append('>').append("<td><img src=\"../../exilityImages/space.gif\" style=\"width:1px; height:1px;\" /></td>").append("</tr>");
            while (n > 1) {
                sbf.append("<tr ").append(headerStyle).append(">").append("<td><img src=\"../../exilityImages/space.gif\" style=\"width:1px; height:1px;\" /></td>").append("<th>.</th></tr>");
                --n;
            }
            sbf.append("</thead>");
            return;
        }
        final StringBuilder hdrSbf = new StringBuilder("<thead>");
        final StringBuilder colSbf = new StringBuilder();
        final String colTag = "<col id=\"" + colTagPrefix + "Col";
        int nbrTdsAdded = 0;
        hdrSbf.append("<tr ").append(headerStyle).append('>');
        if (startIndex == 0) {
            if (this.addSeqNo) {
                final String styleWidth = "style=\"width:30px\" ";
                hdrSbf.append("<th ").append(styleWidth);
                if (this.noOfHeaderLevels > 0) {
                    hdrSbf.append(" rowspan=\"").append(this.noOfHeaderLevels + 1).append("\" ");
                }
                hdrSbf.append(">S No</th>");
                ++nbrTdsAdded;
                colSbf.append(colTag).append(nbrTdsAdded).append("\" ").append(styleWidth).append(" />");
            }
            if (this.rowsCanBeDeleted && !AP.showDeleteOptionAtEnd && startIndex == 0) {
                this.addHeaderCheckBox(hdrSbf, pc);
                ++nbrTdsAdded;
                colSbf.append(colTag).append(nbrTdsAdded).append("\" />");
            }
            if (this.rowsCanBeMoved) {
                hdrSbf.append("<th id=\"").append(this.name).append("MoveRowLabel\" class=\"cssField\" ");
                if (this.noOfHeaderLevels > 0) {
                    hdrSbf.append(" rowspan=\"").append(this.noOfHeaderLevels + 1).append("\" ");
                }
                hdrSbf.append("></th>");
                ++nbrTdsAdded;
                colSbf.append(colTag).append(nbrTdsAdded).append("\" />");
            }
        }
        if (this.noOfHeaderLevels > 0) {
            for (int i = 0; i < this.noOfHeaderLevels; ++i) {
                final String currentLevel = this.headerLevels[i];
                final String[] headers = currentLevel.split(",");
                int curIndex = 0;
                String[] array;
                for (int length = (array = headers).length, k = 0; k < length; ++k) {
                    final String currentHeader = array[k];
                    if (curIndex < endIndex) {
                        final String[] currentHeaderDetails = currentHeader.split(":");
                        curIndex += Integer.parseInt(currentHeaderDetails[0]);
                        if (curIndex > startIndex && curIndex <= endIndex) {
                            ++nbrTdsAdded;
                            hdrSbf.append("<th colspan=\"").append(currentHeaderDetails[0]).append("\">");
                            hdrSbf.append(currentHeaderDetails[1]);
                            hdrSbf.append("</th>");
                        }
                    }
                }
                hdrSbf.append("</tr><tr ").append(headerStyle).append(">");
            }
        }
        int widthIndex = 0;
        int totalWidths = 0;
        if (this.columnWidths != null) {
            totalWidths = this.columnWidths.length;
            if (startIndex != 0) {
                widthIndex = this.rightPanelWidthStartIndex;
            }
        }
        for (int j = startIndex; j < endIndex; ++j) {
            final AbstractElement ele = this.elements[j];
            if (ele == null) {
                Spit.out("Surprising that I have a null element at " + j + " for " + this.name);
            }
            else if (!ele.markedAsComment) {
                if (ele instanceof AbstractPanel) {
                    final String msg = "ERROR: " + this.name + " is a " + this.tableType + " panel. It has another panel by name " + ele.name + " as its child. This is not valid.";
                    Spit.out(msg);
                    pc.reportError(msg);
                }
                else {
                    final String fullName = String.valueOf(this.tableName) + '_' + ele.name;
                    if (ele.inError) {
                        pc.reportError("");
                    }
                    else if (ele.numberOfUnitsToUse != 0) {
                        hdrSbf.append("<th align=\"").append(this.alinementOf(ele)).append("\" id=\"");
                        hdrSbf.append(fullName).append("Label\" ");
                        cellStyle = "";
                        String title = null;
                        if (this.allColumnsAreSortable || ele.isSortable) {
                            title = "Sort";
                        }
                        if (this.allColumnsAreFilterable || ele.isFilterable) {
                            title = ((title == null) ? "Filter" : (String.valueOf(title) + " / Filter"));
                        }
                        if (widthIndex < totalWidths) {
                            cellStyle = "style=\"width:" + this.columnWidths[widthIndex] + "\" ";
                            ++widthIndex;
                            hdrSbf.append(cellStyle);
                        }
                        final String layoutType = pc.getLayoutType();
                        if (layoutType != null && (layoutType.equals("5") || layoutType.equals("css")) && (this.allColumnsAreSortable || ele.isSortable || this.allColumnsAreFilterable || ele.isFilterable)) {
                            hdrSbf.append(" onclick=\"P2.tableHeaderClicked(event, '");
                            hdrSbf.append(this.tableName);
                            hdrSbf.append("','");
                            hdrSbf.append(ele.name);
                            hdrSbf.append("');\" ");
                        }
                        hdrSbf.append('>');
                        if (this.treeViewColumnName != null && this.treeViewColumnName == ele.name && !this.doNotShowTreeViewCheckBox) {
                            hdrSbf.append("<input type=\"checkbox\" title=\"Select/Deselect\" style=\"cursor:pointer;\" id=\"");
                            hdrSbf.append(this.tableName).append("BulkTreeCheck\"");
                            hdrSbf.append(" onclick=\"P2.treeViewBulkSelectDeselectRow(this, '").append(this.tableName).append("', '").append(ele.name).append("');\" />");
                        }
                        hdrSbf.append("<div>");
                        String lbl = ele.label;
                        if (ele instanceof AbstractField) {
                            final AbstractField field = (AbstractField)ele;
                            lbl = field.getLabelToUse(pc);
                            if (field.bulkCheck) {
                                this.addBulkCheckBoxInHeader(hdrSbf, pc, field.name);
                            }
                            hdrSbf.append("&nbsp;");
                        }
                        if (lbl == null || lbl.length() == 0 || lbl.equals(" ")) {
                            hdrSbf.append("&nbsp;");
                        }
                        else {
                            hdrSbf.append(lbl);
                        }
                        if (AP.showRequiredLabelinGrid && ele instanceof AbstractField && ((AbstractField)ele).isRequired) {
                            hdrSbf.append("<span class=\"requiredstar\">*</span>");
                        }
                        if (this.allColumnsAreSortable || ele.isSortable) {
                            hdrSbf.append("<img title=\"Sort Ascending\" data-exilImageType =\"sort\" id=\"").append(fullName).append("SortImage\" src=\"../../exilityImages/sortable.gif\" style=\"border:0; cursor:pointer;\"");
                            hdrSbf.append("/>");
                        }
                        if (this.allColumnsAreFilterable || ele.isFilterable) {
                            hdrSbf.append("<img title=\"Filter\" data-exilImageType =\"filter\" id=\"").append(fullName).append("FilterImage\" src=\"../../exilityImages/filterable.gif\" style=\"border:0; cursor:pointer;\"");
                            hdrSbf.append("/>");
                        }
                        hdrSbf.append("</div>");
                        hdrSbf.append("</th>");
                        ++nbrTdsAdded;
                        colSbf.append(colTag).append(nbrTdsAdded).append("\" ").append(cellStyle).append(" />");
                    }
                }
            }
        }
        if (this.rowsCanBeDeleted && AP.showDeleteOptionAtEnd && (this.frozenColumnIndex == 0 || startIndex > 0)) {
            this.addHeaderCheckBox(hdrSbf, pc);
            ++nbrTdsAdded;
            colSbf.append(colTag).append(nbrTdsAdded).append("\" />");
        }
        hdrSbf.append("</tr></thead>");
        if (colTagPrefix != null) {
            sbf.append((CharSequence)colSbf);
        }
        sbf.append(hdrSbf.toString());
    }
    
    protected void addHeaderCheckBox(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.noOfHeaderLevels > 0) {
            sbf.append("<th rowspan=\"").append(this.noOfHeaderLevels + 1).append("\">");
        }
        else {
            sbf.append("<th>");
        }
        final String layoutType = pageContext.getLayoutType();
        if (layoutType.equals("5") && AP.showIamgeForDeleteOption && this.labelForBulkDeleteCheckBox != null) {
            sbf.append(this.labelForBulkDeleteCheckBox);
        }
        else {
            if (this.labelForBulkDeleteCheckBox != null) {
                sbf.append(this.labelForBulkDeleteCheckBox).append("<br />");
            }
            if (!layoutType.equals("5")) {
                if (AP.showIamgeForDeleteOption) {
                    sbf.append("<img class=\"deleteimg\" src=\"").append(PageGeneratorContext.imageFolderName).append("deleteRow.gif\" border=\"0\" ");
                }
                else {
                    sbf.append("<input type=\"checkbox\" ");
                }
                sbf.append("style=\"cursor:pointer;\" id=\"");
                sbf.append(this.tableName).append("Delete").append("BulkCheck\" ");
                sbf.append("onclick=\"P2.bulkDeleteClicked(this, '").append(this.tableName).append("');\" />");
            }
        }
        sbf.append("</th>");
    }
    
    protected String alinementOf(final AbstractElement ele) {
        if (ele.align != null) {
            return ele.align;
        }
        if (ele instanceof OutputField) {
            final OutputField field = (OutputField)ele;
            final DataValueType vt = field.getValueType();
            if (vt == DataValueType.INTEGRAL || vt == DataValueType.DECIMAL) {
                return "right";
            }
        }
        return "left";
    }
    
    protected void addFooterRow(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex) {
        if (!this.needFooter) {
            return;
        }
        if (endIndex == 0) {
            sbf.append("<tfoot><tr>").append("<td><img src=\"../../exilityImages/space.gif\" style=\"width:1px; height:1px;\" /></td>").append("</tr></tfoot>");
            return;
        }
        sbf.append("<tfoot id=\"").append(this.tableName).append("Footer\"><tr>");
        if (startIndex == 0) {
            if (this.addSeqNo) {
                sbf.append("<td style=\"width:30px\">&nbsp;</td>");
            }
            if (this.rowsCanBeDeleted && !AP.showDeleteOptionAtEnd && startIndex == 0) {
                sbf.append("<td style=\"width:30px\">&nbsp;</td>");
            }
        }
        int totalWidths = 0;
        int widthIndex = 0;
        if (this.columnWidths != null) {
            totalWidths = this.columnWidths.length;
            if (startIndex != 0) {
                widthIndex = this.rightPanelWidthStartIndex;
            }
        }
        for (int i = startIndex; i < endIndex; ++i) {
            final AbstractElement ele = this.elements[i];
            if (!ele.markedAsComment) {
                if (ele.numberOfUnitsToUse > 0 || i == startIndex) {
                    if (i != 0) {
                        sbf.append("</td>");
                    }
                    if (this.isFixedHeight) {
                        String style = "";
                        if (widthIndex < totalWidths) {
                            style = "style=\"width:" + this.columnWidths[widthIndex] + ";";
                            ++widthIndex;
                        }
                        sbf.append("<td id=\"").append(this.tableName).append('_').append(ele.name).append("Footer\" ").append("class=\"").append(this.columnSumCssClassName).append("\"").append(style).append("\">");
                    }
                    else {
                        sbf.append("<td id=\"").append(this.tableName).append('_').append(ele.name).append("Footer\" ").append("class=\"").append(this.columnSumCssClassName).append("\" >");
                    }
                }
                if (ele.footerLabel != null) {
                    sbf.append(ele.footerLabel);
                }
                else {
                    sbf.append("&nbsp;");
                }
            }
        }
        sbf.append("</td>");
        if (this.rowsCanBeDeleted && AP.showDeleteOptionAtEnd && (this.frozenColumnIndex == 0 || startIndex > 0)) {
            sbf.append("<td>&nbsp;</td>");
        }
        sbf.append("</tr></tfoot>");
    }
    
    protected void addDataRow(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex, final String colTagPrefix) {
        final String nestAttr = (this.nestedTableName == null) ? "" : "exil-nestedTable=\"true\" ";
        final StringBuilder bodySbf = new StringBuilder();
        if (this.renderAsADisplayPanel) {
            bodySbf.append("<div ").append(nestAttr).append("><div ");
        }
        else {
            bodySbf.append("<tbody ").append(nestAttr).append("><tr ");
        }
        bodySbf.append("onclick=\"P2.listClicked(this, '").append(this.tableName).append("', event);\" ");
        if (this.rowHelpText != null) {
            bodySbf.append("title=\"").append(this.rowHelpText).append("\" ");
        }
        this.addSubClassSpecificDataRowAttributes(bodySbf, pageContext);
        bodySbf.append('>');
        String colTags = "";
        if (endIndex == 0) {
            bodySbf.append("<td><img src=\"../../exilityImages/space.gif\" style=\"width:1px; height:1px;\" /></td>");
        }
        else if (this.renderAsADisplayPanel) {
            if (pageContext.useHtml5) {
                this.elementsToHtml5(bodySbf, pageContext, false);
            }
            else {
                this.elementsToHtml(bodySbf, pageContext, false);
            }
        }
        else {
            colTags = this.addDataRowCells(bodySbf, pageContext, startIndex, endIndex, colTagPrefix);
        }
        if (this.renderAsADisplayPanel) {
            bodySbf.append("</div></div>");
        }
        else {
            bodySbf.append("</tr></tbody>");
        }
        if (colTagPrefix != null) {
            sbf.append(colTags);
        }
        sbf.append(bodySbf.toString());
    }
    
    protected void addSubClassSpecificDataRowAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
    }
    
    protected String addDataRowCells(final StringBuilder sbf, final PageGeneratorContext pageContext, final int startIndex, final int endIndex, final String colTagPrefix) {
        int nbrAdded = 0;
        final StringBuilder colSbf = new StringBuilder();
        final String colTag = "<col id=\"" + colTagPrefix + "Col";
        if (startIndex == 0) {
            if (this.addSeqNo) {
                sbf.append("<td><span id=\"").append(this.tableName).append("SeqNo").append("\" ></span></td>");
                ++nbrAdded;
                colSbf.append(colTag).append(nbrAdded).append("\" style=\"width:30px\" />");
            }
            if (this.rowsCanBeDeleted && !AP.showDeleteOptionAtEnd && startIndex == 0) {
                this.addDeleteCheckBox(sbf, pageContext, true);
                ++nbrAdded;
                colSbf.append(colTag).append(nbrAdded).append("\" />");
            }
            if (this.rowsCanBeMoved) {
                sbf.append("<td><div class=\"rowUp\" onclick=\"P2.tableRowUp('").append(this.name).append("');\" id=\"").append(this.name).append("RowUp\" ></div></td>");
                ++nbrAdded;
                colSbf.append(colTag).append(nbrAdded).append("\" />");
            }
        }
        int totalWidths = 0;
        int widthIndex = 0;
        if (this.columnWidths != null) {
            totalWidths = this.columnWidths.length;
            if (startIndex != 0) {
                widthIndex = this.rightPanelWidthStartIndex;
            }
        }
        for (int i = startIndex; i < endIndex; ++i) {
            final AbstractElement ele = this.elements[i];
            if (!ele.markedAsComment) {
                if (ele.numberOfUnitsToUse != 0 || i == startIndex) {
                    if (i != startIndex) {
                        sbf.append("</td>");
                    }
                    String style = "";
                    if (this.isFixedHeight) {
                        if (widthIndex < totalWidths) {
                            style = "style=\"width:" + this.columnWidths[widthIndex] + "\";";
                            ++widthIndex;
                        }
                        sbf.append("<td align=\"").append(this.alinementOf(ele));
                        sbf.append("\"").append(style).append("\">");
                    }
                    else {
                        sbf.append("<td align=\"").append(this.alinementOf(ele)).append("\">");
                    }
                    ++nbrAdded;
                    colSbf.append(colTag).append(nbrAdded).append("\" ").append(style).append("/>");
                }
                if (this.treeViewColumnName != null && this.treeViewColumnName.equals(ele.name)) {
                    sbf.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
                    sbf.append("<td style=\"border:0px\" >");
                    sbf.append("<img id=\"").append(ele.name).append("Indent\"").append(" src=\"../../images/spacer.gif\" style=\"vertical-align:middle;width:1px;height:16px\" />");
                    sbf.append("</td><td style=\"border:0px\" >");
                    sbf.append("<img id=\"").append(ele.name).append("PlusMinus\"").append("src=\"../../images/minus.gif\" style=\"cursor:pointer; vertical-align:middle;width:19px;height:16px\"");
                    sbf.append(" onclick=\"P2.treeViewExpandCollapseRow(this, '").append(this.tableName).append("', '").append(ele.name).append("');\" />");
                    sbf.append("</td><td style=\"border:0px\" >");
                    if (this.doNotShowTreeViewCheckBox) {
                        sbf.append("&nbsp;");
                    }
                    else {
                        sbf.append("<input type=\"checkbox\" title=\"Select/Deselect\" style=\"cursor:pointer;\" id=\"");
                        sbf.append(ele.name).append("Select\"");
                        sbf.append(" onclick=\"P2.treeViewSelectDeselectRow(this, '").append(this.tableName).append("', '").append(ele.name).append("');\" />");
                    }
                    sbf.append("</td><td style=\"border:0px\" >");
                }
                if (ele instanceof CheckBoxField && pageContext.getLayoutType().equals("5")) {
                    ele.label = "";
                }
                if (ele instanceof AbstractField) {
                    if (pageContext.useHtml5) {
                        ((AbstractField)ele).fieldToHtml5(sbf, pageContext);
                    }
                    else {
                        ((AbstractField)ele).fieldToHtml(sbf, pageContext);
                    }
                }
                else if (pageContext.useHtml5) {
                    ele.toHtml5(sbf, pageContext);
                }
                else {
                    ele.toHtml(sbf, pageContext);
                }
                if (this.treeViewColumnName != null && this.treeViewColumnName.equals(ele.name)) {
                    sbf.append("</td></tr></table>");
                }
            }
        }
        sbf.append("</td>");
        if (this.rowsCanBeDeleted && AP.showDeleteOptionAtEnd && (this.frozenColumnIndex == 0 || startIndex > 0)) {
            this.addDeleteCheckBox(sbf, pageContext, true);
            ++nbrAdded;
            colSbf.append(colTag).append(nbrAdded).append("\" />");
        }
        return colSbf.toString();
    }
    
    private void addBulkCheckBoxInHeader(final StringBuilder sbf, final PageGeneratorContext pageContext, final String fieldName) {
        sbf.append("\n<input type=\"checkbox\" id=\"").append(this.tableName).append('_').append(fieldName).append("Header\" onclick=\"P2.bulkCheckAction(this, '").append(fieldName).append("', '").append(this.tableName).append("');\" />");
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.hoverText != null) {
            this.rowHelpText = this.hoverText;
            this.hoverText = null;
        }
        if (this.elements == null) {
            return;
        }
        if (this.height != null) {
            this.heightNumber = this.parsePixels(this.height);
            if (this.heightNumber <= 0) {
                Spit.out("Height of a list/grid panel should be expressed as an integer. It is always assumed to be in pixels. Please change it. Va value of 400 is assumed for this trial.");
                this.heightNumber = 400;
            }
        }
        int firstFieldIndex = -1;
        int lastFieldIndex = -1;
        boolean firstFieldFound = false;
        boolean foundFieldWithRowAverage = false;
        boolean foundFieldWithRowSum = false;
        boolean foundRowAverageField = false;
        boolean foundRowSumField = false;
        int wi = 0;
        for (int i = 0; i < this.elements.length; ++i) {
            final AbstractElement e = this.elements[i];
            if (!e.markedAsComment) {
                if (e.numberOfUnitsToUse != 0) {
                    ++wi;
                }
                if (e instanceof AbstractField) {
                    final AbstractField f = (AbstractField)e;
                    if (f.columnAverage || f.columnSum || f.footerLabel != null) {
                        this.needFooter = true;
                    }
                    if (f.rowAverage) {
                        foundFieldWithRowAverage = true;
                    }
                    if (f.rowSum) {
                        foundFieldWithRowSum = true;
                    }
                    if (e.name.equals("rowAverage")) {
                        foundRowAverageField = true;
                    }
                    if (e.name.equals("rowSum")) {
                        foundRowSumField = true;
                    }
                    else if (f instanceof AbstractInputField && !(f instanceof HiddenField)) {
                        lastFieldIndex = i;
                        if (!firstFieldFound) {
                            firstFieldIndex = i;
                            firstFieldFound = true;
                        }
                    }
                }
                if (this.freezeColumn != null && this.freezeColumn.equals(e.name)) {
                    this.frozenColumnIndex = i + 1;
                    this.rightPanelWidthStartIndex = wi;
                }
            }
        }
        if (firstFieldIndex != -1) {
            this.firstFieldName = this.elements[firstFieldIndex].name;
        }
        if (lastFieldIndex != -1) {
            ((AbstractInputField)this.elements[lastFieldIndex]).setLastField();
        }
        if (foundFieldWithRowSum && !foundRowSumField) {
            final String err = "ERROR: " + this.name + " uses row sum, but has not defined a field with name = rowSum";
            Spit.out(err);
            this.inError = true;
        }
        if (foundFieldWithRowAverage && !foundRowAverageField) {
            final String err = "ERROR: " + this.name + " uses row average, but has not defined a field with name = rowAverage";
            Spit.out(err);
            this.inError = true;
        }
        if (this.height != null && (this.rowHeight == 0 || this.headerHeight == 0)) {
            final String err = "Error: " + this.name + " specifies height as " + this.height + ". for fixed header table to work, you shoudl provide both headerHeight and rowHeight in pixles.";
            Spit.out(err);
            this.inError = true;
        }
        if (this.freezeColumn != null) {
            this.doNotResize = true;
            if (this.frozenColumnIndex == 0 || this.leftPanelWidth == 0 || this.rightPanelWidth == 0 || this.widthInPixels == 0) {
                final String err = "ERROR: " + this.name + " specifies " + this.freezeColumn + " to be frozen. For this feature to work, that column name shoudl exist, and you shoudl specify values for width, leftPanelWidth and rightPanelWidth in pixels;";
                Spit.out(err);
                this.inError = true;
            }
        }
        if (this.headerGrouping != null) {
            this.headerLevels = this.headerGrouping.split(";");
            this.noOfHeaderLevels = this.headerLevels.length;
        }
    }
    
    @Override
    void panelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.name.equals(this.tableName)) {
            final String err = "ERROR : Table name of a " + this.tableType + " Panel should be different from its name. Please change " + this.tableType + "  panel " + this.name + ".";
            Spit.out(err);
            pageContext.reportError(err);
            return;
        }
        if (this.elements == null || this.elements.length == 0) {
            final String err = "ERROR : Panel " + this.name + " has no child elements.";
            Spit.out(err);
            pageContext.reportError(err);
            return;
        }
        this.addBeforePanel(sbf, pageContext);
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"").append(this.tableType).append("Panel\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        sbf.append('>');
        this.addBeforeTable(sbf, pageContext);
        if (this.renderAsADisplayPanel) {
            pageContext.setTableName(this.tableName, false);
            sbf.append("\n<div id=\"").append(this.tableName).append("\" class=\"tableTop\" >");
            this.addDataRow(sbf, pageContext, 0, this.elements.length, null);
            sbf.append("</div>");
        }
        else {
            pageContext.setTableName(this.tableName, true);
            if (this.frozenColumnIndex > 0) {
                this.generateFreezeColumnPanel(sbf, pageContext);
            }
            else if (this.isFixedHeight) {
                this.fixedHeightPanel(sbf, pageContext, 0, this.elements.length, 0);
            }
            else {
                this.expandingHeightPanel(sbf, pageContext, 0, this.elements.length, 0);
            }
        }
        pageContext.resetTableName();
        this.addAfterTable(sbf, pageContext);
        sbf.append("</div>");
        this.addAfterPanel(sbf, pageContext);
    }
    
    @Override
    protected String formatHtml(final String html, final PageGeneratorContext context) {
        final List<FragmentToken> tokens = this.tokenize(html, context);
        if (tokens == null) {
            return "Invalid template provided for this panel";
        }
        if (tokens.size() == 1) {
            return html;
        }
        final StringBuilder sbf = new StringBuilder("<div id=\"");
        sbf.append(this.tableName).append("\">\n");
        final FragmentToken firstToken = tokens.get(0);
        final FragmentToken secondToken = tokens.get(1);
        if (secondToken.type == 1) {
            if (tokens.size() < 3 || tokens.get(2).type != 2) {
                final String err = "Missing or misplaced closing tag end-no-data in the template file";
                Spit.out(err);
                context.reportError(err);
                return err;
            }
            if (firstToken.fragment.length() > 0) {
                Spit.out("Initial text before begin-no-data will be ignored in panel " + this.name);
            }
            sbf.append(secondToken.fragment);
        }
        else {
            for (int i = 2; i < tokens.size(); ++i) {
                if (tokens.get(i).type == 1) {
                    final String err2 = "begin-no-data is found after other tags. This is invalid. It can only be out at the beginning of the template";
                    Spit.out(err2);
                    context.reportError(err2);
                    return err2;
                }
            }
        }
        sbf.append("\n</div>");
        return sbf.toString();
    }
    
    @Override
    protected String getJsForTemplate(final String html, final PageGeneratorContext context) {
        final List<FragmentToken> tokens = this.tokenize(html, context);
        if (tokens == null) {
            final String err = "Invalid template provided for this panel";
            Spit.out(err);
            context.reportError(err);
            return null;
        }
        if (tokens.size() == 1) {
            return null;
        }
        final Stack<String> tables = new Stack<String>();
        final StringBuilder sbf = new StringBuilder(10);
        sbf.append("ele").append('.').append("htmlFragments").append(" = [");
        boolean noDataSectionFound = false;
        for (int i = 0; i < tokens.size(); ++i) {
            final FragmentToken token = tokens.get(i);
            switch (token.type) {
                case 1: {
                    if (i != 1) {
                        final String err2 = "begin-no-data is to be right at the beginning of teh template";
                        Spit.out(err2);
                        context.reportError(err2);
                        return null;
                    }
                    noDataSectionFound = true;
                    break;
                }
                case 2: {
                    if (i != 2) {
                        final String err2 = "end-no-data does not match its opening tab begin-no-data";
                        Spit.out(err2);
                        context.reportError(err2);
                        return null;
                    }
                    noDataSectionFound = false;
                    break;
                }
                case 3: {
                    if (token.token == null || token.token.length() == 0) {
                        final String err2 = "Invalid table name for begin-table- token.";
                        Spit.out(err2);
                        context.reportError(err2);
                        return null;
                    }
                    tables.push(token.token);
                    break;
                }
                case 4: {
                    if (tables.size() == 0) {
                        final String err2 = "end-table-" + token.token + " found with no corresponding " + "end-table-" + token.token;
                        Spit.out(err2);
                        context.reportError(err2);
                        return null;
                    }
                    final String tblName = tables.pop();
                    if (!tblName.equals(token.token)) {
                        final String err3 = "end-table-" + token.token + " found as the closing tag for " + "begin-table-" + tblName + ". Mismatched table tags";
                        Spit.out(err3);
                        context.reportError(err3);
                        return null;
                    }
                    break;
                }
            }
            if (i > 0) {
                sbf.append("\n,");
            }
            String htmlText = token.fragment.replace('\n', ' ');
            htmlText = htmlText.replace("'", "\\'");
            htmlText = htmlText.replace("\r", "");
            sbf.append('[').append(token.type).append(",'").append(token.token).append("','").append(htmlText).append("']");
        }
        if (tables.size() > 0) {
            final String err4 = "begin-table-" + tables.pop() + " found with no corresponding " + "end-table-";
            Spit.out(err4);
            context.reportError(err4);
            return null;
        }
        if (noDataSectionFound) {
            final String err4 = "begin-no-data does not have a matching close tab end-no-data";
            Spit.out(err4);
            context.reportError(err4);
            return null;
        }
        sbf.append("];\n");
        return sbf.toString();
    }
    
    private List<FragmentToken> tokenize(final String text, final PageGeneratorContext context) {
        final List<FragmentToken> tokens = new ArrayList<FragmentToken>();
        int posn = text.indexOf("{{");
        if (posn == -1) {
            posn = text.length();
        }
        tokens.add(new FragmentToken(0, null, text.substring(0, posn)));
        while (posn < text.length()) {
            int startAt = posn;
            posn = text.indexOf("}}", posn);
            if (posn == -1) {
                final String err = "Missing closing marker }}";
                Spit.out(err);
                context.reportError(err);
                return null;
            }
            String token = text.substring(startAt + "{{".length(), posn);
            token = token.trim();
            int tokenType = 5;
            if (token.startsWith("begin-table-")) {
                token = token.substring("begin-table-".length());
                tokenType = 3;
            }
            else if (token.startsWith("end-table-")) {
                token = token.substring("end-table-".length());
                tokenType = 4;
            }
            else if (token.startsWith("begin-no-data")) {
                token = "no-data";
                tokenType = 1;
            }
            else if (token.startsWith("end-no-data")) {
                token = "no-data";
                tokenType = 2;
            }
            startAt = posn + "}}".length();
            posn = text.indexOf("{{", startAt);
            if (posn == -1) {
                posn = text.length();
            }
            final String fragment = text.substring(startAt, posn);
            tokens.add(new FragmentToken(tokenType, token, fragment));
        }
        return tokens;
    }
}
