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

public abstract class AbstractStep
{
    static final String STOP = "stop";
    static final String PREVIOUS = "previous";
    static final String NEXT = "next";
    static final String LAST = "last";
    static final String BREAK = "break";
    static final String CONTINUE = "continue";
    String label;
    protected StepType stepType;
    protected String stepSubType;
    String description;
    String techNotes;
    String[] skipOnMessageIds;
    String[] executeOnMessageIds;
    Expression executeOnCondition;
    public Object lastStepOfTheBlock;
    boolean markedAsComment;
    
    AbstractStep() {
        this.label = null;
        this.stepType = StepType.DUMMYSTEP;
        this.stepSubType = null;
        this.description = null;
        this.techNotes = null;
        this.skipOnMessageIds = new String[0];
        this.executeOnMessageIds = new String[0];
        this.executeOnCondition = null;
        this.lastStepOfTheBlock = null;
        this.markedAsComment = false;
        this.stepType = StepType.DUMMYSTEP;
    }
    
    String execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.executeOnCondition != null && !this.executeOnCondition.evaluate(dc).getBooleanValue()) {
            return "next";
        }
        String[] skipOnMessageIds;
        for (int length = (skipOnMessageIds = this.skipOnMessageIds).length, i = 0; i < length; ++i) {
            final String message = skipOnMessageIds[i];
            if (dc.hasMessage(message)) {
                return "next";
            }
        }
        if (this.executeOnMessageIds.length > 0) {
            boolean messageFound = false;
            String[] executeOnMessageIds;
            for (int length2 = (executeOnMessageIds = this.executeOnMessageIds).length, j = 0; j < length2; ++j) {
                final String message2 = executeOnMessageIds[j];
                if (dc.hasMessage(message2)) {
                    messageFound = true;
                    break;
                }
            }
            if (!messageFound) {
                return "next";
            }
        }
        return this.executeStep(dc, handle);
    }
    
    abstract String executeStep(final DataCollection p0, final DbHandle p1) throws ExilityException;
    
    public DataAccessType getDataAccessType(final DataCollection dc) {
        return DataAccessType.NONE;
    }
}
