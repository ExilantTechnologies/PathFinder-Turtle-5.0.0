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

abstract class AbstractField extends AbstractElement
{
    static final String[] ALL_META_ATTRIBUTES;
    static final String[] ALL_TABLE_SENSITIVE_ATTRIBUTES;
    static final String[] ALL_TABLE_SENSITIVE_ARRAYS;
    LabelPosition labelPosition;
    boolean bulkCheck;
    String defaultValue;
    String technicalDescription;
    String businessDescription;
    String dataElementName;
    String globalFieldName;
    String globalFieldLabelName;
    boolean isRequired;
    String altKey;
    boolean breakToNextLine;
    String aliasName;
    boolean donotTrackChanges;
    String repeat;
    boolean rowSum;
    boolean rowAverage;
    boolean columnSum;
    boolean columnAverage;
    String rowSumFunction;
    String rowAverageFunction;
    String columnSumFunction;
    String columnAverageFunction;
    String formatter;
    boolean skipId;
    String dataType;
    DataElement dataElement;
    DataValueType valueType;
    
    static {
        ALL_META_ATTRIBUTES = new String[] { "altKey", "baseSrc", "blankOption", "basedOnFieldValue", "columnAverage", "columnSum", "codePickerSrc", "dataType", "doNotValidate", "defaultValue", "descServiceId", "descQueryFields", "doNotMatchDescNames", "imageExtension", "isFilterOperator", "isUniqueField", "isLocalField", "isRequired", "keyValue", "listServiceId", "mailTo", "maxCharacters", "onChangeActionName", "onFocusActionName", "onBlurActionName", "noAutoLoad", "repeat", "rowSum", "rowAverage", "selectFirstOption", "toBeSentToServer", "uniqueKey", "uncheckedValue", "checkedValue", "isChecked", "minCharsToTriggerService", "selectionValueType", "multipleSelection", "sameListForAllRows", "globalFieldName", "globalFieldLabelName", "isValid", "formatter", "allowHtmlFornattedText", "onUserChangeActionName", "validateOnlyOnUserChange", "breakToNextLine", "aliasName", "isFilterField", "minSelections", "maxSelections", "keyList", "validateQueryFields", "bulkCheck", "trueValue", "falseValue", "supressDescOnLoad", "comboDisplayFields", "reportServiceId", "chartType", "xaxiscolumn", "yaxiscolumn", "isMultiDataSet", "groupbycolumn", "xaxislabel", "yaxislabel", "xaxislabels", "yaxislabels", "columnName", "displayXML", "valueList", "sortable", "filterable", "showValues", "expandAllOnLoad", "showLegend", "yaxislabelformatterid", "minx", "miny", "maxx", "maxy", "fieldToFocusAfterExecution", "rowSumFunction", "rowAverageFunction", "columnSumFunction", "columnAverageFunction", "childHtmlAttributes", "bubblecolumn", "bubbleradiusdenominator", "codePickerLeft", "codePickerTop", "donotTrackChanges", "validationFunction", "suggestionServiceId", "suggestAfterMinChars", "suggestionCss", "firstQualitativeRange", "secondQualitativeRange", "valueOfInterest", "comparativeValue", "bulletlabelcolumn", "distributionvaluecolumn", "fromcolumn", "tocolumn", "corecolumn", "level1column", "level2column", "matchStartingChars", "listCss", "allowHtmlFormattedText", "defaultCss", "legendNbrColumns", "legendLabelFormatter", "legendLabelBoxBorderColor", "legendPosition", "legendContainer", "legendMargin", "legendBackgroundColor", "legendBackgroundOpacity", "rawDataDisplay", "colors", "helpTextColumn", "groupHelpTextColumn", "markedAsComment", "highlightColor", "labelColor", "showFilledPoints", "columnIndexesToShow", "onClickFunctionName", "minPercentToShowLabel", "showMoreFunctionName", "stacking", "direction", "marginLeft", "marginBottom", "onMoveFunctionName", "legendHighlight", "yLabelMaxWidth", "xLabelFormatterFunction", "yLabelFormatterFunction" };
        ALL_TABLE_SENSITIVE_ATTRIBUTES = new String[] { "basedOnField", "copyTo", "fromField", "name", "toField", "otherField", "suggestionDescriptionField" };
        ALL_TABLE_SENSITIVE_ARRAYS = new String[] { "descFields", "dependentSelectionField", "descQueryFieldSources", "listServiceQueryFieldNames", "listServiceQueryFieldSources", "suggestionServiceFields", "suggestionServiceFieldSources" };
    }
    
    AbstractField() {
        this.labelPosition = LabelPosition.left;
        this.bulkCheck = false;
        this.defaultValue = null;
        this.technicalDescription = null;
        this.businessDescription = null;
        this.dataElementName = null;
        this.globalFieldName = null;
        this.globalFieldLabelName = null;
        this.isRequired = false;
        this.altKey = null;
        this.breakToNextLine = false;
        this.aliasName = null;
        this.donotTrackChanges = false;
        this.repeat = null;
        this.rowSum = false;
        this.rowAverage = false;
        this.columnSum = false;
        this.columnAverage = false;
        this.rowSumFunction = null;
        this.rowAverageFunction = null;
        this.columnSumFunction = null;
        this.columnAverageFunction = null;
        this.formatter = null;
        this.skipId = false;
        this.dataType = "text";
        this.dataElement = null;
        this.valueType = DataValueType.TEXT;
    }
    
    DataValueType getValueType() {
        return this.valueType;
    }
    
    @Override
    void addMyAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        super.addMyAttributes(sbf, pageContext);
        final String fieldName = pageContext.getName(this.name);
        if (!this.skipId) {
            sbf.append(" id=\"").append(fieldName).append("\" ");
        }
    }
    
    @Override
    void toHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.breakToNextLine) {
            sbf.append("<br />");
        }
        if (pageContext.renderFieldAsColumn) {
            if (pageContext.useHtml5) {
                this.fieldToHtml5(sbf, pageContext);
            }
            else {
                this.fieldToHtml(sbf, pageContext);
            }
            return;
        }
        boolean tdOpened = false;
        final int units = this.numberOfUnitsToUse;
        if (units > 0) {
            if (this.labelPosition == LabelPosition.left || this.labelPosition == LabelPosition.hide) {
                sbf.append("\n<td style=\"vertical-align: middle\" align=\"right\">");
                tdOpened = true;
            }
            else {
                sbf.append("\n<td style=\"vertical-align: middle\" colspan=\"").append(units * 2).append("\">");
            }
            if (this.breakToNextLine) {
                sbf.append("<br />");
            }
        }
        final String layoutType = pageContext.getLayoutType();
        if (!this.getClass().equals(ChartField.class) && !this.getClass().equals(XmlTreeField.class) && (layoutType == null || !layoutType.equals("5") || !this.getClass().equals(CheckBoxField.class))) {
            this.labelToHtml(sbf, pageContext);
        }
        if (this.labelPosition == LabelPosition.top) {
            sbf.append("<br/>");
        }
        else if (tdOpened) {
            sbf.append("</td><td style=\"vertical-align: middle\" ");
            if (units > 1) {
                sbf.append(" colspan=\"").append(units * 2 - 1).append("\" ");
            }
            sbf.append(">");
        }
        if (pageContext.useHtml5) {
            this.fieldToHtml5(sbf, pageContext);
        }
        else {
            this.fieldToHtml(sbf, pageContext);
        }
    }
    
    @Override
    void toHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.breakToNextLine) {
            sbf.append("<br />");
        }
        if (pageContext.renderFieldAsColumn) {
            this.fieldToHtml5(sbf, pageContext);
            return;
        }
        boolean tdOpened = false;
        final int units = this.numberOfUnitsToUse;
        if (units > 0) {
            if (this.labelPosition == LabelPosition.left || this.labelPosition == LabelPosition.hide) {
                sbf.append("\n<td class=\"labelCell\" id=\"").append(this.name).append("LabelCell\" >");
                tdOpened = true;
            }
            else {
                sbf.append("\n<td class=\"mergedCell\" id=\"").append(this.name).append("MergedCell\" colspan=\"").append(units * 2).append("\">");
            }
            if (this.breakToNextLine) {
                sbf.append("<br />");
            }
        }
        this.labelToHtml5(sbf, pageContext);
        if (this.labelPosition == LabelPosition.top) {
            sbf.append("<br/>");
        }
        else if (tdOpened) {
            sbf.append("</td><td class=\"fieldCell\" id=\"").append(this.name).append("FieldCell\" ");
            if (units > 1) {
                sbf.append(" colspan=\"").append(units * 2 - 1).append("\" ");
            }
            sbf.append(">");
        }
        this.fieldToHtml5(sbf, pageContext);
    }
    
    abstract void fieldToHtml(final StringBuilder p0, final PageGeneratorContext p1);
    
    void fieldToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        this.fieldToHtml(sbf, pageContext);
    }
    
    void labelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String labelToUse = this.getLabelToUse(pageContext);
        if (labelToUse == null) {
            if (this.labelPosition != LabelPosition.none) {
                sbf.append("&nbsp;");
            }
            return;
        }
        if (this.isRequired && AP.starForRequiredField == 2) {
            sbf.append("<reqfield>*</reqfield>");
        }
        sbf.append("<span ");
        if (this.hidden) {
            sbf.append(" style=\"display:none\" ");
        }
        if (this.name != null) {
            sbf.append("id=\"").append(this.name).append("Label\" ");
        }
        if (this.hoverText != null) {
            sbf.append("title=\"").append(this.hoverText).append("\" ");
        }
        if (this.isRequired) {
            sbf.append("class=\"requiredlabel\" >").append(labelToUse);
            if (AP.starForRequiredField == 1) {
                sbf.append("<span class=\"requiredstar\">*</span>");
            }
            sbf.append("</span>");
        }
        else {
            sbf.append("class=\"label\">").append(labelToUse).append("&nbsp;</span>");
        }
    }
    
    void labelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String labelToUse = this.getLabelToUse(pageContext);
        if (labelToUse == null) {
            if (this.labelPosition != LabelPosition.none) {
                sbf.append("&nbsp;");
            }
            return;
        }
        sbf.append("<div class=\"").append(this.isRequired ? "requiredLabel" : "label").append("\" ");
        if (this.hidden) {
            sbf.append(" style=\"display:none\" ");
        }
        if (this.name != null) {
            sbf.append("id=\"").append(this.name).append("Label\" ");
        }
        if (this.hoverText != null) {
            sbf.append("title=\"").append(this.hoverText).append("\" ");
        }
        sbf.append(" >").append(labelToUse).append("</div>");
    }
    
    String getLabelToUse(final PageGeneratorContext pageContext) {
        if (this.labelPosition == LabelPosition.hide) {
            return null;
        }
        String lbl = this.label;
        if (lbl == null) {
            if (pageContext.customLabelName != null && this.dataElement != null && this.dataElement.customLabels != null && this.dataElement.customLabels.containsKey(Page.customLabelKey)) {
                lbl = this.dataElement.customLabels.get(Page.customLabelKey).value;
            }
            if (lbl == null && this.dataElement != null) {
                lbl = this.dataElement.label;
            }
        }
        if (lbl == null || lbl.length() == 0 || lbl.equals(" ")) {
            return null;
        }
        return lbl;
    }
    
    void toJs(final StringBuilder js, final PageGeneratorContext pageContext) {
        js.append("\n\n/* MetaData for the Page element :").append(this.name).append("*/");
        final String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1);
        js.append('\n').append("ele").append(" = new PM.").append(className).append("();");
        pageContext.setAttributes(this, js, AbstractField.ALL_META_ATTRIBUTES);
        pageContext.setTableSensitiveAttributes(this, js, AbstractField.ALL_TABLE_SENSITIVE_ATTRIBUTES);
        pageContext.setTableSensitiveArrays(this, js, AbstractField.ALL_TABLE_SENSITIVE_ARRAYS);
        if (pageContext.isInsideGrid) {
            pageContext.setJsTextAttribute(js, "tableName", pageContext.getTableName());
            pageContext.setJsTextAttribute(js, "unqualifiedName", this.name);
        }
        String ft = this.formatter;
        if (ft == null) {
            ft = this.dataElement.formatter;
        }
        if (ft == null) {
            final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
            if (dt != null) {
                ft = dt.formatter;
            }
        }
        if (ft != null) {
            pageContext.setJsTextAttribute(js, "formatter", ft);
        }
        if (this instanceof AbstractInputField) {
            final AbstractInputField inp = (AbstractInputField)this;
            if (inp.isLocalField) {
                pageContext.setJsAttribute(js, "toBeSentToServer", "false");
            }
            String msgName = inp.messageName;
            if (msgName == null) {
                msgName = inp.dataElement.messageName;
            }
            if (msgName != null) {
                pageContext.setJsTextAttribute(js, "messageName", msgName);
                final Message msg = Messages.getMessage(msgName);
                if (msg == null) {
                    final String msgText = String.valueOf(this.name) + " uses custom error message " + msgName + ". This message is not defined.";
                    Spit.out(msgText);
                    pageContext.reportError(msgText);
                }
                else if (!msg.forClient) {
                    final String msgText = String.valueOf(this.name) + " uses custom error message " + msgName + ". This message is not marked with forClient=true. This message will not be available on the client.";
                    Spit.out(msgText);
                    pageContext.reportError(msgText);
                }
            }
        }
        String labelToUse = this.getLabelToUse(pageContext);
        if (labelToUse == null) {
            labelToUse = "";
        }
        pageContext.setJsTextAttribute(js, "label", labelToUse);
        final String val = (this.defaultValue == null) ? "" : this.defaultValue;
        pageContext.setJsTextAttribute(js, "value", val);
        js.append('\n').append("P2.addField(").append("ele").append(");");
    }
    
    void toSimpleHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        this.labelToHtml5(sbf, pageContext);
        sbf.append("<div id=\"").append(this.name).append("Wrapper\">");
        this.fieldToHtml5(sbf, pageContext);
        sbf.append("</div>");
    }
    
    @Override
    public void initialize() {
        super.initialize();
        String elementName = this.dataElementName;
        if (elementName == null) {
            elementName = this.name;
        }
        this.dataElement = DataDictionary.getElement(elementName);
        if (this.dataElement == null) {
            if (elementName.endsWith("Operator")) {
                this.dataElement = DataDictionary.getDefaultElement(elementName);
            }
            else if (elementName.endsWith("To")) {
                this.dataElement = DataDictionary.getElement(elementName.substring(0, elementName.length() - 2));
            }
            if (this.dataElement == null) {
                Spit.out("ERROR: Element Name " + elementName + " does not refer to any Data Element in Data Dictionary. Either add " + this.name + " to dictionary, or use dataElementName property to associate this with another entry in the dictionary");
                this.dataElement = DataDictionary.getDefaultElement(elementName);
                this.inError = true;
                DataDictionary.addNonExistentElement(this.dataElement);
            }
        }
        this.dataType = this.dataElement.dataType;
        final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
        if (dt == null) {
            this.valueType = DataValueType.TEXT;
        }
        else {
            this.valueType = dt.getValueType();
        }
    }
    
    static AbstractElement getDefaultField(final DataElement element, final boolean editable) {
        if (!editable) {
            return new OutputField();
        }
        AbstractField field = null;
        final AbstractDataType dt = DataTypes.getDataType(element.dataType, null);
        if (dt instanceof BooleanDataType) {
            field = new TextInputField();
        }
        return field;
    }
    
    static AbstractElement getDefaultField(final Column column, final boolean editable) {
        return null;
    }
}
