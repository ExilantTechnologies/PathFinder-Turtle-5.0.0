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

import java.util.Map;
import java.util.HashMap;

public class Normalizer implements GridProcessorInterface
{
    static final String DISTINCT_KEYS_NAME = "RepeatedIds";
    String inputGridName;
    String outputGridName;
    String[] columnNames;
    
    public Normalizer() {
        this.inputGridName = null;
        this.outputGridName = null;
        this.columnNames = new String[0];
    }
    
    @Override
    public int process(final DataCollection dc) {
        final String keysName = String.valueOf(this.inputGridName) + "RepeatedIds";
        if (!dc.hasValue(keysName)) {
            dc.addMessage("exilNoDistinctKeys", keysName);
            return 0;
        }
        final String keysValue = dc.getTextValue(keysName, "");
        final String[] keys = keysValue.split("\t");
        final int nbrKeys = keys.length;
        final int nbrFields = this.columnNames.length;
        final String[][] inputGrid = dc.getGrid(this.inputGridName).getRawData();
        if (inputGrid.length == 0 || inputGrid[0].length == 0) {
            dc.addMessage("exilNoGrid", this.inputGridName);
        }
        final int nbrVals = inputGrid.length - 1;
        final int nbrRows = 1 + nbrVals * nbrKeys;
        final String[][] grid = new String[nbrRows][];
        for (int i = 0; i < grid.length; ++i) {
            grid[i] = new String[nbrFields];
        }
        for (int j = 0; j < nbrFields; ++j) {
            grid[0][j] = this.columnNames[j];
        }
        final Map<String, Integer> columnMap = new HashMap<String, Integer>();
        final String[] header = inputGrid[0];
        for (int k = header.length - 1; k >= 0; --k) {
            columnMap.put(header[k], new Integer(k));
        }
        final int foreignKeyColIdx = columnMap.get(this.columnNames[0]);
        int rowIdx = 1;
        for (int keyIdx = 0; keyIdx < nbrKeys; ++keyIdx) {
            for (int inputRowIdx = 1; inputRowIdx <= nbrVals; ++inputRowIdx) {
                grid[rowIdx][0] = inputGrid[inputRowIdx][foreignKeyColIdx];
                grid[rowIdx][1] = keys[keyIdx];
                ++rowIdx;
            }
        }
        for (int colIdx = 2; colIdx < nbrFields; ++colIdx) {
            rowIdx = 1;
            for (int keyIdx2 = 0; keyIdx2 < nbrKeys; ++keyIdx2) {
                final int thisColIdx = columnMap.get(String.valueOf(this.columnNames[colIdx]) + "__" + keys[keyIdx2]);
                for (int inputRowIdx2 = 1; inputRowIdx2 <= nbrVals; ++inputRowIdx2) {
                    grid[rowIdx][colIdx] = inputGrid[inputRowIdx2][thisColIdx];
                    ++rowIdx;
                }
            }
        }
        dc.addGrid(this.outputGridName, grid);
        return 1;
    }
}
