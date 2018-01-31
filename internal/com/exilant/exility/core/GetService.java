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

public class GetService implements CustomCodeInterface
{
    static final String STEPS_NAME = "steps";
    static final String ID_PREFIX = "step_";
    static final String ID_NAME = "id";
    static String FOLDER_NAME;
    static String FILE_NAME;
    static String STEP_ATTRIBUTES_NAME;
    
    static {
        GetService.FOLDER_NAME = "folderName";
        GetService.FILE_NAME = "fileName";
        GetService.STEP_ATTRIBUTES_NAME = "stepAttributes";
    }
    
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        String serviceName = dc.getTextValue(GetService.FILE_NAME, null);
        final String folderName = dc.getTextValue(GetService.FOLDER_NAME, "");
        if (folderName.length() > 0) {
            serviceName = String.valueOf(folderName) + '.' + serviceName;
        }
        final ServiceInterface s = Services.getService(serviceName, dc);
        if (s == null || !(s instanceof Service)) {
            dc.addMessage("exilityError", "Unable to load " + serviceName + " as a service.");
            return 0;
        }
        final Service service = (Service)s;
        ObjectManager.toDc(service, dc);
        ObjectManager.toDc(service.steps, dc, "step_", GetService.STEP_ATTRIBUTES_NAME);
        final Grid steps = dc.getGrid("steps");
        final int nbrSteps = steps.getNumberOfRows();
        final String[] ids = new String[nbrSteps];
        for (int i = 0; i < nbrSteps; ++i) {
            ids[i] = "step_" + (i + 1);
        }
        final ValueList column = ValueList.newList(ids);
        try {
            steps.addColumn("id", column);
        }
        catch (ExilityException e) {
            dc.addMessage("exilityError", "Error while adding id columns. This is an internal programming error.");
            return 0;
        }
        return 1;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
