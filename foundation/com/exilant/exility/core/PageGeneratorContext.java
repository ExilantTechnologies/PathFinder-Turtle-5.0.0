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

import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;

public class PageGeneratorContext
{
    static final String imageFolderName;
    static String expandedImg;
    static String collapsedImg;
    private String tableName;
    private String prefix;
    boolean isInsideGrid;
    boolean renderFieldAsColumn;
    private static final char BRACE_OPEN = '[';
    private static final char BRACE_CLOSE = ']';
    private static final char COMMA = ',';
    HashMap<String, String> allFieldNames;
    Set<String> allNonFieldNames;
    HashMap<String, Set<String>> duplicateFields;
    ArrayList<String> fieldsToHideOnPageLoad;
    Set<String> allTableNames;
    String customLabelName;
    boolean useHtml5;
    private int lastPanelIdxUsed;
    int nbrErrors;
    private final String pageName;
    private final String pageLayoutType;
    
    static {
        imageFolderName = AP.imageFilePrefix;
        PageGeneratorContext.expandedImg = "\n<img alt=\"collapse\" src=\"" + PageGeneratorContext.imageFolderName + "expanded.gif\" /> ";
        PageGeneratorContext.collapsedImg = "\n<img alt=\"expand\" src=\"" + PageGeneratorContext.imageFolderName + "collapsed.gif\" /> ";
    }
    
    public PageGeneratorContext(final String pageName, final String layOutType) {
        this.tableName = null;
        this.prefix = "";
        this.isInsideGrid = false;
        this.renderFieldAsColumn = false;
        this.allFieldNames = null;
        this.allNonFieldNames = null;
        this.duplicateFields = null;
        this.fieldsToHideOnPageLoad = new ArrayList<String>();
        this.allTableNames = null;
        this.customLabelName = null;
        this.useHtml5 = false;
        this.lastPanelIdxUsed = 0;
        this.nbrErrors = 0;
        this.pageName = pageName;
        this.pageLayoutType = layOutType;
    }
    
    String getLayoutType() {
        return this.pageLayoutType;
    }
    
    String getPanelName() {
        ++this.lastPanelIdxUsed;
        return "_panel_" + this.lastPanelIdxUsed;
    }
    
    void reportError(final String errorText) {
        ++this.nbrErrors;
    }
    
    int getNbrErrors() {
        return this.nbrErrors;
    }
    
    void setAttributes(final Object element, final String elementName, final StringBuilder sbf, final String[] attributes) {
        final HashMap<String, Object> fields = (HashMap<String, Object>)(HashMap)ObjectManager.getAllFieldValues(element);
        for (final String attribute : attributes) {
            if (attribute.equals("linkedTableName")) {
                boolean valFound = false;
                if (fields.containsKey(attribute)) {
                    final Object obj = fields.get(attribute);
                    if (obj != null && obj.toString().length() > 0) {
                        valFound = true;
                    }
                }
                sbf.append("\n//linkedTableName ").append(valFound ? " EXISTS " : " DOES NOT EXIST").append("\n");
            }
            if (fields.containsKey(attribute)) {
                String stringValue = null;
                final Object value = fields.get(attribute);
                if (value != null) {
                    if (value instanceof Integer) {
                        final int i = (int)value;
                        if (i != 0 && i != Integer.MAX_VALUE && i != Integer.MIN_VALUE) {
                            stringValue = value.toString();
                        }
                    }
                    else if (value instanceof Boolean) {
                        if (value) {
                            stringValue = "true";
                        }
                    }
                    else if (value instanceof String) {
                        stringValue = this.format(value.toString());
                    }
                    else if (value instanceof String[]) {
                        final String[] list = (String[])value;
                        stringValue = this.arrayToString(list, false);
                    }
                    else {
                        stringValue = String.valueOf('\'') + value.toString().replace("'", "\\'") + '\'';
                    }
                    if (stringValue != null) {
                        sbf.append('\n').append(elementName).append('.').append(attribute).append(" = ").append(stringValue).append(";");
                    }
                }
            }
        }
    }
    
    private String format(final String val) {
        String stringValue = "''";
        if (val == null) {
            return stringValue;
        }
        if (val.length() > 0) {
            stringValue = String.valueOf('\'') + val.replace("'", "\\'") + '\'';
        }
        return stringValue;
    }
    
    void setTableName(final String tableName, final boolean islistOrGrid) {
        if (tableName == null || tableName.equals(" ")) {
            this.resetTableName();
            return;
        }
        if (this.tableName != null) {
            final String message = "ERROR: A panel that is already inside a table with name '" + this.tableName + "' is trying to use another table with name '" + tableName + "'. This is not possible";
            Spit.out(message);
            this.reportError(message);
        }
        this.tableName = tableName;
        this.prefix = String.valueOf(tableName) + '_';
        this.isInsideGrid = true;
        if (islistOrGrid) {
            this.renderFieldAsColumn = true;
        }
    }
    
    void resetTableName() {
        this.prefix = "";
        this.tableName = null;
        this.isInsideGrid = false;
        this.renderFieldAsColumn = false;
    }
    
    String getName(final String name) {
        if (name == null) {
            return null;
        }
        return String.valueOf(this.prefix) + name;
    }
    
    String getTableName() {
        return this.tableName;
    }
    
    String getFieldNames(final String fieldNames) {
        return String.valueOf(this.prefix) + fieldNames.replace(",", "," + this.prefix);
    }
    
    void setJsAttribute(final StringBuilder js, final String name, final String value) {
        js.append('\n').append("ele").append('.').append(name).append(" = ").append(value).append(";");
    }
    
    void setJsTextAttribute(final StringBuilder js, final String name, final String value) {
        js.append('\n').append("ele").append('.').append(name).append(" = ").append(this.format(value)).append(";");
    }
    
    void setTableSensitiveAttributes(final Object objekt, final StringBuilder js, final String[] names) {
        for (final String name : names) {
            final Object obj = ObjectManager.getFieldValue(objekt, name);
            if (obj != null) {
                String val = obj.toString();
                if (val.length() != 0) {
                    if (!val.equals(" ")) {
                        val = this.getQualifiedFieldName(val);
                        js.append('\n').append("ele").append('.').append(name).append(" = '").append(val).append("';");
                    }
                }
            }
        }
    }
    
    private String getQualifiedFieldName(final String name) {
        if (!this.allFieldNames.containsKey(name)) {
            if (this.allNonFieldNames.contains(name)) {
                return name;
            }
            if (this.allTableNames.contains(name)) {
                Spit.out("Note: table " + name + " is used in place of a field. This is valid only for enable/disable action.");
                return name;
            }
            final String err = "Error: " + name + " is referred but not defined as a field in this page.";
            Spit.out(err);
            this.reportError(err);
            return name;
        }
        else if (this.duplicateFields.containsKey(name)) {
            final Set<String> tableNames = this.duplicateFields.get(name);
            final String tn = (this.tableName == null) ? " " : this.tableName;
            if (!tableNames.contains(tn)) {
                final String err2 = "Warning: " + name + " is defined in more than one place. It is referred in another place. To refer to a field inside a table, use fully qualified name (tableName_fieldName). Otherwise, field name outside the table is assumed.";
                Spit.out(err2);
                return name;
            }
            if (this.tableName == null) {
                return name;
            }
            return String.valueOf(this.tableName) + '_' + name;
        }
        else {
            final String tblName = this.allFieldNames.get(name);
            if (tblName.equals(" ")) {
                return name;
            }
            return String.valueOf(tblName) + '_' + name;
        }
    }
    
    void setTableSensitiveArrays(final Object objekt, final StringBuilder js, final String[] names) {
        for (final String name : names) {
            final Object obj = ObjectManager.getFieldValue(objekt, name);
            if (obj != null) {
                if (!(obj instanceof String[])) {
                    final String err = "ERROR : attribute " + name + " is supposed to be an array of string, but it is " + obj.getClass().getName();
                    Spit.out(err);
                    this.reportError(err);
                }
                final String[] arr = (String[])obj;
                if (arr.length != 0) {
                    js.append('\n').append("ele").append('.').append(name).append(" = ");
                    if (name.equals("descQueryFields")) {
                        boolean hasSources = false;
                        for (final String cmpname : names) {
                            if (cmpname.equals("descQueryFieldSources")) {
                                hasSources = true;
                                break;
                            }
                        }
                        if (hasSources) {
                            js.append(this.arrayToString(arr, false)).append(';');
                        }
                        else {
                            js.append(this.arrayToString(arr, true)).append(';');
                        }
                    }
                    else if (name.equals("listServiceQueryFieldNames")) {
                        boolean hasSources = false;
                        for (final String cmpname : names) {
                            if (cmpname.equals("listServiceQueryFieldSources")) {
                                hasSources = true;
                                break;
                            }
                        }
                        if (hasSources) {
                            js.append(this.arrayToString(arr, false)).append(';');
                        }
                        else {
                            js.append(this.arrayToString(arr, true)).append(';');
                        }
                    }
                    else {
                        js.append(this.arrayToString(arr, true)).append(';');
                    }
                }
            }
        }
    }
    
    private String arrayToString(final String[] vals, final boolean isTableSensitive) {
        final StringBuilder js = new StringBuilder();
        js.append('[');
        char prefixChar = ' ';
        for (final String val : vals) {
            String strval;
            if (val.length() == 0 || val.equals(" ")) {
                strval = "''";
            }
            else if (isTableSensitive) {
                strval = String.valueOf('\'') + this.getQualifiedFieldName(val) + '\'';
            }
            else {
                strval = this.format(val);
            }
            js.append(prefixChar).append(strval);
            prefixChar = ',';
        }
        js.append(']');
        return js.toString();
    }
    
    void setAttributes(final Object objekt, final StringBuilder js, final String[] names) {
        this.setAttributes(objekt, "ele", js, names);
    }
    
    void addHiddenFieldsToPage(final String fieldName) {
        this.fieldsToHideOnPageLoad.add(fieldName);
    }
}
