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

public class CopyColumnsAcrossGrids implements GridProcessorInterface
{
    String fromGridName;
    String toGridName;
    String keyColumnNameToMatchFrom;
    String keyColumnNameToMatchTo;
    String[] columnNamesToCopyFrom;
    String[] columnNamesToCopyTo;
    
    public CopyColumnsAcrossGrids() {
        this.fromGridName = null;
        this.toGridName = null;
        this.keyColumnNameToMatchFrom = null;
        this.keyColumnNameToMatchTo = null;
        this.columnNamesToCopyFrom = null;
        this.columnNamesToCopyTo = null;
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
        final int nbrFields = this.columnNamesToCopyFrom.length;
        if (nbrFields == 0) {
            dc.addMessage("exilNoColumNames", this.fromGridName, this.toGridName);
            Spit.out("No coumn names specified for copying from " + this.toGridName + " to " + this.fromGridName);
            return 0;
        }
        final ValueList fromKeyColumn = fromGrid.getColumn(this.keyColumnNameToMatchFrom);
        if (fromKeyColumn == null) {
            dc.addMessage("exilNoColum", this.keyColumnNameToMatchFrom, this.fromGridName);
            Spit.out("Column " + this.keyColumnNameToMatchFrom + " is not found in grid  " + this.fromGridName);
            return 0;
        }
        final ValueList toKeyColumn = toGrid.getColumn(this.keyColumnNameToMatchTo);
        if (toKeyColumn == null) {
            dc.addMessage("exilNoColum", this.keyColumnNameToMatchFrom, this.toGridName);
            Spit.out("Column " + this.keyColumnNameToMatchFrom + " is not found in grid  " + this.toGridName);
            return 0;
        }
        final ValueList[] fromColumns = new ValueList[nbrFields];
        final ValueList[] toColumns = new ValueList[nbrFields];
        for (int i = 0; i < nbrFields; ++i) {
            final String fromColumnName = this.columnNamesToCopyFrom[i];
            ValueList column = fromGrid.getColumn(fromColumnName);
            if (column == null) {
                dc.addMessage("exilNoColum", fromColumnName, this.fromGridName);
                Spit.out("Column " + fromColumnName + " is not found in grid  " + this.fromGridName);
                return 0;
            }
            fromColumns[i] = column;
            final DataValueType vt = column.getValueType();
            final String toColumnName = this.columnNamesToCopyTo[i];
            column = toGrid.getColumn(toColumnName);
            if (column == null) {
                column = ValueList.newList(vt, nbrRows);
                try {
                    toGrid.addColumn(toColumnName, column);
                }
                catch (ExilityException ex) {}
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
