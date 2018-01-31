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

public class ServiceSpec
{
    String name;
    String techNotes;
    DataSpec inSpec;
    DataSpec outSpec;
    
    static ServiceSpec getDefaultSpec() {
        final ServiceSpec spec = new ServiceSpec();
        return spec;
    }
    
    ServiceSpec() {
        this.name = null;
        this.techNotes = "";
        this.inSpec = new DataSpec();
        this.outSpec = new DataSpec();
    }
    
    public void translateInput(final ServiceData inData, final DataCollection dc) {
        dc.copyInternalFieldsFrom(inData);
        if (this.inSpec == null) {
            dc.copyFrom(inData);
        }
        else {
            this.inSpec.translateInput(inData, dc);
        }
    }
    
    ServiceData translateOutput(final DataCollection dc, final ServiceData outData) {
        dc.copyInternalFieldsTo(outData);
        if (this.outSpec == null) {
            dc.copyTo(outData);
        }
        else {
            this.outSpec.translateOutput(dc, outData);
        }
        return outData;
    }
}
