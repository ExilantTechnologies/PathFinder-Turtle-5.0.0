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

import com.exilant.exility.core.ValueList;
import java.util.HashSet;
import com.exilant.exility.core.Value;
import com.exilant.exility.core.TableInterface;
import com.exilant.exility.core.Tables;
import java.util.ArrayList;
import com.exilant.exility.core.AP;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.exilant.exility.core.Spit;
import com.exilant.exility.core.ExilityException;
import com.exilant.exility.core.DbHandle;
import com.exilant.exility.core.DataCollection;
import com.exilant.exility.core.DataAccessType;
import com.exilant.exility.core.ToBeInitializedInterface;
import com.exilant.exility.core.ServiceInterface;

public class Workflow implements ServiceInterface, ToBeInitializedInterface
{
    String name;
    String description;
    WorkflowState[] states;
    WorkflowActor[] actors;
    WorkflowStep[] steps;
    String tableName;
    String docIdName;
    String customWorkflowClassName;
    DataAccessType dataAccessType;
    boolean letCustomClassHandleEveryThing;
    private Class<CustomLogic> customClass;
    
    public Workflow() {
        this.dataAccessType = DataAccessType.READWRITE;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        CustomLogic customLogic = null;
        if (this.customClass != null) {
            try {
                customLogic = this.customClass.newInstance();
                customLogic.initialize(dc, handle);
            }
            catch (Exception e) {
                throw new ExilityException("Workflow " + this.name + " failed to get an instance of " + this.customWorkflowClassName + " Error :" + e.getMessage());
            }
        }
        final String wfAction = dc.getTextValue("workflowAction", null);
        if (customLogic != null && this.letCustomClassHandleEveryThing) {
            if (wfAction != null && wfAction.length() > 0) {
                customLogic.moveIt();
            }
            else {
                customLogic.getValidActions();
            }
        }
        else {
            new WorkflowLogic(dc, handle, customLogic).execute();
        }
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return this.dataAccessType;
    }
    
    @Override
    public void initialize() {
        if (this.customWorkflowClassName != null) {
            try {
                this.customClass = (Class<CustomLogic>)Class.forName(this.customWorkflowClassName);
            }
            catch (ClassNotFoundException e) {
                Spit.out(String.valueOf(this.customWorkflowClassName) + " is not a valid class name. This class is designated as custom logic class for work flow " + this.name);
            }
        }
    }
    
    private class WorkflowLogic
    {
        private final DataCollection dc;
        private final DbHandle handle;
        private final CustomLogic customLogic;
        private final long workflowId;
        private final String action;
        private final long userId;
        private long docId;
        private String currentState;
        private int nbrKnownActors;
        private String[] knownActors;
        private long[] knownAssignedRoles;
        private WorkflowStep[] possibleSteps;
        private Set<String> loggedInRoles;
        private Map<String, String> actorNames;
        
        WorkflowLogic(final DataCollection dc, final DbHandle dbHandle, final CustomLogic logic) {
            this.nbrKnownActors = 0;
            this.actorNames = new HashMap<String, String>();
            this.dc = dc;
            this.handle = dbHandle;
            this.customLogic = logic;
            this.workflowId = this.dc.getIntegralValue("workflowId", 0L);
            this.docId = this.dc.getIntegralValue(Workflow.this.docIdName, 0L);
            this.userId = this.dc.getIntegralValue("userId", 0L);
            this.action = this.dc.getTextValue("workflowAction", null);
        }
        
        void execute() throws ExilityException {
            if (this.userId == 0L) {
                throw new ExilityException("WorkflowInterface request requires value for field userId");
            }
            this.readData();
            this.setKnownActors();
            this.setCurrentUserRoles();
            this.setPossibleStepsForThisUser();
            if (this.action != null && this.action.length() > 0) {
                this.moveIt();
            }
            else {
                this.getValidActions();
            }
        }
        
        boolean moveIt() throws ExilityException {
            for (int nbrStep = this.possibleSteps.length, i = 0; i < nbrStep; ++i) {
                final WorkflowStep step = this.possibleSteps[i];
                if (step.action.equals(this.action)) {
                    this.dc.addTextValue("currentState", step.newState);
                    if (step.moveItIfValid(this.loggedInRoles, this.currentState, this.dc, this.handle, this.customLogic)) {
                        this.dc.addTextValue("workflowActor", step.actor);
                        this.dc.addTextValue("fromWorkflowState", step.currentState);
                        if (this.workflowId == 0L) {
                            this.dc.addTextValue("initiatorId", this.dc.getTextValue(AP.loggedInUserFieldName, null));
                        }
                        this.saveData();
                        return true;
                    }
                }
            }
            this.dc.addError("No action could be taken on the work flow. Logged-in user may not have the preveleges to take this action on this document at this state");
            return false;
        }
        
        public void getValidActions() throws ExilityException {
            this.getNextSteps();
            final ArrayList<String[]> validActions = new ArrayList<String[]>();
            validActions.add(WorkflowNames.VALID_ACTIONS_HEADER);
            if (this.possibleSteps.length > 0) {
                WorkflowStep[] possibleSteps;
                for (int length = (possibleSteps = this.possibleSteps).length, i = 0; i < length; ++i) {
                    final WorkflowStep step = possibleSteps[i];
                    if (step.isApplicable(this.loggedInRoles, this.currentState, this.dc, this.handle, this.customLogic)) {
                        final String[] row = { step.action, step.actionLabel, step.helpText };
                        validActions.add(row);
                    }
                }
            }
            final String[][] rows = validActions.toArray(new String[0][0]);
            Spit.out(String.valueOf(rows.length) + " valid actions are found with last row as " + rows[rows.length - 1]);
            this.dc.addGrid("validActions", rows);
        }
        
        protected boolean readData() throws ExilityException {
            TableInterface table = null;
            if (this.workflowId != 0L) {
                table = Tables.getTable("exilActiveWorkflows", this.dc);
                if (table.read(this.dc, this.handle, null, null) == 0) {
                    this.dc.addError("Unable to read details for id " + this.workflowId + " for workflow " + Workflow.this.name);
                    return false;
                }
                if (this.docId == 0L) {
                    this.docId = this.dc.getIntegralValue(Workflow.this.docIdName, 0L);
                }
            }
            if (this.docId != 0L) {
                table = Tables.getTable(Workflow.this.tableName, this.dc);
                if (table.read(this.dc, this.handle, null, null) == 0) {
                    this.dc.addError("Unable to read document for id " + this.docId + " from table " + Workflow.this.tableName);
                    return false;
                }
            }
            this.currentState = this.dc.getTextValue("currentState", "draft");
            return true;
        }
        
        protected void setKnownActors() throws ExilityException {
            String[] knownPairs = new String[0];
            final String knownText = this.dc.getTextValue("knownActors", null);
            if (knownText != null && knownText.length() > 0) {
                knownPairs = knownText.split(";");
            }
            this.nbrKnownActors = knownPairs.length;
            this.knownActors = new String[this.nbrKnownActors];
            this.knownAssignedRoles = new long[this.nbrKnownActors];
            final TableInterface assignedUsers = Tables.getTable("exilWorkflowAssignedUsers", this.dc);
            for (int i = 0; i < this.nbrKnownActors; ++i) {
                final String aPair = knownPairs[i];
                try {
                    final String[] actorAndRole = aPair.split("=");
                    final String[] knownActors = this.knownActors;
                    final int n = i;
                    final String s = actorAndRole[0];
                    knownActors[n] = s;
                    final String actor = s;
                    final long[] knownAssignedRoles = this.knownAssignedRoles;
                    final int n2 = i;
                    final long long1 = Long.parseLong(actorAndRole[1]);
                    knownAssignedRoles[n2] = long1;
                    final long assignedRole = long1;
                    this.dc.addValue("assignedRoleId", Value.newValue(assignedRole));
                    if (assignedUsers.readbasedOnKey(this.dc, this.handle) != 0) {
                        final String nickName = this.dc.getTextValue("assignedRoleNickName", "");
                        this.actorNames.put(actor, nickName);
                    }
                }
                catch (Exception e) {
                    this.dc.addError("Internal error: invalid encoded role and id data " + aPair);
                    this.knownActors[i] = "";
                }
            }
        }
        
        private void getNextSteps() throws ExilityException {
            final int nbrTotalSteps = Workflow.this.steps.length;
            int nbrValidSteps = 0;
            final WorkflowStep[] validSteps = new WorkflowStep[nbrTotalSteps];
            WorkflowStep[] steps;
            for (int length = (steps = Workflow.this.steps).length, j = 0; j < length; ++j) {
                final WorkflowStep step = steps[j];
                if (this.currentState.equals(step.currentState)) {
                    validSteps[nbrValidSteps] = step;
                    ++nbrValidSteps;
                }
            }
            final String[][] data = new String[nbrValidSteps + 1][];
            data[0] = WorkflowNames.NEXT_STEPS_HEADER;
            for (int i = 0; i < nbrValidSteps; ++i) {
                final WorkflowStep step2 = validSteps[i];
                data[i + 1] = step2.getFields(this.actorNames);
            }
            this.dc.addGrid("nextSteps", data);
        }
        
        private void setPossibleStepsForThisUser() {
            final ArrayList<WorkflowStep> validSteps = new ArrayList<WorkflowStep>();
            WorkflowStep[] steps;
            for (int length = (steps = Workflow.this.steps).length, i = 0; i < length; ++i) {
                final WorkflowStep step = steps[i];
                if (this.currentState.equals(step.currentState) && this.loggedInRoles.contains(step.actor)) {
                    validSteps.add(step);
                }
            }
            this.possibleSteps = validSteps.toArray(new WorkflowStep[validSteps.size()]);
        }
        
        private void setCurrentUserRoles() throws ExilityException {
            final TableInterface rolesTable = Tables.getTable("exilWorkflowAssignedRoles", this.dc);
            final int nbrRoles = rolesTable.filter(this.dc, this.handle, "roles", WorkflowNames.ROLE_CONDITIONS, null, null, null, null, false);
            if (nbrRoles == 0) {
                Spit.out("No roles found for the logged-in user " + this.userId + " for workflow " + Workflow.this.name);
                return;
            }
            final ValueList ids = this.dc.getGrid("roles").getColumn("assignedRoleId");
            final int nbrIds = ids.length();
            this.loggedInRoles = new HashSet<String>();
            for (int i = 0; i < nbrIds; ++i) {
                final long thisId = ids.getIntegralValue(i);
                for (int j = 0; j < this.knownAssignedRoles.length; ++j) {
                    if (this.knownAssignedRoles[j] == thisId) {
                        this.loggedInRoles.add(this.knownActors[j]);
                    }
                }
            }
        }
        
        private void saveData() throws ExilityException {
            TableInterface table = Tables.getTable("exilActiveWorkflows", this.dc);
            table.save(this.dc, this.handle);
            table = Tables.getTable("exilActiveWorkflowLog", this.dc);
            table.insert(this.dc, this.handle);
        }
    }
}
