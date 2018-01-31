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

class CheckBoxGroupField extends AbstractInputField
{
    String listServiceId;
    String[] listServiceQueryFieldNames;
    String[] listServiceQueryFieldSources;
    String keyValue;
    boolean noAutoLoad;
    String valueList;
    boolean sameListForAllRows;
    SelectionValueType selectionValueType;
    int minSelections;
    int maxSelections;
    String columnName;
    private String[] keyList;
    
    CheckBoxGroupField() {
        this.listServiceId = null;
        this.listServiceQueryFieldNames = null;
        this.listServiceQueryFieldSources = null;
        this.keyValue = null;
        this.noAutoLoad = false;
        this.valueList = null;
        this.sameListForAllRows = false;
        this.selectionValueType = SelectionValueType.text;
        this.minSelections = 0;
        this.maxSelections = 0;
        this.columnName = null;
        this.keyList = null;
    }
    
    @Override
    void fieldToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        sbf.append("\n<div ");
        if (this.cssClassName == null) {
            sbf.append(" class=\"checkBoxGroup\" ");
        }
        super.addMyAttributes(sbf, pageContext);
        sbf.append(">");
        if (this.valueList != null && this.valueList.length() != 0) {
            final String[] list = this.valueList.split(";");
            for (int n = list.length, i = 0; i < n; ++i) {
                final String[] entry = list[i].split(",");
                this.addCheckBox(sbf, entry, pageContext);
            }
        }
        sbf.append("</div>");
    }
    
    private void addCheckBox(final StringBuilder sbf, final String[] entry, final PageGeneratorContext pageContext) {
        if (entry == null || entry.length <= 0) {
            return;
        }
        final String key = entry[0];
        final String lbl = entry[1];
        final String parentName = pageContext.getName(this.name);
        sbf.append("<div><input type=\"checkbox\"");
        if (!pageContext.isInsideGrid) {
            sbf.append("id=\"").append(parentName).append('_').append(key).append('\"');
        }
        sbf.append(" value=\"").append(key).append('\"');
        sbf.append(" onchange=\"P2.checkBoxGroupChanged(this, '").append(parentName).append("');\"");
        if (key.equals(this.defaultValue)) {
            sbf.append("checked=\"checked\"");
        }
        sbf.append("/>");
        sbf.append("\n<label ");
        if (key.equals(this.defaultValue)) {
            sbf.append("for=\"").append(parentName).append('_').append(key).append("\" ");
        }
        sbf.append(">").append(lbl).append("</label></div>");
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (this.dataElementName == null) {
            this.dataElementName = this.name;
        }
        this.dataElement = DataDictionary.getElement(this.dataElementName);
        if (this.dataElement == null) {
            return;
        }
        if (this.listServiceId == null && this.dataElement.listServiceName != null) {
            this.listServiceId = this.dataElement.listServiceName;
        }
        if (this.valueList != null && this.valueList.length() > 0) {
            final String[] rows = this.valueList.split(";");
            final int n = rows.length;
            this.keyList = new String[n];
            for (int i = 0; i < n; ++i) {
                this.keyList[i] = rows[i].split(",")[0];
            }
        }
    }
}
