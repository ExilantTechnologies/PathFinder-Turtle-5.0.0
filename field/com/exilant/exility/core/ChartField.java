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

class ChartField extends AbstractField
{
    String reportServiceId;
    String xaxiscolumn;
    String yaxiscolumn;
    boolean isMultiDataSet;
    String groupbycolumn;
    String xaxislabel;
    String yaxislabel;
    String minx;
    String miny;
    String maxx;
    String maxy;
    boolean showLegend;
    boolean noAutoLoad;
    String yaxislabelformatterid;
    String bubblecolumn;
    double bubbleradiusdenominator;
    String legendContainer;
    int legendNbrColumns;
    String legendLabelFormatter;
    String legendLabelBoxBorderColor;
    String legendPosition;
    int legendMargin;
    String legendBackgroundColor;
    String legendBackgroundOpacity;
    String rawDataDisplay;
    String colors;
    String helpTextColumn;
    String groupHelpTextColumn;
    String labelColor;
    boolean showFilledPoints;
    String onClickFunctionName;
    String minPercentToShowLabel;
    int marginLeft;
    int marginBottom;
    String onMoveFunctionName;
    boolean legendHighlight;
    int yLabelMaxWidth;
    String xLabelFormatterFunction;
    String yLabelFormatterFunction;
    
    ChartField() {
        this.reportServiceId = null;
        this.xaxiscolumn = null;
        this.yaxiscolumn = null;
        this.isMultiDataSet = false;
        this.groupbycolumn = null;
        this.xaxislabel = null;
        this.yaxislabel = null;
        this.minx = null;
        this.miny = null;
        this.maxx = null;
        this.maxy = null;
        this.showLegend = true;
        this.noAutoLoad = false;
        this.yaxislabelformatterid = null;
        this.bubblecolumn = null;
        this.bubbleradiusdenominator = 1.0;
        this.legendContainer = null;
        this.legendNbrColumns = 0;
        this.legendLabelFormatter = null;
        this.legendLabelBoxBorderColor = null;
        this.legendPosition = null;
        this.legendMargin = 0;
        this.legendBackgroundColor = null;
        this.legendBackgroundOpacity = null;
        this.rawDataDisplay = null;
        this.colors = null;
        this.helpTextColumn = null;
        this.groupHelpTextColumn = null;
        this.labelColor = null;
        this.showFilledPoints = false;
        this.onClickFunctionName = null;
        this.minPercentToShowLabel = null;
        this.marginLeft = 0;
        this.marginBottom = 0;
        this.onMoveFunctionName = null;
        this.legendHighlight = false;
        this.yLabelMaxWidth = 0;
        this.xLabelFormatterFunction = null;
        this.yLabelFormatterFunction = null;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("<div ");
        if (this.cssClassName == null) {
            sbf.append("class=\"chartfield\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        sbf.append(">");
        String lbl = this.getLabelToUse(pageContext);
        if (lbl == null) {
            lbl = "";
        }
        sbf.append("<div class=\"chartfieldlabelholder\" id=\"").append(this.name).append("LabelHolder\">");
        sbf.append("<span class=\"chartfieldlabel\" id=\"").append(this.name).append("Label\">").append(lbl).append("</span>");
        sbf.append("</div>");
        if (this.yaxislabel != null) {
            sbf.append("<div class=\"chartfieldyaxislabelholder\" id=\"").append(this.name).append("yaxisLabelHolder\">");
            sbf.append("<span class=\"chartfieldaxislabel\" id=\"").append(this.name).append("yaxisLabel\">");
            for (int i = 0; i < this.yaxislabel.length(); ++i) {
                sbf.append(this.yaxislabel.substring(i, i + 1)).append("<br/>");
            }
            sbf.append("</span>");
            sbf.append("</div>");
        }
        sbf.append("<div class=\"chartfieldcontainer\" id=\"").append(this.name).append("Container\">");
        sbf.append("</div>");
        if (this.xaxislabel != null) {
            sbf.append("<div class=\"chartfieldxaxislabelholder\" id=\"").append(this.name).append("xaxisLabelHolder\">");
            sbf.append("<span class=\"chartfieldaxislabel\" id=\"").append(this.name).append("xaxisLabel\">").append(this.xaxislabel).append("</span>");
            sbf.append("</div>");
        }
        sbf.append("</div>");
        if (this.legendContainer != null) {
            sbf.append("<div id=\"").append(this.legendContainer).append("\" class=\"chartlegendcontainer\"></div>");
        }
    }
}
