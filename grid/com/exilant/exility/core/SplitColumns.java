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

class SplitColumns implements GridProcessorInterface, ToBeInitializedInterface
{
    String gridName;
    String columnNameToSplit;
    Parameter[] paramNamesToSplitInto;
    String[] columnNamesToSplitInto;
    String fieldSeparator;
    
    SplitColumns() {
        this.gridName = null;
        this.columnNameToSplit = null;
        this.paramNamesToSplitInto = new Parameter[0];
        this.columnNamesToSplitInto = new String[0];
        this.fieldSeparator = "-";
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid grid = dc.getGrid(this.gridName);
        if (grid == null) {
            dc.addMessage("exilNoGrid", this.gridName);
            Spit.out("input grid  " + this.gridName + " is ot found in dc");
            return 0;
        }
        final int nbrRows = grid.getNumberOfRows();
        if (nbrRows == 0) {
            return 0;
        }
        final String[] vals = grid.getColumnAsTextArray(this.columnNameToSplit);
        final int nbrFields = vals[0].split(this.fieldSeparator).length;
        if (nbrFields != this.paramNamesToSplitInto.length) {
            final String s = "Column " + this.columnNameToSplit + " is designed to have " + this.paramNamesToSplitInto.length + ". But actually it has " + nbrFields + " values";
            Spit.out(s);
            dc.addMessage("exilError", s);
            return 0;
        }
        try {
            final ValueList[] splitLists = new ValueList[nbrFields];
            for (int i = 0; i < nbrFields; ++i) {
                final Parameter col = this.paramNamesToSplitInto[i];
                final ValueList lst = ValueList.newList(col.getValueType(), nbrRows);
                grid.addColumn(col.name, lst);
                splitLists[i] = lst;
            }
            for (int i = 0; i < nbrRows; ++i) {
                final String[] fieldVals = vals[i].split(this.fieldSeparator);
                if (fieldVals.length != nbrFields) {
                    final String s2 = "Column " + this.columnNameToSplit + " is designed to have " + this.paramNamesToSplitInto.length + ". But actually it has " + fieldVals.length + " values";
                    Spit.out(s2);
                    dc.addMessage("exilError", s2);
                    return 0;
                }
                for (int j = 0; j < nbrFields; ++j) {
                    final ValueList lst2 = splitLists[j];
                    final Value v = Value.newValue(fieldVals[j], lst2.getValueType());
                    lst2.setValue(v, i);
                }
            }
        }
        catch (ExilityException e) {
            dc.addMessage("exilError", e.getMessage(), " stacktrace=" + e.getStackTrace());
            Spit.out(String.valueOf(e.getMessage()) + " stacktrace=" + e.getStackTrace());
            return 0;
        }
        dc.addGrid(this.gridName, grid);
        return nbrRows;
    }
    
    @Override
    public void initialize() {
        this.paramNamesToSplitInto = new Parameter[this.columnNamesToSplitInto.length];
        for (int i = 0; i < this.columnNamesToSplitInto.length; ++i) {
            final String columnName = this.columnNamesToSplitInto[i];
            try {
                final Parameter param = Parameter.class.newInstance();
                param.name = columnName;
                param.initialize();
                this.paramNamesToSplitInto[i] = param;
            }
            catch (InstantiationException iEx) {
                Spit.out(iEx);
            }
            catch (IllegalAccessException iEx2) {
                Spit.out(iEx2);
            }
        }
    }
}
