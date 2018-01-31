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

import org.apache.poi.ss.usermodel.Workbook;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.http.HttpServletRequest;

public class HtmlRequestHandler
{
    public static final String GLOBAL_SERVER_DATA_NAME = "sessionData";
    private static final String PAGINATION_SERVICE_ID = "paginationService";
    private static final String PAGINATION_SERVICE_FIELD_NAME = "paginationServiceName";
    private static final String SORT_COLUMN = "paginationColumnToSort";
    private static final String SORT_DESC = "paginationSortDesc";
    private static final String MATCH_COLUMN = "paginationColumnToMatch";
    private static final String MATCH_VALUE = "paginationValueToMatch";
    private static final String PAGE_SIZE = "PageSize";
    private static final Object LIST_SERVICE;
    public static final Object PATH_SUFFIX;
    private static final String GLOBAL_FIELDS_NAME = "exilityGlobalFields";
    private static HtmlRequestHandler singletonInstance;
    private static boolean suppressSqlLog;
    
    static {
        LIST_SERVICE = "listService";
        PATH_SUFFIX = "__ExilityFilePath";
        HtmlRequestHandler.singletonInstance = new HtmlRequestHandler();
    }
    
    private static String getUserIdName() {
        return AP.loggedInUserFieldName;
    }
    
    private static String getLoginServiceName() {
        return AP.loginServiceId;
    }
    
    private static String getLogoutServiceNamr() {
        return AP.logoutServiceId;
    }
    
    public static HtmlRequestHandler getHandler() {
        return HtmlRequestHandler.singletonInstance;
    }
    
    public ServiceData createInData(final HttpServletRequest req, final boolean formIsSubmitted, final boolean hasSerializedDc, final ServiceData outData) throws ExilityException {
        final ServiceData inData = new ServiceData();
        if (!formIsSubmitted) {
            this.extractSerializedData(req, hasSerializedDc, inData);
        }
        else if (!hasSerializedDc) {
            this.extractParametersAndFiles(req, inData);
        }
        else {
            final HttpSession session = req.getSession();
            if (!ServletFileUpload.isMultipartContent(req)) {
                final String txt = session.getAttribute("dc").toString();
                this.extractSerializedDc(txt, inData);
                this.extractFilesToDc(req, inData);
            }
            else {
                try {
                    final ServletFileUpload fileUploader = new ServletFileUpload();
                    fileUploader.setHeaderEncoding("UTF-8");
                    final FileItemIterator iterator = fileUploader.getItemIterator(req);
                    while (iterator.hasNext()) {
                        final FileItemStream stream = iterator.next();
                        final String fieldName = stream.getFieldName();
                        InputStream inStream = null;
                        inStream = stream.openStream();
                        try {
                            if (stream.isFormField()) {
                                final String fieldValue = Streams.asString(inStream);
                                if (fieldName.equals("dc")) {
                                    this.extractSerializedDc(fieldValue, inData);
                                }
                                else {
                                    inData.addValue(fieldName, fieldValue);
                                }
                            }
                            else {
                                final String fileContents = IOUtils.toString(inStream);
                                inData.addValue(String.valueOf(fieldName) + HtmlRequestHandler.PATH_SUFFIX, fileContents);
                            }
                        }
                        catch (Exception e) {
                            Spit.out("error whiel extracting data from request stream " + e.getMessage());
                        }
                        IOUtils.closeQuietly(inStream);
                    }
                }
                catch (Exception ex) {}
                final Enumeration e2 = session.getAttributeNames();
                while (e2.hasMoreElements()) {
                    final String name = e2.nextElement();
                    if (name.equals("dc")) {
                        this.extractSerializedDc(req.getSession().getAttribute(name).toString(), inData);
                    }
                    final String value = req.getSession().getAttribute(name).toString();
                    inData.addValue(name, value);
                    System.out.println("name is: " + name + " value is: " + value);
                }
            }
        }
        this.getStandardFields(req, inData);
        return inData;
    }
    
    public ServiceData createInDataForStream(final HttpServletRequest req, final boolean formIsSubmitted, final boolean hasSerializedDc, final ServiceData outData) throws ExilityException {
        final ServiceData inData = new ServiceData();
        if (!formIsSubmitted) {
            this.extractSerializedData(req, hasSerializedDc, inData);
            return inData;
        }
        if (!hasSerializedDc) {
            this.extractParametersAndFiles(req, inData);
            return inData;
        }
        final HttpSession session = req.getSession();
        try {
            if (!ServletFileUpload.isMultipartContent(req)) {
                final String txt = session.getAttribute("dc").toString();
                this.extractSerializedDc(txt, inData);
                this.extractFilesToDc(req, inData);
                return inData;
            }
            final ServletFileUpload fileUploader = new ServletFileUpload();
            fileUploader.setHeaderEncoding("UTF-8");
            final FileItemIterator iterator = fileUploader.getItemIterator(req);
            while (iterator.hasNext()) {
                final FileItemStream stream = iterator.next();
                InputStream inStream = null;
                try {
                    inStream = stream.openStream();
                    final String fieldName = stream.getFieldName();
                    if (stream.isFormField()) {
                        final String fieldValue = Streams.asString(inStream);
                        if (fieldName.equals("dc")) {
                            this.extractSerializedDc(fieldValue, inData);
                        }
                        else {
                            inData.addValue(fieldName, fieldValue);
                        }
                    }
                    else {
                        final String fileContents = IOUtils.toString(inStream);
                        inData.addValue(String.valueOf(fieldName) + HtmlRequestHandler.PATH_SUFFIX, fileContents);
                    }
                    inStream.close();
                }
                finally {
                    IOUtils.closeQuietly(inStream);
                }
                IOUtils.closeQuietly(inStream);
            }
            final Enumeration e = req.getSession().getAttributeNames();
            while (e.hasMoreElements()) {
                final String name = e.nextElement();
                if (name.equals("dc")) {
                    this.extractSerializedDc(req.getSession().getAttribute(name).toString(), inData);
                }
                final String value = req.getSession().getAttribute(name).toString();
                inData.addValue(name, value);
                System.out.println("name is: " + name + " value is: " + value);
            }
        }
        catch (Exception ex) {}
        return inData;
    }
    
    private void extractFilesToDc(final HttpServletRequest req, final ServiceData inData) {
        final HttpSession session = req.getSession();
        final Object o1 = session.getAttribute("ExilityIsMultipart");
        final Object o2 = session.getAttribute("ExilityFileItemsList");
        if (o1 == null || o2 == null) {
            return;
        }
        final boolean isMultipart = (boolean)o1;
        final List<String> items = (List<String>)o2;
        if (isMultipart) {
            final Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                final String fldName = String.valueOf(iterator.next().toString()) + HtmlRequestHandler.PATH_SUFFIX;
                final Object obj = session.getAttribute(fldName);
                if (obj != null) {
                    inData.addValue(fldName, obj.toString());
                }
            }
        }
    }
    
    public ServiceData serve(final String serviceName, final HttpServletRequest req) throws ExilityException {
        final ServiceData outData = new ServiceData();
        final ServiceData inData = this.createInData(req, true, false, outData);
        if (inData == null) {
            return outData;
        }
        if (!SecurityGuard.getGuard().cleared(req, inData, outData)) {
            return outData;
        }
        this.executeService(req, inData, outData, serviceName);
        return outData;
    }
    
    public ServiceData processRequest(final HttpServletRequest req, final HttpServletResponse resp, final boolean formIsSubmitted, final boolean hasSerializedDc) throws ExilityException {
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Spit.out("Not UTF-8 : '" + e.getMessage() + "'");
        }
        final ServiceData outData = new ServiceData();
        final String simpleFileUpload = req.getHeader("simple_file_upload");
        String outDataServiceId = null;
        if (simpleFileUpload != null) {
            final boolean isFileUploadSucceeded = this.processSimpleFileUpload(req, outData);
            if (!isFileUploadSucceeded) {
                return outData;
            }
            outDataServiceId = outData.getValue("serviceId");
        }
        ServiceData inData = new ServiceData();
        final String isStream = req.getHeader("isStream");
        if (isStream == null) {
            inData = this.createInData(req, formIsSubmitted, hasSerializedDc, outData);
        }
        else {
            inData = this.createInDataForStream(req, formIsSubmitted, hasSerializedDc, outData);
        }
        if (outDataServiceId != null && inData != null) {
            inData.extractData(outData);
        }
        if (inData == null) {
            return outData;
        }
        final String serviceName = inData.getValue("serviceId");
        if (serviceName == null || serviceName.length() == 0) {
            outData.addMessage("exilNoService", new String[0]);
            return outData;
        }
        if (serviceName.equals(getLoginServiceName())) {
            this.executeService(req, inData, outData, serviceName);
            this.doLogin(req, resp, outData);
            return outData;
        }
        if (serviceName.equals(getLogoutServiceNamr())) {
            this.executeService(req, inData, outData, serviceName);
            this.doLogout(req, resp);
            return outData;
        }
        if (!serviceName.equals(HtmlRequestHandler.LIST_SERVICE) && !SecurityGuard.getGuard().cleared(req, inData, outData)) {
            return outData;
        }
        if (serviceName.equals("paginationService")) {
            this.getPaginationData(req, inData, outData);
            return outData;
        }
        this.executeService(req, inData, outData, serviceName);
        this.savePaginationData(req, inData, outData);
        if (outData.hasValue("exilityGlobalFields")) {
            final String sessionObjName = "sessionData" + outData.getValue(getUserIdName());
            Map<String, String> sessionData = (Map<String, String>)req.getSession(true).getAttribute(sessionObjName);
            if (sessionData == null) {
                sessionData = new HashMap<String, String>();
                req.getSession(true).setAttribute(sessionObjName, (Object)sessionData);
            }
            final String fieldNames = outData.getValue("exilityGlobalFields");
            String[] split;
            for (int length = (split = fieldNames.split(",")).length, i = 0; i < length; ++i) {
                final String fieldName = split[i];
                sessionData.put(fieldName, outData.getValue(fieldName));
            }
            outData.removeValue("exilityGlobalFields");
        }
        return outData;
    }
    
    private void executeService(final HttpServletRequest req, final ServiceData inData, final ServiceData outData, final String serviceName) {
        ServiceCleanserInterface serviceCleanser = null;
        if (AP.cleanserName != null) {
            serviceCleanser = ServiceCleansers.getCleanser(AP.cleanserName);
            if (serviceCleanser == null) {
                outData.addMessage("exilNoSuchCleanser", AP.cleanserName);
                return;
            }
            if (!serviceCleanser.cleanseBeforeService(req, inData)) {
                outData.addMessage("cleanseBeforeServiceFailed", AP.cleanserName);
                return;
            }
        }
        final String userId = inData.getValue(getUserIdName());
        final ServiceAgent agent = new ServiceAgent();
        agent.serve(serviceName, userId, inData, outData);
        if (serviceCleanser != null && !serviceCleanser.cleanseAfterService(req, outData)) {
            outData.addMessage("cleanseAfterServiceFailed", AP.cleanserName);
        }
    }
    
    private void extractSerializedData(final HttpServletRequest req, final boolean hasSerializedDc, final ServiceData inData) throws ExilityException {
        try {
            final DataInputStream sr = new DataInputStream((InputStream)req.getInputStream());
            final InputStreamReader isr = new InputStreamReader(sr, "UTF-8");
            final StringBuffer buffer = new StringBuffer();
            final Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char)ch);
            }
            final String inputText = buffer.toString();
            if (inputText.length() == 0) {
                return;
            }
            if (hasSerializedDc) {
                this.extractSerializedDc(inputText, inData);
            }
            else {
                this.deserialize(inputText, inData);
            }
        }
        catch (IOException ex) {}
    }
    
    private void deserialize(final String str, final ServiceData data) {
        if (str == null || str.length() == 0) {
            return;
        }
        String[] split;
        for (int length = (split = str.split("&")).length, i = 0; i < length; ++i) {
            final String pair = split[i];
            final String[] vals = pair.split("=");
            final String val = (vals.length > 1) ? vals[1] : "";
            data.addValue(vals[0], val);
        }
    }
    
    private void extractParametersAndFiles(final HttpServletRequest req, final ServiceData data) {
        final Enumeration<String> paramNames = (Enumeration<String>)req.getParameterNames();
        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            final String[] vals = req.getParameterValues(paramName);
            if (vals == null || vals.length == 0) {
                data.addValue(paramName, "");
            }
            else if (vals.length == 1) {
                data.addValue(paramName, vals[0]);
            }
            else {
                data.addList(paramName, vals);
            }
        }
        this.extractFilesToDc(req, data);
    }
    
    private void extractSerializedDc(final String inputText, final ServiceData inData) throws ExilityException {
        if (inputText.length() == 0) {
            return;
        }
        try {
            inData.extractData(inputText);
        }
        catch (Exception e) {
            Spit.out(e);
            inData.addMessage("exilError", e.getMessage());
        }
    }
    
    private void getStandardFields(final HttpServletRequest req, final ServiceData inData) {
        if (HtmlRequestHandler.suppressSqlLog) {
            inData.addValue("suppress-sql-log", "1");
        }
        if (AP.cookiesToBeExtracted != null) {
            final Cookie[] cookies = req.getCookies();
            if (cookies != null && cookies.length > 0) {
                Cookie[] array;
                for (int length = (array = cookies).length, i = 0; i < length; ++i) {
                    final Cookie cookie = array[i];
                    if (AP.cookiesToBeExtracted.contains(cookie.getName())) {
                        Spit.out(String.valueOf(cookie.getName()) + " extracted from cookie");
                        inData.addValue(cookie.getName(), cookie.getValue());
                    }
                }
            }
        }
        this.extractParametersAndFiles(req, inData);
        final HttpSession session = req.getSession();
        String token = req.getHeader("X-CSRF-Token");
        if (token == null) {
            token = inData.getValue("X-CSRF-Token");
        }
        if (token == null) {
            final Object obj = session.getAttribute("LAST_TOKEN_USED");
            if (obj != null) {
                token = obj.toString();
            }
        }
        if (token != null) {
            final Object obj = session.getAttribute(token);
            if (obj != null && obj instanceof SessionData) {
                Spit.out("Session fields being extracted from new token based object.");
                ((SessionData)obj).extractAll(inData);
            }
            else {
                Spit.out("CSRF token found to be " + token + " but session data not found");
            }
        }
        else {
            Spit.out("NO CSRF token. Will try old ways of session data.");
            final Object data = session.getAttribute("sessionData" + inData.getValue(getUserIdName()));
            if (data != null && data instanceof Map) {
                final Map<String, String> sessionData = (Map<String, String>)data;
                for (final String name : sessionData.keySet()) {
                    final String val = sessionData.get(name);
                    if (val != null && val.length() > 0) {
                        inData.addValue(name, val);
                    }
                }
            }
        }
    }
    
    void addGlobalDataToSession(final HttpServletRequest req, final ServiceData outData) {
        final Map<String, String> sessionData = new HashMap<String, String>();
        String[] globalServerDataNames;
        for (int length = (globalServerDataNames = AP.globalServerDataNames).length, i = 0; i < length; ++i) {
            final String name = globalServerDataNames[i];
            final String val = outData.getValue(name);
            if (val != null) {
                if (val.length() != 0) {
                    Spit.out("Gloabal Field " + name + " is added and its value is " + val);
                    sessionData.put(name, val);
                }
            }
        }
        req.getSession().setAttribute("sessionData" + outData.getValue(getUserIdName()), (Object)sessionData);
    }
    
    public boolean login(final HttpServletRequest req, final HttpServletResponse resp, final boolean formIsSubmitted) throws ExilityException {
        final ServiceData data = new ServiceData();
        for (final Object param : req.getParameterMap().keySet()) {
            data.addValue((String)param, req.getParameterMap().get(param));
        }
        if (formIsSubmitted) {
            this.extractParametersAndFiles(req, data);
        }
        else {
            this.extractSerializedData(req, false, data);
        }
        if (AP.cleanserName != null) {
            final ServiceCleanserInterface serviceCleanser = ServiceCleansers.getCleanser(AP.cleanserName);
            if (serviceCleanser == null) {
                data.addMessage("exilNoSuchCleanser", AP.cleanserName);
                return false;
            }
            if (!serviceCleanser.cleanseBeforeService(req, data)) {
                data.addMessage("cleanseBeforeServiceFailed", AP.cleanserName);
                return false;
            }
        }
        final ServiceAgent agent = new ServiceAgent();
        agent.serve(getLoginServiceName(), null, data, data);
        return this.doLogin(req, resp, data);
    }
    
    private boolean doLogin(final HttpServletRequest req, final HttpServletResponse resp, final ServiceData data) {
        if (data.getErrorStatus() != 0) {
            return false;
        }
        req.getSession().setAttribute(AP.loggedInUserFieldName, (Object)data.getValue(AP.loggedInUserFieldName));
        Cookie cookie = new Cookie(AP.loggedInUserFieldName, data.getValue(AP.loggedInUserFieldName));
        final Date now = DateUtility.addDays(new Date(), 400);
        cookie.setMaxAge((int)now.getTime());
        resp.addCookie(cookie);
        if (AP.setCookies != null) {
            String[] setCookies;
            for (int length = (setCookies = AP.setCookies).length, i = 0; i < length; ++i) {
                final String name = setCookies[i];
                cookie = new Cookie(name, data.getValue(name));
                cookie.setPath(req.getContextPath());
                if (data.hasValue(name)) {
                    Spit.out(" cookie " + name + " is set with value = " + data.getValue(name));
                    cookie.setMaxAge((int)now.getTime());
                }
                else {
                    Spit.out(String.valueOf(name) + " does not have value and hence cookie is not set");
                    cookie.setMaxAge(-12);
                }
                resp.addCookie(cookie);
            }
        }
        data.addValue("*_usersession", req.getSession().getId());
        this.addGlobalDataToSession(req, data);
        if (AP.cleanserName != null) {
            final ServiceCleanserInterface serviceCleanser = ServiceCleansers.getCleanser(AP.cleanserName);
            if (serviceCleanser == null) {
                data.addMessage("exilNoSuchCleanser", AP.cleanserName);
                return false;
            }
            if (!serviceCleanser.cleanseAfterService(req, data)) {
                data.addMessage("cleanseAfterServiceFailed", AP.cleanserName);
                return false;
            }
        }
        return true;
    }
    
    public void logout(final HttpServletRequest req, final HttpServletResponse resp) {
        final ServiceData data = new ServiceData();
        final ServiceAgent agent = new ServiceAgent();
        agent.serve(getLogoutServiceNamr(), null, data, data);
        this.doLogout(req, resp);
    }
    
    private void doLogout(final HttpServletRequest req, final HttpServletResponse resp) {
        final Cookie cookie = new Cookie(AP.loggedInUserFieldName, "");
        final Date now = DateUtility.addDays(new Date(), -2);
        cookie.setMaxAge((int)now.getTime());
        resp.addCookie(cookie);
        req.getSession().invalidate();
    }
    
    private void savePaginationData(final HttpServletRequest req, final ServiceData inData, final ServiceData outData) {
        final ArrayList<String> gridNames = new ArrayList<String>();
        for (final String name : outData.grids.keySet()) {
            gridNames.add(name);
        }
        final HttpSession session = req.getSession();
        for (final String name2 : gridNames) {
            final String pageSize = inData.getValue(String.valueOf(name2) + "PageSize");
            if (pageSize == null) {
                continue;
            }
            final int ps = Integer.parseInt(pageSize);
            final String[][] grid = outData.grids.get(name2);
            final int n = grid.length - 1;
            if (n <= ps) {
                session.removeAttribute(name2);
            }
            else {
                session.setAttribute(name2, (Object)grid);
                final String[][] newGrid = new String[ps + 1][];
                newGrid[0] = grid[0];
                for (int i = 1; i < newGrid.length; ++i) {
                    newGrid[i] = grid[i];
                }
                outData.addGrid(name2, newGrid);
                outData.addValue(String.valueOf(name2) + "TotalRows", Integer.toString(n));
            }
        }
    }
    
    private void getPaginationData(final HttpServletRequest req, final ServiceData inData, final ServiceData outData) {
        final String tableName = inData.getValue("tableName");
        if (tableName == null) {
            outData.addMessage("exilError", "Client did not send table name for paginated data.");
            return;
        }
        Object paginationData = req.getSession().getAttribute(tableName);
        int pageNo = 0;
        final String txt = inData.getValue("pageNo");
        if (txt != null && txt.length() > 0) {
            pageNo = Integer.parseInt(txt);
        }
        if (pageNo == 0) {
            if (paginationData != null) {
                req.getSession().removeAttribute(tableName);
            }
            return;
        }
        if (paginationData == null) {
            final String newService = inData.getValue("paginationServiceName");
            if (newService == null) {
                outData.addMessage("exilError", "Pagination data is not available, probably because you have re-logged-in. Please resubmit your query, or reset page.");
                return;
            }
            this.executeService(req, inData, outData, newService);
            paginationData = outData.getGrid(tableName);
            if (paginationData == null) {
                outData.addMessage("exilError", "Pagination service " + newService + " did not provide data in table " + tableName);
                return;
            }
            req.getSession().setAttribute(tableName, paginationData);
        }
        final String[][] grid = (String[][])paginationData;
        final int totalRows = grid.length - 1;
        int pageSize = Integer.parseInt(inData.getValue("pageSize"));
        final String columnToSort = inData.getValue("paginationColumnToSort");
        if (columnToSort != null) {
            final String sortDesc = inData.getValue("paginationSortDesc");
            final boolean toSortDesc = sortDesc != null && sortDesc.equals("1");
            Util.sortGrid(grid, columnToSort, toSortDesc);
        }
        int istart = (pageNo - 1) * pageSize + 1;
        if (istart > totalRows) {
            istart = 1;
        }
        final int iend = istart + pageSize - 1;
        if (iend > totalRows) {
            pageSize = totalRows - istart + 1;
        }
        final String[][] newGrid = new String[pageSize][];
        for (int i = 0; i < newGrid.length; ++i) {
            newGrid[i] = grid[istart];
            ++istart;
        }
        outData.addGrid(tableName, newGrid);
    }
    
    private int sortGrid(final String[][] grid, final ServiceData inData) {
        final String sortColumn = inData.getValue("paginationColumnToSort");
        final String sortDesc = inData.getValue("paginationSortDesc");
        final boolean toSortDesc = sortDesc != null && sortDesc.equals("1");
        Util.sortGrid(grid, sortColumn, toSortDesc);
        final String columnToMatch = inData.getValue("paginationColumnToMatch");
        if (columnToMatch == null) {
            return 1;
        }
        final String valueToMatch = inData.getValue("paginationValueToMatch");
        if (valueToMatch == null) {
            Spit.out("Value not supplied for column " + columnToMatch + " to look-up a row. First page is retrned");
            return 1;
        }
        final int rowNumber = Util.getMatchingRow(grid, columnToMatch, valueToMatch);
        if (rowNumber > 0) {
            return rowNumber;
        }
        Spit.out("Could not find a row with a value of " + valueToMatch + " for column " + columnToMatch + ". First page is returned.");
        return 1;
    }
    
    private boolean processSimpleFileUpload(final HttpServletRequest req, final ServiceData data) {
        if (!ServletFileUpload.isMultipartContent(req)) {
            data.addMessage("noMultipartContentFound", "No multipart content found.");
            return false;
        }
        XLSHandler.parseMultiPartData(req, data);
        try {
            this.extractSerializedDc(data.getValue("dc"), data);
            data.removeValue("dc");
        }
        catch (ExilityException e1) {
            data.addMessage("exilError", e1.getMessage());
            Spit.out(e1);
            data.removeValue("dc");
            return false;
        }
        final String gridName = data.getValue("xlsGridName");
        final String fileFieldNameVal = data.getValue("fileFieldName");
        final String allowMultiple = req.getHeader("allowMultiple");
        Spit.out("fileFieldName returns file path at the server: ===>" + fileFieldNameVal);
        if (gridName == null || gridName.trim().isEmpty() || allowMultiple != null) {
            return true;
        }
        Workbook wbHandler = null;
        final String fileNameWithPath = data.getValue("filePath");
        Spit.out("fileFieldName returns file path at the server: ===>" + fileNameWithPath);
        if (fileNameWithPath == null || fileNameWithPath.isEmpty()) {
            Spit.out("fileFieldName returns null or empty file path at the server: check in write()===>");
            return false;
        }
        wbHandler = XLSHandler.getXLSHandler(fileNameWithPath);
        if (wbHandler == null) {
            data.addMessage("fileReadFailure", "file [" + fileNameWithPath + "] content cant be read as Grid, check whether you supplied correct excel file or not.");
            Spit.out("file [" + fileNameWithPath + "] content cant be read as Grid, check whether you supplied correct excel file or not. cause \n");
            return false;
        }
        final DataCollection dc = new DataCollection();
        dc.addTextValue("xlsGridName", gridName);
        new XLSReader().readAWorkbook(wbHandler, dc);
        dc.copyTo(data);
        if (!dc.hasError()) {
            data.addMessage("fileReadSuccess", "rows extracted from the file [" + fileNameWithPath + "] successfully");
            data.addValue(fileFieldNameVal, fileNameWithPath);
            data.removeValue("filePath");
            return true;
        }
        data.addMessage("fileReadFailure", "file [" + fileNameWithPath + "] content cant be read as Grid, check whether you supplied correct excel file or not.");
        Spit.out("file [" + fileNameWithPath + "] content cant be read as Grid, check whether you supplied correct excel file or not.");
        return false;
    }
    
    public static void setSuppressSqlLog(final boolean suppress) {
        HtmlRequestHandler.suppressSqlLog = suppress;
    }
}
