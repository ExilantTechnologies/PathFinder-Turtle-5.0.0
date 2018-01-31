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

abstract class AbstractAction
{
    static final String[] ACTION_ATTRIBUTE_NAMES;
    static final String[] ALL_TABLE_SENSITIVE_ATTRIBUTES;
    static final String[] ALL_TABLE_SENSITIVE_ARRAYS;
    String name;
    String[] showPanels;
    String[] hidePanels;
    String[] disableFields;
    String[] enableFields;
    boolean warnIfFormIsModified;
    boolean resetFormModifiedState;
    String fieldToFocusAfterExecution;
    String popupPanel;
    String popdownPanel;
    String description;
    boolean markedAsComment;
    
    static {
        ACTION_ATTRIBUTE_NAMES = new String[] { "name", "warnIfFormIsModified", "functionName", "pageToGo", "windowToGo", "windowDisposal", "serviceId", "callBackActionName", "waitForResponse", "submitForm", "toRefreshPage", "disableForm", "passDc", "mailTo", "resetFormModifiedState", "atLeastOneFieldIsRequired", "reportName", "queryFieldNames", "fieldsToReset", "windowName", "validateQueryFields", "submitInNewWindow", "fieldToFocusAfterExecution", "closeWindow", "callBackEvenOnError", "popupPanel", "popdownPanel" };
        ALL_TABLE_SENSITIVE_ATTRIBUTES = new String[] { "substituteValueFrom", "parameter" };
        ALL_TABLE_SENSITIVE_ARRAYS = new String[] { "showPanels", "hidePanels", "queryFieldSources", "disableFields", "enableFields" };
    }
    
    AbstractAction() {
        this.name = null;
        this.showPanels = null;
        this.hidePanels = null;
        this.disableFields = null;
        this.enableFields = null;
        this.warnIfFormIsModified = false;
        this.resetFormModifiedState = false;
        this.fieldToFocusAfterExecution = null;
        this.popupPanel = null;
        this.popdownPanel = null;
        this.description = null;
        this.markedAsComment = false;
    }
    
    void toJavaScript(final StringBuilder js, final PageGeneratorContext pageContext) {
        js.append("\n/***** action field = ").append(this.name).append("  ********/\n");
        js.append("ele").append(" = new PM.").append(this.getClass().getName().substring(this.getClass().getName().lastIndexOf(46) + 1)).append("();");
        pageContext.setAttributes(this, js, AbstractAction.ACTION_ATTRIBUTE_NAMES);
        pageContext.setTableSensitiveAttributes(this, js, AbstractAction.ALL_TABLE_SENSITIVE_ATTRIBUTES);
        pageContext.setTableSensitiveArrays(this, js, AbstractAction.ALL_TABLE_SENSITIVE_ARRAYS);
        js.append('\n').append("P2.addAction(").append("ele").append(");");
    }
}
