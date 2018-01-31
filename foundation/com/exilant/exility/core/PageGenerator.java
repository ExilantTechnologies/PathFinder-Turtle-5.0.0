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

@Deprecated
public class PageGenerator
{
    public static Page getPageObject(final String path) {
        try {
            final Page page = (Page)ResourceManager.loadResourceFromFile(path, Page.class);
            return page;
        }
        catch (Exception e) {
            try {
                Pages.getPanel(path);
                Spit.out(String.valueOf(path) + " is an include panel, but you are trying it as a page.");
            }
            catch (Exception e2) {
                Spit.out("Error while parsing xml file " + path + ": " + e.getMessage());
                return null;
            }
            return null;
        }
    }
    
    public static String getHtmlFileName(final Page page) {
        String htmlPath;
        if (page.module == null || page.module.length() == 0) {
            htmlPath = String.valueOf(page.name) + ".htm";
        }
        else {
            htmlPath = String.valueOf(page.module) + "/" + page.name + ".htm";
        }
        return htmlPath;
    }
    
    public static String getJavaScriptFileName(final Page page) {
        String jsPath;
        if (page.module == null || page.module.length() == 0) {
            jsPath = String.valueOf(page.name) + ".metadata.js";
        }
        else {
            jsPath = String.valueOf(page.module) + "/" + page.name + ".metadata.js";
        }
        return jsPath;
    }
}
