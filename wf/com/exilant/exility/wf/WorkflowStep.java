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

package com.exilant.exility.wf;

import java.util.Map;
import com.exilant.exility.core.TableInterface;
import com.exilant.exility.core.Tables;
import com.exilant.exility.core.ServiceInterface;
import com.exilant.exility.core.Services;
import com.exilant.exility.core.ExilityException;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import java.util.Set;
import com.exilant.exility.core.Expression;

public class WorkflowStep
{
    String name;
    String description;
    String currentState;
    String actor;
    String action;
    String actionLabel;
    String helpText;
    Expression validityExpression;
    String validityFunction;
    String validityServiceName;
    String newState;
    Expression newStateExpression;
    String newStateFunction;
    String actionServiceName;
    String[] tablesToBeSaved;
    String notificationsToBeSent;
    String notificationsToBeRemoved;
    
    public boolean isApplicable(final Set<String> forActors, final String forState, final DataCollection dc, final DbHandle dbHandle, final CustomLogic customLogic) throws ExilityException {
        if (this.currentState != null && !this.currentState.equals(forState)) {
            return false;
        }
        if (forActors != null && this.actor != null && !forActors.contains(this.actor)) {
            return false;
        }
        if (this.validityExpression != null) {
            return this.validityExpression.evaluate(dc).getBooleanValue();
        }
        if (this.validityFunction != null) {
            if (customLogic == null) {
                throw new ExilityException("Worlfow step  " + this.name + " has specified " + this.validityFunction + " as validity funciton, but a custom logic class is not specified for teh workflow");
            }
            customLogic.executeFunction(this.validityFunction);
        }
        else {
            if (this.validityServiceName == null) {
                return true;
            }
            final ServiceInterface service = Services.getService(this.validityServiceName, dc);
            if (service == null) {
                throw new ExilityException(String.valueOf(this.validityServiceName) + " is not a valid service.");
            }
            service.execute(dc, dbHandle);
        }
        final boolean isValid = dc.getBooleanValue("_workFlowStepIsValid", false);
        dc.removeValue("_workFlowStepIsValid");
        return isValid;
    }
    
    boolean moveItIfValid(final Set<String> forActors, final String forState, final DataCollection dc, final DbHandle handle, final CustomLogic customLogic) throws ExilityException {
        if (!this.isApplicable(forActors, forState, dc, handle, customLogic)) {
            return false;
        }
        String st = this.newState;
        if (st == null && this.newStateExpression != null) {
            st = this.newStateExpression.evaluate(dc).getTextValue();
        }
        if (st != null) {
            dc.addTextValue("toWorkflowState", st);
        }
        else if (this.newStateFunction != null) {
            customLogic.executeFunction(this.newStateFunction);
        }
        if (this.actionServiceName != null) {
            final ServiceInterface service = Services.getService(this.actionServiceName, dc);
            service.execute(dc, handle);
        }
        if (this.tablesToBeSaved != null) {
            String[] tablesToBeSaved;
            for (int length = (tablesToBeSaved = this.tablesToBeSaved).length, i = 0; i < length; ++i) {
                final String tableToBeSaved = tablesToBeSaved[i];
                final TableInterface table = Tables.getTable(tableToBeSaved, dc);
                if (table == null) {
                    throw new ExilityException(String.valueOf(tableToBeSaved) + " is not a valid table.");
                }
                table.save(dc, handle);
            }
        }
        if (this.notificationsToBeSent != null) {
            this.sendNotifications(dc, handle);
        }
        if (this.notificationsToBeRemoved != null) {
            this.removeNotifications(dc, handle);
        }
        return true;
    }
    
    private void removeNotifications(final DataCollection dc, final DbHandle handle) {
    }
    
    private void sendNotifications(final DataCollection dc, final DbHandle handle) {
    }
    
    public String[] getFields(final Map<String, String> actorNames) {
        String userNickName = null;
        if (actorNames != null) {
            userNickName = actorNames.get(this.actor);
        }
        if (userNickName == null) {
            userNickName = "";
        }
        final String[] fields = { this.name, this.description, this.currentState, this.actor, this.action, this.actionLabel, this.helpText, userNickName };
        return fields;
    }
}
