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

abstract class AbstractInputField extends AbstractField
{
    String onChangeActionName;
    String onFocusActionName;
    String onBlurActionName;
    String onUserChangeActionName;
    String basedOnField;
    String basedOnFieldValue;
    String copyTo;
    boolean doNotValidate;
    boolean isLocalField;
    boolean validateOnlyOnUserChange;
    String validationFunction;
    String[] dependentSelectionField;
    String messageName;
    private boolean isLastField;
    
    AbstractInputField() {
        this.onChangeActionName = null;
        this.onFocusActionName = null;
        this.onBlurActionName = null;
        this.onUserChangeActionName = null;
        this.basedOnField = null;
        this.basedOnFieldValue = null;
        this.copyTo = null;
        this.doNotValidate = false;
        this.isLocalField = false;
        this.validateOnlyOnUserChange = false;
        this.validationFunction = null;
        this.dependentSelectionField = null;
        this.isLastField = false;
    }
    
    void setLastField() {
        this.isLastField = true;
    }
    
    boolean getIslastField() {
        return this.isLastField;
    }
    
    @Override
    void addMyAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        super.addMyAttributes(sbf, pageContext);
        this.addMyAttributesOnly(sbf, pageContext);
    }
    
    void addMyParentsAttributes(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        super.addMyAttributes(sbf, pageContext);
    }
    
    void addMyAttributesOnly(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        final String myName = pageContext.getName(this.name);
        sbf.append(" onchange=\"P2.fieldChanged(this, '").append(myName).append("');\"");
        if (this.htmlAttributes == null || !this.htmlAttributes.contains("onkeydown=")) {
            if (AP.lastKeyEventTrigger && pageContext.isInsideGrid && this.isLastField) {
                sbf.append(" onkeydown=\"P2.handleGridNav(this, '").append(myName).append("', event);P2.keyPressedOnField(this, '").append(myName).append("', event);\"");
            }
            else if (pageContext.isInsideGrid) {
                sbf.append(" onkeydown=\"P2.handleGridNav(this, '").append(myName).append("', event);\"");
            }
        }
        else if (AP.lastKeyEventTrigger && pageContext.isInsideGrid && this.isLastField) {
            sbf.append(" onkeydown=\"P2.keyPressedOnField(this, '").append(myName).append("', event);\"");
        }
        if (this.focusAndBlurNeeded() || pageContext.isInsideGrid || this.onFocusActionName != null) {
            sbf.append(" onfocus=\"P2.fieldFocussed(this, '").append(myName).append("');\"");
        }
        if (this.focusAndBlurNeeded() || this.onBlurActionName != null) {
            sbf.append(" onblur=\"P2.inputFocusOut(this, '").append(myName).append("');\"");
        }
        if (this.hoverText != null && pageContext.useHtml5) {
            sbf.append(" placeholder=\"").append(this.hoverText).append("\" ");
        }
    }
    
    protected boolean focusAndBlurNeeded() {
        return false;
    }
}
