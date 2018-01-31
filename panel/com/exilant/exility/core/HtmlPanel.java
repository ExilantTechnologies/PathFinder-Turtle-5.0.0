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

class HtmlPanel extends AbstractPanel
{
    String fileName;
    
    HtmlPanel() {
        this.fileName = null;
    }
    
    @Override
    void panelToHtml(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        if (this.fileName == null) {
            sbf.append("File name not specified for this htmlPanel");
            return;
        }
        final String fname = String.valueOf(ResourceManager.getResourceFolder()) + "page/" + this.fileName;
        final String s = ResourceManager.readFile(fname);
        if (s == null) {
            sbf.append("Unable to read file " + fname + " that is referred in html panel " + this.name);
        }
        sbf.append(s);
    }
    
    @Override
    void panelToHtml5(final StringBuilder sbf, final PageGeneratorContext pageContext) {
        this.panelToHtml(sbf, pageContext);
    }
}
