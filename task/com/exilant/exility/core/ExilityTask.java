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

public abstract class ExilityTask
{
    public String taskName;
    public String[] taskParameters;
    public boolean raiseExceptionIfNoWorkDone;
    public boolean raiseExceptionIfWorkDone;
    public String ifNoWorkDoneMessageName;
    public String[] ifNoWorkDoneMessageParameters;
    public String ifWorkDoneMessageName;
    public String[] ifWorkDoneMessageParameters;
    public String gridName;
    public boolean repeatForRowsInGrid;
    
    public ExilityTask() {
        this.taskName = null;
        this.taskParameters = new String[0];
        this.raiseExceptionIfNoWorkDone = false;
        this.raiseExceptionIfWorkDone = false;
        this.ifNoWorkDoneMessageName = null;
        this.ifNoWorkDoneMessageParameters = new String[0];
        this.ifWorkDoneMessageName = null;
        this.ifWorkDoneMessageParameters = new String[0];
        this.gridName = null;
        this.repeatForRowsInGrid = false;
    }
    
    public int execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        int workDone = 0;
        final Date startDate = new Date();
        if (this.repeatForRowsInGrid) {
            if (dc.hasGrid(this.gridName)) {
                workDone = this.executeBulkTask(dc, handle);
            }
            else {
                workDone = 0;
            }
        }
        else {
            workDone = this.executeTask(dc, handle);
        }
        final Date endDate = new Date();
        final long diffTime = endDate.getTime() - startDate.getTime();
        Spit.out("Time taken by Task : " + this.taskName + "  is :" + diffTime + " milliseconds");
        if (workDone == 0) {
            if (this.ifNoWorkDoneMessageName != null) {
                if (this.raiseExceptionIfNoWorkDone) {
                    dc.raiseException(this.ifNoWorkDoneMessageName, this.getErrorParameterValues(dc, this.ifNoWorkDoneMessageParameters));
                }
                else {
                    dc.addMessage(this.ifNoWorkDoneMessageName, this.getErrorParameterValues(dc, this.ifNoWorkDoneMessageParameters));
                }
            }
        }
        else if (this.ifWorkDoneMessageName != null) {
            if (this.raiseExceptionIfWorkDone) {
                dc.raiseException(this.ifWorkDoneMessageName, this.getErrorParameterValues(dc, this.ifWorkDoneMessageParameters));
            }
            else {
                dc.addMessage(this.ifWorkDoneMessageName, this.getErrorParameterValues(dc, this.ifWorkDoneMessageParameters));
            }
        }
        return workDone;
    }
    
    private String[] getErrorParameterValues(final DataCollection dc, final String[] names) {
        if (names == null || names.length == 0) {
            return new String[0];
        }
        final String[] parms = new String[names.length];
        for (int i = 0; i < names.length; ++i) {
            final String parm = names[i];
            if (dc.hasValue(parm)) {
                parms[i] = dc.getTextValue(parm, "");
            }
            else {
                parms[i] = parm;
            }
        }
        return parms;
    }
    
    public abstract int executeTask(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    public abstract int executeBulkTask(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    public DataAccessType getDataAccessType() {
        return DataAccessType.NONE;
    }
}
