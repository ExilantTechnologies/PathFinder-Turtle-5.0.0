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
import java.util.Map;

class LoopStepWorker
{
    private LoopStep step;
    private Grid grid;
    private int maxIndex;
    private int nextIndexToUse;
    private String prefix;
    private final Map<String, Value> savedValues;
    
    LoopStepWorker(final LoopStep step, final DataCollection dc) {
        this.step = null;
        this.maxIndex = 0;
        this.nextIndexToUse = 0;
        this.prefix = null;
        this.savedValues = new HashMap<String, Value>();
        this.step = step;
        this.grid = dc.getGrid(this.step.tableToLoopOn);
        if (this.grid == null) {
            Spit.out("Deesign Error: Service has a loopstep defined on " + this.step.tableToLoopOn + " but there is no grid with this name.");
            return;
        }
        this.maxIndex = this.grid.getNumberOfRows();
        if (this.maxIndex == 0) {
            return;
        }
        this.prefix = this.step.prefix;
        if (this.prefix == null) {
            this.prefix = "";
        }
        if (this.step.outputFields != null) {
            this.addOutputColumns(this.step.outputFields);
        }
        this.saveValues(dc);
    }
    
    boolean toContinue(final DataCollection dc) {
        if (this.maxIndex == 0) {
            return false;
        }
        if (this.nextIndexToUse > 0 && this.step.outputFields != null) {
            this.copyOutputFields(dc, this.step.outputFields);
        }
        if (this.nextIndexToUse == this.maxIndex) {
            this.endLoop(dc);
            return false;
        }
        if (this.step.inputFields != null) {
            this.copyInputFields(dc, this.step.inputFields);
        }
        else {
            this.copyInputFields(dc);
        }
        ++this.nextIndexToUse;
        return true;
    }
    
    private void copyInputFields(final DataCollection dc, final String[] inputFields) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String columnName : inputFields) {
            dc.addValue(String.valueOf(this.prefix) + columnName, this.grid.getValue(columnName, this.nextIndexToUse));
        }
    }
    
    private void copyInputFields(final DataCollection dc) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String columnName : this.grid.columnValues.keySet()) {
            dc.addValue(String.valueOf(this.prefix) + columnName, this.grid.getValue(columnName, this.nextIndexToUse));
        }
    }
    
    private void removeInputFields(final DataCollection dc, final String[] inputFields) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String columnName : inputFields) {
            dc.removeValue(String.valueOf(this.prefix) + columnName);
        }
    }
    
    private void removeInputFields(final DataCollection dc) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String columnName : this.grid.columnValues.keySet()) {
            dc.removeValue(String.valueOf(this.prefix) + columnName);
        }
    }
    
    private void copyOutputFields(final DataCollection dc, final String[] outputFields) {
        if (this.maxIndex == 0) {
            return;
        }
        final int idx = this.nextIndexToUse - 1;
        for (final String name : outputFields) {
            final String fullName = String.valueOf(this.prefix) + name;
            if (dc.hasValue(fullName)) {
                this.grid.setValue(name, dc.getValue(fullName), idx);
            }
        }
    }
    
    private void addOutputColumns(final String[] outputFields) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String name : outputFields) {
            if (!this.grid.hasColumn(name)) {
                final DataValueType vt = DataDictionary.getValueType(name);
                try {
                    this.grid.addColumn(name, ValueList.newList(vt, this.maxIndex));
                }
                catch (ExilityException ex) {}
            }
        }
    }
    
    void endLoop(final DataCollection dc) {
        if (this.maxIndex == 0) {
            return;
        }
        if (this.step.inputFields != null) {
            this.removeInputFields(dc, this.step.inputFields);
        }
        else {
            this.removeInputFields(dc);
        }
        this.restoreValues(dc);
    }
    
    void saveValues(final DataCollection dc) {
        if (this.maxIndex == 0) {
            return;
        }
        for (final String columnName : this.grid.columnValues.keySet()) {
            final Value val = dc.getValue(columnName);
            if (val != null) {
                this.savedValues.put(columnName, val);
            }
        }
    }
    
    void restoreValues(final DataCollection dc) {
        for (final String key : this.savedValues.keySet()) {
            dc.addValue(key, this.savedValues.get(key));
        }
    }
}
