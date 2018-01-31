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

import java.util.Date;

public class ApplicationParameters implements ToBeInitializedInterface
{
    String projectName;
    boolean definitionsToBeCached;
    boolean cachePrimaryKeys;
    boolean trace;
    String systemDateFunction;
    String connectionString;
    String dateFormattingPrefix;
    String dateFormattingPostfix;
    String dateTimeFormattingPrefix;
    String dateTimeFormattingPostfix;
    String loggedInUserFieldName;
    boolean useQuotesInSql;
    String dbDriver;
    boolean useNullForEmptyString;
    boolean uniqueNamesAcrossGroups;
    Date globalMaxDate;
    Date globalMinDate;
    String dataElementSeparator;
    String[] globalServerDataNames;
    String loginServiceId;
    String logoutServiceId;
    String commonPrimaryKeyColumnName;
    String imageFilePrefix;
    String commonFolderPrefix;
    String exilityFolderPrefix;
    String pageLayoutType;
    int defaultPageHeight;
    int defaultPageWidth;
    int defaultPaginationSize;
    String paginateButtonType;
    String[] setCookies;
    String[] getCookies;
    String fileUploadMeans;
    String filePath;
    String cleanserName;
    String excelFilePath;
    String batchConnectionString;
    String ExcelTemplatesPath;
    String excelReportFileSavePath;
    String eMailFromUserId;
    String eMailFromPassword;
    String eMailBody;
    String eMailHost;
    String eMailPort;
    boolean showIamgeForDeleteOption;
    boolean showDeleteOptionAtEnd;
    String nlsDateFormat;
    boolean licenceValidation;
    String cleanserNameSpace;
    String cleanserAssemblyName;
    boolean lastKeyEventTrigger;
    int commandTimeOutTime;
    boolean enableAuditForAll;
    boolean isSeparateAuditSchema;
    String audittableSuffix;
    String auditConnectionString;
    boolean alignPanels;
    boolean httpNoCacheTagRequires;
    boolean spanForButtonPanelRequires;
    boolean showRequiredLabelinGrid;
    String htmlRootRelativeToResourcePath;
    boolean loggedInUserDataTypeIsInteger;
    boolean assumeTextForMissingDataElement;
    public boolean quietResetAction;
    String projectPackageName;
    int starForRequiredField;
    boolean generateColTags;
    String dataSource;
    String auditDataSource;
    boolean securityEnabled;
    boolean treatZeroAndEmptyStringAsNullForDataSource;
    String trueValueForSql;
    String falseValueForSql;
    boolean enforceNullPolicyForText;
    
    public ApplicationParameters() {
        this.definitionsToBeCached = false;
        this.cachePrimaryKeys = true;
        this.trace = true;
        this.systemDateFunction = "SYSDATE";
        this.connectionString = "";
        this.dateFormattingPrefix = "'";
        this.dateFormattingPostfix = "'";
        this.dateTimeFormattingPrefix = "'";
        this.dateTimeFormattingPostfix = "'";
        this.loggedInUserFieldName = "userId";
        this.useQuotesInSql = false;
        this.dbDriver = "";
        this.useNullForEmptyString = false;
        this.uniqueNamesAcrossGroups = false;
        this.globalMaxDate = null;
        this.globalMinDate = null;
        this.dataElementSeparator = ".";
        this.globalServerDataNames = new String[0];
        this.commonPrimaryKeyColumnName = "id";
        this.imageFilePrefix = "";
        this.commonFolderPrefix = "";
        this.exilityFolderPrefix = "";
        this.pageLayoutType = "0";
        this.defaultPageHeight = 0;
        this.defaultPageWidth = 0;
        this.defaultPaginationSize = 0;
        this.paginateButtonType = "linear";
        this.setCookies = null;
        this.getCookies = null;
        this.fileUploadMeans = "0";
        this.filePath = "";
        this.cleanserName = null;
        this.excelFilePath = "";
        this.batchConnectionString = null;
        this.ExcelTemplatesPath = "";
        this.excelReportFileSavePath = "";
        this.eMailFromUserId = "";
        this.eMailFromPassword = "";
        this.eMailBody = "";
        this.eMailHost = "";
        this.eMailPort = "";
        this.showIamgeForDeleteOption = false;
        this.showDeleteOptionAtEnd = false;
        this.licenceValidation = false;
        this.cleanserNameSpace = null;
        this.cleanserAssemblyName = null;
        this.lastKeyEventTrigger = false;
        this.commandTimeOutTime = 0;
        this.enableAuditForAll = false;
        this.isSeparateAuditSchema = false;
        this.audittableSuffix = null;
        this.auditConnectionString = null;
        this.alignPanels = false;
        this.httpNoCacheTagRequires = false;
        this.spanForButtonPanelRequires = false;
        this.showRequiredLabelinGrid = false;
        this.htmlRootRelativeToResourcePath = "";
        this.loggedInUserDataTypeIsInteger = false;
        this.assumeTextForMissingDataElement = false;
        this.quietResetAction = false;
        this.projectPackageName = "";
        this.starForRequiredField = 0;
        this.generateColTags = false;
        this.dataSource = null;
        this.auditDataSource = null;
        this.securityEnabled = true;
        this.treatZeroAndEmptyStringAsNullForDataSource = false;
        this.trueValueForSql = "1";
        this.falseValueForSql = "0";
        this.enforceNullPolicyForText = false;
    }
    
    @Override
    public void initialize() {
        if (this.starForRequiredField == 0) {
            if (this.pageLayoutType.equals("5")) {
                this.starForRequiredField = 2;
            }
            else {
                this.starForRequiredField = 1;
            }
        }
    }
}
