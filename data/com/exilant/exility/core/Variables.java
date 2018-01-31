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

import java.util.Iterator;
import java.util.HashMap;

public class Variables
{
    private final HashMap<String, Value> variables;
    
    public Variables() {
        this.variables = new HashMap<String, Value>();
    }
    
    public void extractVariablesFromDc(final String[] variableNames, final String[] inputValues, final DataCollection dc) {
        for (int i = 0; i < inputValues.length; ++i) {
            final String inputValue = inputValues[i];
            final char firstChar = inputValue.charAt(0);
            Value value = null;
            if (Chars.isTextQuote(firstChar)) {
                value = Value.newValue(inputValue.substring(1, inputValue.length() - 1));
            }
            else if (Chars.isDateQuote(firstChar)) {
                value = Value.newValue(DateUtility.parseDate(inputValue.substring(1, inputValue.length() - 1)));
            }
            else if (Chars.isNumeric(firstChar) || '-' == firstChar) {
                if (inputValue.indexOf(46) > 0) {
                    value = Value.newValue(Long.parseLong(inputValue));
                }
                else {
                    value = Value.newValue(Double.parseDouble(inputValue));
                }
            }
            else if (inputValue.equalsIgnoreCase("true")) {
                value = Value.newValue(true);
            }
            else if (inputValue.equalsIgnoreCase("false")) {
                value = Value.newValue(false);
            }
            else {
                value = dc.getValue(inputValue);
            }
            this.variables.put(variableNames[i], value);
        }
    }
    
    public void extractVariablesFromGrid(final DataCollection dc, final String[] variableNames, final String[] inputValues, final Grid grid, final int idx) {
        for (int i = 0; i < inputValues.length; ++i) {
            final String columnName = inputValues[i];
            Value value = null;
            if (grid.hasColumn(columnName)) {
                value = grid.getValue(columnName, idx);
            }
            else {
                value = dc.getValue(columnName);
            }
            this.variables.put(variableNames[i], value);
        }
    }
    
    public void setValue(final String name, final Value value) {
        this.variables.put(name, value);
    }
    
    public Value getValue(final String name) {
        return this.variables.get(name);
    }
    
    public void removeValue(final String name) {
        this.variables.remove(name);
    }
    
    public boolean hasVariable(final String name) {
        return this.variables.containsKey(name);
    }
    
    boolean exists(final String varName) {
        return this.variables.containsKey(varName);
    }
    
    String render() {
        final StringBuilder sbf = new StringBuilder();
        for (final String variable : this.variables.keySet()) {
            final Value value = this.variables.get(variable);
            sbf.append("\n ").append(variable).append('[').append(value.getValueType()).append("] = ").append(value.getQuotedValue());
        }
        return sbf.toString();
    }
}
