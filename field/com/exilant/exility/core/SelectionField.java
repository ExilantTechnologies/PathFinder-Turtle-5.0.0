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

class SelectionField extends AbstractInputField
{
    String listServiceId;
    String[] listServiceQueryFieldNames;
    String[] listServiceQueryFieldSources;
    String keyValue;
    String blankOption;
    boolean noAutoLoad;
    String valueList;
    boolean selectFirstOption;
    boolean multipleSelection;
    int size;
    String descServiceId;
    String[] descFields;
    String[] descQueryFields;
    String[] descQueryFieldSources;
    boolean doNotMatchDescNames;
    boolean sameListForAllRows;
    String validateQueryFields;
    boolean supressDescOnLoad;
    String otherField;
    SelectionValueType selectionValueType;
    boolean isFilterOperator;
    boolean isUniqueField;
    String fieldToFocusAfterExecution;
    String showMoreFunctionName;
    
    SelectionField() {
        this.listServiceId = null;
        this.listServiceQueryFieldNames = null;
        this.listServiceQueryFieldSources = null;
        this.keyValue = null;
        this.blankOption = null;
        this.noAutoLoad = false;
        this.valueList = null;
        this.selectFirstOption = false;
        this.multipleSelection = false;
        this.size = 0;
        this.descServiceId = null;
        this.descFields = null;
        this.descQueryFields = null;
        this.descQueryFieldSources = null;
        this.doNotMatchDescNames = false;
        this.sameListForAllRows = false;
        this.validateQueryFields = null;
        this.supressDescOnLoad = false;
        this.otherField = null;
        this.selectionValueType = SelectionValueType.text;
        this.isFilterOperator = false;
        this.isUniqueField = false;
        this.fieldToFocusAfterExecution = null;
        this.showMoreFunctionName = null;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<select ");
        super.addMyAttributes(sbf, pageContext);
        if (this.multipleSelection) {
            sbf.append("multiple=\"multiple\"");
        }
        if (this.showMoreFunctionName != null) {
            sbf.append(" onclick=\"P2.moreOptionClicked('").append(this.name).append("', event);\" ");
        }
        sbf.append(">");
        if (!this.isRequired || (this.valueList != null && this.valueList.length() > 0 && !this.isRequired)) {
            sbf.append("<option value=\"\">");
            if (this.blankOption != null && this.blankOption.length() != 0) {
                sbf.append(this.blankOption);
            }
            sbf.append("</option>");
        }
        if (this.valueList != null && this.valueList.length() != 0) {
            final String[] list = this.valueList.split(";");
            final int n = list.length;
            if (n == 1 && this.isRequired) {
                this.defaultValue = list[0].split(",")[0];
            }
            if (this.defaultValue == null && this.selectFirstOption) {
                this.defaultValue = list[0].split(",")[0];
            }
            for (int i = 0; i < n; ++i) {
                final String[] entry = list[i].split(",");
                this.appendListValues(sbf, entry);
            }
        }
        else if (this.defaultValue != null && this.defaultValue.length() > 0) {
            sbf.append("<option value=\"").append(this.defaultValue).append("\" selected=\"selected\" >").append(this.defaultValue).append("</option>");
        }
        sbf.append("</select>");
    }
    
    private void appendListValues(final StringBuilder sbf, final String[] grid) {
        if (grid == null) {
            return;
        }
        if (grid.length <= 0) {
            return;
        }
        sbf.append("<option value=\"").append(grid[0]).append("\" ");
        if (grid.length > 1 && grid[0].equals(this.defaultValue)) {
            sbf.append(" selected=\"selected\" ");
        }
        if (grid.length > 2) {
            sbf.append(" class=\"").append(grid[2]).append("\" ");
        }
        sbf.append(">");
        if (grid.length > 1) {
            sbf.append(grid[1]);
        }
        sbf.append("</option>");
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.listServiceQueryFieldSources == null && this.listServiceQueryFieldSources != null) {
            this.listServiceQueryFieldSources = this.listServiceQueryFieldNames;
        }
        if (this.dataElementName == null) {
            this.dataElementName = this.name;
        }
        this.dataElement = DataDictionary.getElement(this.dataElementName);
        if (this.dataElement == null) {
            return;
        }
        if (this.listServiceId == null) {
            if (this.dataElement.listServiceName != null) {
                Spit.out("listServiceId for " + this.name + " set to " + this.dataElement.listServiceName + " based on settings in data dictionary for " + this.dataElement.name);
                this.listServiceId = this.dataElement.listServiceName;
            }
            else if (this.valueList == null || this.valueList.length() == 0) {
                Spit.out("No list service set for " + this.name);
            }
        }
        if (this.descQueryFields != null && this.descQueryFieldSources == null) {
            this.descQueryFieldSources = this.descQueryFields;
        }
    }
}
