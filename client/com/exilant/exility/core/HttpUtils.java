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
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import javax.servlet.http.HttpServletRequest;

public class HttpUtils
{
    private static HttpUtils instance;
    
    static {
        HttpUtils.instance = null;
    }
    
    public static HttpUtils getInstance() {
        if (HttpUtils.instance == null) {
            HttpUtils.instance = new HttpUtils();
        }
        return HttpUtils.instance;
    }
    
    public void simpleExtract(final HttpServletRequest req, final ServiceData data) {
        final FileItemFactory factory = (FileItemFactory)new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            final List<?> list = (List<?>)upload.parseRequest(req);
            if (list != null) {
                for (final Object item : list) {
                    if (item instanceof FileItem) {
                        final FileItem fileItem = (FileItem)item;
                        data.addValue(fileItem.getFieldName(), fileItem.getString());
                    }
                    else {
                        Spit.out("Servlet Upload retruned an item that is not a FileItem. Ignorinig that");
                    }
                }
            }
        }
        catch (FileUploadException e) {
            Spit.out((Exception)e);
            data.addMessage("exilityError", "Error while parsing form data. " + e.getMessage());
        }
    }
}
