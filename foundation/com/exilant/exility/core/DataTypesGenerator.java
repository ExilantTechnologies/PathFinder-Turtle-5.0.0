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

import java.util.Set;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;

public class DataTypesGenerator
{
    static final String JS_VAR_NAME = "dt";
    private static final String[] CLIENT_MESSAGE_NAMES;
    
    static {
        CLIENT_MESSAGE_NAMES = new String[] { "exilColumnIsRequired", "exilValueRequired", "exilPageParameterMissing", "exilValidateDependencyFailed", "exilValidateUniqueColumnsFailed", "exilFromToDataTypeMismatch", "exilFromToValidationError", "exilInvalidFromTo" };
    }
    
    public static String toJavaScript() {
        final StringBuilder js = new StringBuilder();
        js.append("\n var dt;");
        js.append("\nvar dataTypes = {};");
        final HashMap<String, String> messageTexts = new HashMap<String, String>();
        for (final String name : DataTypes.getDataTypeNames()) {
            js.append("\ndataTypes['").append(name).append("'] = new ");
            final AbstractDataType dt = DataTypes.getDataType(name, null);
            final String messageName = dt.messageName;
            final Message msg = Messages.getMessage(messageName);
            String txt = (msg != null) ? msg.text : dt.description;
            if (txt == null) {
                txt = "";
            }
            messageTexts.put(messageName, txt);
            if (dt instanceof TextDataType) {
                final TextDataType dte = (TextDataType)dt;
                final String rx = (dte.regex == null) ? "null" : (String.valueOf('/') + dte.regex.toString() + '/');
                js.append("TextDataType('").append(name).append("', '").append(messageName).append("', ").append(rx).append(", ").append(dte.minLength).append(", ").append(dte.maxLength).append(");");
            }
            else if (dt instanceof IntegralDataType) {
                final IntegralDataType dte2 = (IntegralDataType)dt;
                final String max = (dte2.maxValue == Long.MAX_VALUE) ? "null" : Long.toString(dte2.maxValue);
                final String min = (dte2.minValue == Long.MIN_VALUE) ? "null" : Long.toString(dte2.minValue);
                js.append("IntegralDataType('").append(name).append("', '").append(messageName).append("', ").append(min).append(", ").append(max).append(", ");
                if (dte2.allowNegativeValue) {
                    js.append("true");
                }
                else {
                    js.append("false");
                }
                js.append(");");
            }
            else if (dt instanceof DecimalDataType) {
                final DecimalDataType dte3 = (DecimalDataType)dt;
                final String max = (dte3.maxValue == Double.MAX_VALUE) ? "null" : Double.toString(dte3.maxValue);
                final String min = (dte3.minValue == Double.MIN_VALUE) ? "null" : Double.toString(dte3.minValue);
                js.append("DecimalDataType('").append(name).append("', '").append(messageName).append("', ").append(min).append(", ").append(max).append(", ");
                if (dte3.allowNegativeValue) {
                    js.append("true");
                }
                else {
                    js.append("false");
                }
                js.append(", ").append(dte3.numberOfDecimals);
                js.append(");");
            }
            else if (dt instanceof DateDataType) {
                final DateDataType dte4 = (DateDataType)dt;
                final String bef = (dte4.maxDaysBeforeToday == Integer.MAX_VALUE) ? "null" : Integer.toString(dte4.maxDaysBeforeToday);
                final String aft = (dte4.maxDaysAfterToday == Integer.MAX_VALUE) ? "null" : Integer.toString(dte4.maxDaysAfterToday);
                js.append("DateDataType('").append(name).append("', '").append(messageName).append("', ").append(bef).append(", ").append(aft).append(", ").append(Boolean.toString(dte4.includesTime).toLowerCase()).append(");");
            }
            else if (dt instanceof TimeStampDataType) {
                js.append("TimeStampDataType('").append(name).append("', '").append(messageName).append("'); ");
            }
            else if (dt instanceof BooleanDataType) {
                final BooleanDataType dte5 = (BooleanDataType)dt;
                final String tval = (dte5.trueValue == null) ? "null" : (String.valueOf('\'') + dte5.trueValue + '\'');
                final String fval = (dte5.falseValue == null) ? "null" : (String.valueOf('\'') + dte5.falseValue + '\'');
                js.append("BooleanDataType('").append(name).append("', '").append(messageName).append("', ").append(fval).append(", ").append(tval).append(");");
            }
            else {
                final String s = "//unknown data type encountered during conversion " + dt.getClass().getName();
                js.append(s);
                Spit.out(s);
            }
            js.append('\n');
        }
        js.append("\n\n//************ messages ****************");
        js.append("\n var dataTypeMessages = new Object();");
        js.append("\n// client messages for generic validation **");
        String[] client_MESSAGE_NAMES;
        for (int length = (client_MESSAGE_NAMES = DataTypesGenerator.CLIENT_MESSAGE_NAMES).length, i = 0; i < length; ++i) {
            final String name = client_MESSAGE_NAMES[i];
            js.append("\ndataTypeMessages['").append(name).append("'] = '").append(Messages.getMessageText(name).replace("'", "''")).append("';");
        }
        js.append("\n// data type specific messge. If the message is not found in meesages.xml, description of data type is put here *");
        for (final String messageName2 : messageTexts.keySet()) {
            if (messageTexts.get(messageName2) != null) {
                js.append("\ndataTypeMessages['").append(messageName2).append("'] = '").append(messageTexts.get(messageName2).replaceAll("'", "''")).append("';");
            }
            else {
                js.append("\ndataTypeMessages['").append(messageName2).append("'] = '';");
            }
        }
        for (final Message msg2 : Messages.getClientMessages()) {
            js.append("\ndataTypeMessages['").append(msg2.name).append("'] = '").append(msg2.text.replaceAll("'", "''")).append("';");
        }
        return js.toString();
    }
    
    public static String toXsd() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<xs:schema xmlns=\"http://com.exilant.exility/schema\" \n\t\txmlns:xs=\"http://www.w3.org/2001/XMLSchema\">");
        sbf.append("\n\n\t<xs:simpleType name=\"declaredDataTypes\">\n\t\t<xs:restriction base=\"xs:NCName\">");
        final Set<String> c = DataTypes.getDataTypeNames();
        String[] names = new String[c.size()];
        names = c.toArray(new String[c.size()]);
        Arrays.sort(names);
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            sbf.append("\n\t\t\t<xs:enumeration value=\"").append(name).append("\" />");
        }
        sbf.append("\n\t\t</xs:restriction>\n\t</xs:simpleType>\n</xs:schema>\n");
        return sbf.toString();
    }
}
