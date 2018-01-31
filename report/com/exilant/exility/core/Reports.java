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

import java.util.HashMap;
import java.util.Map;

public class Reports
{
    private static Reports instance;
    Map<String, ReportParameters> reports;
    
    static {
        Reports.instance = null;
    }
    
    public Reports() {
        this.reports = new HashMap<String, ReportParameters>();
    }
    
    public static ReportParameters getReport(final String reportName) {
        load();
        if (Reports.instance.reports.containsKey(reportName)) {
            return Reports.instance.reports.get(reportName);
        }
        Spit.out("Design error: " + reportName + " is not a valid report name");
        return null;
    }
    
    private static void load() {
        Reports.instance = (Reports)ResourceManager.loadResource("reportList", Reports.class);
        Spit.out("Loaded " + Reports.instance.reports.size() + " reports.");
    }
}
