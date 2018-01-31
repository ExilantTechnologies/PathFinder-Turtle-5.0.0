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
import java.util.HashMap;

public class ActionTask extends ExilityTask
{
    ActionStep[] steps;
    
    public ActionTask() {
        this.steps = new ActionStep[0];
    }
    
    @Override
    public int executeBulkTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        throw new ExilityException("Bulk Use task is not yet implemented");
    }
    
    @Override
    public int executeTask(final DataCollection dc, final DbHandle handle) throws ExilityException {
        final Map<String, String> actions = new HashMap<String, String>();
        ActionStep[] steps;
        for (int length = (steps = this.steps).length, i = 0; i < length; ++i) {
            final ActionStep step = steps[i];
            if (step.condition != null) {
                final BooleanValue val = (BooleanValue)step.condition.evaluate(dc);
                if (!val.getBooleanValue()) {
                    continue;
                }
            }
            if (step.task == null || step.task.execute(dc, handle) > 0) {
                String[] actionsToEnable;
                for (int length2 = (actionsToEnable = step.actionsToEnable).length, j = 0; j < length2; ++j) {
                    final String action = actionsToEnable[j];
                    actions.put(action, action);
                }
            }
        }
        final String[] listOfNames = actions.keySet().toArray(new String[0]);
        final ValueList list = ValueList.newList(listOfNames);
        final Grid grid = new Grid(this.gridName);
        grid.addColumn("actions", list);
        dc.addGrid(this.gridName, grid);
        return listOfNames.length;
    }
    
    @Override
    public DataAccessType getDataAccessType() {
        return DataAccessType.READONLY;
    }
}
