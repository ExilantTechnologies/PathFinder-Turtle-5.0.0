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

public class PageParameter implements ToBeInitializedInterface
{
    String name;
    String defaultValue;
    boolean isRequired;
    String dataElementName;
    String setTo;
    boolean isPrimaryKey;
    String description;
    private String dataType;
    boolean inError;
    private static final String[] ATTR_NAMES;
    
    static {
        ATTR_NAMES = new String[] { "name", "defaultValue", "isRequired", "dataType", "setTo", "isPrimaryKey" };
    }
    
    public PageParameter() {
        this.name = null;
        this.defaultValue = null;
        this.isRequired = false;
        this.dataElementName = null;
        this.setTo = null;
        this.isPrimaryKey = false;
        this.description = null;
        this.dataType = null;
        this.inError = false;
    }
    
    String getDataType() {
        return this.dataType;
    }
    
    void toJavaScript(final StringBuilder js, final PageGeneratorContext pc) {
        js.append('\n').append("ele").append(" = new PM.PageParameter();");
        pc.setAttributes(this, js, PageParameter.ATTR_NAMES);
    }
    
    @Override
    public void initialize() {
        final String name1 = (this.dataElementName != null) ? this.dataElementName : this.name;
        DataElement de = DataDictionary.getElement(name1);
        if (de == null) {
            Spit.out("Error: Page Parameter " + this.name + " does not exist in dictioanry, nor is it linked to another data element name.");
            this.inError = true;
            de = DataDictionary.getDefaultElement(this.name);
            DataDictionary.addNonExistentElement(de);
        }
        final AbstractDataType dt = DataDictionary.getDataType(name1);
        this.dataType = dt.name;
    }
}
