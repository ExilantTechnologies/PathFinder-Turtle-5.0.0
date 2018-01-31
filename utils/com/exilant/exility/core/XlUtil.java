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

import org.w3c.dom.Document;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class XlUtil
{
    public static final String DATE_TYPE = "DateTime";
    public static final String TEXT_TYPE = "String";
    public static final String NUMBER_TYPE = "Number";
    public static final String BOOLEAN_TYPE = "Boolean";
    public static final String XL_DATE_FORMAT = "Medium Date";
    public static final String XL_DATE_STYLE_NAME = "dateStyle";
    public static final String XL_BEGIN = "<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>";
    public static final String XL_END = "\n</Workbook>";
    private static final String WORK_SHEET_TAG_NAME = "Worksheet";
    private static final String QUALIFIED_WORK_SHEET_TAG_NAME = "ss:Worksheet";
    private static final String TABLE_TAG_NAME = "Table";
    private static final String ROW_TAG_NAME = "Row";
    private static final String CELL_TAG_NAME = "Cell";
    private static final String DATA_TAG_NAME = "Data";
    private static final String NAME_NAME = "ss:Name";
    private static final String TYPE_NAME = "ss:Type";
    private static final String INDEX_NAME = "ss:Index";
    
    public int extract(final String fullyQualifiedFileName, final DataCollection dc, final boolean useDictionaryForDataType) {
        final NodeList sheets = this.getSheets(fullyQualifiedFileName, dc);
        if (sheets == null) {
            return 0;
        }
        final int n = sheets.getLength();
        Spit.out("There are " + n + " sheets in " + fullyQualifiedFileName);
        for (int i = 0; i < n; ++i) {
            final Element sheet = (Element)sheets.item(i);
            final String nam = sheet.getAttribute("ss:Name");
            final NodeList tables = sheet.getElementsByTagName("Table");
            final int m = tables.getLength();
            if (m == 0) {
                Spit.out(String.valueOf(nam) + " has no data in it. Skipping this sheet.");
            }
            else {
                final Element table = (Element)tables.item(0);
                if (nam.equals("values")) {
                    this.extractValues(table, dc, useDictionaryForDataType);
                }
                else {
                    final String[][] rawData = this.getRawData(table);
                    if (rawData == null || rawData.length == 0) {
                        dc.addMessage("exilityInfo", "Work sheet " + nam + " could not be parsed in spread sheet " + fullyQualifiedFileName);
                    }
                    else {
                        Grid grid = null;
                        if (useDictionaryForDataType) {
                            grid = new Grid();
                            try {
                                grid.setRawData(rawData);
                            }
                            catch (ExilityException e) {
                                grid = null;
                            }
                        }
                        else {
                            grid = this.getGrid(table);
                        }
                        if (grid == null) {
                            dc.addMessage("exilityError", "Work sheet " + nam + " could not be parsed in spread sheet " + fullyQualifiedFileName + ". error: ");
                        }
                        else {
                            dc.addGrid(nam, grid);
                        }
                    }
                }
            }
        }
        return 1;
    }
    
    public Map<String, String[][]> extractAsText(final String fullyQualifiedFileName) {
        final Map<String, String[][]> allData = new HashMap<String, String[][]>();
        final NodeList sheets = this.getSheets(fullyQualifiedFileName, null);
        if (sheets == null) {
            return allData;
        }
        final int n = sheets.getLength();
        Spit.out("There are " + n + " sheets in " + fullyQualifiedFileName);
        for (int i = 0; i < n; ++i) {
            final Element sheet = (Element)sheets.item(i);
            final String nam = sheet.getAttribute("ss:Name");
            final NodeList tables = sheet.getElementsByTagName("Table");
            final int m = tables.getLength();
            if (m == 0) {
                Spit.out(String.valueOf(nam) + " has no data in it. Skipping this sheet.");
            }
            else {
                final String[][] rawData = this.getRawData((Element)tables.item(0));
                if (rawData == null || rawData.length == 0) {
                    Spit.out("Work sheet " + nam + " could not be parsed in spread sheet " + fullyQualifiedFileName);
                }
                else {
                    allData.put(nam, rawData);
                }
            }
        }
        return allData;
    }
    
    public int saveDc(final String fullyQualifiedFileName, final DataCollection dc) {
        final File file = new File(fullyQualifiedFileName);
        if (file.exists() && !ResourceManager.renameAsBackup(file)) {
            file.delete();
        }
        final StringBuilder sbf = new StringBuilder();
        this.toSpreadSheetXml(sbf, dc);
        ResourceManager.saveText(fullyQualifiedFileName, sbf.toString());
        return 1;
    }
    
    private String[][] getRawData(final Element sheet) {
        final NodeList rowNodes = sheet.getElementsByTagName("Row");
        final int nbrRows = rowNodes.getLength();
        if (nbrRows == 0) {
            return null;
        }
        final String[][] rawData = new String[nbrRows][];
        String[] row = this.textValuesOf((Element)rowNodes.item(0), 0);
        if (row == null) {
            return null;
        }
        final int nbrCols = row.length;
        rawData[0] = row;
        for (int i = 1; i < nbrRows; ++i) {
            row = (rawData[i] = this.textValuesOf((Element)rowNodes.item(i), nbrCols));
            if (row == null) {
                return null;
            }
        }
        return rawData;
    }
    
    private String[] textValuesOf(final Element row, final int expectedNbrCells) {
        if (row == null) {
            return null;
        }
        final Element[] dataCells = this.getDataCells(row, expectedNbrCells);
        if (dataCells == null) {
            return null;
        }
        final int nbrCols = dataCells.length;
        final String[] values = new String[nbrCols];
        for (int i = 0; i < nbrCols; ++i) {
            final Element cell = dataCells[i];
            values[i] = ((cell == null) ? "" : this.textValueOf(cell));
        }
        return values;
    }
    
    private Element[] getDataCells(final Element row, final int expectedNbrCells) {
        if (row == null) {
            return null;
        }
        final NodeList cells = row.getElementsByTagName("Cell");
        final int nbrCells = cells.getLength();
        if (nbrCells == 0) {
            return null;
        }
        final int nbrCols = (expectedNbrCells == 0) ? nbrCells : expectedNbrCells;
        final Element[] dataCells = new Element[nbrCols];
        int colIdx = 0;
        for (int i = 0; i < nbrCells; ++i) {
            final Element cell = (Element)cells.item(i);
            final String text = cell.getAttribute("ss:Index");
            if (text != null && text.length() > 0) {
                int idx = Integer.parseInt(text);
                --idx;
                if (expectedNbrCells == 0 && idx != i) {
                    Spit.out("header row has blank cells in the spred sheet. It will not be read properly");
                    return null;
                }
                while (colIdx < idx && colIdx < nbrCols) {
                    dataCells[colIdx] = null;
                    ++colIdx;
                }
                if (idx >= nbrCols) {
                    Spit.out("Data found beyound column nbr " + nbrCols + ". It is ignored.");
                    break;
                }
            }
            dataCells[colIdx] = cell;
            ++colIdx;
        }
        return dataCells;
    }
    
    private String textValueOf(final Element cell) {
        if (cell == null) {
            return "";
        }
        final NodeList datas = cell.getElementsByTagName("Data");
        if (datas.getLength() == 0) {
            return "";
        }
        return datas.item(0).getTextContent();
    }
    
    private Grid getGrid(final Element sheet) {
        final NodeList rowNodes = sheet.getElementsByTagName("Row");
        final int nbrRows = rowNodes.getLength();
        if (nbrRows <= 1) {
            return null;
        }
        final String[] header = this.textValuesOf((Element)rowNodes.item(0), 0);
        if (header == null) {
            return null;
        }
        final int nbrCols = header.length;
        final int nbrDataRows = nbrRows - 1;
        final Element[][] dataCells = new Element[nbrDataRows][];
        for (int i = 0; i < nbrDataRows; ++i) {
            dataCells[i] = this.getDataCells((Element)rowNodes.item(i + 1), nbrCols);
            if (dataCells[i] == null) {
                return null;
            }
        }
        final DataValueType[] types = new DataValueType[nbrCols];
        for (int j = 0; j < nbrCols; ++j) {
            DataValueType dt = DataValueType.NULL;
            for (int k = 0; k < nbrDataRows; ++k) {
                final Element dataCell = dataCells[k][j];
                if (dataCell != null) {
                    final DataValueType d = this.dataTypeOf(dataCell);
                    if (d != DataValueType.NULL) {
                        dt = d;
                        break;
                    }
                }
            }
            if (dt == DataValueType.NULL) {
                Spit.out("Column " + header[j] + " has no data in it, and hnece assuming it to be text.");
                dt = DataValueType.TEXT;
            }
            types[j] = dt;
        }
        final Grid grid = new Grid();
        for (int l = 0; l < nbrCols; ++l) {
            final String[] values = new String[nbrDataRows];
            final DataValueType type = types[l];
            for (int m = 0; m < nbrDataRows; ++m) {
                String value = this.textValueOf(dataCells[m][l]);
                if (value.length() > 0 && type == DataValueType.DATE) {
                    value = String.valueOf(value.substring(0, 10)) + ' ' + value.substring(11);
                    Spit.out(value);
                }
                values[m] = value;
            }
            final ValueList column = ValueList.newList(values, types[l]);
            try {
                grid.addColumn(header[l], column);
            }
            catch (ExilityException e) {
                Spit.out("Error while adding column " + header[l] + " - " + e.getMessage());
            }
        }
        return grid;
    }
    
    private DataValueType dataTypeOf(final Element cell) {
        if (cell == null) {
            return DataValueType.NULL;
        }
        final NodeList datas = cell.getElementsByTagName("Data");
        if (datas.getLength() == 0) {
            return DataValueType.NULL;
        }
        final Element dataElement = (Element)datas.item(0);
        final String type = dataElement.getAttribute("ss:Type");
        return Value.getTypeFromXl(type);
    }
    
    private NodeList getSheets(final String fileName, final DataCollection dc) {
        final File file = new File(fileName);
        String msg = null;
        if (!file.exists() || !file.isFile()) {
            msg = " is not a valid file name.";
        }
        else {
            final Document doc = ObjectManager.getDocument(file);
            if (doc == null) {
                msg = " has an invalid xml.";
            }
            else {
                NodeList sheets = doc.getElementsByTagName("Worksheet");
                if (sheets.getLength() == 0) {
                    sheets = doc.getElementsByTagName("ss:Worksheet");
                }
                if (sheets.getLength() != 0) {
                    return sheets;
                }
                msg = " does not have any worksheets in it.";
            }
        }
        msg = String.valueOf(fileName) + msg;
        if (dc != null) {
            dc.addError(msg);
        }
        Spit.out(msg);
        return null;
    }
    
    private void toSpreadSheetXml(final StringBuilder sbf, final DataCollection dc) {
        sbf.append("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
        dc.toSpreadSheetXml(sbf, null);
        sbf.append("\n</Workbook>");
    }
    
    private void extractValues(final Element table, final DataCollection dc, final boolean useDictionaryForDataType) {
        final NodeList rows = table.getElementsByTagName("Row");
        for (int n = rows.getLength(), i = 1; i < n; ++i) {
            final NodeList cells = rows.item(i).getChildNodes();
            final int nbrCells = cells.getLength();
            if (nbrCells != 0) {
                final String fieldName = this.textValueOf((Element)cells.item(0));
                if (fieldName.length() != 0) {
                    Element dataCell = null;
                    String fieldValue;
                    if (nbrCells == 1) {
                        fieldValue = "";
                    }
                    else {
                        dataCell = (Element)cells.item(1);
                        fieldValue = this.textValueOf(dataCell);
                    }
                    if (useDictionaryForDataType) {
                        dc.addValueAfterCheckingInDictionary(fieldName, fieldValue);
                    }
                    else {
                        final DataValueType type = this.dataTypeOf(dataCell);
                        dc.addValue(fieldName, fieldValue, type);
                    }
                }
            }
        }
    }
}
