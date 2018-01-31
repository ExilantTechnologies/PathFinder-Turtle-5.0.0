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

public class ServiceAgent
{
    public static ServiceAgent getAgent() {
        return new ServiceAgent();
    }
    
    DataCollection getReport(final String serviceName, final String userId, final ServiceData inData) {
        final DataCollection dc = new DataCollection();
        final ServiceEntry entry = ServiceList.getServiceEntry(serviceName, true);
        if (entry == null) {
            Spit.out("ServiceAgent: no such service called" + serviceName);
            dc.addMessage("exilNoServiceName", serviceName);
            return dc;
        }
        this.fillDc(entry, dc, inData);
        entry.serve(dc);
        return dc;
    }
    
    public void serve(final String serviceName, final String userId, final ServiceData inData, final ServiceData outData) {
        final String existingTraceText = Spit.startWriter();
        final Date startTime = new Date();
        this.serveWorker(serviceName, userId, inData, outData);
        final Date endTime = new Date();
        final long diffTime = endTime.getTime() - startTime.getTime();
        final String traceText = String.valueOf(Spit.stopWriter()) + "\nTime taken by Service " + serviceName + " is : " + diffTime + " milliseconds";
        if (existingTraceText != null) {
            Spit.startWriter();
            Spit.out(existingTraceText);
        }
        Spit.writeServiceLog(traceText, userId, serviceName);
        final String traceNeeded = inData.getValue("exilityServerTrace");
        if (traceNeeded != null) {
            outData.addValue("exilityServerTraceText", traceText);
        }
    }
    
    private void serveWorker(final String serviceName, final String userId, final ServiceData inData, final ServiceData outData) {
        if (!this.licenseCleared(outData)) {
            return;
        }
        final DataCollection dc = new DataCollection();
        try {
            if (serviceName.equals("listService")) {
                dc.copyFrom(inData);
                ListService.getService().execute(dc);
                dc.copyTo(outData);
                return;
            }
            if (serviceName.equals("reportService")) {
                final ReportService service = ReportService.getService();
                dc.copyFrom(inData);
                service.execute(dc);
                dc.copyTo(outData);
                return;
            }
            final ServiceEntry entry = ServiceList.getServiceEntry(serviceName, true);
            if (entry == null) {
                Spit.out("ServiceAgent: No service entry found for " + serviceName);
                outData.addMessage("exilNoServiceName", serviceName);
                return;
            }
            if (entry.hasUserBasedSecurity && !UserBasedSecurityAgent.isCleared(userId, serviceName, outData)) {
                return;
            }
            final ServiceSpec spec = this.fillDc(entry, dc, inData);
            entry.serve(dc);
            if (entry.allowAllOutput || spec == null) {
                dc.copyTo(outData);
            }
            else {
                spec.translateOutput(dc, outData);
            }
        }
        catch (Exception e) {
            Spit.out(e);
            dc.addError(e.getMessage());
            dc.copyMessages(outData);
        }
    }
    
    private ServiceSpec fillDc(final ServiceEntry entry, final DataCollection dc, final ServiceData inData) {
        ServiceSpec spec = null;
        String specName = entry.name;
        if (entry.specName != null) {
            specName = entry.specName;
        }
        if (!entry.allowAllInput || !entry.allowAllOutput) {
            spec = ServiceSpecs.getServiceSpec(specName, dc);
            if (spec == null || spec.name == null) {
                Spit.out("Warning: Spec " + specName + " not defined. AllowAll Assumed for service " + entry.name);
            }
        }
        if (entry.allowAllInput || spec == null) {
            dc.copyFrom(inData);
        }
        else {
            spec.translateInput(inData, dc);
        }
        return spec;
    }
    
    void serve(final String serviceName, final DataCollection dc, final DbHandle handle) {
        DataCollection newDc = dc;
        final ServiceEntry entry = ServiceList.getServiceEntry(serviceName, true);
        final String specName = (entry.specName == null) ? serviceName : entry.specName;
        final ServiceSpec spec = ServiceSpecs.getServiceSpec(specName, dc);
        if (!entry.allowAllInput && spec != null && spec.inSpec != null) {
            newDc = new DataCollection(dc);
            spec.inSpec.translate(dc, newDc);
        }
        try {
            final ServiceInterface service = Services.getService(serviceName, dc);
            service.execute(newDc, handle);
        }
        catch (Exception e) {
            dc.addError(e.getMessage());
            Spit.out(e);
        }
        if (!entry.allowAllInput && !entry.allowAllOutput && spec != null && spec.outSpec != null) {
            spec.outSpec.translate(newDc, dc);
        }
    }
    
    private boolean licenseCleared(final ServiceData outData) {
        if (!AP.licenceValidation) {
            return true;
        }
        try {
            final LicenseUtility licUtility = new LicenseUtility();
            if (!licUtility.isValidLicense(true)) {
                outData.addMessage("exilError", "The license is invalid. Please Contact the Exility Support Team to get a valid License.");
                return false;
            }
            final long daysLeft = licUtility.getDaysLeft();
            if (daysLeft < 10L) {
                outData.addMessage("warning", "The license is valid for only " + daysLeft + " days. Please Contact the Exility Support Team to extend the License.");
            }
        }
        catch (Exception ex) {
            outData.addMessage("exilError", ex.getMessage());
            return false;
        }
        return true;
    }
}
