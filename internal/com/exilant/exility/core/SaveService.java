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

import java.util.Map;
import java.lang.reflect.Field;

public class SaveService implements CustomCodeInterface
{
    @Override
    public int execute(final DataCollection dc, final DbHandle dbHandle, final String gridName, final String[] parameters) {
        final String serviceName = dc.getTextValue(GetService.FILE_NAME, null);
        final String folderName = dc.getTextValue(GetService.FOLDER_NAME, "");
        String qualifiedName = serviceName;
        if (folderName.length() > 0) {
            qualifiedName = String.valueOf(folderName) + '.' + serviceName;
        }
        final Service service = new Service();
        ObjectManager.fromDc(service, dc);
        final int nbrSteps = (service.steps == null) ? 0 : service.steps.length;
        if (nbrSteps == 0) {
            dc.addMessage("exilityError", "Service has no steps");
            return 0;
        }
        final String[] ids = dc.getGrid("steps").getColumnAsTextArray("id");
        final String[][] stepAttributes = dc.getGrid(GetService.STEP_ATTRIBUTES_NAME).getRawData();
        for (int i = 0; i < nbrSteps; ++i) {
            final String id = ids[i];
            final AbstractStep step = service.steps[i];
            final Map<String, Field> fields = ObjectManager.getAllFields(step.getClass(), false);
            for (int j = 1; j < stepAttributes.length; ++j) {
                final String[] row = stepAttributes[j];
                if (row != null) {
                    if (row[0].equals(id)) {
                        final Field field = fields.get(row[1]);
                        if (field != null) {
                            ObjectManager.setFieldValue(step, field, row[2]);
                        }
                        stepAttributes[j] = null;
                    }
                }
            }
        }
        ResourceManager.saveResource("service." + qualifiedName, service);
        return 1;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
