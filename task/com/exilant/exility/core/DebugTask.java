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

public class DebugTask extends ExilityTask
{
    String valueName;
    String listName;
    
    public DebugTask() {
        this.valueName = null;
        this.listName = null;
    }
    
    @Override
    public int executeBulkTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        throw new ExilityException("Bulk Use task is not yet implemented");
    }
    
    @Override
    public int executeTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        int workDone = 0;
        String debugString = null;
        debugString = "<table border='1'><tr><td>";
        debugString = String.valueOf(debugString) + "Debug Task : " + this.taskName;
        debugString = String.valueOf(debugString) + "</td></tr>";
        if (this.valueName != null) {
            debugString = String.valueOf(debugString) + "<tr><td>values are </td></tr>";
            if (dc.values.containsKey(this.valueName)) {
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + this.valueName + "</b></td><td>" + dc.values.get(this.valueName).toString() + "</td></tr></table></td></tr>";
            }
        }
        else if (this.listName != null) {
            debugString = String.valueOf(debugString) + "<tr><td>lists are </td></tr>";
            if (dc.lists.containsKey(this.listName)) {
                final ValueList valList = dc.lists.get(this.valueName);
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + this.listName + " has " + valList.length() + " values </b></td></tr>";
                for (int ctr = 0; ctr < valList.length(); ++ctr) {
                    debugString = String.valueOf(debugString) + "<tr><td>" + valList.getValue(ctr).toString() + "</td></tr>";
                }
                debugString = String.valueOf(debugString) + "</table></td></tr>";
            }
        }
        else if (this.gridName != null) {
            debugString = String.valueOf(debugString) + "<tr><td>grids are </td></tr>";
            if (dc.grids.containsKey(this.gridName)) {
                final Grid valGrid = dc.grids.get(this.gridName);
                final String[] colNames = valGrid.getColumnNames();
                final int noOfRows = valGrid.getNumberOfRows();
                final int noOfCols = valGrid.getNumberOfColumns();
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + this.gridName + " has " + noOfRows + " rows and " + noOfCols + " columns </b></td></tr>";
                debugString = String.valueOf(debugString) + "<tr>";
                for (int ctr2 = 0; ctr2 < noOfCols; ++ctr2) {
                    debugString = String.valueOf(debugString) + "<td><b>" + colNames[ctr2] + "</b></td>";
                }
                debugString = String.valueOf(debugString) + "</tr><tr>";
                for (int ctr2 = 0; ctr2 < noOfCols; ++ctr2) {
                    debugString = String.valueOf(debugString) + "<td>" + valGrid.getColumn(colNames[ctr2]).getValueType() + "</td>";
                }
                debugString = String.valueOf(debugString) + "</tr>";
                for (int rowCtr = 0; rowCtr < noOfRows; ++rowCtr) {
                    debugString = String.valueOf(debugString) + "<tr>";
                    for (int colCtr = 0; colCtr < noOfCols; ++colCtr) {
                        debugString = String.valueOf(debugString) + "<td>" + valGrid.getValue(colNames[colCtr], rowCtr).toString() + "</td>";
                    }
                    debugString = String.valueOf(debugString) + "</tr>";
                }
                debugString = String.valueOf(debugString) + "</table></td></tr>";
            }
        }
        else {
            debugString = String.valueOf(debugString) + "<tr><td>values are </td></tr>";
            for (final String key : dc.values.keySet()) {
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + key + "</b></td><td>" + dc.values.get(key).toString() + "</td></tr></table></td></tr>";
            }
            debugString = String.valueOf(debugString) + "<tr><td>lists are </td></tr>";
            for (final String key : dc.lists.keySet()) {
                final ValueList valList2 = dc.lists.get(key);
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + key + " has " + valList2.length() + " values </b></td></tr>";
                for (int ctr3 = 0; ctr3 < valList2.length(); ++ctr3) {
                    debugString = String.valueOf(debugString) + "<tr><td>" + valList2.getValue(ctr3).toString() + "</td></tr>";
                }
                debugString = String.valueOf(debugString) + "</table></td></tr>";
            }
            debugString = String.valueOf(debugString) + "<tr><td>grids are </td></tr>";
            for (final String key : dc.grids.keySet()) {
                final Grid valGrid2 = dc.grids.get(key);
                final String[] colNames2 = valGrid2.getColumnNames();
                final int noOfRows2 = valGrid2.getNumberOfRows();
                final int noOfCols2 = valGrid2.getNumberOfColumns();
                debugString = String.valueOf(debugString) + "<tr><td><table border='1'><tr><td><b>" + key + " has " + noOfRows2 + " rows and " + noOfCols2 + " columns </b></td></tr>";
                debugString = String.valueOf(debugString) + "<tr>";
                for (int ctr4 = 0; ctr4 < noOfCols2; ++ctr4) {
                    debugString = String.valueOf(debugString) + "<td><b>" + colNames2[ctr4] + "</b></td>";
                }
                debugString = String.valueOf(debugString) + "</tr>";
                for (int rowCtr2 = 0; rowCtr2 < noOfRows2; ++rowCtr2) {
                    debugString = String.valueOf(debugString) + "<tr>";
                    for (int colCtr2 = 0; colCtr2 < noOfCols2; ++colCtr2) {
                        debugString = String.valueOf(debugString) + "<td>" + valGrid2.getValue(colNames2[colCtr2], rowCtr2).toString() + "</td>";
                    }
                    debugString = String.valueOf(debugString) + "</tr>";
                }
                debugString = String.valueOf(debugString) + "</table></td></tr>";
            }
        }
        debugString = String.valueOf(debugString) + "</table>";
        Spit.out(debugString);
        workDone = 1;
        return workDone;
    }
}
