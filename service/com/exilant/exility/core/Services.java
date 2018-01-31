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

import com.exilant.exility.wf.Workflow;
import java.util.HashMap;

public class Services
{
    private static final HashMap<String, ServiceInterface> services;
    public static final String WORKFLOW_SERVICE_PREFIX = "workflow.";
    private static final int PREFIX_LENGTH;
    
    static {
        services = new HashMap<String, ServiceInterface>();
        PREFIX_LENGTH = "workflow.".length();
    }
    
    public static ServiceInterface getService(final String serviceName, final DataCollection dc) {
        if (Services.services.containsKey(serviceName)) {
            return Services.services.get(serviceName);
        }
        ServiceInterface service = null;
        if (serviceName.startsWith("workflow.")) {
            final String workflowName = serviceName.substring(Services.PREFIX_LENGTH);
            service = (ServiceInterface)ResourceManager.loadResource("workflow." + workflowName, Workflow.class);
        }
        else {
            service = (ServiceInterface)ResourceManager.loadResource("service." + serviceName, Service.class);
        }
        if (AP.definitionsToBeCached && service != null) {
            Services.services.put(serviceName, service);
        }
        return service;
    }
    
    static void saveService(final Service service) {
        ResourceManager.saveResource(service.name, service);
    }
    
    static void saveWorkflow(final Workflow workflow) {
        ResourceManager.saveResource(workflow.getName(), workflow);
    }
    
    static void flush() {
        Services.services.clear();
    }
}
