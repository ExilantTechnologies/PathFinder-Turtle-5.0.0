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
import java.util.Map;

public abstract class Operator
{
    static final Operator ADD;
    static final Operator SUBTRACT;
    static final Operator MULTIPLY;
    static final Operator DIVIDE;
    static final Operator REMAINDER;
    static final Operator POWER;
    static final Operator EQUAL;
    static final Operator NOT_EQUAL;
    static final Operator LESS_THAN;
    static final Operator LESS_THAN_OR_EQUAL;
    static final Operator GREATER_THAN;
    static final Operator GREATER_THAN_OR_EQUAL;
    static final Operator NOT;
    static final Operator AND;
    static final Operator OR;
    static final Operator EXISTS;
    static final Operator NOT_EXISTS;
    static final int LAST_PRECEDENCE_LEVEL = 5;
    private static Map<String, Operator> operatorTexts;
    private static Operator[] VALUES;
    
    static {
        ADD = new AddOperator();
        SUBTRACT = new SubtractOperator();
        MULTIPLY = new MultiplyOperator();
        DIVIDE = new DivideOperator();
        REMAINDER = new RemainderOperator();
        POWER = new PowerOperator();
        EQUAL = new EqualOperator();
        NOT_EQUAL = new NotEqualOperator();
        LESS_THAN = new LessThanOperator();
        LESS_THAN_OR_EQUAL = new LessThanOrEqualOperator();
        GREATER_THAN = new GreaterThanOperator();
        GREATER_THAN_OR_EQUAL = new GreaterThanOrEqualOperator();
        NOT = new NotOperator();
        AND = new AndOperator();
        OR = new OrOperator();
        EXISTS = new ExistsOperator();
        NOT_EXISTS = new NotExistsOperator();
        Operator.operatorTexts = new HashMap<String, Operator>();
        Operator.VALUES = new Operator[] { Operator.ADD, Operator.SUBTRACT, Operator.MULTIPLY, Operator.DIVIDE, Operator.REMAINDER, Operator.POWER, Operator.EQUAL, Operator.NOT_EQUAL, Operator.LESS_THAN, Operator.LESS_THAN_OR_EQUAL, Operator.GREATER_THAN, Operator.GREATER_THAN_OR_EQUAL, Operator.NOT, Operator.AND, Operator.OR, Operator.EXISTS, Operator.NOT_EXISTS };
        Operator[] values;
        for (int length = (values = Operator.VALUES).length, i = 0; i < length; ++i) {
            final Operator oper = values[i];
            Operator.operatorTexts.put(oper.toString(), oper);
        }
    }
    
    public static Operator[] values() {
        return Operator.VALUES;
    }
    
    public static Operator parse(final String value, final int startAt) {
        final int max = startAt + 2;
        Operator oper = null;
        String opText = null;
        if (value.length() >= max) {
            opText = value.substring(startAt, startAt + 2);
            if (Operator.operatorTexts.containsKey(opText)) {
                oper = Operator.operatorTexts.get(opText);
            }
        }
        if (oper == null) {
            opText = value.substring(startAt, startAt + 1);
            if (Operator.operatorTexts.containsKey(opText)) {
                oper = Operator.operatorTexts.get(opText);
            }
        }
        return oper;
    }
    
    abstract int getPrecedence();
    
    public abstract DataValueType getValueType(final DataValueType p0, final DataValueType p1);
    
    Value operate(final Value operand1) throws ExilityException {
        throw new ExilityException("Operator " + this.toString() + " is not a unary operator");
    }
    
    Value operate(final Value operand1, final Value operand2) throws ExilityException {
        throw new ExilityException("Operator " + this.toString() + " is a unary operator");
    }
    
    void raiseException(final Value val1, final Value val2) throws ExilityException {
        throw new ExilityException("Operator " + this.toString() + " is not applicable between type " + val1.getValueType() + " and " + val2.getValueType());
    }
    
    void raiseException(final Value val1) throws ExilityException {
        throw new ExilityException("Unary operator " + this.toString() + " is not applicable for value of type " + val1.getValueType());
    }
}
