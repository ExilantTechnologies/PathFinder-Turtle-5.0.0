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

import java.util.ArrayList;

public class Deaggregater implements GridProcessorInterface
{
    String inputGridName;
    String outputGridName;
    String outputColumnNames;
    String deaggrgatedInputColumnName;
    String deaggrgatedOutputColumnName;
    String additionalOutputColumnName;
    
    public Deaggregater() {
        this.inputGridName = null;
        this.outputGridName = null;
        this.outputColumnNames = null;
        this.deaggrgatedInputColumnName = null;
        this.deaggrgatedOutputColumnName = null;
        this.additionalOutputColumnName = null;
    }
    
    @Override
    public int process(final DataCollection dc) {
        final String[][] inputGrid = dc.getGrid(this.inputGridName).getRawData();
        if (inputGrid == null || inputGrid.length == 0 || inputGrid[0].length == 0) {
            Spit.out(String.valueOf(this.inputGridName) + " not found or has no data in it");
            return 0;
        }
        final int nbrInputRows = inputGrid.length;
        final int nbrInputColumns = inputGrid[0].length;
        final int nbrOutputColumns = (this.deaggrgatedInputColumnName == null) ? nbrInputColumns : (nbrInputColumns + 1);
        final ArrayList<String[]> newRows = new ArrayList<String[]>();
        String[] newRow = new String[nbrOutputColumns];
        for (int j = 0; j < nbrInputColumns; ++j) {
            newRow[j] = inputGrid[0][j];
        }
        newRow[nbrInputColumns - 1] = this.deaggrgatedOutputColumnName;
        if (this.additionalOutputColumnName != null) {
            newRow[nbrInputColumns] = this.additionalOutputColumnName;
        }
        newRows.add(newRow);
        for (int i = 1; i < nbrInputRows; ++i) {
            final String[] packedFields = inputGrid[i][nbrInputColumns - 1].trim().split(",");
            String[] array;
            for (int length = (array = packedFields).length, l = 0; l < length; ++l) {
                final String pfield = array[l];
                newRow = new String[nbrOutputColumns];
                for (int k = nbrInputColumns - 2; k >= 0; --k) {
                    newRow[k] = inputGrid[i][k];
                }
                final String packedField = pfield.trim();
                if (packedField.length() != 0) {
                    if (this.additionalOutputColumnName == null) {
                        newRow[nbrInputColumns - 1] = packedField;
                    }
                    else {
                        final String[] unpackedFields = packedField.split("-");
                        newRow[nbrInputColumns - 1] = unpackedFields[0].trim();
                        newRow[nbrInputColumns] = unpackedFields[1].trim();
                    }
                    newRows.add(newRow);
                }
            }
        }
        final String[][] outputGrid = newRows.toArray(new String[0][]);
        dc.addGrid(this.outputGridName, outputGrid);
        return 1;
    }
    
    static void main(final String[] args) {
        final Deaggregater ag = new Deaggregater();
        final String inputGridName = "inputGrid";
        final String outputGridName = "outputGrid";
        ag.inputGridName = inputGridName;
        ag.outputGridName = outputGridName;
        ag.outputColumnNames = "k1,k2";
        ag.deaggrgatedInputColumnName = "k3";
        ag.deaggrgatedOutputColumnName = "size";
        ag.additionalOutputColumnName = "qty";
        final DataCollection dc = new DataCollection();
        final String[][] inputGrid = { { "k1", "k2", "k3" }, { "a1", "b3", "S-10, M - 20 , C-20" }, { "a2", "b2", "" }, { "a3", "b1", "P-21" } };
        dc.addGrid(inputGridName, inputGrid);
        ag.process(dc);
        Spit.out(dc.toString());
    }
}
