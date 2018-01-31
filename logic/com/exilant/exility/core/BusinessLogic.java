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

import java.util.HashSet;
import java.util.Set;

@Deprecated
class BusinessLogic implements ToBeInitializedInterface
{
    String name;
    String module;
    String description;
    String techNotes;
    Parameter[] outputParameters;
    Parameter[] inputParameters;
    AbstractLogicStatement[] statements;
    private Set<String> objectivesSet;
    private String[] inputFields;
    private String[] outputFields;
    
    BusinessLogic() {
        this.name = null;
        this.module = null;
        this.description = null;
        this.techNotes = null;
        this.outputParameters = new Parameter[0];
        this.inputParameters = new Parameter[0];
        this.statements = new AbstractLogicStatement[0];
        this.objectivesSet = null;
        this.inputFields = null;
        this.outputFields = null;
    }
    
    public int execute(final DataCollection dc, final String gridName, final String[] parameters) throws ExilityException {
        Variables variables = new Variables();
        final String[] inputNames = new String[this.inputFields.length];
        final String[] outputNames = new String[this.outputFields.length];
        if (parameters == null || parameters.length == 0) {
            for (int i = 0; i < this.inputFields.length; ++i) {
                inputNames[i] = this.inputFields[i];
            }
            for (int i = 0; i < this.outputFields.length; ++i) {
                outputNames[i] = this.outputFields[i];
            }
        }
        else {
            if (parameters.length != this.outputFields.length + this.inputFields.length) {
                dc.raiseException("exilDesignError", "Business logic module" + this.name + " invoked with invalid number of parameters. It should contain a total of " + (this.inputFields.length + this.outputFields.length) + " comma separated elements");
            }
            for (int i = 0; i < this.inputFields.length; ++i) {
                final char c = parameters[i].toCharArray()[0];
                if (Chars.isTextQuote(c) || Chars.isNumeric(c) || '-' == c || Chars.isDateQuote(c)) {
                    inputNames[i] = parameters[i];
                }
                else {
                    inputNames[i] = parameters[i];
                }
            }
            final int totalInputs = inputNames.length;
            for (int j = 0; j < this.outputFields.length; ++j) {
                outputNames[j] = parameters[j + totalInputs];
            }
        }
        if (gridName == null || gridName.length() == 0) {
            variables.extractVariablesFromDc(this.inputFields, inputNames, dc);
            this.executeOnce(dc, variables);
            this.copyResultToDc(variables, dc, outputNames);
            return 1;
        }
        if (!dc.hasGrid(gridName)) {
            return 0;
        }
        final Grid grid = dc.getGrid(gridName);
        final int n = grid.getNumberOfRows();
        if (n == 0) {
            return 0;
        }
        for (int k = 0; k < outputNames.length; ++k) {
            final String columnName = outputNames[k];
            if (!grid.hasColumn(columnName)) {
                grid.addColumn(columnName, ValueList.newList(this.outputParameters[k].getValueType(), n));
            }
        }
        for (int k = 0; k < n; ++k) {
            variables = new Variables();
            variables.extractVariablesFromGrid(dc, this.inputFields, inputNames, grid, k);
            this.executeOnce(dc, variables);
            this.copyResultToGrid(variables, grid, outputNames, k);
        }
        return 1;
    }
    
    private void copyResultToGrid(final Variables variables, final Grid grid, final String[] outputNames, final int idx) {
        for (int i = 0; i < this.outputFields.length; ++i) {
            final Value value = variables.getValue(this.outputFields[i]);
            if (value != null) {
                grid.setValue(outputNames[i], value, idx);
            }
        }
    }
    
    private void copyResultToDc(final Variables variables, final DataCollection dc, final String[] namesInDc) {
        for (int i = 0; i < this.outputFields.length; ++i) {
            final Value value = variables.getValue(this.outputFields[i]);
            if (value != null) {
                dc.addValue(namesInDc[i], value);
            }
        }
    }
    
    int executeOnce(final DataCollection dc, final Variables variants) throws ExilityException {
        int objectivesMetSoFar = 0;
        final HashSet<String> objectivesMet = new HashSet<String>();
        AbstractLogicStatement[] statements;
        for (int length = (statements = this.statements).length, i = 0; i < length; ++i) {
            final AbstractLogicStatement statement = statements[i];
            final String objective = statement.objective;
            if (this.objectivesSet.contains(objective)) {
                if (objectivesMet.contains(objective)) {
                    continue;
                }
            }
            else if (variants.exists(objective)) {
                continue;
            }
            if (statement.isApplicable(variants)) {
                final Value result = statement.execute(dc, variants);
                if (result != null) {
                    variants.setValue(objective, result);
                }
                if (dc.hasError()) {
                    break;
                }
                if (this.objectivesSet.contains(objective)) {
                    ++objectivesMetSoFar;
                    objectivesMet.add(objective);
                    if (objectivesMetSoFar == this.outputFields.length) {
                        return objectivesMetSoFar;
                    }
                }
            }
        }
        return objectivesMetSoFar;
    }
    
    @Override
    public void initialize() {
        int n = this.outputParameters.length;
        this.outputFields = new String[n];
        this.objectivesSet = new HashSet<String>();
        for (int i = 0; i < this.outputParameters.length; ++i) {
            final String fieldName = this.outputParameters[i].name;
            this.objectivesSet.add(fieldName);
            this.outputFields[i] = fieldName;
        }
        n = this.inputParameters.length;
        this.inputFields = new String[n];
        for (int i = 0; i < this.inputParameters.length; ++i) {
            final String fieldName = this.inputParameters[i].name;
            this.inputFields[i] = fieldName;
        }
    }
    
    public static void main(final String[] args) throws ExilityException {
        final Parameter p1 = new Parameter();
        p1.name = "a1";
        p1.dataElementName = "sample_int";
        final Parameter p2 = new Parameter();
        p2.name = "b1";
        p2.dataElementName = "sample_int";
        final Parameter p3 = new Parameter();
        p3.name = "c1";
        p3.dataElementName = "sample_int";
        final Parameter p4 = new Parameter();
        p4.name = "d1";
        p4.dataElementName = "sample_int";
        final Parameter p5 = new Parameter();
        p5.name = "e1";
        p5.dataElementName = "sample_int";
        final Parameter[] pmin = { p1, p2, p3 };
        final Parameter[] pmout = { p4, p5 };
        final ExpressionStatement st1 = new ExpressionStatement();
        st1.objective = "d1";
        final Expression x1 = new Expression();
        x1.setExpression("a1+b1");
        st1.expression = x1;
        final ExpressionStatement st2 = new ExpressionStatement();
        st2.objective = "e1";
        final Expression x2 = new Expression();
        x2.setExpression("a1+b1+c1+d1");
        st2.expression = x2;
        final AbstractLogicStatement[] sts = { st1, st2 };
        final BusinessLogic bl = new BusinessLogic();
        bl.name = "test";
        bl.inputParameters = pmin;
        bl.outputParameters = pmout;
        bl.statements = sts;
        bl.initialize();
        final DataCollection dc = new DataCollection();
        dc.addIntegralValue("a", 12L);
        dc.addIntegralValue("b", 2L);
        dc.addIntegralValue("c", 3L);
        final String[] passedParams = { "a", "b", "c", "d", "e" };
        bl.execute(dc, null, passedParams);
        Spit.out("value of d = " + dc.getIntegralValue("d", 0L));
        Spit.out("value of e = " + dc.getIntegralValue("e", 0L));
        final long[] ai = { 12L, 13L, 14L, 15L };
        final long[] bi = { 2L, 3L, 45L, 5L };
        final long[] ci = { 3L, 4L, 5L, 6L };
        final int len = ai.length;
        final Grid grid = new Grid("test");
        grid.addColumn("a", ValueList.newList(ai));
        grid.addColumn("b", ValueList.newList(bi));
        grid.addColumn("c", ValueList.newList(ci));
        grid.addColumn("d", ValueList.newList(DataValueType.INTEGRAL, len));
        dc.addGrid("myGrid", grid);
        bl.execute(dc, "myGrid", passedParams);
        final String[][] d = grid.getRawData();
        String str = "";
        String[][] array;
        for (int length = (array = d).length, i = 0; i < length; ++i) {
            final String[] row = array[i];
            String[] array2;
            for (int length2 = (array2 = row).length, j = 0; j < length2; ++j) {
                final String s = array2[j];
                str = String.valueOf(str) + "\t" + s;
            }
            str = String.valueOf(str) + '\n';
        }
        Spit.out(str);
    }
}
