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

import org.apache.commons.io.FilenameUtils;
import java.io.File;

public class LoadHandler implements Runnable, LoadHandlerInterface
{
    @Override
    public void run() {
        ResourceManager.loadAllResources(null, "WEB-INF/resource");
        final String fileName = "WEB-INF/resource/test/";
        final String stamp = ResourceManager.getTimeStamp();
        final String summary = String.valueOf(fileName) + "/" + "summary" + "/" + "summary" + stamp + ".xml";
        try {
            this.process(fileName, summary);
        }
        catch (ExilityException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void process(final String fileName, final String summaryFile) throws ExilityException {
        final TestReportInterface testProcessor = new TestReport();
        final File dir = new File(fileName);
        File[] listFiles;
        for (int length = (listFiles = dir.listFiles()).length, i = 0; i < length; ++i) {
            final File testFolder = listFiles[i];
            final String qualifiedFileName = FilenameUtils.getName(testFolder.toString());
            if (!qualifiedFileName.equals(".DS_Store")) {
                if (!qualifiedFileName.equals("summary")) {
                    try {
                        testProcessor.process(testFolder.toString(), summaryFile);
                    }
                    catch (ExilityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
