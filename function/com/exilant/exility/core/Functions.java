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

import java.util.HashMap;

class Functions
{
    HashMap<String, FunctionInterface> builtInFuntions;
    String defaultNameSpace;
    HashMap<String, FunctionEntry> functionEntries;
    private static Functions instance;
    
    static {
        Functions.instance = null;
        load();
        if (Functions.instance == null) {
            Functions.instance = new Functions();
        }
        Functions.instance.builtInFuntions.put("Concat", new Concat());
        Functions.instance.builtInFuntions.put("In", new In());
        Functions.instance.builtInFuntions.put("AnyIn", new AnyIn());
        Functions.instance.builtInFuntions.put("Encrypt", new Encrypt());
    }
    
    Functions() {
        this.builtInFuntions = new HashMap<String, FunctionInterface>();
        this.defaultNameSpace = "com.exilant.exility.core";
        this.functionEntries = new HashMap<String, FunctionEntry>();
    }
    
    static FunctionInterface getFunction(final String functionName) {
        final FunctionInterface fn = Functions.instance.builtInFuntions.get(functionName);
        if (fn != null) {
            return fn;
        }
        final FunctionEntry entry = Functions.instance.functionEntries.get(functionName);
        if (entry == null) {
            return null;
        }
        if (entry.function == null) {
            final String packageName = (entry.nameSpace != null) ? entry.nameSpace : Functions.instance.defaultNameSpace;
            entry.function = (FunctionInterface)ObjectManager.createNew(packageName, functionName);
        }
        return entry.function;
    }
    
    static void load() {
        Functions.instance = (Functions)ResourceManager.loadResource("function", Functions.class);
    }
    
    public static Value evaluateFunction(final String functionName, final String[] inputParameters, final DataCollection dc) {
        Value[] ps = null;
        if (inputParameters != null) {
            ps = new Value[inputParameters.length];
            for (int i = 0; i < ps.length; ++i) {
                ps[i] = dc.getValue(inputParameters[i]);
            }
        }
        final FunctionInterface function = getFunction(functionName);
        return function.evaluate(ps);
    }
    
    public static Value evaluateFunction(final String functionName, final String[] inputParameters, final Variables vars, final DataCollection dc) {
        Value[] ps = null;
        if (inputParameters != null) {
            ps = new Value[inputParameters.length];
            for (int i = 0; i < ps.length; ++i) {
                ps[i] = vars.getValue(inputParameters[i]);
            }
        }
        final FunctionInterface function = getFunction(functionName);
        return function.evaluate(ps);
    }
    
    static Functions getInstance() {
        return Functions.instance;
    }
}
