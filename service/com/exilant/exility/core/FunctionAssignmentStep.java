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

class FunctionAssignmentStep extends AbstractStep
{
    String fieldName;
    String functionName;
    String[] inputParameters;
    
    FunctionAssignmentStep() {
        this.fieldName = null;
        this.functionName = null;
        this.inputParameters = null;
    }
    
    @Override
    String executeStep(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final Value val = Functions.evaluateFunction(this.functionName, this.inputParameters, dc);
        dc.addValue(this.fieldName, val);
        return "next";
    }
    
    public static void main(final String[] args) throws ExilityException {
        final DataCollection dc = new DataCollection();
        final FunctionAssignmentStep step = new FunctionAssignmentStep();
        step.fieldName = "junk";
        step.functionName = "Concat";
        final String[] p = { "a", "b" };
        step.inputParameters = p;
        dc.addTextValue("a", "a,b");
        dc.addTextValue("b", "b,d,aa,n,aa");
        final String ret = step.executeStep(dc, null);
        System.out.println("junk=" + dc.getTextValue("junk", "undefined") + " and ret = " + ret);
    }
}
