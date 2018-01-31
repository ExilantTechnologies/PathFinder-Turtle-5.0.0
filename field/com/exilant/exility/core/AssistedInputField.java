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

class AssistedInputField extends TextInputField
{
    String suggestionServiceId;
    int suggestAfterMinChars;
    String[] suggestionServiceFields;
    String[] suggestionServiceFieldSources;
    String columnIndexesToShow;
    String listServiceId;
    String[] listServiceQueryFieldNames;
    String[] listServiceQueryFieldSources;
    String keyValue;
    String blankOption;
    boolean noAutoLoad;
    boolean selectFirstOption;
    boolean sameListForAllRows;
    boolean multiplOptions;
    boolean matchStartingChars;
    String listCss;
    String selectionValueType;
    
    AssistedInputField() {
        this.suggestionServiceId = null;
        this.suggestAfterMinChars = 1;
        this.suggestionServiceFields = null;
        this.suggestionServiceFieldSources = null;
        this.columnIndexesToShow = null;
        this.listServiceId = null;
        this.listServiceQueryFieldNames = null;
        this.listServiceQueryFieldSources = null;
        this.keyValue = null;
        this.blankOption = null;
        this.noAutoLoad = false;
        this.selectFirstOption = false;
        this.sameListForAllRows = false;
        this.multiplOptions = false;
        this.matchStartingChars = false;
        this.listCss = null;
        this.selectionValueType = null;
    }
    
    @Override
    protected boolean focusAndBlurNeeded() {
        return true;
    }
    
    @Override
    void addMyAttributesOnly(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        super.addMyAttributesOnly(sbf, pageContext);
        sbf.append(" autocomplete=\"off\" ");
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        this.fieldToHtmlWorker(sbf, pageContext);
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.suggestionServiceFieldSources == null) {
            this.suggestionServiceFieldSources = this.suggestionServiceFields;
        }
        if (this.listServiceQueryFieldSources == null) {
            this.listServiceQueryFieldSources = this.listServiceQueryFieldNames;
        }
        if (this.valueList == null) {
            final AbstractDataType dt = DataTypes.getDataType(this.dataType, null);
            if (dt != null && dt.getValueType() == DataValueType.BOOLEAN) {
                final BooleanDataType b = (BooleanDataType)dt;
                this.valueList = "0," + b.falseValue + ";1," + b.trueValue;
            }
        }
        else if (this.blankOption != null && this.blankOption.length() > 0) {
            if (this.valueList.charAt(0) == ',') {
                Spit.out(String.valueOf(this.name) + " has blankOption set to " + this.blankOption + " but also has taken care of this in its valueList. blankOption is ignored.");
            }
            else {
                this.valueList = "," + this.blankOption + ";" + this.valueList;
            }
            this.blankOption = null;
        }
    }
}
