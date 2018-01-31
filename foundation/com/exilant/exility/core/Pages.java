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

public class Pages
{
    public static Page getPage(final String folderName, final String pageName) {
        final Object o = ResourceManager.loadResource("page." + folderName + '/' + pageName, Page.class);
        if (o == null) {
            Spit.out(String.valueOf(pageName) + " Could not be loaded. Possible that that the XML is in error. Lok at log file.");
            return null;
        }
        if (o instanceof Page) {
            return (Page)o;
        }
        Spit.out(String.valueOf(pageName) + " is an nclude panel...");
        return null;
    }
    
    public static void savePage(final Page thisPage) {
        final String fileName = "page/" + ((thisPage.module != null) ? (String.valueOf(thisPage.module) + '.') : "") + thisPage.name;
        ResourceManager.saveResource(fileName, thisPage);
    }
    
    static AbstractPanel getPanel(final String panelName) {
        Spit.out("Going to load panel with file name = page." + panelName + ".xml");
        final Object o = ResourceManager.loadResource("page/" + panelName, AbstractPanel.class);
        if (o != null) {
            return (AbstractPanel)o;
        }
        Spit.out(String.valueOf(panelName) + " could not be loaded");
        return null;
    }
}
