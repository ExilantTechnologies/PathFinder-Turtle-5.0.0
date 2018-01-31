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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class ServiceList
{
    static ServiceList instance;
    private static final ReentrantLock lock;
    Map<String, ServiceEntry> serviceEntries;
    
    static {
        ServiceList.instance = new ServiceList();
        lock = new ReentrantLock();
    }
    
    ServiceList() {
        this.serviceEntries = new HashMap<String, ServiceEntry>();
    }
    
    static ServiceEntry getServiceEntry(final String serviceName, final boolean createIfNotFound) {
        if (ServiceList.instance == null) {
            load();
        }
        ServiceEntry entry = ServiceList.instance.serviceEntries.get(serviceName);
        if (entry == null && createIfNotFound) {
            entry = getTempServiceEntry(serviceName);
            if (entry != null) {
                addServiceEntry(serviceName, entry);
            }
        }
        return entry;
    }
    
    static void reload(final boolean removeExistingEntries) {
        ServiceList.lock.lock();
        if (ServiceList.instance == null || removeExistingEntries) {
            ServiceList.instance = new ServiceList();
        }
        loadUnderLockAndKey();
        ServiceList.lock.unlock();
    }
    
    private static void load() {
        ServiceList.lock.lock();
        if (ServiceList.instance == null) {
            ServiceList.instance = new ServiceList();
            loadUnderLockAndKey();
        }
        ServiceList.lock.unlock();
    }
    
    private static void loadUnderLockAndKey() {
        try {
            final Map<String, Object> sls = ResourceManager.loadFromFileOrFolder("serviceList", "serviceEntry");
            for (final String fileName : sls.keySet()) {
                final Object obj = sls.get(fileName);
                if (!(obj instanceof ServiceList)) {
                    Spit.out("serviceEntry folder contains an xml that is not a serviceList. File ignored");
                }
                else {
                    final ServiceList sl = (ServiceList)obj;
                    ServiceList.instance.copyFrom(sl, fileName);
                }
            }
            Spit.out(String.valueOf(ServiceList.instance.serviceEntries.size()) + " service entries loaded");
        }
        catch (Exception e) {
            Spit.out("Unable to load Servicelists....");
        }
    }
    
    private void copyFrom(final ServiceList sl, final String fileName) {
        Spit.out("Going to copy serviceEntries from " + fileName);
        final String prefix = (fileName.length() == 0) ? "" : (String.valueOf(fileName) + '.');
        for (final ServiceEntry se : sl.serviceEntries.values()) {
            final String qualifiedName = String.valueOf(prefix) + se.name;
            if (this.serviceEntries.containsKey(qualifiedName)) {
                Spit.out("Error : message " + qualifiedName + " is defined more than once" + ((prefix.length() == 0) ? "." : (" in " + prefix + ".")));
            }
            else {
                se.qualifyServiceName(prefix);
                this.serviceEntries.put(qualifiedName, se);
            }
        }
    }
    
    static void addServiceEntry(final String serviceName, final ServiceEntry entry) {
        if (ServiceList.instance == null) {
            load();
        }
        ServiceList.instance.serviceEntries.put(serviceName, entry);
    }
    
    static ServiceEntry getTempServiceEntry(final String serviceName) {
        final ServiceInterface service = Services.getService(serviceName, null);
        if (service == null) {
            return null;
        }
        final ServiceEntry entry = new ServiceEntry();
        entry.name = serviceName;
        entry.serviceName = serviceName;
        entry.allowAllInput = true;
        entry.allowAllOutput = true;
        entry.dataAccessType = service.getDataAccessType(null);
        entry.hasUserBasedSecurity = true;
        entry.initialize();
        return entry;
    }
    
    static ServiceList getInstance() {
        return ServiceList.instance;
    }
}
