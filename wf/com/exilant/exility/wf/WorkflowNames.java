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

import com.exilant.exility.core.Comparator;
import com.exilant.exility.core.Condition;

public class WorkflowNames
{
    public static final String ROLES_TABLE = "exilWorkflowRoles";
    public static final String ROLE_ID = "roleId";
    public static final String ROLE_NAME = "roleName";
    public static final String INACTIVE = "isActive";
    public static final String USERS_TABLE = "exilWorkflowUsers";
    public static final String USER_ID = "userId";
    public static final String USER_NICK_NAME = "userNickName";
    public static final String ASSIGNED_ROLES_TABLE = "exilWorkflowAssignedRoles";
    public static final String ASSIGNED_ROLE_ID = "assignedRoleId";
    public static final String REPORTING_ID = "reportingAssignedRoleId";
    public static final String WORKFLOW_TABLE = "exilActiveWorkflows";
    public static final String WORKFLOW_NAME = "workflowName";
    public static final String WORKFLOW_ID = "workflowId";
    public static final String DOCUMENT_ID = "documentId";
    public static final String CURRENT_STATE = "currentState";
    public static final String INITIATOR = "initiatorId";
    public static final String INITIATOR_ROLE_ID = "initiatorAssignedRoleId";
    public static final String KNOWN_ACTORS = "knownActors";
    public static final String KNOWN_ACTORS_LIST_DELIMITER = ";";
    public static final String KNOWN_ACTOR_ROLE_SEPARATOR = "=";
    public static final String WORKFLOW_LOG_TABLE = "exilActiveWorkflowLog";
    public static final String WORKFLOW_ACTOR = "workflowActor";
    public static final String WORKFLOW_ACTION = "workflowAction";
    public static final String FROM_STATE = "fromWorkflowState";
    public static final String TO_STATE = "toWorkflowState";
    public static final String DEFAULT_STATE = "draft";
    public static final String ACTION_REMARKS = "actionRemarks";
    public static final Condition[] ROLE_CONDITIONS;
    public static final String ROLES_GRID = "roles";
    public static final String VALID_ACTIONS_GRID_NAME = "validActions";
    public static final String[] VALID_ACTIONS_HEADER;
    public static final String STEP_IS_VALID = "_workFlowStepIsValid";
    public static final String NEXT_STEPS_GRID_NAME = "nextSteps";
    public static final String[] NEXT_STEPS_HEADER;
    static final String KNOWN_ASSIGNED_ROLES_GRID_NAME = "knownRoles";
    public static final String ASSIGNED_USERS_VIEW = "exilWorkflowAssignedUsers";
    public static final String ASSIGNED_USER_NICKNAME = "assignedRoleNickName";
    
    static {
        ROLE_CONDITIONS = new Condition[] { new Condition("userId", "userId", Comparator.EQUALTO), new Condition("isActive", "isActive", Comparator.EQUALTO) };
        VALID_ACTIONS_HEADER = new String[] { "name", "label", "description" };
        NEXT_STEPS_HEADER = new String[] { "name", "description", "currentState", "actor", "action", "actionLabel", "helpText", "userNickName" };
    }
}
