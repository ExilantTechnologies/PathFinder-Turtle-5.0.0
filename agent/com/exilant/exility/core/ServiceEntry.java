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

import java.util.Calendar;
import java.util.UUID;

class ServiceEntry implements ToBeInitializedInterface
{
    public static final String TEST_CASE_FIELD_NAME = "_testCaseToBeSaved";
    public static final String[] ALL_ATTRIBUTES;
    String name;
    String description;
    String testFolder;
    boolean allowAllInput;
    boolean allowAllOutput;
    boolean hasUserBasedSecurity;
    DataAccessType dataAccessType;
    String specName;
    boolean isToBeCached;
    String serviceName;
    boolean submitAsBackgroundProcess;
    String fullyQualifiedClassName;
    
    static {
        ALL_ATTRIBUTES = new String[] { "name", "description", "allowAllInput", "allowAllOutput", "hasUserBasedSecurity", "dataAccessType", "isToBeCached" };
    }
    
    ServiceEntry() {
        this.name = null;
        this.description = "";
        this.testFolder = null;
        this.allowAllInput = false;
        this.allowAllOutput = false;
        this.hasUserBasedSecurity = false;
        this.dataAccessType = DataAccessType.READONLY;
        this.specName = null;
        this.serviceName = null;
        this.submitAsBackgroundProcess = false;
        this.fullyQualifiedClassName = null;
    }
    
    void serve(final DataCollection dc) {
        final boolean suppressLog = dc.hasValue("suppress-sql-log");
        if (this.submitAsBackgroundProcess) {
            final String jobId = String.valueOf(this.name) + "$" + UUID.randomUUID().toString();
            dc.addTextValue("_backgroundJobId", jobId);
            final ServiceData sd = new ServiceData();
            dc.copyTo(sd);
            final DataCollection newDc = new DataCollection();
            newDc.copyFrom(sd);
            final ServiceSubmitter submitter = new ServiceSubmitter(this, newDc);
            final Thread thread = new Thread(submitter);
            thread.start();
            Spit.out(String.valueOf(this.serviceName) + " submitted ");
            return;
        }
        try {
            final DbHandle handle = DbHandle.borrowHandle(this.dataAccessType, suppressLog);
            this.execute(dc, handle);
            DbHandle.returnHandle(handle);
        }
        catch (ExilityException e) {
            Spit.out(e);
            dc.addMessage("exilityError", e.getMessage());
        }
    }
    
    protected void execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        ServiceInterface service = null;
        if (this.fullyQualifiedClassName != null) {
            try {
                final Class<?> klass = Class.forName(this.fullyQualifiedClassName);
                final Object instance = klass.newInstance();
                if (instance instanceof ServiceInterface) {
                    service = (ServiceInterface)instance;
                }
            }
            catch (Exception ex) {}
            if (service == null) {
                dc.addError("Setup error : unable to load class " + this.fullyQualifiedClassName + " for service " + this.name + " as a ServiceInterface.");
                return;
            }
        }
        else {
            service = Services.getService(this.serviceName, dc);
        }
        this.executeOnce(dc, handle, service, null, this.dataAccessType);
    }
    
    protected void executeOnce(final DataCollection dc, final DbHandle handle, final ServiceInterface serviceToExecute, final ExilityTask taskToExecute, final DataAccessType accessType) throws ExilityException {
        final boolean testCaseToBeSaved = dc.getBooleanValue("_testCaseToBeCaptured", false);
        StringBuilder inDc = null;
        long startedAt = 0L;
        if (testCaseToBeSaved) {
            Spit.out(String.valueOf(this.serviceName) + " will be saved as a test case");
            inDc = new StringBuilder();
            startedAt = Calendar.getInstance().getTimeInMillis();
            dc.toSpreadSheetXml(inDc, "in_");
        }
        if (accessType == DataAccessType.READWRITE) {
            handle.beginTransaction();
        }
        else if (accessType == DataAccessType.AUTOCOMMIT) {
            handle.startAutoCommit();
        }
        try {
            if (taskToExecute != null) {
                taskToExecute.execute(dc, handle);
            }
            if (serviceToExecute != null) {
                serviceToExecute.execute(dc, handle);
            }
        }
        catch (Exception e) {
            if (!(e instanceof ExilityException) || ((ExilityException)e).messageToBeAdded) {
                Spit.out(e);
                dc.addError(e.getMessage());
                Spit.out("Service Returned with Exception : " + e.getMessage());
            }
        }
        if (accessType == DataAccessType.READWRITE) {
            if (dc.hasError()) {
                handle.rollback();
            }
            else {
                handle.commit();
            }
        }
        else if (accessType == DataAccessType.AUTOCOMMIT) {
            handle.stopAutoCommit();
        }
        String stamp = ResourceManager.getTimeStamp();
        if (testCaseToBeSaved) {
            this.testFolder = String.valueOf(ResourceManager.getResourceFolder()) + "test/" + "TestCase" + "/";
            stamp = ResourceManager.getTimeStamp();
            final String testFileName = String.valueOf(this.testFolder) + this.name.replace('.', '/') + "/" + "test" + stamp + ".xml";
            final TestProcessor testCase = new TestProcessor();
            testCase.testId = stamp;
            testCase.description = "Test case captured";
            testCase.actualTimeInMs = Calendar.getInstance().getTimeInMillis() - startedAt;
            testCase.testCleared = dc.hasError();
            testCase.service = this.serviceName;
            final Grid controlSheet = new Grid();
            controlSheet.setRawData(testCase.getControlSheet());
            final StringBuilder sbf = new StringBuilder("<?xml version=\"1.0\"?>\n<?mso-application progid=\"Excel.Sheet\"?>\n<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n<Styles><Style ss:ID=\"dateStyle\"><NumberFormat ss:Format=\"Medium Date\"/></Style></Styles>");
            controlSheet.toSpreadSheetXml(sbf, "_controlSheet");
            sbf.append((CharSequence)inDc);
            dc.toSpreadSheetXml(sbf, "out_");
            sbf.append("\n</Workbook>");
            ResourceManager.saveText(testFileName, sbf.toString());
        }
    }
    
    public void qualifyServiceName(final String prefix) {
        if (this.serviceName == null) {
            this.serviceName = this.name;
        }
        if (this.serviceName.indexOf(".") == -1) {
            this.serviceName = String.valueOf(prefix) + this.serviceName;
        }
    }
    
    @Override
    public void initialize() {
        if (this.serviceName == null) {
            this.serviceName = this.name;
        }
    }
}
