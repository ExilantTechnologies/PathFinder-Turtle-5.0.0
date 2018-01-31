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

class GridSpec
{
    String name;
    boolean isOptional;
    int minRows;
    int maxRows;
    FieldSpec[] columns;
    
    GridSpec() {
        this.name = null;
        this.isOptional = false;
        this.minRows = 0;
        this.maxRows = 0;
        this.columns = new FieldSpec[0];
    }
    
    void translateInput(final ServiceData inData, final DataCollection dc) {
        if (this.columns == null || this.columns.length == 0) {
            return;
        }
        final String[][] data = inData.getGrid(this.name);
        if (data == null || data.length == 0) {
            if (!this.isOptional) {
                dc.addMessage("exilGridIsRequired", this.name);
            }
            return;
        }
        final int nbrDataRows = data.length - 1;
        if (nbrDataRows < this.minRows) {
            dc.addMessage("exilNotEnoughRows", this.name, String.valueOf(this.minRows), String.valueOf(nbrDataRows));
            return;
        }
        if (this.maxRows > 0 && nbrDataRows > this.maxRows) {
            dc.addMessage("exilTooManyRows", this.name, String.valueOf(this.maxRows), String.valueOf(nbrDataRows));
            return;
        }
        final Grid grid = new Grid(this.name);
        FieldSpec[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final FieldSpec column = columns[i];
            final String fieldName = column.name;
            final String[] columnData = this.getColumnData(fieldName, data);
            if (columnData == null) {
                dc.addMessage("exilNoColumnInGrid", this.name, fieldName);
                return;
            }
            final AbstractDataType dt = column.getDataType();
            final ValueList valueList = dt.getValueList(fieldName, columnData, column.isOptional, dc);
            try {
                grid.addColumn(fieldName, valueList);
            }
            catch (ExilityException e) {
                Spit.out(e);
                dc.addError(e.getMessage());
                return;
            }
        }
        dc.addGrid(this.name, grid);
    }
    
    private String[] getColumnData(final String fieldName, final String[][] data) {
        int columnIndex = -1;
        final String[] names = data[0];
        for (int i = 0; i < names.length; ++i) {
            if (names[i].equals(fieldName)) {
                columnIndex = i;
                break;
            }
        }
        if (columnIndex < 0) {
            return null;
        }
        final String[] columnData = new String[data.length - 1];
        for (int row = 0; row < columnData.length; ++row) {
            final String[] aRow = data[row + 1];
            if (aRow.length > columnIndex) {
                columnData[row] = aRow[columnIndex];
            }
            else {
                columnData[row] = "";
            }
        }
        return columnData;
    }
    
    boolean validate(final DataCollection dc, final DbHandle handle) {
        boolean valueToReturn = true;
        FieldSpec[] columns;
        for (int length = (columns = this.columns).length, i = 0; i < length; ++i) {
            final FieldSpec column = columns[i];
            valueToReturn = (valueToReturn && column.validateColumn(this.name, dc, handle));
        }
        return valueToReturn;
    }
    
    void translateOutput(final DataCollection dc, final ServiceData outData) {
        final Grid grid = dc.getGrid(this.name);
        if (grid == null) {
            dc.addMessage("exilNoGrid", this.name);
            return;
        }
        final String[][] data = grid.getRawData(this.columns);
        outData.addGrid(this.name, data);
    }
    
    void translate(final DataCollection fromDc, final DataCollection toDc) {
        toDc.addGrid(this.name, fromDc.getGrid(this.name));
    }
}
