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

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class XLSReader
{
    private HashMap<Integer, ColumnMetaData> columnsData;
    private List<Value[]> rows;
    public static final int UNKNOWN_TYPE = -1;
    public static final int UNKNOWN_COLUMN_IDX = -1;
    public static final String UNKNOWN_COLUMN_NAME = "UNKNOWN";
    private static final String AT_SYMBOL = "@";
    private static final String INVALID_COLUMN_TYPE = " has invailid excel data type in row ";
    private static final String SKIP_BLANK_ROW = "Skiping blank row---->";
    private static final String INVALID_HEADER = "\n Sheet must have first non-empty row as valid column header.No blank column allow between first and last column in first row.";
    private static final String INSUFFICIENT_DATA_ROWS = " has insufficient data rows to be read.It must have more than 1 data rows.";
    private static final String ILLEGAL_ARGUMENT = "Supplied Parameter(s) canot be null";
    private static final String EXCEPTION_MSG = "@1 colud not read because of an exception\n@2. \n Please check in log for full stackTrace.";
    private static final String DATATYPE_MISMATCH = "Trying to set @1 value and expected @2 value for column '@3' in row @4";
    private static final String DATA_ELEMENT_NOT_IN_DICTIONARY = " not defined in data dictionary. Excel column type will be assigned";
    
    public XLSReader() {
        this.columnsData = null;
        this.rows = null;
        this.columnsData = new HashMap<Integer, ColumnMetaData>();
        this.rows = new ArrayList<Value[]>();
    }
    
    public void readAWorkbook(final Workbook wb, final DataCollection dc) {
        if (wb == null || dc == null) {
            throw new IllegalArgumentException("Supplied Parameter(s) canot be null");
        }
        final int nbrSheets = wb.getNumberOfSheets();
        String sheetName = null;
        String gridName = dc.getTextValue("gridName", null);
        Sheet sheet = null;
        int nbrColumns = -1;
        int nbrPhysicalRows = 0;
        for (int k = 0; k < nbrSheets; ++k) {
            sheet = wb.getSheetAt(k);
            sheetName = sheet.getSheetName();
            nbrPhysicalRows = sheet.getPhysicalNumberOfRows();
            if (nbrPhysicalRows < 2) {
                Spit.out(String.valueOf(sheetName) + " has insufficient data rows to be read.It must have more than 1 data rows.");
            }
            else {
                try {
                    nbrColumns = this.readASheet(sheet);
                }
                catch (ExilityException e) {
                    final String msg = this.replaceMessageParams("@1 colud not read because of an exception\n@2. \n Please check in log for full stackTrace.", new String[] { sheetName, e.getMessage() });
                    dc.addError(msg);
                    Spit.out(e);
                }
                if (nbrColumns != -1) {
                    if (gridName != null) {
                        sheetName = gridName;
                        gridName = null;
                    }
                    dc.addGrid(sheetName, this.getGrid());
                    Spit.out(String.valueOf(sheetName) + " added to dc with " + this.rows.size() + " row(s)");
                    this.columnsData.clear();
                    this.rows.clear();
                }
            }
        }
    }
    
    public int readASheet(final Sheet sheet) throws ExilityException {
        int nonEmptyFirstRowIdx = 0;
        int lastRowIdx = 0;
        final int nbrPhysicalRows = sheet.getPhysicalNumberOfRows();
        final String sheetName = sheet.getSheetName();
        if (nbrPhysicalRows < 2) {
            Spit.out(String.valueOf(sheetName) + " has insufficient data rows to be read.It must have more than 1 data rows.");
            return -1;
        }
        try {
            nonEmptyFirstRowIdx = sheet.getFirstRowNum();
            lastRowIdx = sheet.getLastRowNum();
            final Row headerRow = sheet.getRow(nonEmptyFirstRowIdx);
            for (int nbrCol = headerRow.getPhysicalNumberOfCells(), colIdx = 0; colIdx < nbrCol; ++colIdx) {
                final Cell hCell = headerRow.getCell(colIdx);
                if (hCell == null || hCell.getCellType() == 3) {
                    Spit.out("Error--->Found blank column " + (colIdx + 1) + " in Sheet " + sheetName + "\n Sheet must have first non-empty row as valid column header.No blank column allow between first and last column in first row.");
                    this.columnsData.clear();
                    return -1;
                }
                final String columnName = hCell.getStringCellValue();
                this.setDataType(columnName, colIdx);
            }
        }
        catch (Exception e) {
            Spit.out(String.valueOf(sheetName) + "\n Sheet must have first non-empty row as valid column header.No blank column allow between first and last column in first row.");
            Spit.out(e);
            return -1;
        }
        final int nbrColumnsInARow = this.columnsData.size();
        Spit.out(String.valueOf(sheetName) + ":\n");
        for (int rowIdx = nonEmptyFirstRowIdx + 1; rowIdx <= lastRowIdx; ++rowIdx) {
            final Row row = sheet.getRow(rowIdx);
            if (row == null) {
                Spit.out("Skiping blank row---->" + rowIdx);
            }
            else {
                this.readARow(row, nbrColumnsInARow);
            }
        }
        return this.columnsData.size();
    }
    
    private void readARow(final Row row, final int nbrColumnsInARow) throws ExilityException {
        final Value[] columnValues = new Value[nbrColumnsInARow];
        Value aColumnValue = null;
        String rawValue = null;
        for (int c = 0; c < nbrColumnsInARow; ++c) {
            final Cell cell = row.getCell(c, Row.CREATE_NULL_AS_BLANK);
            final ColumnMetaData columnInfo = this.columnsData.get(new Integer(c));
            final int xlsColumnDataType = columnInfo.getXlsDataType();
            DataValueType exilDataType = null;
            int cellType = cell.getCellType();
            if (xlsColumnDataType != -1) {
                cellType = xlsColumnDataType;
            }
            try {
                switch (cellType) {
                    case 0: {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            rawValue = DateUtility.formatDate(cell.getDateCellValue());
                            exilDataType = DataValueType.DATE;
                            break;
                        }
                        final double decimalNumber = cell.getNumericCellValue();
                        rawValue = NumberToTextConverter.toText(decimalNumber);
                        final boolean isDecimal = rawValue.contains(".");
                        if (isDecimal) {
                            exilDataType = DataValueType.DECIMAL;
                            break;
                        }
                        exilDataType = DataValueType.INTEGRAL;
                        break;
                    }
                    case 1: {
                        rawValue = cell.getStringCellValue().trim();
                        exilDataType = DataValueType.TEXT;
                        break;
                    }
                    case 2: {
                        rawValue = cell.getStringCellValue().trim();
                        exilDataType = DataValueType.TEXT;
                        break;
                    }
                    case 3: {
                        rawValue = cell.getStringCellValue();
                        exilDataType = DataValueType.NULL;
                        columnInfo.setExilDataType(exilDataType);
                        break;
                    }
                    case 4: {
                        rawValue = (cell.getBooleanCellValue() ? "true" : "false");
                        exilDataType = DataValueType.BOOLEAN;
                        break;
                    }
                    default: {
                        final String msg = String.valueOf(columnInfo.getColumnName()) + " has invailid excel data type in row " + row.getRowNum();
                        Spit.out(msg);
                        break;
                    }
                }
            }
            catch (Exception e) {
                final String[] params = { this.getXlsTypeAsText(cell.getCellType()), this.getXlsTypeAsText(cellType), columnInfo.getColumnName(), new StringBuilder().append(row.getRowNum()).toString() };
                final String message = this.replaceMessageParams("Trying to set @1 value and expected @2 value for column '@3' in row @4", params);
                throw new ExilityException(message);
            }
            if (xlsColumnDataType == -1 && cellType != 3) {
                columnInfo.setXlsDataType(cellType);
                columnInfo.setExilDataType(exilDataType);
            }
            exilDataType = columnInfo.getExilDataType();
            aColumnValue = Value.newValue(rawValue, exilDataType);
            columnValues[c] = aColumnValue;
            this.columnsData.put(new Integer(c), columnInfo);
        }
        this.rows.add(columnValues);
    }
    
    private void setDataType(final String columnName, final int colIdx) {
        final DataElement de = DataDictionary.getElement(columnName);
        AbstractDataType dt = null;
        if (de != null) {
            dt = DataTypes.getInstance().dataTypes.get(de.dataType);
        }
        final ColumnMetaData colInfo = new ColumnMetaData();
        if (de == null || dt == null) {
            Spit.out(String.valueOf(columnName) + " not defined in data dictionary. Excel column type will be assigned");
            colInfo.setColumnIndex(colIdx);
            colInfo.setColumnName(columnName);
            this.columnsData.put(new Integer(colIdx), colInfo);
            return;
        }
        final DataValueType valueType = dt.getValueType();
        Spit.out(String.valueOf(columnName) + " has data type " + valueType + " in data dictionary");
        int xlDataType = -1;
        switch (valueType) {
            case INTEGRAL:
            case DECIMAL:
            case DATE:
            case TIMESTAMP: {
                xlDataType = 0;
                break;
            }
            case BOOLEAN: {
                xlDataType = 4;
                break;
            }
            case TEXT: {
                xlDataType = 1;
                break;
            }
            case NULL: {
                xlDataType = 1;
                break;
            }
        }
        colInfo.setColumnIndex(colIdx);
        colInfo.setColumnName(columnName);
        colInfo.setExilDataType(valueType);
        colInfo.setXlsDataType(xlDataType);
        this.columnsData.put(new Integer(colIdx), colInfo);
    }
    
    public Grid getGrid() {
        final int nbrColumns = this.columnsData.size();
        final Grid aGrid = new Grid();
        final DataValueType[] valueTypes = new DataValueType[nbrColumns];
        final String[] columnNames = new String[nbrColumns];
        ColumnMetaData colInfo = null;
        for (int colIdx = 0; colIdx < nbrColumns; ++colIdx) {
            colInfo = this.columnsData.get(new Integer(colIdx));
            valueTypes[colIdx] = colInfo.getExilDataType();
            columnNames[colIdx] = colInfo.getColumnName();
        }
        aGrid.setValues(columnNames, valueTypes, this.rows, null);
        return aGrid;
    }
    
    public String getXlsTypeAsText(final int cellType) {
        String rawType = null;
        switch (cellType) {
            case 0: {
                rawType = "NUMERIC";
                break;
            }
            case 1:
            case 2: {
                rawType = "STRING";
                break;
            }
            case 4: {
                rawType = "BOOLEAN";
                break;
            }
            default: {
                rawType = "UNKNOWN";
                break;
            }
        }
        return rawType;
    }
    
    public String replaceMessageParams(final String message, final String[] params) {
        final int nbrParams = params.length;
        StringBuilder msg = new StringBuilder(message);
        int startAt = 0;
        for (int i = 0; i < nbrParams; ++i) {
            startAt = msg.indexOf("@" + (i + 1));
            msg = msg.replace(startAt, startAt + 2, params[i]);
        }
        return msg.toString();
    }
    
    public void printXlSRec(final String[][] testRows) {
        if (testRows == null) {
            return;
        }
        for (final String[] row : testRows) {
            String[] array;
            for (int length2 = (array = row).length, j = 0; j < length2; ++j) {
                final String aColVal = array[j];
                Spit.out(String.valueOf(aColVal) + "\t");
            }
            Spit.out("\n");
        }
    }
    
    private class ColumnMetaData
    {
        private DataValueType exilDataType;
        private int xlsDataType;
        private int columnIndex;
        private String columnName;
        
        public ColumnMetaData() {
            this.exilDataType = DataValueType.TEXT;
            this.xlsDataType = -1;
            this.columnIndex = -1;
            this.columnName = "UNKNOWN";
        }
        
        public DataValueType getExilDataType() {
            return this.exilDataType;
        }
        
        public void setExilDataType(final DataValueType dtVal) {
            this.exilDataType = dtVal;
        }
        
        public int getXlsDataType() {
            return this.xlsDataType;
        }
        
        public void setXlsDataType(final int xlsDataType) {
            this.xlsDataType = xlsDataType;
        }
        
        public int getColumnIndex() {
            return this.columnIndex;
        }
        
        public void setColumnIndex(final int columnIndex) {
            this.columnIndex = columnIndex;
        }
        
        public String getColumnName() {
            return this.columnName;
        }
        
        public void setColumnName(final String columnName) {
            this.columnName = columnName;
        }
    }
}
