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

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class Denormalizer implements GridProcessorInterface
{
    static final String REPEATED_IDS = "RepeatedIds";
    static final String REPEATED_HEADINGS = "RepeatedHeadings";
    static final String DENORMALIZED_PRIMARY_KEY_NAME = "DenormalizedPrimaryKeyName";
    static final String DENORMALIZED_SECONDARY_KEY_NAME = "DenormalizedSecondaryKeyName";
    String outputGridName;
    String inputParentGridName;
    String inputChildGridName;
    
    public Denormalizer() {
        this.outputGridName = null;
        this.inputParentGridName = null;
        this.inputChildGridName = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final String[][] mainTable = dc.getGrid(this.inputParentGridName).getRawData();
        final String[][] childTable = dc.getGrid(this.inputChildGridName).getRawData();
        final int nbrMainRows = mainTable.length;
        final int nbrChildRows = childTable.length;
        if (nbrMainRows == 0 || mainTable[0].length == 0) {
            dc.addMessage("exilNoDataInGrid", this.inputParentGridName);
            return 0;
        }
        if (nbrChildRows == 0 || childTable[0].length == 0) {
            dc.addMessage("exilNoDataInGrid", this.inputChildGridName);
            return 0;
        }
        final int nbrMainCols = mainTable[0].length;
        final int nbrChildCols = childTable[0].length;
        final Map<String, String> distinctKeys = new HashMap<String, String>();
        final Map<String, String[]> dataRows = new HashMap<String, String[]>();
        getHashedValues(childTable, distinctKeys, dataRows);
        final int nbrDistinctKeys = distinctKeys.size();
        final String[] uniqueKeys = new String[nbrDistinctKeys];
        final StringBuilder keysToOutput = new StringBuilder();
        final StringBuilder displaysToOutput = new StringBuilder();
        int i = 0;
        for (final String key : distinctKeys.keySet()) {
            uniqueKeys[i] = key;
            ++i;
            keysToOutput.append(key).append('\t');
            displaysToOutput.append(distinctKeys.get(key)).append('\t');
        }
        dc.addTextValue(String.valueOf(this.outputGridName) + "RepeatedIds", keysToOutput.delete(keysToOutput.length() - 1, 1).toString());
        dc.addTextValue(String.valueOf(this.outputGridName) + "RepeatedHeadings", displaysToOutput.delete(displaysToOutput.length() - 1, 1).toString());
        dc.addTextValue(String.valueOf(this.outputGridName) + "DenormalizedPrimaryKeyName", mainTable[0][0]);
        dc.addTextValue(String.valueOf(this.outputGridName) + "DenormalizedSecondaryKeyName", childTable[0][1]);
        final int totalCols = nbrMainCols + (nbrChildCols - 3) * nbrDistinctKeys;
        final String[][] grid = new String[nbrMainRows][];
        for (i = 0; i < nbrMainRows; ++i) {
            grid[i] = new String[totalCols];
            for (int j = 0; j < nbrMainCols; ++j) {
                grid[i][j] = mainTable[i][j];
            }
        }
        int mainColIdx = nbrMainCols;
        String[] array;
        for (int length = (array = uniqueKeys).length, n = 0; n < length; ++n) {
            final String childKey = array[n];
            for (int k = 3; k < nbrChildCols; ++k) {
                grid[0][mainColIdx] = String.valueOf(childTable[0][k]) + "__" + childKey;
                ++mainColIdx;
            }
        }
        final String[] blankRow = new String[nbrChildCols];
        for (int l = 0; l < nbrChildCols; ++l) {
            blankRow[l] = "";
        }
        for (i = 1; i < nbrMainRows; ++i) {
            final String mainKey = mainTable[i][0];
            mainColIdx = nbrMainCols;
            String[] array2;
            for (int length2 = (array2 = uniqueKeys).length, n2 = 0; n2 < length2; ++n2) {
                final String childKey2 = array2[n2];
                String[] vals = dataRows.get(String.valueOf(mainKey) + "_" + childKey2);
                if (vals == null) {
                    vals = blankRow;
                }
                for (int m = 3; m < nbrChildCols; ++m) {
                    grid[i][mainColIdx] = vals[m];
                    ++mainColIdx;
                }
            }
        }
        dc.addGrid(this.outputGridName, grid);
        return 1;
    }
    
    private static void getHashedValues(final String[][] childTable, final Map<String, String> distinctKeys, final Map<String, String[]> dataRows) {
        for (int n = childTable.length, i = 1; i < n; ++i) {
            final String[] aRow = childTable[i];
            distinctKeys.put(aRow[1], aRow[2]);
            dataRows.put(String.valueOf(aRow[0]) + "_" + aRow[1], aRow);
        }
    }
    
    static void main(final String[] args) {
        final String[][] mainTable = { { "M1", "M2", "M3" }, { "1", "A1", "B1" }, { "2", "A2", "B2" }, { "3", "A3", "B3" }, { "4", "A4", "B4" } };
        final String[][] childTable = { { "M1", "C1", "C2", "C3", "C4" }, { "1", "1", "S", "1C3S20", "1C4S40" }, { "1", "2", "M", "1C3M340", "1C4M220" }, { "1", "3", "L", "1C3L30", "1C4L345" }, { "2", "4", "X", "2C3X40", "2C4X23" }, { "2", "1", "S", "2C3S100", "2C4S100" }, { "4", "2", "M", "4C3M2000", "4C4M2000" } };
        final DataCollection dc = new DataCollection();
        final String childTablename = "childTable";
        final String mainTableName = "mainTable";
        dc.addGrid(childTablename, childTable);
        dc.addGrid(mainTableName, mainTable);
        final Denormalizer dn = new Denormalizer();
        dn.outputGridName = "denormalizedTable";
        dn.inputParentGridName = mainTableName;
        dn.inputChildGridName = childTablename;
        dn.process(dc);
        Spit.out(dc.toString());
    }
}
