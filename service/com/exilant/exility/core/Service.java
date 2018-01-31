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

import java.util.HashMap;

public class Service implements ServiceInterface, ToBeInitializedInterface
{
    private static final int NOT_IN_A_LOOP = -1;
    String name;
    String module;
    String description;
    String techNotes;
    DataAccessType dataAccessType;
    AbstractStep[] steps;
    private final HashMap<String, Integer> stepIndexes;
    
    public Service() {
        this.name = null;
        this.module = null;
        this.description = null;
        this.techNotes = null;
        this.dataAccessType = null;
        this.steps = new AbstractStep[0];
        this.stepIndexes = new HashMap<String, Integer>();
    }
    
    @Override
    public void execute(final DataCollection dc, final DbHandle handle) throws ExilityException {
        if (this.steps.length == 0) {
            dc.raiseException("exilNoSteps", this.name);
        }
        Spit.out("Service: " + this.name + " Steps:");
        int loopBeginsAt = -1;
        int loopEndsAt = -1;
        LoopStepWorker worker = null;
        int stepIndex = 0;
        while (stepIndex < this.steps.length && stepIndex >= 0) {
            if (worker != null && (stepIndex < loopBeginsAt || stepIndex > loopEndsAt)) {
                if (worker.toContinue(dc)) {
                    worker.endLoop(dc);
                }
                worker = null;
            }
            final AbstractStep aStep = this.steps[stepIndex];
            if (aStep.markedAsComment) {
                ++stepIndex;
            }
            else {
                Spit.out("Task step no:" + stepIndex + "    desc:" + aStep.description);
                if (aStep.lastStepOfTheBlock != null) {
                    if (worker != null && loopBeginsAt != stepIndex) {
                        dc.raiseException("exilError", "Service " + this.name + " has loop inside a loop. This is not implemented.");
                    }
                    if (worker == null) {
                        worker = new LoopStepWorker((LoopStep)aStep, dc);
                        loopBeginsAt = stepIndex;
                        if (this.stepIndexes.containsKey(aStep.lastStepOfTheBlock)) {
                            loopEndsAt = this.stepIndexes.get(aStep.lastStepOfTheBlock);
                        }
                        else {
                            dc.raiseException("exilError", "Service " + this.name + " uses " + aStep.lastStepOfTheBlock + " as a label, but no step is defined with that label ");
                        }
                    }
                    if (worker.toContinue(dc)) {
                        ++stepIndex;
                    }
                    else {
                        worker = null;
                        stepIndex = loopEndsAt + 1;
                    }
                }
                else {
                    final String result = aStep.execute(dc, handle);
                    if (result.equals("next")) {
                        if (worker != null && loopEndsAt == stepIndex) {
                            stepIndex = loopBeginsAt;
                        }
                        else {
                            ++stepIndex;
                        }
                    }
                    else if (result.equals("previous")) {
                        --stepIndex;
                    }
                    else if (result.equals("last")) {
                        stepIndex = this.steps.length - 1;
                    }
                    else {
                        if (result.equals("stop")) {
                            break;
                        }
                        if (this.stepIndexes.containsKey(result)) {
                            stepIndex = this.stepIndexes.get(result);
                        }
                        else {
                            Spit.out("Step:" + aStep.label + " returned " + result + " but this is not an id of any step in this service. this is ignored, and execution continued with next step ");
                            dc.addMessage("exilNoSuchStep", this.name, result);
                            ++stepIndex;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void initialize() {
        for (int i = 0; i < this.steps.length; ++i) {
            final AbstractStep step = this.steps[i];
            if (step == null) {
                Spit.out("Design error: " + ((i == 0) ? " first step " : (String.valueOf(i) + " th")) + " step is null " + this.steps[i - 1].getClass().getSimpleName());
            }
            else {
                final String label = step.label;
                if (label != null) {
                    if (this.stepIndexes.containsKey(label)) {
                        Spit.out("Design error: " + this.name + " has more than one steps with label = " + label);
                    }
                    else {
                        this.stepIndexes.put(label, new Integer(i));
                    }
                }
            }
        }
    }
    
    @Override
    public DataAccessType getDataAccessType(final DataCollection dc) {
        if (this.dataAccessType != null) {
            return this.dataAccessType;
        }
        boolean readRequired = false;
        AbstractStep[] steps;
        for (int length = (steps = this.steps).length, i = 0; i < length; ++i) {
            final AbstractStep step = steps[i];
            final DataAccessType thisType = step.getDataAccessType(dc);
            if (thisType == DataAccessType.READWRITE) {
                return DataAccessType.READWRITE;
            }
            if (thisType == DataAccessType.READONLY) {
                readRequired = true;
            }
        }
        if (readRequired) {
            return DataAccessType.READONLY;
        }
        return DataAccessType.NONE;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public AbstractStep[] getSteps() {
        return this.steps;
    }
}
