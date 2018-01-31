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

class TextInputField extends AbstractInputField
{
    String toField;
    String fromField;
    String otherField;
    int size;
    boolean isProtected;
    boolean isUniqueField;
    String codePickerSrc;
    String[] descFields;
    String[] descQueryFields;
    String[] descQueryFieldSources;
    String descServiceId;
    boolean doNotMatchDescNames;
    String validateQueryFields;
    boolean supressDescOnLoad;
    int minCharsToTriggerService;
    String[] comboDisplayFields;
    boolean showAmPm;
    boolean suppressSeconds;
    boolean isValid;
    boolean isFilterField;
    String fieldToFocusAfterExecution;
    String codePickerImage;
    int codePickerLeft;
    int codePickerTop;
    String valueList;
    
    TextInputField() {
        this.toField = null;
        this.fromField = null;
        this.otherField = null;
        this.size = 20;
        this.isProtected = false;
        this.isUniqueField = false;
        this.codePickerSrc = null;
        this.descFields = null;
        this.descQueryFields = null;
        this.descQueryFieldSources = null;
        this.descServiceId = null;
        this.doNotMatchDescNames = false;
        this.validateQueryFields = null;
        this.supressDescOnLoad = false;
        this.minCharsToTriggerService = 1;
        this.comboDisplayFields = null;
        this.showAmPm = false;
        this.suppressSeconds = false;
        this.isValid = true;
        this.isFilterField = false;
        this.fieldToFocusAfterExecution = null;
        this.codePickerImage = null;
        this.codePickerLeft = -1;
        this.codePickerTop = -1;
    }
    
    void setFilterField() {
        this.isFilterField = true;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
        if (dt.getValueType() == DataValueType.DATE) {
            if (((DateDataType)dt).includesTime) {
                this.dateTimeFieldToHtml(sbf, pageContext);
            }
            else {
                this.fieldToHtmlWorker(sbf, pageContext);
                this.addDatePicker(sbf, pageContext);
            }
            return;
        }
        this.fieldToHtmlWorker(sbf, pageContext);
        if (this.codePickerSrc != null) {
            this.addCodePicker(sbf, pageContext);
        }
    }
    
    void dateTimeFieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String myName = pageContext.getName(this.name);
        sbf.append("<table id=\"").append(myName).append("FieldsTable\"><tr><td>");
        this.fieldToHtmlWorker(sbf, pageContext);
        sbf.append("</td><td>");
        this.skipId = true;
        String commonStartText = "\n<input type=\"text\" class=\"intdecinputfield\" size=\"2\"  maxlength=\"2\" ";
        if (this.isProtected) {
            commonStartText = String.valueOf(commonStartText) + " readonly=\"readonly\" tabindex=\"-1\" ";
        }
        commonStartText = String.valueOf(commonStartText) + "id=\"" + myName;
        sbf.append(commonStartText).append("Hr\" ");
        this.addMyAttributes(sbf, pageContext);
        sbf.append("/></td><td>");
        sbf.append("<span class=\"colonstyle\">:</span></td><td>");
        sbf.append(commonStartText).append("Mn\" ");
        this.addMyAttributes(sbf, pageContext);
        sbf.append("/></td><td>");
        if (!this.suppressSeconds) {
            sbf.append("<span class=\"colonstyle\">:</span></td><td>");
            sbf.append(commonStartText).append("Sc\" ");
            this.addMyAttributes(sbf, pageContext);
            sbf.append("/></td><td>");
        }
        if (this.showAmPm) {
            sbf.append("\n<select id=\"").append(myName).append("Am\"");
            super.addMyAttributes(sbf, pageContext);
            sbf.append("><option value=\"\">AM</option><option value=\"1\">PM</option></select></td><td>");
        }
        this.addDatePicker(sbf, pageContext);
        sbf.append("</td></tr></table>");
        this.skipId = false;
    }
    
    protected void fieldToHtmlWorker(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<input type=\"text\" autocomplete=\"off\" ");
        if (this.cssClassName == null) {
            if (this.valueType == DataValueType.INTEGRAL || this.valueType == DataValueType.DECIMAL) {
                sbf.append("class=\"intdecinputfield\" ");
            }
            else if (this.valueType == DataValueType.DATE || this.valueType == DataValueType.TIMESTAMP) {
                sbf.append("class=\"dateinputfield\" ");
            }
            else {
                sbf.append("class=\"inputfield\" ");
            }
        }
        if (this.size > 0) {
            sbf.append(" size=\"").append(this.size).append("\" ");
        }
        String valToSet = this.defaultValue;
        if (this.valueList != null) {
            valToSet = this.getDefaultValueFromValueList();
        }
        if (valToSet != null) {
            sbf.append(" value=\"").append(valToSet).append("\" ");
        }
        if (this.isProtected) {
            sbf.append(" readonly=\"readonly\" tabindex=\"-1\" ");
        }
        final int len = DataTypes.getDataType(this.dataType, null).getMaxLength();
        if (len > 0) {
            sbf.append(" maxlength=\"").append(len).append("\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        final String myName = pageContext.getName(this.name);
        final boolean needPicker = this.valueType == DataValueType.DATE || this.codePickerSrc != null;
        final boolean needCombo = this.descServiceId != null && this.minCharsToTriggerService > 0;
        if (needCombo) {
            sbf.append(" autocomplete=\"off\" ");
            if (this.onBlurActionName == null) {
                sbf.append(" onblur=\"P2.inputFocusOut(this, '").append(myName).append("');\"");
            }
        }
        if (needCombo || needPicker) {
            sbf.append(" onkeyup=\"P2.inputKeyUp(this, '").append(myName).append("', event);\"");
        }
        sbf.append("/>");
    }
    
    private String getDefaultValueFromValueList() {
        final String valueToMatch = (this.defaultValue == null) ? "" : this.defaultValue.toLowerCase();
        final String[] vals = this.valueList.split(";");
        String[] array;
        for (int length = (array = vals).length, i = 0; i < length; ++i) {
            final String val = array[i];
            final String[] parts = val.split(",");
            final String internalValue = parts[0].trim();
            final String displayVal = (parts.length > 1) ? parts[1].trim() : internalValue;
            if (internalValue.toLowerCase().equals(valueToMatch)) {
                return displayVal;
            }
        }
        return null;
    }
    
    private void addDatePicker(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final DatePickerImageElement ele = new DatePickerImageElement();
        ele.hidden = this.hidden;
        ele.name = String.valueOf(this.name) + "Picker";
        ele.targetName = pageContext.getName(this.name);
        ele.toHtml(sbf, pageContext);
    }
    
    protected void addCodePicker(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final CodePickerImageElement ele = new CodePickerImageElement();
        ele.hidden = this.hidden;
        ele.targetName = pageContext.getName(this.name);
        ele.name = String.valueOf(this.name) + "Picker";
        ele.toHtml(sbf, pageContext);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.descQueryFields != null && this.descQueryFieldSources == null) {
            this.descQueryFieldSources = this.descQueryFields;
        }
        if (this.descServiceId != null) {
            this.isValid = true;
        }
    }
}
