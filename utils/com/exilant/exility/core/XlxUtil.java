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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Date;
import java.text.NumberFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.apache.poi.ss.usermodel.Workbook;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;

public class XlxUtil
{
    private static final String SHEETS_TO_BE_LOADED = "sheetsToBeLoaded";
    
    public static XlxUtil getInstance() {
        return new XlxUtil();
    }
    
    public String[][] getRawData(final String fullyQualifiedName) {
        final List<Sheet> sheets = this.getSheets(fullyQualifiedName, null);
        if (sheets.size() == 0) {
            return null;
        }
        return this.getRawData(sheets.get(0));
    }
    
    public String[][] getRawData(final InputStream inputStream) {
        final List<Sheet> sheets = this.getSheets(inputStream, null);
        if (sheets.size() == 0) {
            return null;
        }
        return this.getRawData(sheets.get(0));
    }
    
    public Grid getGrid(final String fullyQualifiedName, final boolean useDataDictionary) {
        final List<Sheet> sheets = this.getSheets(fullyQualifiedName, null);
        if (sheets.size() == 0) {
            return null;
        }
        return this.getGrid(sheets.get(0), useDataDictionary);
    }
    
    public Grid getGrid(final InputStream inputStream, final boolean useDataDictionary) {
        final List<Sheet> sheets = this.getSheets(inputStream, null);
        if (sheets.size() == 0) {
            return null;
        }
        return this.getGrid(sheets.get(0), useDataDictionary);
    }
    
    public int extract(final String fullyQualifiedName, final DataCollection dc, final boolean useDataDictionary) {
        try {
            final InputStream inputStream = this.getInputStream(fullyQualifiedName, dc);
            if (inputStream == null) {
                return 0;
            }
            final int n = this.extract(inputStream, dc, useDataDictionary);
            inputStream.close();
            return n;
        }
        catch (Exception ex) {
            return 0;
        }
    }
    
    public int extract(final InputStream inputStream, final DataCollection dc, final boolean useDictionaryForDataType) {
        final List<Sheet> sheets = this.getSheets(inputStream, dc);
        if (sheets.size() == 0) {
            return 0;
        }
        Set<String> sheetsToBeRead = null;
        for (final Sheet sheet : sheets) {
            final String nam = sheet.getSheetName();
            if (nam.equals("values")) {
                this.extractValues(sheet, dc, useDictionaryForDataType);
                final String sheetNames = dc.getTextValue("sheetsToBeLoaded", null);
                if (sheetNames == null) {
                    continue;
                }
                sheetsToBeRead = new HashSet<String>();
                final String[] names = sheetNames.split(",");
                String[] array;
                for (int length = (array = names).length, i = 0; i < length; ++i) {
                    final String sn = array[i];
                    sheetsToBeRead.add(sn.trim());
                }
            }
            else if (sheetsToBeRead != null && !sheetsToBeRead.contains(nam)) {
                Spit.out("skipping sheet " + nam + " as directed by first sheet.");
            }
            else {
                final Grid grid = this.getGrid(sheet, useDictionaryForDataType);
                if (grid == null) {
                    continue;
                }
                dc.addGrid(nam, grid);
            }
        }
        return 1;
    }
    
    public boolean save(final String fullyQualifiedName, final String[][] data) {
        final Workbook workbook = this.getWorkbookForFile(fullyQualifiedName);
        this.addSheet(workbook, data, "data");
        return this.save(workbook, fullyQualifiedName);
    }
    
    public boolean save(final String fullyQualifiedName, final DataCollection dc) {
        final Workbook workbook = this.getWorkbookForFile(fullyQualifiedName);
        return this.save(this.copyToWorkbook(dc, workbook), fullyQualifiedName);
    }
    
    public boolean save(final String fullyQualifiedName, final Grid grid) {
        final Workbook workbook = this.getWorkbookForFile(fullyQualifiedName);
        this.addSheet(workbook, grid);
        return this.save(workbook, fullyQualifiedName);
    }
    
    private List<Sheet> getSheets(final String fullyQualifiedFileName, final DataCollection dc) {
        Spit.out("Going to create an input stream out of file : " + fullyQualifiedFileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fullyQualifiedFileName);
        }
        catch (FileNotFoundException e) {
            final String msg = "error while opening file fullyQualifiedFileName " + e.getMessage();
            if (dc != null) {
                dc.addError(msg);
            }
            Spit.out(msg);
        }
        if (inputStream == null) {
            return new ArrayList<Sheet>();
        }
        final List<Sheet> sheets = this.getSheets(inputStream, dc);
        try {
            inputStream.close();
        }
        catch (IOException ex) {}
        return sheets;
    }
    
    private List<Sheet> getSheets(final InputStream inputStream, final DataCollection dc) {
        final List<Sheet> sheets = new ArrayList<Sheet>();
        Workbook workbook = null;
        boolean valuesSheetFound = false;
        try {
            workbook = WorkbookFactory.create(inputStream);
            final int n = workbook.getNumberOfSheets();
            Spit.out("There are " + n + " sheets.");
            for (int i = 0; i < n; ++i) {
                final Sheet sheet = workbook.getSheetAt(i);
                final int nbrRows = sheet.getPhysicalNumberOfRows();
                final String sheetName = sheet.getSheetName();
                Spit.out("There are " + nbrRows + " rows in " + sheetName);
                if (nbrRows > 0) {
                    sheets.add(sheet);
                }
                if (!valuesSheetFound && sheetName.equals("values")) {
                    if (i != 0) {
                        Spit.out("Shifting values because it was found at " + i);
                        sheets.add(i, sheets.get(0));
                        sheets.add(0, sheet);
                    }
                    valuesSheetFound = true;
                }
            }
        }
        catch (Exception e) {
            final String msg = "Error while reading spread sheet. " + e.getMessage();
            Spit.out(msg);
            if (dc != null) {
                dc.addError(msg);
            }
        }
        return sheets;
    }
    
    private Grid getGrid(final Sheet sheet, final boolean useDataDictionary) {
        final String[][] rawData = this.getRawData(sheet);
        if (rawData == null) {
            return null;
        }
        DataValueType[] types = null;
        if (!useDataDictionary) {
            types = this.getExilityTypes(sheet, rawData[0].length);
        }
        final Grid grid = new Grid(sheet.getSheetName());
        try {
            grid.setRawData(rawData, types);
        }
        catch (ExilityException e) {
            Spit.out("Error while converting sheet " + sheet.getSheetName() + " to grid " + e.getMessage());
        }
        return grid;
    }
    
    private String[][] getRawData(final Sheet sheet) {
        final int lastRow = sheet.getLastRowNum();
        if (lastRow < 1) {
            return null;
        }
        final int firstRowIdx = sheet.getFirstRowNum();
        final Row firstRow = sheet.getRow(firstRowIdx);
        final int firstCellIdx = firstRow.getFirstCellNum();
        final int lastCellAt = firstRow.getLastCellNum();
        final int nbrCells = lastCellAt - firstCellIdx;
        final List<String[]> rawData = new ArrayList<String[]>();
        for (int rowNbr = firstRowIdx; rowNbr < lastRow; ++rowNbr) {
            final Row row = sheet.getRow(rowNbr);
            if (row == null || row.getPhysicalNumberOfCells() == 0) {
                Spit.out("row at " + rowNbr + "is empty. Whiel this is not an error, we certianly discourage this.");
            }
            else {
                rawData.add(this.getTextValues(row, firstCellIdx, nbrCells));
            }
        }
        return rawData.toArray(new String[0][0]);
    }
    
    private String[] getTextValues(final Row row, final int startingCell, final int nbrCells) {
        if (row == null) {
            return null;
        }
        final String[] values = new String[nbrCells];
        for (int lastCell = startingCell + nbrCells, i = startingCell; i < lastCell; ++i) {
            values[i] = this.getTextValue(row.getCell(i));
        }
        return values;
    }
    
    private String getTextValue(final Cell cell) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        if (cellType == 2) {
            cellType = cell.getCachedFormulaResultType();
        }
        if (cellType == 0) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return DateUtility.formatDate(cell.getDateCellValue());
            }
            return NumberFormat.getInstance().format(cell.getNumericCellValue());
        }
        else {
            if (cellType != 4) {
                return cell.getStringCellValue().trim();
            }
            if (cell.getBooleanCellValue()) {
                return "1";
            }
            return "0";
        }
    }
    
    private void extractValues(final Sheet sheet, final DataCollection dc, final boolean useDictionaryForDataType) {
        for (int n = sheet.getLastRowNum(), i = 1; i < n; ++i) {
            final Row row = sheet.getRow(i);
            if (row != null) {
                final int nbrCells = row.getLastCellNum();
                if (nbrCells >= 1) {
                    final String fieldName = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                    if (fieldName.length() != 0) {
                        final Cell dataCell = null;
                        String fieldValue = "";
                        if (nbrCells > 1) {
                            fieldValue = this.getTextValue(row.getCell(1, Row.CREATE_NULL_AS_BLANK));
                        }
                        if (useDictionaryForDataType) {
                            dc.addValueAfterCheckingInDictionary(fieldName, fieldValue);
                        }
                        else {
                            dc.addValue(fieldName, fieldValue, this.getExilityType(dataCell));
                        }
                    }
                }
            }
        }
    }
    
    private DataValueType[] getExilityTypes(final Sheet sheet, final int nbrCells) {
        final DataValueType[] types = new DataValueType[nbrCells];
        for (int i = 0; i < nbrCells; ++i) {
            types[i] = DataValueType.NULL;
        }
        final int rowStart = sheet.getFirstRowNum();
        final int rowEnd = sheet.getLastRowNum();
        int nbrFound = 0;
        final Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        final int startingCellIdx = firstRow.getFirstCellNum();
        final int endCellIdx = startingCellIdx + nbrCells;
        for (int j = rowStart; j < rowEnd; ++j) {
            final Row row = sheet.getRow(j);
            if (row != null) {
                for (int k = startingCellIdx; k < endCellIdx; ++k) {
                    if (types[k] == DataValueType.NULL) {
                        final Cell cell = row.getCell(k, Row.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            types[k] = this.getExilityType(cell);
                            if (++nbrFound == nbrCells) {
                                return types;
                            }
                        }
                    }
                }
            }
        }
        for (int j = 0; j < nbrCells; ++j) {
            if (types[j] == DataValueType.NULL) {
                types[j] = DataValueType.TEXT;
            }
        }
        return types;
    }
    
    private DataValueType getExilityType(final Cell cell) {
        if (cell == null) {
            return DataValueType.TEXT;
        }
        int cellType = cell.getCellType();
        if (cellType == 2) {
            cellType = cell.getCachedFormulaResultType();
        }
        if (cellType == 0) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return DataValueType.DATE;
            }
            return DataValueType.DECIMAL;
        }
        else {
            if (cellType == 4) {
                return DataValueType.BOOLEAN;
            }
            return DataValueType.TEXT;
        }
    }
    
    private InputStream getInputStream(final String fullyQualifiedName, final DataCollection dc) {
        try {
            return new FileInputStream(fullyQualifiedName);
        }
        catch (Exception e) {
            final String msg = "error while opening file " + fullyQualifiedName + e.getMessage();
            if (dc != null) {
                dc.addError(msg);
            }
            Spit.out(msg);
            return null;
        }
    }
    
    private Workbook addSheet(final Workbook workbook, final String[][] data, final String sheetName) {
        final Sheet sheet = workbook.createSheet(sheetName);
        int rowIdx = 0;
        for (final String[] dataRow : data) {
            final Row row = sheet.createRow(rowIdx);
            ++rowIdx;
            int cellIdx = 0;
            String[] array;
            for (int length2 = (array = dataRow).length, j = 0; j < length2; ++j) {
                final String val = array[j];
                row.createCell(cellIdx).setCellValue(val);
                ++cellIdx;
            }
        }
        return workbook;
    }
    
    private Workbook copyToWorkbook(final DataCollection dc, final Workbook workbook) {
        final Sheet sheet = workbook.createSheet("values");
        final Row header = sheet.createRow(0);
        final Row dataRow = sheet.createRow(1);
        int colIdx = 0;
        final String[] fieldNames = dc.getFieldNames();
        String[] array;
        for (int length = (array = fieldNames).length, i = 0; i < length; ++i) {
            final String fieldName = array[i];
            header.createCell(colIdx).setCellValue(fieldName);
            this.setValue(dataRow.createCell(colIdx), dc.getValue(fieldName));
            ++colIdx;
        }
        final String[] gridNames = dc.getGridNames();
        if (gridNames != null && gridNames.length == 0) {
            String[] array2;
            for (int length2 = (array2 = gridNames).length, j = 0; j < length2; ++j) {
                final String gridName = array2[j];
                this.addSheet(workbook, dc.getGrid(gridName));
            }
        }
        return workbook;
    }
    
    private void addSheet(final Workbook workbook, final Grid grid) {
        final Sheet sheet = workbook.createSheet(grid.getName());
        for (int nbrRows = grid.getNumberOfRows() + 1, rowIdx = 0; rowIdx < nbrRows; ++rowIdx) {
            sheet.createRow(rowIdx);
        }
        int colIdx = 0;
        String[] columnNames;
        for (int length = (columnNames = grid.getColumnNames()).length, i = 0; i < length; ++i) {
            final String columnName = columnNames[i];
            this.addColumn(sheet, colIdx, grid.getColumn(columnName), columnName);
            ++colIdx;
        }
    }
    
    private void setValue(final Cell cell, final Value value) {
        if (value == null || value.isNull()) {
            cell.setCellValue("");
            return;
        }
        switch (value.getValueType()) {
            case BOOLEAN: {
                cell.setCellValue(value.getBooleanValue());
                break;
            }
            case DATE: {
                cell.setCellValue(value.getDateValue());
                break;
            }
            case DECIMAL: {
                cell.setCellValue(value.getDecimalValue());
                break;
            }
            case INTEGRAL: {
                cell.setCellValue((double)value.getIntegralValue());
                break;
            }
            case NULL: {
                cell.setCellValue("");
                break;
            }
            case TEXT: {
                cell.setCellValue(value.getTextValue());
                break;
            }
            case TIMESTAMP: {
                cell.setCellValue(value.getDateValue());
                break;
            }
            default: {
                cell.setCellValue(value.getTextValue());
                break;
            }
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final ValueList values, final String header) {
        sheet.getRow(0).createCell(colIdx).setCellValue(header);
        switch (values.getValueType()) {
            case BOOLEAN: {
                this.addColumn(sheet, colIdx, values.getBooleanList());
            }
            case DATE:
            case TIMESTAMP: {
                this.addColumn(sheet, colIdx, values.getDateList());
            }
            case DECIMAL: {
                this.addColumn(sheet, colIdx, values.getDecimalList());
            }
            case INTEGRAL: {
                this.addColumn(sheet, colIdx, values.getIntegralList());
            }
            default: {
                this.addColumn(sheet, colIdx, values.getTextList());
            }
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final Date[] values) {
        int rowIdx = 1;
        for (final Date date : values) {
            sheet.getRow(rowIdx).createCell(colIdx).setCellValue(date);
            ++rowIdx;
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final String[] values) {
        int rowIdx = 1;
        for (final String value : values) {
            sheet.getRow(rowIdx).createCell(colIdx).setCellValue(value);
            ++rowIdx;
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final double[] values) {
        int rowIdx = 1;
        for (final double value : values) {
            sheet.getRow(rowIdx).createCell(colIdx).setCellValue(value);
            ++rowIdx;
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final long[] values) {
        int rowIdx = 1;
        for (final long value : values) {
            sheet.getRow(rowIdx).createCell(colIdx).setCellValue((double)value);
            ++rowIdx;
        }
    }
    
    private void addColumn(final Sheet sheet, final int colIdx, final boolean[] values) {
        int rowIdx = 1;
        for (final boolean value : values) {
            sheet.getRow(rowIdx).createCell(colIdx).setCellValue(value);
            ++rowIdx;
        }
    }
    
    private boolean save(final Workbook workbook, final String fullyQualifiedName) {
        File file = new File(fullyQualifiedName);
        if (file.exists()) {
            ResourceManager.renameAsBackup(file);
            file = new File(fullyQualifiedName);
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            workbook.write(os);
            os.close();
            return true;
        }
        catch (Exception e) {
            Spit.out("Error while saving " + fullyQualifiedName + ". " + e.getMessage());
            try {
                if (os != null) {
                    os.close();
                }
            }
            catch (IOException ex) {}
            return false;
        }
    }
    
    public boolean appendMissingOnes(final String fileName, final String[][] rows) {
        final File file = new File(fileName);
        Workbook workbook;
        Sheet sheet;
        if (file.exists()) {
            try {
                final InputStream is = new FileInputStream(file);
                workbook = WorkbookFactory.create(is);
                is.close();
                Spit.out(String.valueOf(fileName) + " read into a workbook.");
            }
            catch (Exception e) {
                Spit.out(String.valueOf(fileName) + " is not saved because of an error while reading existing contents. " + e.getMessage());
                Spit.out(e);
                return false;
            }
            sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                sheet = workbook.createSheet();
            }
        }
        else {
            Spit.out(String.valueOf(fileName) + " does not exist. New file will be created.");
            workbook = this.getWorkbookForFile(fileName);
            sheet = workbook.createSheet();
        }
        if (sheet.getLastRowNum() > 0) {
            this.addMissingRows(sheet, rows);
        }
        else {
            this.addRows(sheet, rows);
        }
        return this.save(workbook, fileName);
    }
    
    private void addRows(final Sheet sheet, final String[][] rows) {
        int rowIdx = 0;
        for (final String[] row : rows) {
            final Row xlRow = sheet.createRow(rowIdx);
            int colIdx = 0;
            String[] array;
            for (int length2 = (array = row).length, j = 0; j < length2; ++j) {
                final String columnValue = array[j];
                xlRow.createCell(colIdx).setCellValue(columnValue);
                ++colIdx;
            }
            ++rowIdx;
        }
    }
    
    private void addMissingRows(final Sheet sheet, final String[][] rows) {
        final Set<String> existingEntries = new HashSet<String>();
        int lastRow = sheet.getLastRowNum();
        for (int i = 0; i <= lastRow; ++i) {
            final Row row = sheet.getRow(i);
            if (row != null) {
                final Cell cell = row.getCell(0);
                if (cell != null) {
                    existingEntries.add(cell.getStringCellValue());
                }
            }
        }
        for (final String[] row2 : rows) {
            if (!existingEntries.contains(row2[0])) {
                ++lastRow;
                final Row xlRow = sheet.createRow(lastRow);
                int colIdx = 0;
                String[] array;
                for (int length2 = (array = row2).length, k = 0; k < length2; ++k) {
                    final String columnValue = array[k];
                    xlRow.createCell(colIdx).setCellValue(columnValue);
                    ++colIdx;
                }
            }
        }
    }
    
    private Workbook getWorkbookForFile(final String fileName) {
        if (fileName.lastIndexOf(120) == fileName.length() - 1) {
            return (Workbook)new XSSFWorkbook();
        }
        return (Workbook)new HSSFWorkbook();
    }
}
