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

import java.util.regex.Matcher;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.ArrayList;

public class Expression
{
    String originalExpression;
    private Component[] components;
    private ExecutionStep[] steps;
    private int stepIdx;
    private String[] variableNames;
    private Value calcuatedValue;
    
    public Expression() {
        this.originalExpression = null;
        this.calcuatedValue = null;
    }
    
    public void setExpression(final String expr) throws ExilityException {
        if (expr == null || expr.length() == 0) {
            this.originalExpression = "";
            return;
        }
        this.originalExpression = expr;
        final int totalChars = expr.length();
        final ArrayList<Component> componentList = new ArrayList<Component>();
        int netBrackets = 0;
        boolean operandExpected = true;
        boolean unaryOperatorParsed = false;
        Component component = new Component();
        for (int i = 0; i < totalChars; ++i) {
            final char c = expr.charAt(i);
            if (!Chars.isWhiteSpace(c)) {
                if (operandExpected) {
                    if (Chars.isOpenBracket(c)) {
                        if (unaryOperatorParsed) {
                            throw new ExilityParseException(i, this.originalExpression, "This character is not approapriate here");
                        }
                        ++netBrackets;
                        final Component component2 = component;
                        ++component2.brackets;
                    }
                    else if (Chars.isUnaryOperator(c)) {
                        if (unaryOperatorParsed) {
                            throw new ExilityParseException(i, this.originalExpression, "A variable or a constant is expected");
                        }
                        if ('!' == c) {
                            final int iplus1 = i + 1;
                            if (iplus1 < totalChars && expr.charAt(iplus1) == '?') {
                                ++i;
                                component.unaryOperator = Operator.NOT_EXISTS;
                            }
                            else {
                                component.unaryOperator = Operator.NOT;
                            }
                        }
                        else if ('-' == c) {
                            component.unaryOperator = Operator.SUBTRACT;
                        }
                        else {
                            component.unaryOperator = Operator.EXISTS;
                        }
                        unaryOperatorParsed = true;
                    }
                    else {
                        if (Chars.isNumeric(c)) {
                            i = this.parseNumber(expr, i, component);
                        }
                        else if (Chars.isAlpha(c)) {
                            i = this.parseName(expr, i, component);
                        }
                        else if (Chars.isTextQuote(c)) {
                            i = this.parseString(expr, i, component);
                        }
                        else {
                            if ('\'' != c) {
                                throw new ExilityParseException(i, this.originalExpression, "A variable or a constant is expected");
                            }
                            i = this.parseDate(expr, i, component);
                        }
                        if (unaryOperatorParsed && component.isConstant()) {
                            final Value oldValue = component.getValue();
                            final Value newValue = component.unaryOperator.operate(oldValue);
                            component.setValue(newValue);
                            component.unaryOperator = null;
                        }
                        unaryOperatorParsed = false;
                        operandExpected = false;
                    }
                }
                else if (Chars.isCloseBracket(c)) {
                    if (netBrackets <= 0) {
                        throw new ExilityParseException(i, this.originalExpression, "This close bracket does not have a corresponding open bracket");
                    }
                    final Component component3 = component;
                    --component3.brackets;
                    --netBrackets;
                }
                else {
                    final Operator operator = Operator.parse(expr, i);
                    if (operator == null) {
                        throw new ExilityParseException(i, this.originalExpression, "expecting an operator : +,-,*,/,%, ^ , <, > = or ! expetced ");
                    }
                    final int operLength = operator.toString().length();
                    if (operLength > 1) {
                        i += operLength - 1;
                    }
                    componentList.add(component);
                    component = new Component();
                    component.operator = operator;
                    operandExpected = true;
                    unaryOperatorParsed = false;
                }
            }
        }
        if (operandExpected) {
            throw new ExilityParseException(totalChars, this.originalExpression, "A variable or a constant is expected");
        }
        if (netBrackets != 0) {
            throw new ExilityParseException(totalChars, this.originalExpression, "One or more open brackets have no matching close brackets");
        }
        componentList.add(component);
        final int nbrCompnents = componentList.size();
        if (nbrCompnents == 0) {
            this.calcuatedValue = new NullValue(DataValueType.TEXT);
            return;
        }
        this.saveComponents(componentList);
        this.compileSteps();
        if (this.variableNames == null || this.variableNames.length == 0) {
            this.calcuatedValue = this.evaluate(new DataCollection());
        }
    }
    
    private void saveComponents(final ArrayList<Component> componentList) {
        final int n = componentList.size();
        this.components = new Component[n];
        int nbrNames = 0;
        final String[] names = new String[n];
        for (int i = 0; i < this.components.length; ++i) {
            final Component c = componentList.get(i);
            this.components[i] = c;
            if (!c.isConstant()) {
                names[nbrNames++] = c.getName();
            }
        }
        if (nbrNames > 0) {
            this.variableNames = new String[nbrNames];
            for (int j = 0; j < this.variableNames.length; ++j) {
                this.variableNames[j] = names[j];
            }
        }
    }
    
    private int parseString(final String expr, final int quoteAt, final Component componentToParsedTo) throws ExilityParseException {
        int istart = quoteAt + 1;
        String val = "";
        int lastCharAt;
        int ret;
        for (lastCharAt = expr.length() - 1, ret = -1; istart <= lastCharAt; istart = ret + 2) {
            ret = Chars.getCharIndex(Chars.QUOTES, istart, expr);
            if (ret < 0) {
                throw new ExilityParseException(quoteAt, this.originalExpression, "No matching quotaiton mark");
            }
            if (ret == lastCharAt) {
                break;
            }
            if (!Chars.isTextQuote(expr.charAt(ret + 1))) {
                break;
            }
            val = String.valueOf(val) + expr.substring(istart, ret + 1);
        }
        val = String.valueOf(val) + expr.substring(istart, ret);
        componentToParsedTo.setValue(Value.newValue(val));
        return ret;
    }
    
    private int parseDate(final String expr, final int quoteAt, final Component componentToParsedTo) throws ExilityParseException {
        final int startAt = quoteAt + 1;
        final int ret = expr.indexOf(39, startAt);
        if (ret < 0) {
            throw new ExilityParseException(quoteAt, this.originalExpression, "No matching quotaiton mark");
        }
        final String val = expr.substring(startAt, ret);
        final Date d = DateUtility.parseDate(val);
        if (d == null) {
            throw new ExilityParseException(quoteAt, this.originalExpression, "Date fields are of the form 'YYYY-MM-DD'. Note that the quotes are required");
        }
        componentToParsedTo.setValue(Value.newValue(d));
        return ret;
    }
    
    private int parseNumber(final String expr, int startAt, final Component componentToParsedTo) {
        boolean dotYetToBeParsed = expr.charAt(startAt) != '.';
        while (++startAt < expr.length()) {
            final char c = expr.charAt(startAt);
            if (Chars.isDigit(c)) {
                continue;
            }
            if (!dotYetToBeParsed || '.' != c) {
                break;
            }
            dotYetToBeParsed = false;
        }
        final String val = expr.substring(startAt, startAt);
        if (dotYetToBeParsed) {
            final long l = Long.parseLong(val);
            componentToParsedTo.setValue(Value.newValue(l));
        }
        else {
            final double d = Double.parseDouble(val);
            componentToParsedTo.setValue(Value.newValue(d));
        }
        return startAt - 1;
    }
    
    private int parseName(final String expr, int startAt, final Component componentToParsedTo) {
        while (++startAt < expr.length()) {
            final char c = expr.charAt(startAt);
            if (!Chars.isAlphaNumeric(c)) {
                break;
            }
        }
        String val = expr.substring(startAt, startAt);
        if (val.equals("true")) {
            componentToParsedTo.setValue(Value.newValue(true));
        }
        else if (val.equals("false")) {
            componentToParsedTo.setValue(Value.newValue(false));
        }
        else {
            componentToParsedTo.setName(val);
            val = null;
            componentToParsedTo.setValue(Value.newValue(val));
        }
        return startAt - 1;
    }
    
    private void compileSteps() {
        final int n = this.components.length;
        final int[] brackets = new int[n];
        final Operator[] operatorToAdd = new Operator[n];
        for (int i = 0; i < this.components.length; ++i) {
            final Component comp = this.components[i];
            operatorToAdd[i] = comp.operator;
            brackets[i] = comp.brackets;
        }
        final int[] array = brackets;
        final int n2 = 0;
        ++array[n2];
        final int[] array2 = brackets;
        final int n3 = n - 1;
        --array2[n3];
        int openBracketAt = 0;
        this.stepIdx = 0;
        final int totalSteps = n - 1;
        this.steps = new ExecutionStep[totalSteps];
        while (this.stepIdx < totalSteps) {
            for (int j = 0; j < n; ++j) {
                if (brackets[j] > 0) {
                    openBracketAt = j;
                }
                else if (brackets[j] < 0) {
                    this.addSteps(operatorToAdd, openBracketAt, j);
                    final int[] array3 = brackets;
                    final int n4 = openBracketAt;
                    --array3[n4];
                    final int[] array4 = brackets;
                    final int n5 = j;
                    ++array4[n5];
                    break;
                }
            }
        }
    }
    
    private void addSteps(final Operator[] operatorToAdd, final int firstOne, final int lastOne) {
        int stepsToAdd = 0;
        for (int i = firstOne; i <= lastOne; ++i) {
            if (operatorToAdd[i] != null) {
                ++stepsToAdd;
            }
        }
        if (stepsToAdd == 0) {
            return;
        }
        for (int stepsAdded = 0, precedence = 1; stepsAdded < stepsToAdd && precedence <= 5; ++precedence) {
            int operand1Idx = firstOne;
            for (int j = firstOne + 1; j <= lastOne; ++j) {
                final Operator op = operatorToAdd[j];
                if (op != null) {
                    if (op.getPrecedence() == precedence) {
                        final ExecutionStep step = new ExecutionStep();
                        step.operator = op;
                        step.operand1Index = operand1Idx;
                        step.operand2Index = j;
                        this.steps[this.stepIdx] = step;
                        ++this.stepIdx;
                        ++stepsAdded;
                        operatorToAdd[j] = null;
                    }
                    else {
                        operand1Idx = j;
                    }
                }
            }
        }
    }
    
    String[] getVarableNames() {
        return this.variableNames;
    }
    
    public String renderExpression() {
        final StringBuilder sbf = new StringBuilder();
        Component[] components;
        for (int length = (components = this.components).length, i = 0; i < length; ++i) {
            final Component c = components[i];
            sbf.append(c);
        }
        return sbf.toString();
    }
    
    public String renderSteps() {
        final StringBuilder sbf = new StringBuilder();
        ExecutionStep[] steps;
        for (int length = (steps = this.steps).length, i = 0; i < length; ++i) {
            final ExecutionStep step = steps[i];
            sbf.append("\n Value[").append(step.operand1Index).append("] ").append(step.operator.toString()).append(" value[").append(step.operand2Index).append("]");
        }
        return sbf.toString();
    }
    
    private Value[] getValues(final DataCollection dc, final Variables variables) throws ExilityException {
        final Value[] vals = new Value[this.components.length];
        for (int i = 0; i < vals.length; ++i) {
            final Component c = this.components[i];
            final Operator unary = c.unaryOperator;
            Value value = null;
            final String name = c.getName();
            if (c.isConstant()) {
                value = c.getValue();
            }
            else if (variables != null) {
                value = variables.getValue(name);
            }
            else {
                value = dc.getValue(name);
            }
            if (Operator.EXISTS.equals(unary)) {
                if (value == null || value.isNull()) {
                    value = BooleanValue.FALSE_VALUE;
                }
                else {
                    value = BooleanValue.TRUE_VALUE;
                }
            }
            else if (Operator.NOT_EXISTS.equals(unary)) {
                if (value == null || value.isNull()) {
                    value = BooleanValue.TRUE_VALUE;
                }
                else {
                    value = BooleanValue.FALSE_VALUE;
                }
            }
            else if (value == null) {
                value = new NullValue(DataValueType.NULL);
                Spit.out(String.valueOf(name) + " is not found and hence is set to null during expression evaluation");
            }
            else if (Operator.NOT.equals(unary)) {
                value = Operator.NOT.operate(value);
            }
            else if (Operator.SUBTRACT.equals(unary)) {
                value = Operator.SUBTRACT.operate(value);
            }
            vals[i] = value;
        }
        return vals;
    }
    
    public Value evaluate(final DataCollection dc) throws ExilityException {
        if (this.calcuatedValue != null) {
            return this.calcuatedValue;
        }
        final Value[] values = this.getValues(dc, null);
        return this.evaluate(values);
    }
    
    public Value evaluate(final Variables variables) throws ExilityException {
        if (this.calcuatedValue != null) {
            return this.calcuatedValue;
        }
        final Value[] values = this.getValues(null, variables);
        return this.evaluate(values);
    }
    
    private Value evaluate(final Value[] values) throws ExilityException {
        ExecutionStep[] steps;
        for (int length = (steps = this.steps).length, i = 0; i < length; ++i) {
            final ExecutionStep step = steps[i];
            final int leftIndex = step.operand1Index;
            final int rightIndex = step.operand2Index;
            values[leftIndex] = step.operator.operate(values[leftIndex], values[rightIndex]);
        }
        return values[0];
    }
    
    @Override
    public String toString() {
        return this.originalExpression;
    }
    
    public void evaluateColumn(final Grid grid, final DataCollection dc, final String columnName) {
        final ValueList result = grid.getColumn(columnName);
        if (result == null) {
            final String str = "No column with name " + columnName + " in grid for colculation.";
            Spit.out(str);
            dc.addMessage("exilityError", str);
            return;
        }
        final int nbrRows = grid.getNumberOfRows();
        int nbrVariables = 0;
        if (this.variableNames != null) {
            nbrVariables = this.variableNames.length;
        }
        if (nbrVariables == 0) {
            for (int i = 0; i < nbrRows; ++i) {
                result.setValue(this.calcuatedValue, i);
            }
            return;
        }
        final Variables variables = new Variables();
        final ValueList[] columnsNeeded = new ValueList[nbrVariables];
        for (int j = 0; j < this.variableNames.length; ++j) {
            final String name = this.variableNames[j];
            if (grid.hasColumn(name)) {
                columnsNeeded[j] = grid.getColumn(name);
            }
            else {
                if (!dc.hasValue(name)) {
                    final String str2 = "No value is supplied for variable " + name + " while colculating column " + columnName;
                    Spit.out(str2);
                    dc.addMessage("exilityError", str2);
                    return;
                }
                variables.setValue(name, dc.getValue(name));
            }
        }
        for (int j = 0; j < nbrRows; ++j) {
            for (int k = 0; k < this.variableNames.length; ++k) {
                final String name2 = this.variableNames[k];
                final ValueList col = columnsNeeded[k];
                if (col != null) {
                    variables.setValue(name2, col.getValue(j));
                }
            }
            try {
                result.setValue(this.evaluate(variables), j);
            }
            catch (ExilityException e) {
                Spit.out(e);
                dc.addMessage("exilityError", e.getMessage());
                return;
            }
        }
    }
    
    public DataValueType getValueType(final DataValueType[] types) throws ExilityException {
        final DataValueType[] valueTypes = this.getValueTypes(types);
        ExecutionStep[] steps;
        for (int length = (steps = this.steps).length, i = 0; i < length; ++i) {
            final ExecutionStep step = steps[i];
            final int leftIndex = step.operand1Index;
            final int rightIndex = step.operand2Index;
            valueTypes[leftIndex] = step.operator.getValueType(valueTypes[leftIndex], valueTypes[rightIndex]);
        }
        return valueTypes[0];
    }
    
    private DataValueType[] getValueTypes(final DataValueType[] knownValueTypes) throws ExilityException {
        DataValueType[] types = null;
        if (knownValueTypes != null && knownValueTypes.length > 0) {
            if (knownValueTypes.length != this.variableNames.length) {
                final String str = "Expression has " + this.variableNames.length + " variables but only " + knownValueTypes.length + " value types are passed for determining its vaue type ";
                Spit.out(str);
                throw new ExilityException(str);
            }
            types = knownValueTypes;
        }
        final DataValueType[] valueTypes = new DataValueType[this.components.length];
        int j = 0;
        for (int i = 0; i < valueTypes.length; ++i) {
            final Component c = this.components[i];
            if (c.isConstant()) {
                valueTypes[i] = c.getValue().getValueType();
            }
            else if (types != null) {
                valueTypes[i] = types[j];
                ++j;
            }
            else {
                valueTypes[i] = DataDictionary.getValueType(c.getName());
            }
        }
        return valueTypes;
    }
    
    public static void main(final String[] args) {
        final Variables variables = new Variables();
        variables.setValue("s", Value.newValue("123\"abcd123"));
        variables.setValue("i", Value.newValue(1234L));
        variables.setValue("f", Value.newValue(123.45));
        variables.setValue("b", Value.newValue(true));
        variables.setValue("d", Value.newValue("2007-05-23", DataValueType.DATE));
        final Pattern p = Pattern.compile("([a-zA-Z]\\w*)\\s*=\\s*(.*)");
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean valid = true;
        while (true) {
            if (valid) {
                Spit.out(variables.render());
            }
            else {
                valid = true;
            }
            Spit.out("\n type a statement in the form variable = expression\n or just enter to quit");
            try {
                final String input = in.readLine();
                if (input.length() == 0) {
                    break;
                }
                final Matcher m = p.matcher(input);
                if (!m.matches()) {
                    Spit.out("Sorry, you typed an invalid statmement.");
                    valid = false;
                }
                else {
                    Spit.out("left = " + m.group(1) + " right = " + m.group(2));
                    final String fieldName = m.group(1);
                    final Expression expr = new Expression();
                    expr.setExpression(m.group(2));
                    Spit.out("\n parsed statement : " + fieldName + " = " + expr.renderExpression());
                    Spit.out("\nsteps are " + expr.renderSteps());
                    variables.setValue(fieldName, expr.evaluate(variables));
                }
            }
            catch (Exception e) {
                Spit.out("Exception raised :");
                Spit.out(e.getMessage());
            }
        }
        Spit.out("Thank you, Bye");
    }
    
    class Variant
    {
        String name;
        Value value;
        
        boolean isConstant() {
            return this.name == null;
        }
        
        void setName(final String name) {
            this.name = name;
        }
        
        String getName() {
            return this.name;
        }
        
        void setValue(final Value value) {
            this.value = value;
        }
        
        Value getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            if (this.name == null) {
                return this.value.toString();
            }
            return this.name;
        }
    }
    
    class Component
    {
        Variant variant;
        Operator unaryOperator;
        Operator operator;
        int brackets;
        
        Component() {
            this.variant = new Variant();
            this.unaryOperator = null;
            this.operator = null;
        }
        
        boolean isConstant() {
            return this.variant.isConstant();
        }
        
        void setName(final String name) {
            this.variant.setName(name);
        }
        
        void setValue(final Value value) {
            this.variant.setValue(value);
        }
        
        Value getValue() {
            return this.variant.getValue();
        }
        
        String getName() {
            return this.variant.getName();
        }
        
        @Override
        public String toString() {
            final StringBuilder sbf = new StringBuilder();
            if (this.operator != null) {
                sbf.append(' ').append(this.operator).append(' ');
            }
            if (this.brackets > 0) {
                for (int i = 0; i < this.brackets; ++i) {
                    sbf.append('(');
                }
            }
            if (this.unaryOperator != null) {
                sbf.append(this.unaryOperator);
            }
            sbf.append(this.variant);
            if (this.brackets < 0) {
                for (int i = 0; i > this.brackets; --i) {
                    sbf.append(')');
                }
            }
            return sbf.toString();
        }
    }
    
    class ExecutionStep
    {
        int operand1Index;
        int operand2Index;
        Operator operator;
        
        @Override
        public String toString() {
            return "value[" + this.operand1Index + "] " + this.operator + " value[" + this.operand2Index + "]";
        }
    }
}
