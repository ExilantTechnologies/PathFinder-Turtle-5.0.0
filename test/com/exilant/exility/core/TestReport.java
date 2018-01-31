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
import java.util.Calendar;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestReport implements TestReportInterface
{
    private static final String STRING = "";
    String testId;
    String description;
    String service;
    String preTestService;
    String postTestService;
    long expectedTimeInMs;
    long actualTimeInMs;
    boolean isTestCleared;
    String message;
    String fileName;
    DataCollection inDc;
    DataCollection expectedDc;
    DataCollection dc;
    List<String[]> summary;
    List<String[]> list;
    
    public TestReport() {
        this.testId = "";
        this.description = "";
        this.service = "";
        this.preTestService = "";
        this.postTestService = "";
        this.expectedTimeInMs = 0L;
        this.actualTimeInMs = 0L;
        this.isTestCleared = false;
        this.message = "";
        this.fileName = "";
        this.inDc = new DataCollection();
        this.expectedDc = new DataCollection();
        this.dc = new DataCollection();
        this.summary = new ArrayList<String[]>();
        this.list = new ArrayList<String[]>();
    }
    
    @Override
    public void process(final String fileInput, final String masterSummary) throws ExilityException {
        final String stamp = ResourceManager.getTimeStamp();
        String qualifiedFileName = null;
        String testFileOutput = null;
        String testFile = null;
        final File file = new File(fileInput);
        final File dir = new File(fileInput);
        testFileOutput = String.valueOf(fileInput) + "/" + FilenameUtils.getName(file.toString()) + "_" + stamp + "_out";
        final String summaryFile = String.valueOf(fileInput) + "/" + "summary" + "/" + "summary" + "_" + stamp + ".xml";
        try {
            if (file.isFile() && file.getCanonicalPath().endsWith(".xml")) {
                qualifiedFileName = FilenameUtils.getName(file.toString());
                testFile = String.valueOf(testFileOutput) + "/" + qualifiedFileName + "_out" + stamp + ".xml";
                this.test(file.toString(), testFile, this.list);
                this.generate(testFile, this.list);
            }
            else {
                this.getFiles(dir);
            }
            this.merge(this.list);
            this.generateSummary(summaryFile, this.list, null);
            if (masterSummary != null) {
                this.generateSummary(null, this.summary, masterSummary);
            }
        }
        catch (IOException e) {
            Spit.out("Output file could not be created for " + fileInput);
            e.printStackTrace();
        }
    }
    
    private void test(final String inputFileName, final String outputFileName, final List<String[]> results) {
        try {
            this.doTest(inputFileName);
        }
        catch (Exception e) {
            Spit.out(e);
            this.message = "Error: " + e.getMessage();
            this.dc.addMessage("exilityError", e.getMessage());
        }
        if (results != null) {
            results.add(this.toArray());
        }
        if (outputFileName != null) {
            this.toDc(this.dc);
        }
    }
    
    private void doTest(final String testFileName) throws ExilityException {
        this.fileName = testFileName;
        this.resetAttributes();
        final XlUtil util = new XlUtil();
        util.extract(this.fileName, this.dc, true);
        final Grid controls = this.dc.getGrid("_controlSheet");
        if (controls == null) {
            this.message = String.valueOf(this.fileName) + " does not have the sheet with name " + "_controlSheet" + " that is to contain control information.";
            return;
        }
        this.service = controls.getValueAsText("service", 0, null);
        this.testId = controls.getValueAsText("testId", 0, null);
        this.description = controls.getValueAsText("description", 0, null);
        if (this.service == null || this.service.length() == 0) {
            this.message = "_controlSheet sheet in " + this.fileName + " does not have serviceName.";
        }
        final ServiceEntry se = ServiceList.getServiceEntry(this.service, true);
        if (se == null) {
            this.message = String.valueOf(this.service) + " is not a valid service.";
            return;
        }
        ServiceEntry preSe = null;
        ServiceEntry postSe = null;
        this.preTestService = controls.getValueAsText("preTestService", 0, null);
        if (this.preTestService != null && this.preTestService.length() > 0) {
            preSe = ServiceList.getServiceEntry(this.preTestService, true);
            if (preSe == null) {
                this.message = String.valueOf(this.preTestService) + " is not a valid service.";
            }
        }
        this.postTestService = controls.getValueAsText("postTestService", 0, null);
        if (this.postTestService != null && this.postTestService.length() > 0) {
            postSe = ServiceList.getServiceEntry(this.postTestService, true);
            if (postSe == null) {
                this.message = String.valueOf(this.postTestService) + " is not a valid service entry or a service.";
            }
        }
        final String text = controls.getValueAsText("expectedTimeInMs", 0, null);
        if (text != null && text.length() > 0) {
            try {
                this.expectedTimeInMs = Long.parseLong(text);
            }
            catch (Exception e) {
                this.message = "expectedTimeInMs has a non-integral valaue of " + text + ". Please provide expected completion time in milliseconds.";
            }
        }
        this.dc.removeGrid("_controlSheet");
        this.loadGrids(this.dc, this.inDc, this.expectedDc);
        if (this.message != null && preSe != null) {
            preSe.serve(this.inDc);
        }
        final long startedAt = Calendar.getInstance().getTimeInMillis();
        se.serve(this.inDc);
        this.actualTimeInMs = Calendar.getInstance().getTimeInMillis() - startedAt;
        if (postSe != null) {
            postSe.serve(this.inDc);
        }
        if (!this.inDc.hasAllFieldsOf(this.expectedDc)) {
            this.message = "Output data does not match with expected data.";
        }
        this.isTestCleared = true;
    }
    
    private void resetAttributes() {
        this.message = null;
        this.actualTimeInMs = 0L;
        this.isTestCleared = false;
    }
    
    private String[] toArray() {
        final String[] arr = { this.testId, this.description, this.service, this.preTestService, this.postTestService, Long.toString(this.expectedTimeInMs), Long.toString(this.actualTimeInMs), this.isTestCleared ? "true" : "false", this.message };
        return arr;
    }
    
    private void toDc(final DataCollection thisDc) {
        thisDc.addTextValue("testId", this.testId);
        thisDc.addTextValue("description", this.description);
        thisDc.addTextValue("service", this.service);
        thisDc.addTextValue("preTestService", this.preTestService);
        thisDc.addTextValue("postTestService", this.postTestService);
        thisDc.addTextValue("expectedTimeInMs", new StringBuilder(String.valueOf(this.expectedTimeInMs)).toString());
        thisDc.addTextValue("isTestCleared", new StringBuilder(String.valueOf(this.isTestCleared)).toString());
        thisDc.addTextValue("actualTimeInMs", new StringBuilder(String.valueOf(this.actualTimeInMs)).toString());
        thisDc.addTextValue("message", this.message);
    }
    
    private void loadGrids(final DataCollection thisDc, final DataCollection thisInDc, final DataCollection thisExpectedDc) {
        String[] gridNames;
        for (int length = (gridNames = thisDc.getGridNames()).length, i = 0; i < length; ++i) {
            final String gridName = gridNames[i];
            final Grid grid = thisDc.getGrid(gridName);
            if (gridName.startsWith("in_")) {
                thisInDc.addGrid(gridName.substring("in_".length()), grid);
            }
            else {
                if (!gridName.startsWith("out_")) {
                    this.message = String.valueOf(gridName) + " is a sheet in " + this.fileName + ". All sheets for input should have " + "in_" + " as prefix and all output sheets should have " + "out_" + " as prefix.";
                    return;
                }
                thisExpectedDc.addGrid(gridName.substring("out_".length()), grid);
            }
        }
        if (thisInDc.hasGrid("_values")) {
            thisInDc.gridToValues("_values");
            thisInDc.removeGrid("_values");
        }
        if (thisExpectedDc.hasGrid("_values")) {
            thisExpectedDc.gridToValues("_values");
            thisExpectedDc.removeGrid("_values");
        }
    }
    
    private void generate(final String testFileOutput, final List<String[]> thisList) {
        StringBuilder sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
        final Grid controlSheet = new Grid();
        for (final String[] controlContents : thisList) {
            this.testId = controlContents[0];
            this.description = controlContents[1];
            this.service = controlContents[2];
            this.preTestService = controlContents[3];
            this.postTestService = controlContents[4];
            this.expectedTimeInMs = Long.parseLong(controlContents[5]);
            this.actualTimeInMs = Long.parseLong(controlContents[6]);
            this.isTestCleared = Boolean.parseBoolean(controlContents[7]);
            this.message = controlContents[8];
            try {
                controlSheet.setRawData(this.getControlSheet());
            }
            catch (ExilityException e) {
                e.printStackTrace();
            }
            sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
            controlSheet.toSpreadSheetXml(sbf, "_controlSheet");
            this.inDc.toSpreadSheetXml(sbf, "out_");
            sbf.append("\n</Workbook>");
            ResourceManager.saveText(testFileOutput, sbf.toString());
        }
    }
    
    private void generateSummary(final String summaryFile, final List<String[]> thisList, final String masterSummary) throws ExilityException {
        final Grid controlSheet = new Grid();
        final StringBuilder sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
        if (masterSummary != null) {
            final String[][] arraySummary = new String[this.summary.size() + 1][];
            for (int i = 0; i < this.summary.size(); ++i) {
                arraySummary[i + 1] = this.summary.get(i);
            }
            arraySummary[0] = Constants.SUMMARY_FIELD_NAMES;
            controlSheet.setRawData(arraySummary);
            controlSheet.toSpreadSheetXml(sbf, "_summary");
            sbf.append(this.inDc);
            sbf.append("\n</Workbook>");
            ResourceManager.saveText(masterSummary, sbf.toString());
        }
        else {
            final String[][] arraySummary = new String[thisList.size() + 1][];
            for (int i = 0; i < thisList.size(); ++i) {
                arraySummary[i + 1] = thisList.get(i);
            }
            arraySummary[0] = Constants.ALL_FIELD_NAMES;
            controlSheet.setRawData(arraySummary);
            controlSheet.toSpreadSheetXml(sbf, "_summary");
            sbf.append(this.inDc);
            sbf.append("\n</Workbook>");
            ResourceManager.saveText(summaryFile, sbf.toString());
        }
    }
    
    String[][] getControlSheet() {
        final String[][] sheet = { Constants.ALL_FIELD_NAMES, this.toArray() };
        return sheet;
    }
    
    private void merge(final List<String[]> summaryList) {
        long timeTaken = 0L;
        String thisTestId = "";
        String thisDescription = "";
        String thisService = "";
        String thisPreTestService = "";
        String thisPostTestService = "";
        int numberOfTestCases = 0;
        int numberPassed = 0;
        int numberFailed = 0;
        boolean localIsTestCleared = false;
        final String[][] sheet = { null };
        for (final String[] listContents : summaryList) {
            thisTestId = listContents[0];
            thisDescription = listContents[1];
            thisService = listContents[2];
            thisPreTestService = listContents[3];
            thisPostTestService = listContents[4];
            timeTaken += Long.parseLong(listContents[6]);
            ++numberOfTestCases;
            localIsTestCleared = Boolean.parseBoolean(listContents[7]);
            if (!localIsTestCleared) {
                ++numberFailed;
            }
            else {
                if (!localIsTestCleared) {
                    continue;
                }
                ++numberPassed;
            }
        }
        final String[] arr = { thisTestId, thisDescription, thisService, thisPreTestService, thisPostTestService, Long.toString(timeTaken), Integer.toString(numberOfTestCases), Integer.toString(numberPassed), Integer.toString(numberFailed) };
        sheet[0] = arr;
        this.summary.add(arr);
    }
    
    void getFiles(final File dir) {
        File[] listFiles;
        for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; ++i) {
            final File testFileInput = listFiles[i];
            String qualifiedFileName = null;
            final String testFileOutput = null;
            try {
                qualifiedFileName = FilenameUtils.getName(testFileInput.toString());
                if (!qualifiedFileName.equals(".DS_Store")) {
                    if (!qualifiedFileName.equals("summary")) {
                        if (testFileInput.isFile() && testFileInput.getCanonicalPath().endsWith(".xml")) {
                            final String testFile = String.valueOf(testFileOutput) + "/" + qualifiedFileName.replace(".xml", "_out") + ".xml";
                            this.test(testFileInput.toString(), testFile, this.list);
                            this.generate(testFile, this.list);
                        }
                        if (testFileInput.isDirectory()) {
                            this.getFiles(testFileInput);
                        }
                    }
                }
            }
            catch (IOException e) {
                Spit.out("Output file could not be created for " + qualifiedFileName);
                e.printStackTrace();
            }
        }
    }
}
