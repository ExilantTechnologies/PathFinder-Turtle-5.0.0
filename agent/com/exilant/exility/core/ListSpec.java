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

class ListSpec extends Parameter
{
    int minCols;
    int maxCols;
    boolean allEntriesRequired;
    
    ListSpec() {
        this.minCols = 0;
        this.maxCols = 0;
        this.allEntriesRequired = false;
    }
    
    void translateInput(final ServiceData inData, final DataCollection dc) {
        final String[] val = inData.getList(this.name);
        if (val == null || val.length == 0) {
            if (!this.isOptional) {
                dc.addMessage("exilListIsRequired", this.name);
            }
            return;
        }
        if (this.minCols > 0 && val.length < this.minCols) {
            dc.addMessage("exilMinCols", this.name, new StringBuilder(String.valueOf(this.minCols)).toString());
            return;
        }
        if (this.maxCols > 0 && val.length > this.maxCols) {
            dc.addMessage("exilMaxCols", this.name, new StringBuilder(String.valueOf(this.minCols)).toString());
            return;
        }
        final AbstractDataType dt = this.getDataType();
        final ValueList valueList = dt.getValueList(this.name, val, this.allEntriesRequired, dc);
        if (valueList != null) {
            dc.addValueList(this.name, valueList);
        }
    }
    
    void translateOutput(final DataCollection dc, final ServiceData outData) {
        final AbstractDataType dt = this.getDataType();
        final ValueList valueList = dc.getValueList(this.name);
        if (valueList != null) {
            outData.addList(this.name, dt.format(valueList));
        }
    }
    
    public void translate(final DataCollection fromDc, final DataCollection toDc) {
        toDc.addValueList(this.name, fromDc.getValueList(this.name));
    }
}
