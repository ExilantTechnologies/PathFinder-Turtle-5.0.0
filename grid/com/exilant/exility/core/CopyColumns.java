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

public class CopyColumns implements GridProcessorInterface
{
    String fromGridName;
    String toGridName;
    String keyColumnNameToMatch;
    String[] columnNamesToCopy;
    
    public CopyColumns() {
        this.fromGridName = null;
        this.toGridName = null;
        this.keyColumnNameToMatch = null;
        this.columnNamesToCopy = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final Grid fromGrid = dc.getGrid(this.fromGridName);
        if (fromGrid == null) {
            dc.addMessage("exilNoGrid", this.fromGridName);
            Spit.out("input grid  " + this.fromGridName + " is not found in dc");
            return 0;
        }
        final Grid toGrid = dc.getGrid(this.toGridName);
        if (toGrid == null) {
            dc.addMessage("exilNoGrid", this.toGridName);
            Spit.out(" grid  " + this.toGridName + " is not found in dc");
            return 0;
        }
        final int nbrRows = toGrid.getNumberOfRows();
        String[] copyTheseColumns = new String[0];
        if (this.columnNamesToCopy == null) {
            copyTheseColumns = toGrid.getColumnNames();
        }
        else {
            final int nbrFields = this.columnNamesToCopy.length;
            if (nbrFields == 0) {
                copyTheseColumns = toGrid.getColumnNames();
            }
            else if (nbrFields == 1 && this.columnNamesToCopy[0].startsWith("@")) {
                final String variableName = this.columnNamesToCopy[0].substring(1);
                final String names = dc.getTextValue(variableName, null);
                if (names == null) {
                    final String err = "You have used " + this.columnNamesToCopy[0] + " as a variable name for list of columns. This variable is not found in dc.values";
                    Spit.out(err);
                    dc.addMessage("exilError", err);
                    return 0;
                }
                copyTheseColumns = names.split(",");
            }
        }
        final int nbrFields = copyTheseColumns.length;
        final ValueList fromKeyColumn = fromGrid.getColumn(this.keyColumnNameToMatch);
        if (fromKeyColumn == null) {
            dc.addMessage("exilNoColum", this.keyColumnNameToMatch, this.fromGridName);
            Spit.out("Column " + this.keyColumnNameToMatch + " is not found in grid  " + this.fromGridName);
            return 0;
        }
        final ValueList toKeyColumn = toGrid.getColumn(this.keyColumnNameToMatch);
        if (toKeyColumn == null) {
            dc.addMessage("exilNoColum", this.keyColumnNameToMatch, this.toGridName);
            Spit.out("Column " + this.keyColumnNameToMatch + " is not found in grid  " + this.toGridName);
            return 0;
        }
        final ValueList[] fromColumns = new ValueList[nbrFields];
        final ValueList[] toColumns = new ValueList[nbrFields];
        for (int i = 0; i < nbrFields; ++i) {
            final String columnName = copyTheseColumns[i].trim();
            ValueList column = fromGrid.getColumn(columnName);
            if (column == null) {
                dc.addMessage("exilNoColum", columnName, this.fromGridName);
                Spit.out("Column " + columnName + " is not found in grid  " + this.fromGridName);
                return 0;
            }
            fromColumns[i] = column;
            final DataValueType vt = column.getValueType();
            column = toGrid.getColumn(columnName);
            if (column == null) {
                column = ValueList.newList(vt, nbrRows);
                try {
                    toGrid.addColumn(columnName, column);
                }
                catch (ExilityException exilEx) {
                    Spit.out(exilEx);
                    dc.addMessage("exilError", exilEx.getMessage());
                    return 0;
                }
            }
            toColumns[i] = column;
        }
        final int nbrFromRows = fromGrid.getNumberOfRows();
        for (int j = 0; j < nbrRows; ++j) {
            final String toValue = toKeyColumn.getTextValue(j);
            for (int k = 0; k < nbrFromRows; ++k) {
                final Value fromValue = fromKeyColumn.getValue(k);
                if (toValue.equals(fromValue.getTextValue())) {
                    for (int l = 0; l < nbrFields; ++l) {
                        toColumns[l].setValue(fromColumns[l].getValue(k), j);
                    }
                    break;
                }
            }
        }
        return 1;
    }
}
