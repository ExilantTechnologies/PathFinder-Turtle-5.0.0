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
import java.io.IOException;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

@Deprecated
public class PMTUtility
{
    public static int fileCtr;
    public static int errCtr;
    public static ArrayList<String> erroredFiles;
    public static int perrCtr;
    public static ArrayList<String> perroredFiles;
    public static int pfileCtr;
    public static FileWriter errorWriter;
    public static FileWriter sqlWriter;
    
    static {
        PMTUtility.fileCtr = 0;
        PMTUtility.errCtr = 0;
        PMTUtility.erroredFiles = new ArrayList<String>();
        PMTUtility.perrCtr = 0;
        PMTUtility.perroredFiles = new ArrayList<String>();
        PMTUtility.pfileCtr = 0;
        PMTUtility.errorWriter = null;
        PMTUtility.sqlWriter = null;
    }
    
    public static void parseFilesInDirectory(final String dirName) {
        final File rootDir = new File(dirName);
        if (rootDir.exists() && rootDir.isDirectory()) {
            final File[] fileList = rootDir.listFiles();
            File[] array;
            for (int length = (array = fileList).length, i = 0; i < length; ++i) {
                final File curFile = array[i];
                if (curFile.isDirectory()) {
                    parseFilesInDirectory(curFile.getAbsolutePath());
                }
                else if (curFile.isFile() && curFile.getName().endsWith("xml")) {
                    String curFileName = curFile.getAbsolutePath();
                    curFileName = curFileName.replaceAll("^.*\\\\sql\\\\", "");
                    curFileName = curFileName.replaceAll("\\\\", ".");
                    curFileName = curFileName.replaceAll("\\.xml$", "");
                    curFileName = "sql." + curFileName;
                    ++PMTUtility.fileCtr;
                    try {
                        final Sql sql = (Sql)ResourceManager.loadResource(curFileName, Sql.class);
                        final SqlParameter[] sqlParams = sql.inputParameters;
                        final DataCollection dc = new DataCollection();
                        if (sqlParams != null) {
                            SqlParameter[] array2;
                            for (int length2 = (array2 = sqlParams).length, j = 0; j < length2; ++j) {
                                final SqlParameter sqlParam = array2[j];
                                if (sqlParam.parameterType.equals(SqlParameterType.FILTER)) {
                                    dc.addIntegralValue(String.valueOf(sqlParam.name) + "Operator", 1L);
                                }
                                if (sqlParam.getValueType().equals(DataValueType.BOOLEAN)) {
                                    if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                        final boolean[] values = { false };
                                        Grid grid;
                                        if (dc.hasGrid(sqlParam.gridName)) {
                                            grid = dc.getGrid(sqlParam.gridName);
                                        }
                                        else {
                                            grid = new Grid(sqlParam.gridName);
                                        }
                                        grid.addColumn(sqlParam.name, ValueList.newList(values));
                                        dc.addGrid(sqlParam.gridName, grid);
                                    }
                                    else {
                                        dc.addBooleanValue(sqlParam.name, false);
                                    }
                                }
                                else if (sqlParam.getValueType().equals(DataValueType.DATE) || sqlParam.getValueType().equals(DataValueType.TIMESTAMP)) {
                                    if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                        final Date[] values2 = { new Date() };
                                        Grid grid;
                                        if (dc.hasGrid(sqlParam.gridName)) {
                                            grid = dc.getGrid(sqlParam.gridName);
                                        }
                                        else {
                                            grid = new Grid(sqlParam.gridName);
                                        }
                                        grid.addColumn(sqlParam.name, ValueList.newList(values2));
                                        dc.addGrid(sqlParam.gridName, grid);
                                    }
                                    else {
                                        dc.addDateValue(sqlParam.name, new Date());
                                    }
                                }
                                else if (sqlParam.getValueType().equals(DataValueType.DECIMAL)) {
                                    if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                        final double[] values3 = { 1.0 };
                                        Grid grid;
                                        if (dc.hasGrid(sqlParam.gridName)) {
                                            grid = dc.getGrid(sqlParam.gridName);
                                        }
                                        else {
                                            grid = new Grid(sqlParam.gridName);
                                        }
                                        grid.addColumn(sqlParam.name, ValueList.newList(values3));
                                        dc.addGrid(sqlParam.gridName, grid);
                                    }
                                    else {
                                        dc.addDecimalValue(sqlParam.name, 1.0);
                                    }
                                }
                                else if (sqlParam.getValueType().equals(DataValueType.INTEGRAL)) {
                                    if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                        final long[] values4 = { 1L };
                                        Grid grid;
                                        if (dc.hasGrid(sqlParam.gridName)) {
                                            grid = dc.getGrid(sqlParam.gridName);
                                        }
                                        else {
                                            grid = new Grid(sqlParam.gridName);
                                        }
                                        grid.addColumn(sqlParam.name, ValueList.newList(values4));
                                        dc.addGrid(sqlParam.gridName, grid);
                                    }
                                    else {
                                        dc.addIntegralValue(sqlParam.name, 1L);
                                    }
                                }
                                else if (sqlParam.getValueType().equals(DataValueType.TEXT)) {
                                    if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                        final String[] values5 = { "DEFAULTVALUE" };
                                        Grid grid;
                                        if (dc.hasGrid(sqlParam.gridName)) {
                                            grid = dc.getGrid(sqlParam.gridName);
                                        }
                                        else {
                                            grid = new Grid(sqlParam.gridName);
                                        }
                                        grid.addColumn(sqlParam.name, ValueList.newList(values5));
                                        dc.addGrid(sqlParam.gridName, grid);
                                    }
                                    else {
                                        dc.addTextValue(sqlParam.name, new String("DEFAULTVALUE"));
                                    }
                                }
                                else if (sqlParam.parameterType.equals(SqlParameterType.LIST)) {
                                    final String[] values5 = { "DEFAULTVALUE" };
                                    Grid grid;
                                    if (dc.hasGrid(sqlParam.gridName)) {
                                        grid = dc.getGrid(sqlParam.gridName);
                                    }
                                    else {
                                        grid = new Grid(sqlParam.gridName);
                                    }
                                    grid.addColumn(sqlParam.name, ValueList.newList(values5));
                                    dc.addGrid(sqlParam.gridName, grid);
                                }
                                else {
                                    dc.addTextValue(sqlParam.name, new String("DEFAULTVALUE"));
                                }
                            }
                            final String sqlText = sql.getSql(dc);
                            PMTUtility.sqlWriter.write("=============================================================\r\n");
                            PMTUtility.sqlWriter.write("The sql for the file '" + curFileName + "' is :\r\n\r\n");
                            PMTUtility.sqlWriter.write("\r\n" + sqlText + "\r\n");
                            PMTUtility.sqlWriter.write("=============================================================\r\n");
                        }
                        else {
                            final String sqlText = sql.getSql(dc);
                            PMTUtility.sqlWriter.write("=============================================================\r\n");
                            PMTUtility.sqlWriter.write("The sql for the file '" + curFileName + "' is :\r\n\r\n");
                            PMTUtility.sqlWriter.write("\r\n" + sqlText + "\r\n");
                            PMTUtility.sqlWriter.write("=============================================================\r\n");
                        }
                        ++PMTUtility.pfileCtr;
                    }
                    catch (ExilityException eEx) {
                        ++PMTUtility.perrCtr;
                        PMTUtility.perroredFiles.add(curFileName);
                        try {
                            PMTUtility.errorWriter.write("The exception in '" + curFileName + "' is '" + eEx.getMessage() + "'\r\n");
                            StackTraceElement[] stackTrace;
                            for (int length3 = (stackTrace = eEx.getStackTrace()).length, k = 0; k < length3; ++k) {
                                final StackTraceElement ste = stackTrace[k];
                                PMTUtility.errorWriter.write(String.valueOf(ste.toString()) + "\r\n");
                            }
                            PMTUtility.errorWriter.write("\r\n");
                        }
                        catch (Exception ex2) {}
                    }
                    catch (Exception ex) {
                        ++PMTUtility.errCtr;
                        PMTUtility.erroredFiles.add(curFileName);
                        try {
                            PMTUtility.errorWriter.write("The exception in '" + curFileName + "' is '" + ex.getMessage() + "'\r\n");
                            StackTraceElement[] stackTrace2;
                            for (int length4 = (stackTrace2 = ex.getStackTrace()).length, l = 0; l < length4; ++l) {
                                final StackTraceElement ste = stackTrace2[l];
                                PMTUtility.errorWriter.write(String.valueOf(ste.toString()) + "\r\n");
                            }
                            PMTUtility.errorWriter.write("\r\n");
                        }
                        catch (Exception ex3) {}
                    }
                }
            }
        }
    }
    
    public static void main(final String[] args) {
        String inputResourceDirectory = "C:/workspace/SRESOURCE/";
        if (args.length > 0) {
            inputResourceDirectory = String.valueOf(args[0].replaceAll("\\\\", "/")) + "/";
        }
        ResourceManager.setResourceFolder(inputResourceDirectory);
        try {
            System.out.println("Processing the files ....");
            PMTUtility.sqlWriter = new FileWriter(new File("convertedSQLs.txt"));
            PMTUtility.errorWriter = new FileWriter(new File("errors.txt"));
            parseFilesInDirectory(String.valueOf(inputResourceDirectory) + "/sql");
            final FileWriter summaryWriter = new FileWriter("summary.txt");
            summaryWriter.write("Files with content errors : '" + PMTUtility.errCtr + "'\r\n");
            for (final String curFileName : PMTUtility.erroredFiles) {
                summaryWriter.write("'" + curFileName + "'\r\n");
            }
            summaryWriter.write("\r\n");
            summaryWriter.write("Files with parameter errors : '" + PMTUtility.perrCtr + "'\r\n");
            for (final String curFileName : PMTUtility.perroredFiles) {
                summaryWriter.write("'" + curFileName + "'\r\n");
            }
            summaryWriter.write("\r\n");
            summaryWriter.write("Files processed successfully : '" + PMTUtility.pfileCtr + "'\r\n\r\n");
            summaryWriter.write("Total files in the directory : '" + PMTUtility.fileCtr + "'\r\n\r\n");
            summaryWriter.close();
            PMTUtility.errorWriter.close();
            PMTUtility.sqlWriter.close();
            System.out.println("The files have been processed. Check summary.txt, errors.txt, convertedSQLs.txt for further details");
        }
        catch (IOException ioEx) {
            System.out.println("Processing failed with the error");
            System.out.println(ioEx.getMessage());
        }
    }
}
