
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

import java.util.HashSet;
import java.util.Set;
import java.util.Date;

public class AP
{
    public static final int STAR_NOT_SPECIFIED = 0;
    public static final int STAR_AFTER_LABEL = 1;
    public static final int STAR_BEFORE_LABEL = 2;
    public static final int NO_STAR_FOR_LABEL = 3;
    public static String parametersFileName;
    private static ApplicationParameters instance;
    public static String projectName;
    public static boolean definitionsToBeCached;
    public static boolean cachePrimaryKeys;
    public static boolean trace;
    public static boolean securityEnabled;
    public static String systemDateFunction;
    public static String dateFormattingPrefix;
    public static String dateFormattingPostfix;
    public static String dateTimeFormattingPrefix;
    public static String dateTimeFormattingPostfix;
    public static String connectionString;
    public static String loggedInUserFieldName;
    public static boolean useQuotesInSql;
    public static String dbDriver;
    public static boolean useNullForEmptyString;
    public static boolean enforceNullPolicyForText;
    public static boolean uniqueNamesAcrossGroups;
    public static Date globalMinDate;
    public static Date globalMaxDate;
    public static String dataElementSeparator;
    public static String[] globalServerDataNames;
    public static String loginServiceId;
    public static String logoutServiceId;
    public static String commonPrimaryKeyColumnName;
    public static String imageFilePrefix;
    public static String commonFolderPrefix;
    public static String exilityFolderPrefix;
    public static String pageLayoutType;
    public static int defaultPageHeight;
    public static int defaultPageWidth;
    public static int defaultPaginationSize;
    public static String paginateButtonType;
    public static String[] setCookies;
    public static String[] getCookies;
    public static String fileUploadMeans;
    public static String filePath;
    public static String cleanserName;
    public static String excelFilePath;
    public static String batchConnectionString;
    public static String ExcelTemplatesPath;
    public static String excelReportFileSavePath;
    public static String eMailFromUserId;
    public static String eMailFromPassword;
    public static String eMailBody;
    public static String eMailHost;
    public static String eMailPort;
    public static boolean showIamgeForDeleteOption;
    public static boolean showDeleteOptionAtEnd;
    public static String nlsDateFormat;
    public static boolean licenceValidation;
    public static String cleanserNameSpace;
    public static String cleanserAssemblyName;
    public static boolean lastKeyEventTrigger;
    public static int commandTimeOutTime;
    public static boolean enableAuditForAll;
    public static boolean isSeparateAuditSchema;
    public static String audittableSuffix;
    public static String auditConnectionString;
    public static boolean alignPanels;
    public static boolean httpNoCacheTagRequires;
    public static boolean spanForButtonPanelRequires;
    public static boolean showRequiredLabelinGrid;
    public static String htmlRootRelativeToResourcePath;
    public static boolean loggedInUserDataTypeIsInteger;
    public static boolean assumeTextForMissingDataElement;
    public static boolean quietResetAction;
    public static Set<String> cookiesToBeExtracted;
    public static String projectPackageName;
    public static int starForRequiredField;
    public static boolean generateColTags;
    public static String dataSource;
    public static String auditDataSource;
    public static boolean treatZeroAndEmptyStringAsNullForDataSource;
    public static String trueValueForSql;
    public static String falseValueForSql;
    
    static {
        AP.parametersFileName = "applicationParameters";
        AP.instance = null;
        AP.securityEnabled = true;
        AP.enforceNullPolicyForText = false;
        AP.setCookies = null;
        AP.getCookies = null;
        AP.enableAuditForAll = false;
        AP.isSeparateAuditSchema = false;
        AP.audittableSuffix = null;
        AP.alignPanels = false;
        AP.httpNoCacheTagRequires = false;
        AP.spanForButtonPanelRequires = false;
        AP.showRequiredLabelinGrid = false;
        AP.htmlRootRelativeToResourcePath = null;
        AP.loggedInUserDataTypeIsInteger = false;
        AP.assumeTextForMissingDataElement = false;
        AP.quietResetAction = false;
        AP.cookiesToBeExtracted = null;
        AP.projectPackageName = null;
        AP.starForRequiredField = 1;
        AP.generateColTags = false;
        AP.dataSource = null;
        AP.auditDataSource = null;
        AP.treatZeroAndEmptyStringAsNullForDataSource = false;
        AP.trueValueForSql = "1";
        AP.falseValueForSql = "0";
    }
    
    static ApplicationParameters getInstance() {
        return AP.instance;
    }
    
    static synchronized void load() {
        ApplicationParameters ap = (ApplicationParameters)ResourceManager.loadResourceFromFile("applicationParameters.xml", ApplicationParameters.class);
        if (ap == null) {
            Spit.out("Unable to get applicationParametrs.xml");
            ap = new ApplicationParameters();
        }
        setInstance(ap);
    }
    
    public static void setInstance(final ApplicationParameters ap) {
        AP.instance = ap;
        AP.projectName = ap.projectName;
        AP.definitionsToBeCached = ap.definitionsToBeCached;
        AP.cachePrimaryKeys = ap.cachePrimaryKeys;
        AP.trace = ap.trace;
        AP.systemDateFunction = ap.systemDateFunction;
        AP.connectionString = ap.connectionString;
        AP.dateFormattingPrefix = ap.dateFormattingPrefix;
        AP.dateFormattingPostfix = ap.dateFormattingPostfix;
        AP.dateTimeFormattingPrefix = ap.dateTimeFormattingPrefix;
        AP.dateTimeFormattingPostfix = ap.dateTimeFormattingPostfix;
        AP.loggedInUserFieldName = ap.loggedInUserFieldName;
        AP.useQuotesInSql = ap.useQuotesInSql;
        AP.dbDriver = ap.dbDriver;
        AP.useNullForEmptyString = ap.useNullForEmptyString;
        AP.enforceNullPolicyForText = ap.enforceNullPolicyForText;
        AP.uniqueNamesAcrossGroups = ap.uniqueNamesAcrossGroups;
        AP.globalMinDate = ap.globalMinDate;
        AP.globalMaxDate = ap.globalMaxDate;
        AP.dataElementSeparator = ap.dataElementSeparator;
        AP.globalServerDataNames = ap.globalServerDataNames;
        AP.loginServiceId = ap.loginServiceId;
        AP.logoutServiceId = ap.logoutServiceId;
        AP.imageFilePrefix = ap.imageFilePrefix;
        AP.commonFolderPrefix = ap.commonFolderPrefix;
        AP.exilityFolderPrefix = ap.exilityFolderPrefix;
        AP.pageLayoutType = ap.pageLayoutType;
        AP.defaultPageHeight = ap.defaultPageHeight;
        AP.defaultPageWidth = ap.defaultPageWidth;
        AP.commonPrimaryKeyColumnName = ap.commonPrimaryKeyColumnName;
        AP.defaultPaginationSize = ap.defaultPaginationSize;
        AP.paginateButtonType = ap.paginateButtonType;
        AP.fileUploadMeans = ap.fileUploadMeans;
        AP.filePath = ap.filePath;
        AP.setCookies = ap.setCookies;
        AP.cookiesToBeExtracted = null;
        AP.getCookies = ap.getCookies;
        if (ap.getCookies != null) {
            AP.cookiesToBeExtracted = new HashSet<String>();
            String[] getCookies;
            for (int length = (getCookies = ap.getCookies).length, i = 0; i < length; ++i) {
                final String cuki = getCookies[i];
                AP.cookiesToBeExtracted.add(cuki);
            }
        }
        AP.cleanserName = ap.cleanserName;
        AP.excelFilePath = ap.excelFilePath;
        AP.batchConnectionString = ap.batchConnectionString;
        AP.ExcelTemplatesPath = ap.ExcelTemplatesPath;
        AP.excelReportFileSavePath = ap.excelReportFileSavePath;
        AP.eMailFromUserId = ap.eMailFromUserId;
        AP.eMailFromPassword = ap.eMailFromPassword;
        AP.eMailBody = ap.eMailBody;
        AP.eMailHost = ap.eMailHost;
        AP.eMailPort = ap.eMailPort;
        AP.showIamgeForDeleteOption = ap.showIamgeForDeleteOption;
        AP.showDeleteOptionAtEnd = ap.showDeleteOptionAtEnd;
        AP.nlsDateFormat = ap.nlsDateFormat;
        AP.licenceValidation = ap.licenceValidation;
        AP.cleanserNameSpace = ap.cleanserNameSpace;
        AP.cleanserAssemblyName = ap.cleanserAssemblyName;
        AP.lastKeyEventTrigger = ap.lastKeyEventTrigger;
        AP.commandTimeOutTime = ap.commandTimeOutTime;
        AP.enableAuditForAll = ap.enableAuditForAll;
        AP.isSeparateAuditSchema = ap.isSeparateAuditSchema;
        AP.audittableSuffix = ap.audittableSuffix;
        AP.auditConnectionString = ap.auditConnectionString;
        AP.dataSource = ap.dataSource;
        AP.auditDataSource = ap.auditDataSource;
        if (!AP.isSeparateAuditSchema) {
            AP.auditConnectionString = ap.connectionString;
            AP.auditDataSource = ap.dataSource;
        }
        AP.alignPanels = ap.alignPanels;
        AP.httpNoCacheTagRequires = ap.httpNoCacheTagRequires;
        AP.spanForButtonPanelRequires = ap.spanForButtonPanelRequires;
        AP.showRequiredLabelinGrid = ap.showRequiredLabelinGrid;
        AP.htmlRootRelativeToResourcePath = ap.htmlRootRelativeToResourcePath;
        Spit.out("htmlRootRelativeToResourcePath et to " + ap.htmlRootRelativeToResourcePath);
        AP.loggedInUserDataTypeIsInteger = ap.loggedInUserDataTypeIsInteger;
        AP.assumeTextForMissingDataElement = ap.assumeTextForMissingDataElement;
        AP.quietResetAction = ap.quietResetAction;
        AP.projectPackageName = ap.projectPackageName;
        AP.starForRequiredField = ap.starForRequiredField;
        AP.generateColTags = ap.generateColTags;
        AP.securityEnabled = ap.securityEnabled;
        AP.treatZeroAndEmptyStringAsNullForDataSource = ap.treatZeroAndEmptyStringAsNullForDataSource;
        AP.trueValueForSql = ap.trueValueForSql;
        AP.falseValueForSql = ap.falseValueForSql;
    }
    
    static void setFileName(final String applicationParametersFileNameWithNoExtension) {
        AP.parametersFileName = applicationParametersFileNameWithNoExtension;
    }
}
