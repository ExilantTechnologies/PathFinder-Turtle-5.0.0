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

public class ServiceSubmitter implements Runnable
{
    private final DataCollection dc;
    private final ServiceEntry entry;
    
    public ServiceSubmitter(final ServiceEntry entry, final DataCollection dc) {
        this.entry = entry;
        this.dc = dc;
    }
    
    @Override
    public void run() {
        Spit.startWriter();
        final Date startedAt = new Date();
        final boolean suppressLog = this.dc.hasValue("suppress-sql-log");
        DbHandle handle = null;
        try {
            handle = DbHandle.borrowHandle(this.entry.dataAccessType, suppressLog);
            this.entry.execute(this.dc, handle);
            DbHandle.returnHandle(handle);
        }
        catch (Exception e) {
            this.dc.addError(e.getMessage());
            Spit.out(e);
        }
        String myTraceText = Spit.stopWriter();
        final String serviceName = this.dc.getTextValue("serviceId", "unknown service");
        final String userId = this.dc.getTextValue(AP.loggedInUserFieldName, "unknown user");
        final long ms = new Date().getTime() - startedAt.getTime();
        myTraceText = "\n background service started processing at " + startedAt.toString() + " and took " + ms + " ms\n" + myTraceText;
        Spit.writeServiceLog(myTraceText, userId, serviceName);
        final String jobId = this.dc.getTextValue("_backgroundJobId", null);
        if (jobId != null) {
            if (this.dc.hasValue("exilityServerTrace")) {
                this.dc.addTextValue("exilityServerTraceText", myTraceText);
            }
            final ServiceData outData = new ServiceData();
            this.dc.copyTo(outData);
            FileUtility.writeText(1, jobId, outData.toSerializedData());
        }
    }
}
