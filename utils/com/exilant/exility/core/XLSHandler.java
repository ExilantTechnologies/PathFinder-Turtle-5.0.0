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
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSHandler
{
    public static Workbook getXLSHandler(final String file) {
        Workbook wb = null;
        try {
            final InputStream is = getStream(file);
            try {
                wb = WorkbookFactory.create(is);
            }
            catch (Exception e) {
                Spit.out(e);
            }
            is.close();
        }
        catch (Exception e2) {
            Spit.out(e2);
        }
        return wb;
    }
    
    public static Workbook getXLSHandler(final InputStream inputStream) throws IOException, InvalidFormatException {
        if (inputStream == null) {
            Spit.out("Parameter(InputStream inputStream) being supplied in getXLSXHandler() cant be null");
            return null;
        }
        Workbook wb;
        try {
            wb = WorkbookFactory.create(inputStream);
        }
        catch (InvalidFormatException e) {
            Spit.out((Exception)e);
            return null;
        }
        catch (IOException e2) {
            Spit.out(e2);
            return null;
        }
        return wb;
    }
    
    public static InputStream getStream(final HttpServletRequest req) throws IOException, FileUploadException {
        return getFileItem(req).getInputStream();
    }
    
    public static FileItem getFileItem(final HttpServletRequest req) throws IOException, FileUploadException {
        final DiskFileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload sFileUpload = new ServletFileUpload((FileItemFactory)factory);
        final List<FileItem> items = (List<FileItem>)sFileUpload.parseRequest(req);
        for (final FileItem item : items) {
            if (!item.isFormField()) {
                return item;
            }
        }
        throw new FileUploadException("File field not found");
    }
    
    public static boolean parseMultiPartData(final HttpServletRequest req, final ServiceData container) {
        final DiskFileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload sFileUpload = new ServletFileUpload((FileItemFactory)factory);
        List<FileItem> items = null;
        try {
            items = (List<FileItem>)sFileUpload.parseRequest(req);
        }
        catch (FileUploadException e) {
            container.addMessage("fileUploadFailed", e.getMessage());
            Spit.out((Exception)e);
            return false;
        }
        final String filesPathGridName = req.getHeader("filesPathGridName");
        final String[] coulumnNames = { "fileName", "fileSize", "filePath" };
        final DataValueType[] valueTypes = { DataValueType.TEXT, DataValueType.INTEGRAL, DataValueType.TEXT };
        Value[] columnValues = null;
        FileItem f = null;
        final String allowMultiple = req.getHeader("allowMultiple");
        final List<Value[]> rows = new ArrayList<Value[]>();
        String fileNameWithPath = "";
        final String rootPath = getResourcePath();
        String fileName = null;
        int fileCount = 0;
        for (final FileItem item : items) {
            if (item.isFormField()) {
                final String name = item.getFieldName();
                container.addValue(name, item.getString());
            }
            else {
                f = item;
                if (allowMultiple == null) {
                    continue;
                }
                ++fileCount;
                fileName = item.getName();
                fileNameWithPath = String.valueOf(rootPath) + getUniqueName(fileName);
                final String path = write(item, fileNameWithPath, container);
                if (path == null) {
                    return false;
                }
                if (filesPathGridName != null && filesPathGridName.length() > 0) {
                    columnValues = new Value[] { Value.newValue(fileName), Value.newValue(f.getSize()), Value.newValue(fileNameWithPath) };
                    rows.add(columnValues);
                    fileNameWithPath = "";
                }
                else {
                    container.addValue("file" + fileCount + "_ExilityPath", fileNameWithPath);
                    fileNameWithPath = "";
                }
            }
        }
        if (f == null || allowMultiple != null) {
            if (rows.size() > 0) {
                final Grid aGrid = new Grid(filesPathGridName);
                aGrid.setValues(coulumnNames, valueTypes, rows, null);
                container.addGrid(filesPathGridName, aGrid.getRawData());
            }
            return true;
        }
        fileNameWithPath = String.valueOf(rootPath) + getUniqueName(f.getName());
        final String path2 = write(f, fileNameWithPath, container);
        if (path2 == null) {
            return false;
        }
        container.addValue(container.getValue("fileFieldName"), path2);
        return true;
    }
    
    public static InputStream getStream(final String file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File name being supplied cant be null or blank");
        }
        return new FileInputStream(file);
    }
    
    public static String write(final FileItem fileItem, final String fileNameWithPath, final ServiceData data) {
        try {
            final File file = new File(fileNameWithPath);
            fileItem.write(file);
        }
        catch (IOException ioe) {
            data.addMessage("fileUploadFailed", "file [" + fileNameWithPath + "]  upload failed");
            Spit.out(ioe);
            return null;
        }
        catch (Exception e) {
            data.addMessage("fileUploadFailed", "file [" + fileNameWithPath + "]  upload failed");
            Spit.out(e);
            return null;
        }
        data.addMessage("fileUploadSuccess", "file [" + fileNameWithPath + "] uploaded successfully");
        Spit.out("file [" + fileNameWithPath + "] uploaded successfully");
        return fileNameWithPath;
    }
    
    public static String getUniqueName(final String fileName) {
        final int extIdx = fileName.lastIndexOf(46);
        final String timeStamp = "_" + Calendar.getInstance().getTimeInMillis();
        if (extIdx != -1) {
            final String fileExt = fileName.substring(extIdx);
            final String fname = fileName.substring(0, extIdx);
            return String.valueOf(fname) + timeStamp + fileExt;
        }
        return String.valueOf(fileName) + timeStamp;
    }
    
    public static String getResourcePath() {
        final String apPath = AP.filePath;
        final String rootPath = ResourceManager.getRootFolderName();
        if (apPath == null || apPath.length() < 1) {
            return rootPath;
        }
        if (apPath.lastIndexOf(47) != -1) {
            return String.valueOf(rootPath) + apPath;
        }
        return String.valueOf(rootPath) + apPath + "/";
    }
}
