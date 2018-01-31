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
import java.util.List;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.util.ArrayList;

public class TestProcessor implements TestProcessorInterface
{
    private static final String STRING = "";
    String testId;
    String description;
    String service;
    String preTestService;
    String postTestService;
    long expectedTimeInMs;
    long actualTimeInMs;
    boolean testCleared;
    String message;
    String fileName;
    private static TestProcessor thisProcessor;
    
    TestProcessor() {
        this.testId = "";
        this.description = "";
        this.service = "";
        this.preTestService = "";
        this.postTestService = "";
        this.expectedTimeInMs = 0L;
        this.actualTimeInMs = 0L;
        this.testCleared = false;
        this.message = "";
        this.fileName = "";
    }
    
    public static TestProcessorInterface getInstance() {
        if (TestProcessor.thisProcessor == null) {
            TestProcessor.thisProcessor = new TestProcessor();
        }
        return TestProcessor.thisProcessor;
    }
    
    @Override
    public void process(final String FileInput) throws ExilityException {
        final String stamp = ResourceManager.getTimeStamp();
        String qualifiedFileName = null;
        String testFileOutput = null;
        String testFile = null;
        final List<String[]> list = new ArrayList<String[]>();
        DataCollection dc = new DataCollection();
        final File file = new File(FileInput);
        final File dir = new File(FileInput);
        testFileOutput = String.valueOf(FileInput) + "/" + FilenameUtils.getName(file.toString()) + "_out";
        final String summaryFile = String.valueOf(FileInput) + "/" + "summary" + "/" + "summary" + stamp + ".xml";
        try {
            if (file.isFile() && file.getCanonicalPath().endsWith(".xml")) {
                qualifiedFileName = FilenameUtils.getName(file.toString());
                testFile = String.valueOf(testFileOutput) + "/" + qualifiedFileName + "_out" + stamp + ".xml";
                dc = this.test(file.toString(), testFile, list);
                generate(dc, testFile, list);
            }
            else {
                File[] listFiles;
                for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; ++i) {
                    final File testFileInput = listFiles[i];
                    try {
                        if (testFileInput.isFile() && testFileInput.getCanonicalPath().endsWith(".xml")) {
                            qualifiedFileName = FilenameUtils.getName(dir.toString());
                            testFile = String.valueOf(testFileOutput) + "/" + qualifiedFileName + "_out" + stamp + ".xml";
                            dc = this.test(testFileInput.toString(), testFile, list);
                            generate(dc, testFile, list);
                        }
                    }
                    catch (IOException e) {
                        Spit.out("Output file could not be created for " + qualifiedFileName);
                        e.printStackTrace();
                    }
                }
            }
            generateSummary(dc, summaryFile, list);
        }
        catch (IOException e2) {
            Spit.out("Output file could not be created for " + FileInput);
            e2.printStackTrace();
        }
    }
    
    private DataCollection test(final String inputFileName, final String outputFileName, final List<String[]> results) {
        final DataCollection dc = new DataCollection();
        DataCollection inDc = new DataCollection();
        try {
            inDc = this.doTest(inputFileName, dc);
        }
        catch (Exception e) {
            Spit.out(e);
            this.message = "Error: " + e.getMessage();
            dc.addMessage("exilityError", e.getMessage());
        }
        if (results != null) {
            results.add(this.toArray());
        }
        if (outputFileName != null) {
            this.toDc(dc);
        }
        return inDc;
    }
    
    private DataCollection doTest(final String testFileName, final DataCollection dc) throws ExilityException {
        this.fileName = testFileName;
        this.resetAttributes();
        final DataCollection inDc = new DataCollection();
        final DataCollection expectedDc = new DataCollection();
        final XlUtil util = new XlUtil();
        util.extract(this.fileName, dc, true);
        final Grid controls = dc.getGrid("_controlSheet");
        if (controls == null) {
            this.message = String.valueOf(this.fileName) + " does not have the sheet with name " + "_controlSheet" + " that is to contain control information.";
            return null;
        }
        this.service = controls.getValueAsText("service", 0, null);
        this.testId = controls.getValueAsText("testId", 0, null);
        this.description = controls.getValueAsText("description", 0, null);
        if (this.service == null || this.service.length() == 0) {
            this.message = "_controlSheet sheet in " + this.fileName + " does not have serviceName.";
            return null;
        }
        final ServiceEntry se = ServiceList.getServiceEntry(this.service, true);
        if (se == null) {
            this.message = String.valueOf(this.service) + " is not a valid service.";
            return null;
        }
        ServiceEntry preSe = null;
        ServiceEntry postSe = null;
        this.preTestService = controls.getValueAsText("preTestService", 0, null);
        if (this.preTestService != null && this.preTestService.length() > 0) {
            preSe = ServiceList.getServiceEntry(this.preTestService, true);
            if (preSe == null) {
                this.message = String.valueOf(this.preTestService) + " is not a valid service.";
                return null;
            }
        }
        this.postTestService = controls.getValueAsText("postTestService", 0, null);
        if (this.postTestService != null && this.postTestService.length() > 0) {
            postSe = ServiceList.getServiceEntry(this.postTestService, true);
            if (postSe == null) {
                this.message = String.valueOf(this.postTestService) + " is not a valid service entry or a service.";
                return null;
            }
        }
        final String text = controls.getValueAsText("expectedTimeInMs", 0, null);
        if (text != null && text.length() > 0) {
            try {
                this.expectedTimeInMs = Long.parseLong(text);
            }
            catch (Exception e) {
                this.message = "expectedTimeInMs has a non-integral valaue of " + text + ". Please provide expected completion time in milliseconds.";
                return null;
            }
        }
        dc.removeGrid("_controlSheet");
        this.loadGrids(dc, inDc, expectedDc);
        if (this.message != null) {
            return null;
        }
        if (preSe != null) {
            preSe.serve(inDc);
        }
        final long startedAt = Calendar.getInstance().getTimeInMillis();
        se.serve(inDc);
        this.actualTimeInMs = Calendar.getInstance().getTimeInMillis() - startedAt;
        if (postSe != null) {
            postSe.serve(inDc);
        }
        if (!inDc.hasAllFieldsOf(expectedDc)) {
            this.message = "Output data does not match with expected data.";
            return null;
        }
        this.testCleared = true;
        return inDc;
    }
    
    private void resetAttributes() {
        this.message = null;
        this.actualTimeInMs = 0L;
        this.testCleared = false;
    }
    
    private String[] toArray() {
        final String[] arr = { this.testId, this.description, this.service, this.preTestService, this.postTestService, Long.toString(this.expectedTimeInMs), Long.toString(this.actualTimeInMs), this.testCleared ? "true" : "false", this.message };
        return arr;
    }
    
    private void toDc(final DataCollection dc) {
        dc.addTextValue("testId", this.testId);
        dc.addTextValue("description", this.description);
        dc.addTextValue("service", this.service);
        dc.addTextValue("preTestService", this.preTestService);
        dc.addTextValue("postTestService", this.postTestService);
        dc.addTextValue("expectedTimeInMs", new StringBuilder(String.valueOf(this.expectedTimeInMs)).toString());
        dc.addTextValue("testCleared", new StringBuilder(String.valueOf(this.testCleared)).toString());
        dc.addTextValue("actualTimeInMs", new StringBuilder(String.valueOf(this.actualTimeInMs)).toString());
        dc.addTextValue("message", this.message);
    }
    
    private void loadGrids(final DataCollection dc, final DataCollection inDc, final DataCollection expectedDc) {
        String[] gridNames;
        for (int length = (gridNames = dc.getGridNames()).length, i = 0; i < length; ++i) {
            final String gridName = gridNames[i];
            final Grid grid = dc.getGrid(gridName);
            if (gridName.startsWith("in_")) {
                inDc.addGrid(gridName.substring("in_".length()), grid);
            }
            else {
                if (!gridName.startsWith("out_")) {
                    this.message = String.valueOf(gridName) + " is a sheet in " + this.fileName + ". All sheets for input should have " + "in_" + " as prefix and all output sheets should have " + "out_" + " as prefix.";
                    return;
                }
                expectedDc.addGrid(gridName.substring("out_".length()), grid);
            }
        }
        if (inDc.hasGrid("_values")) {
            inDc.gridToValues("_values");
            inDc.removeGrid("_values");
        }
        if (expectedDc.hasGrid("_values")) {
            expectedDc.gridToValues("_values");
            expectedDc.removeGrid("_values");
        }
    }
    
    private static void generate(final DataCollection dc, final String testFileOutput, final List<String[]> list) {
        StringBuilder sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
        final Grid controlSheet = new Grid();
        for (final String[] controlContents : list) {
            TestProcessor.thisProcessor.testId = controlContents[0];
            TestProcessor.thisProcessor.description = controlContents[1];
            TestProcessor.thisProcessor.service = controlContents[2];
            TestProcessor.thisProcessor.preTestService = controlContents[3];
            TestProcessor.thisProcessor.postTestService = controlContents[4];
            TestProcessor.thisProcessor.expectedTimeInMs = Long.parseLong(controlContents[5]);
            TestProcessor.thisProcessor.actualTimeInMs = Long.parseLong(controlContents[6]);
            TestProcessor.thisProcessor.testCleared = Boolean.parseBoolean(controlContents[7]);
            TestProcessor.thisProcessor.message = controlContents[8];
            try {
                controlSheet.setRawData(TestProcessor.thisProcessor.getControlSheet());
            }
            catch (ExilityException e) {
                e.printStackTrace();
            }
            sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
            controlSheet.toSpreadSheetXml(sbf, "_controlSheet");
            dc.toSpreadSheetXml(sbf, "out_");
            sbf.append("\n</Workbook>");
            ResourceManager.saveText(testFileOutput, sbf.toString());
        }
    }
    
    private static void generateSummary(final DataCollection dc, final String summaryFile, final List<String[]> list) throws ExilityException {
        final Grid controlSheet = new Grid();
        final String[][] arraySummary = new String[list.size() + 1][];
        final StringBuilder sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
        for (int i = 0; i < list.size(); ++i) {
            arraySummary[i + 1] = list.get(i);
        }
        arraySummary[0] = Constants.ALL_FIELD_NAMES;
        controlSheet.setRawData(arraySummary);
        controlSheet.toSpreadSheetXml(sbf, "_summary");
        sbf.append(dc);
        sbf.append("\n</Workbook>");
        ResourceManager.saveText(summaryFile, sbf.toString());
    }
    
    String[][] getControlSheet() {
        final String[][] sheet = { Constants.ALL_FIELD_NAMES, this.toArray() };
        return sheet;
    }
}
