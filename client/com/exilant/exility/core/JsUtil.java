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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Date;

public class JsUtil
{
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '\"';
    private static final char COLON = ':';
    private static final char SQUARE_BRACKET_OPEN = '[';
    private static final char SQUARE_BRACKET_CLOSE = ']';
    private static final char BRACE_OPEN = '{';
    private static final char BRACE_CLOSE = '}';
    private static final char SPACE = ' ';
    private static final char COMMA = ',';
    private static final String INITIAL_HTML = "<html><head><link rel=\"stylesheet\" href=\"default.css\" type=\"text/css\" />";
    private static final String FINAL_HTML = "</body></html>";
    
    public static void toJson(final StringBuilder sbf, final Object val) {
        if (val == null) {
            sbf.append("''");
            return;
        }
        if (val instanceof String) {
            final String str = ((String)val).replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            sbf.append('\'').append(str).append('\'');
            return;
        }
        if (val.getClass().isEnum()) {
            sbf.append('\'').append(val).append('\'');
            return;
        }
        if (val instanceof Value) {
            final Value value = (Value)val;
            toJson(sbf, value.getTextValue());
            return;
        }
        if (val.getClass().isPrimitive()) {
            sbf.append(' ').append(val).append(' ');
            return;
        }
        if (val instanceof ValueList) {
            sbf.append('[');
            char prefixChar = ' ';
            final String[] vals = ((ValueList)val).format();
            String[] array;
            for (int length = (array = vals).length, i = 0; i < length; ++i) {
                final String v = array[i];
                sbf.append(prefixChar);
                sbf.append('\'').append(v).append('\'');
                prefixChar = ',';
            }
            sbf.append(']');
            return;
        }
        if (val instanceof Date) {
            sbf.append('\'').append(DateUtility.formatDate((Date)val)).append('\'');
            return;
        }
        if (val instanceof Object[]) {
            sbf.append('[');
            char prefixChar = ' ';
            final Object[] list = (Object[])val;
            Object[] array2;
            for (int length2 = (array2 = list).length, j = 0; j < length2; ++j) {
                final Object v2 = array2[j];
                sbf.append(prefixChar);
                toJson(sbf, v2);
                prefixChar = ',';
            }
            sbf.append(']');
            return;
        }
        if (val instanceof Set) {
            final Set list2 = (Set)val;
            sbf.append('[');
            char prefixChar2 = ' ';
            for (final Object v2 : list2) {
                sbf.append(prefixChar2);
                toJson(sbf, v2);
                prefixChar2 = ',';
            }
            sbf.append(']');
            return;
        }
        if (val instanceof Grid) {
            sbf.append('[');
            char prefixChar = ' ';
            final String[][] grid = ((Grid)val).getRawData();
            String[][] array3;
            for (int length3 = (array3 = grid).length, k = 0; k < length3; ++k) {
                final String[] row = array3[k];
                sbf.append(prefixChar);
                toJson(sbf, row);
                prefixChar = ',';
            }
            sbf.append(']');
            return;
        }
        if (val instanceof Map) {
            sbf.append('{');
            String prefix = "";
            final Map map = (Map)val;
            for (final Object key : map.keySet()) {
                sbf.append(prefix).append('\"').append(key).append('\"').append(':');
                toJson(sbf, map.get(key));
                prefix = "\n,";
            }
            sbf.append('}');
            return;
        }
        sbf.append('{');
        char prefix2 = ' ';
        final Map<String, Object> fields = ObjectManager.getAllFieldValues(val);
        for (final String key2 : fields.keySet()) {
            sbf.append(prefix2).append('\"').append(key2).append('\"').append(':');
            toJson(sbf, fields.get(key2));
            prefix2 = ',';
        }
        sbf.append('}');
    }
    
    public static void toJson(final StringBuilder sbf, final ServiceData serviceData) {
        sbf.append("{\"success\":");
        if (serviceData.getErrorStatus() >= 2) {
            sbf.append("false");
        }
        else {
            sbf.append("true");
        }
        sbf.append("\n,\"values\":");
        toJson(sbf, serviceData.values);
        sbf.append("\n,\"lists\":");
        toJson(sbf, serviceData.lists);
        sbf.append("\n,\"grids\":");
        toJson(sbf, serviceData.grids);
        sbf.append("\n,\"messages\":");
        toJson(sbf, serviceData.messageList);
        sbf.append('}');
    }
    
    public static void toJson(final StringBuilder sbf, final DataCollection dc) {
        sbf.append("{\"success\":");
        if (dc.hasError()) {
            sbf.append("false");
        }
        else {
            sbf.append("true");
        }
        sbf.append("\n,\"values\":");
        toJson(sbf, dc.values);
        sbf.append("\n,\"lists\":");
        toJson(sbf, dc.lists);
        sbf.append("\n,\"grids\":");
        toJson(sbf, dc.grids);
        sbf.append("\n,\"messages\":");
        toJson(sbf, dc.messageList);
        sbf.append('}');
    }
    
    public static void toJs(final StringBuilder sbf, final ServiceData serviceData) {
        sbf.append("var dc = new PM.DataColelction();");
        sbf.append("\ndc.success = ").append((serviceData.getErrorStatus() > 1) ? "false;" : "true;");
        for (final String name : serviceData.values.keySet()) {
            sbf.append("\ndc.values['").append(name).append("'] = '").append(serviceData.getValue(name).replaceAll("'", "\\'")).append("';");
        }
        for (final String name : serviceData.lists.keySet()) {
            sbf.append("\ndc.lists['").append(name).append("'] = ");
            toJs(sbf, serviceData.lists.get(name));
            sbf.append(";");
        }
        for (final String name : serviceData.grids.keySet()) {
            sbf.append("\ndc.grids['").append(name).append("'] = ");
            toJs(sbf, serviceData.grids.get(name));
            sbf.append(";");
        }
        toJs(sbf, serviceData.messageList);
    }
    
    private static void toJs(final StringBuilder sbf, final MessageList messageList) {
        String[] msgs = messageList.getErrorTexts();
        if (msgs != null && msgs.length > 0) {
            sbf.append("dc.messages['error'] = ");
            toJs(sbf, msgs);
        }
        msgs = messageList.getWarningTexts();
        if (msgs != null && msgs.length > 0) {
            sbf.append("dc.messages['warning'] = ");
            toJs(sbf, msgs);
        }
        msgs = messageList.getInfoTexts();
        if (msgs != null && msgs.length > 0) {
            sbf.append("dc.messages['info'] = ");
            toJs(sbf, msgs);
        }
    }
    
    private static void toJs(final StringBuilder sbf, final String[][] strings) {
        sbf.append('[');
        String prefix = "\n";
        for (final String[] arr : strings) {
            sbf.append(prefix);
            toJs(sbf, arr);
            prefix = "\n,";
        }
        sbf.append('[');
    }
    
    private static void toJs(final StringBuilder sbf, final String[] strings) {
        sbf.append('[');
        String prefix = "\n\t'";
        for (final String s : strings) {
            sbf.append(prefix).append(s.replaceAll("'", "\\'")).append('\'');
            prefix = "\n\t,'";
        }
        sbf.append(']');
    }
    
    public static void toJson(final StringBuilder sbf, final MessageList messages) {
        sbf.append('{');
        String[] msgs = messages.getInfoTexts();
        char prefix = ' ';
        if (msgs != null && msgs.length > 0) {
            sbf.append("\n").append(prefix).append(" \"Info\":");
            prefix = ',';
            toJson(sbf, msgs);
        }
        msgs = messages.getWarningTexts();
        if (msgs != null && msgs.length > 0) {
            sbf.append("\n").append(prefix).append(" \"Warning\":");
            prefix = ',';
            toJson(sbf, msgs);
        }
        msgs = messages.getErrorTexts();
        if (msgs != null && msgs.length > 0) {
            sbf.append("\n").append(prefix).append(" \"Error\":");
            prefix = ',';
            toJson(sbf, msgs);
        }
        sbf.append('}');
    }
    
    @Deprecated
    public static void SaveDataTypeJs(final String fileName) {
        final Set<String> names = DataTypes.getDataTypeNames();
        if (names.size() == 0) {
            Spit.out("No data types defined for the project");
            return;
        }
        final StringBuilder sbf = new StringBuilder();
        sbf.append("var dataTypes = {};\nvar dt;\n");
        final StringBuilder msgs = new StringBuilder();
        msgs.append("\n messages = {");
        final char prefix = ' ';
        for (final String name : names) {
            final AbstractDataType dt = DataTypes.getDataType(name, null);
            String className = dt.getClass().getName();
            className = className.substring(className.lastIndexOf(46) + 1);
            sbf.append("\ndt = new PM.").append(className).append("());");
            ObjectManager.serializePrimitiveAttributes(dt, "dt", sbf);
            final String msgId = dt.messageName;
            msgs.append(prefix).append("\"").append(msgId).append("\" : ");
            toJson(msgs, Messages.getMessageText(msgId));
            sbf.append("dataTypes['").append(name).append("'] = dt;\n");
        }
        msgs.append("};");
        ResourceManager.saveResource(fileName, String.valueOf(sbf.toString()) + msgs.toString());
    }
    
    public static String toHtml(final ServiceData data) {
        final StringBuilder sbf = new StringBuilder();
        messageToHtml(sbf, data.messageList);
        valuesToHtml(sbf, data.values);
        listToHtml(sbf, data.lists);
        for (final String gridName : data.grids.keySet()) {
            gridToHtml(sbf, gridName, data.getGrid(gridName));
        }
        return "<html><head><link rel=\"stylesheet\" href=\"default.css\" type=\"text/css\" />" + sbf.toString() + "</body></html>";
    }
    
    private static void gridToHtml(final StringBuilder sbf, final String gridName, final String[][] grid) {
        sbf.append("<br /><fieldset><legend>gridName</legend><table border=\"1\"><tr>");
        final String[] header = grid[0];
        String[] array;
        for (int length = (array = header).length, j = 0; j < length; ++j) {
            final String name = array[j];
            sbf.append("<th>").append(name).append("</th>");
        }
        sbf.append("</tr>");
        for (int i = 1; i < grid.length; ++i) {
            sbf.append("<tr>");
            final String[] row = grid[i];
            String[] array2;
            for (int length2 = (array2 = row).length, k = 0; k < length2; ++k) {
                final String txt = array2[k];
                sbf.append("<td>").append(txt).append("</td>");
            }
            sbf.append("</tr>");
        }
        sbf.append("</table></fieldset>");
    }
    
    private static void listToHtml(final StringBuilder sbf, final Map<String, String[]> lists) {
        sbf.append("<br /><fieldset><legend>Lists</legend><table border=\"0\">");
        for (final String listName : lists.keySet()) {
            sbf.append("<tr><td>").append(listName).append(" :</td><td>");
            final String[] vals = lists.get(listName);
            if (vals == null || vals.length == 0) {
                sbf.append("&nbsp;");
            }
            else {
                sbf.append(vals[0]);
                for (int i = 1; i < vals.length; ++i) {
                    sbf.append(", ").append(vals[i]);
                }
            }
            sbf.append("</td></tr>");
        }
        sbf.append("</table></fieldset>");
    }
    
    private static void valuesToHtml(final StringBuilder sbf, final Map<String, String> values) {
        sbf.append("<br /><fieldset><legend>Values</legend><table border=\"0\">");
        for (final String name : values.keySet()) {
            String txt = values.get(name);
            if (txt == null || txt.length() == 0) {
                txt = "&nbsp";
            }
            sbf.append("<tr><td>").append(name).append(" :</td><td>").append(txt).append("</td></tr>");
        }
        sbf.append("</table></fieldset>");
    }
    
    private static void messageToHtml(final StringBuilder sbf, final MessageList messageList) {
        if (messageList.size() == 0) {
            return;
        }
        sbf.append("<br /><fieldset><legend>Messages</legend><table border=\"0\">");
        String[] msgs = messageList.getErrorTexts();
        if (msgs.length > 0) {
            sbf.append("<tr style=\"color:red;\"><td style=\"valign:top;\">Error</td><td>)");
            String[] array;
            for (int length = (array = msgs).length, i = 0; i < length; ++i) {
                final String msg = array[i];
                sbf.append("<li>").append(msg).append("</li>");
            }
            sbf.append("</td></tr");
        }
        msgs = messageList.getWarningTexts();
        if (msgs.length > 0) {
            sbf.append("<tr style=\"color:blue;\"><td style=\"valign:top;\">Warning</td><td>)");
            String[] array2;
            for (int length2 = (array2 = msgs).length, j = 0; j < length2; ++j) {
                final String msg = array2[j];
                sbf.append("<li>").append(msg).append("</li>");
            }
            sbf.append("</td></tr");
        }
        msgs = messageList.getInfoTexts();
        if (msgs.length > 0) {
            sbf.append("<tr><td style=\"valign:top;\">Information</td><td>)");
            String[] array3;
            for (int length3 = (array3 = msgs).length, k = 0; k < length3; ++k) {
                final String msg = array3[k];
                sbf.append("<li>").append(msg).append("</li>");
            }
            sbf.append("</td></tr");
        }
        sbf.append("</table></fieldset>");
    }
    
    public static String toHtml(final Exception e) {
        return "<html><head><link rel=\"stylesheet\" href=\"default.css\" type=\"text/css\" /><h1>Error</h1><br/>" + e.getMessage() + "<br /> stack trace<br/>" + "</body></html>";
    }
}
