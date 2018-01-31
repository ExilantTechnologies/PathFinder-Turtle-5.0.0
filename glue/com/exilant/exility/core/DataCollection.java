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

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DataCollection
{
    private static Pattern sqlRegex;
    public Map<String, Value> values;
    public Map<String, ValueList> lists;
    public Map<String, Grid> grids;
    public HashMap<String, Object> dataSet;
    public MessageList messageList;
    public String prefix;
    private final Map<String, Map<Long, Long>> generatedKeys;
    private Value timeStamp;
    
    static {
        DataCollection.sqlRegex = Pattern.compile("sql[0-9]*");
    }
    
    public DataCollection() {
        this.values = new HashMap<String, Value>();
        this.lists = new HashMap<String, ValueList>();
        this.grids = new HashMap<String, Grid>();
        this.dataSet = new HashMap<String, Object>();
        this.prefix = null;
        this.generatedKeys = new HashMap<String, Map<Long, Long>>();
        this.timeStamp = Value.newValue(new Date());
        this.messageList = new MessageList();
    }
    
    DataCollection(final DataCollection dc) {
        this.values = new HashMap<String, Value>();
        this.lists = new HashMap<String, ValueList>();
        this.grids = new HashMap<String, Grid>();
        this.dataSet = new HashMap<String, Object>();
        this.prefix = null;
        this.generatedKeys = new HashMap<String, Map<Long, Long>>();
        this.messageList = dc.messageList;
        this.copyInputFieldsTo(dc);
    }
    
    public void addBooleanValue(final String name, final boolean value) {
        this.addValue(name, Value.newValue(value));
    }
    
    public void addIntegralValue(final String name, final long value) {
        this.addValue(name, Value.newValue(value));
    }
    
    public void addDecimalValue(final String name, final double value) {
        this.addValue(name, Value.newValue(value));
    }
    
    public void addDateValue(final String name, final Date value) {
        this.addValue(name, Value.newValue(value));
    }
    
    public void addTextValue(final String name, final String value) {
        this.addValue(name, Value.newValue(value));
    }
    
    public void addTextValue(final String name, final String[] value) {
        String textValue = "";
        if (value != null && value.length > 0) {
            final StringBuilder sbf = new StringBuilder();
            for (final String val : value) {
                sbf.append(val).append(",");
            }
            sbf.deleteCharAt(sbf.length() - 1);
            textValue = sbf.toString();
        }
        this.addValue(name, Value.newValue(textValue));
    }
    
    public void addTextValue(final String name, final long[] value) {
        String textValue = "";
        if (value != null && value.length > 0) {
            final StringBuilder sbf = new StringBuilder();
            for (final long val : value) {
                sbf.append(val).append(',');
            }
            sbf.deleteCharAt(sbf.length() - 1);
            textValue = sbf.toString();
        }
        this.addValue(name, Value.newValue(textValue));
    }
    
    public void addNullValue(final String name) {
        this.addValue(name, new NullValue(DataValueType.TEXT));
    }
    
    public void addValue(final String name, final Value value) {
        if (value == null) {
            final String msg = "Field " + name + " is being added with NULL as its value. This is not allowed.";
            Spit.out(msg);
            this.addError(msg);
            return;
        }
        this.values.put(name, value);
    }
    
    public void addValue(final String name, final String textValue, final DataValueType dataValueType) {
        final Value value = Value.newValue(textValue, dataValueType);
        if (value == null) {
            final String msg = "Field " + name + " has value '" + textValue + "'. This is not a valid " + dataValueType;
            Spit.out(msg);
            this.addError(msg);
            return;
        }
        this.values.put(name, value);
    }
    
    public boolean hasValue(final String name) {
        return this.values.containsKey(name);
    }
    
    public boolean isNull(final String name) {
        final Value value = this.values.get(name);
        return value == null || value.isNull();
    }
    
    public Value getValue(final String name) {
        return this.values.get(name);
    }
    
    public void removeValue(final String name) {
        this.values.remove(name);
    }
    
    public DataValueType getValueType(final String name) {
        final Value value = this.getValue(name);
        if (value != null) {
            return value.getValueType();
        }
        return null;
    }
    
    public long getIntegralValue(final String name, final long defaultValue) {
        final Value value = this.getValue(name);
        if (value != null) {
            return value.getIntegralValue();
        }
        return defaultValue;
    }
    
    public double getDecimalValue(final String name, final double defaultValue) {
        final Value value = this.getValue(name);
        if (value != null) {
            return value.getDecimalValue();
        }
        return defaultValue;
    }
    
    public boolean getBooleanValue(final String name, final boolean defaultValue) {
        final Value value = this.getValue(name);
        if (value != null) {
            return value.getBooleanValue();
        }
        return defaultValue;
    }
    
    public Date getDateValue(final String name, final Date defaultValue) {
        final Value value = this.getValue(name);
        if (value != null) {
            final Date d = value.getDateValue();
            if (!d.equals("")) {
                return d;
            }
        }
        return defaultValue;
    }
    
    public String getTextValue(final String name, final String defaultValue) {
        final Value value = this.getValue(name);
        if (value != null) {
            return value.getTextValue();
        }
        return defaultValue;
    }
    
    public void addValueList(final String name, final ValueList valueList) {
        this.lists.put(name, valueList);
    }
    
    public ValueList getValueList(final String name) {
        ValueList list = null;
        list = this.lists.get(name);
        return list;
    }
    
    public boolean hasList(final String name) {
        return this.lists.containsKey(name);
    }
    
    public void removeValueList(final String name) {
        this.lists.remove(name);
    }
    
    public void addGrid(final String name, final Grid grid) {
        grid.setName(name);
        this.grids.put(name, grid);
    }
    
    public void addGrid(final String name, final String[][] data) {
        final Grid grid = new Grid(name);
        try {
            grid.setRawData(data);
            this.grids.put(name, grid);
        }
        catch (ExilityException e) {
            this.addError("Error while adding raw data to gird " + name + ". Either header row is missing, or columns may have invalid data.");
        }
    }
    
    public boolean hasGrid(final String name) {
        return this.grids.containsKey(name);
    }
    
    public Grid getGrid(final String name) {
        Grid grid = null;
        grid = this.grids.get(name);
        return grid;
    }
    
    public void removeGrid(final String name) {
        this.grids.remove(name);
    }
    
    public void addDataSet(final String name, final Object val) {
        this.dataSet.put(name, val);
    }
    
    public boolean hasDataSet(final String name) {
        return this.dataSet.containsKey(name);
    }
    
    public Object getDataSet(final String name) {
        if (this.dataSet.containsKey(name)) {
            return this.dataSet.get(name);
        }
        return null;
    }
    
    public void removeDataSet(final String name) {
        this.dataSet.remove(name);
    }
    
    public MessageSeverity addMessage(final String messageId, final String... parameters) {
        return this.messageList.addMessage(messageId, parameters);
    }
    
    public void addError(final String errorText) {
        this.addMessage("exilityError", errorText);
    }
    
    public void addWarning(final String warningText) {
        this.addMessage("exilityWarning", warningText);
    }
    
    public void addInfo(final String infoText) {
        this.addMessage("exilityInfo", infoText);
    }
    
    public boolean hasMessage(final String messageId) {
        return this.messageList.hasMessage(messageId);
    }
    
    public boolean hasError() {
        return this.messageList.hasError();
    }
    
    public void raiseException(final String msgId, final String... parameters) throws ExilityException {
        this.addMessage(msgId, parameters);
        Spit.out("Exception raised with message Id =  " + msgId);
        throw new ExilityException();
    }
    
    public String[] getFieldNames() {
        return this.keysToArray(this.values.keySet());
    }
    
    public String[] getGridNames() {
        return this.keysToArray(this.grids.keySet());
    }
    
    public String[] keysToArray(final Set<String> keys) {
        final String[] keysArray = new String[keys.size()];
        int i = 0;
        for (final String key : keys) {
            keysArray[i++] = key;
        }
        return keysArray;
    }
    
    public MessageSeverity getLastSevirity() {
        return this.messageList.getLastSeverity();
    }
    
    public void append(final DataCollection dc) {
        for (final String key : dc.values.keySet()) {
            this.values.put(key, dc.values.get(key));
        }
        for (final String key : dc.grids.keySet()) {
            this.grids.put(key, dc.grids.get(key));
        }
        for (final String key : dc.lists.keySet()) {
            this.lists.put(key, dc.lists.get(key));
        }
    }
    
    public MessageSeverity getSeverity() {
        return this.messageList.getSevirity();
    }
    
    public void addValueAfterCheckingInDictionary(final String name, final String value) {
        if (value == null) {
            return;
        }
        final DataValueType vt = DataDictionary.getDataType(name).getValueType();
        Value val = null;
        final String trimmedvalue = value.trim();
        if (trimmedvalue.length() == 0) {
            val = new NullValue(vt);
        }
        else {
            val = Value.newValue(trimmedvalue, vt);
            if (val == null) {
                final String msg = "Field " + name + " has value '" + trimmedvalue + "'. This is not a valid " + vt;
                Spit.out(msg);
                this.addError(msg);
                return;
            }
        }
        this.values.put(name, val);
    }
    
    public void addListAfterCheckingInDictionary(final String name, final String[] value) {
        final AbstractDataType dt = DataDictionary.getDataType(name);
        final ValueList list = dt.getValueList(name, value, true, this);
        if (list != null) {
            this.lists.put(name, list);
        }
    }
    
    public void addGridAfterCheckingInDictionary(final String name, final String[][] data) {
        final Grid grid = new Grid(name);
        try {
            grid.setRawData(data);
            this.grids.put(name, grid);
        }
        catch (Exception e) {
            this.addError("Grid " + name + " has some invalid numeric/date field/s. " + e.getMessage());
        }
    }
    
    public void copyFrom(final ServiceData serviceData) {
        for (final String key : serviceData.values.keySet()) {
            this.addValueAfterCheckingInDictionary(key, serviceData.values.get(key));
        }
        for (final String key : serviceData.lists.keySet()) {
            this.addListAfterCheckingInDictionary(key, serviceData.lists.get(key));
        }
        for (final String key : serviceData.grids.keySet()) {
            this.addGridAfterCheckingInDictionary(key, serviceData.grids.get(key));
        }
    }
    
    public void copyTo(final ServiceData serviceData) {
        try {
            for (final String key : this.values.keySet()) {
                if (key.indexOf("sql") == 0 && !key.equals("sql") && DataCollection.sqlRegex.matcher(key).matches()) {
                    Spit.out(this.values.get(key).toString());
                }
                else {
                    final Value value = this.values.get(key);
                    String textValue = "";
                    if (value != null && !value.isNull()) {
                        final AbstractDataType dt = DataDictionary.getDataType(key);
                        textValue = dt.format(value);
                        if (textValue == null) {
                            final String msg = "Field " + key + " has value '" + value.toString() + "'. This is declared as datatype: " + dt.name + " .This is not right.";
                            Spit.out(msg);
                            this.addError(msg);
                            return;
                        }
                    }
                    serviceData.values.put(key, textValue);
                }
            }
            for (final String key : this.lists.keySet()) {
                serviceData.lists.put(key, this.lists.get(key).format());
            }
            for (final String key : this.grids.keySet()) {
                serviceData.grids.put(key, this.grids.get(key).getRawData());
            }
            serviceData.messageList = this.messageList;
        }
        catch (Exception ex) {
            this.addError(ex.getMessage());
            Spit.out(ex.getMessage());
        }
    }
    
    public void copyInternalFieldsTo(final ServiceData serviceData) {
        InternalOutputField[] values;
        for (int length = (values = InternalOutputField.values()).length, i = 0; i < length; ++i) {
            final InternalOutputField field = values[i];
            final String name = field.toString();
            if (this.hasValue(name)) {
                serviceData.values.put(name, this.getTextValue(name, ""));
            }
        }
        serviceData.messageList = this.messageList;
    }
    
    public void copyInternalFieldsFrom(final ServiceData serviceData) {
        InternalInputField[] values;
        for (int length = (values = InternalInputField.values()).length, i = 0; i < length; ++i) {
            final InternalInputField field = values[i];
            final String key = field.toString();
            if (serviceData.hasValue(key)) {
                final String val = serviceData.values.get(key);
                this.values.put(key, Value.newValue(val));
            }
        }
    }
    
    public void copyMessages(final ServiceData outData) {
        outData.messageList = this.messageList;
    }
    
    public void copyInputFieldsTo(final DataCollection dc) {
        InternalInputField[] values;
        for (int length = (values = InternalInputField.values()).length, i = 0; i < length; ++i) {
            final InternalInputField field = values[i];
            final String fieldName = field.toString();
            dc.addValue(fieldName, this.getValue(fieldName));
        }
    }
    
    public String getErrorMessage() {
        String msg = "";
        if (this.hasError()) {
            String[] errorTexts;
            for (int length = (errorTexts = this.messageList.getErrorTexts()).length, i = 0; i < length; ++i) {
                final String m = errorTexts[i];
                msg = String.valueOf(msg) + '\n' + m;
            }
        }
        return msg;
    }
    
    public void addGeneratedKey(final String tableName, final long oldKey, final long generatedKey) {
        Map<Long, Long> keys = this.generatedKeys.get(tableName);
        if (keys == null) {
            keys = new HashMap<Long, Long>();
            this.generatedKeys.put(tableName, keys);
        }
        keys.put(new Long(oldKey), new Long(generatedKey));
    }
    
    public void addGeneratedKey(final String tableName, final Map<Long, Long> keys) {
        if (this.generatedKeys.containsKey(tableName)) {
            this.generatedKeys.get(tableName).putAll(keys);
            return;
        }
        this.generatedKeys.put(tableName, keys);
    }
    
    public Map<Long, Long> getGeneratedKeys(final String tableName) {
        return this.getGeneratedKeys(tableName);
    }
    
    public long getGeneratedKey(final String tableName, final long oldKey) {
        final Map<Long, Long> keys = this.getGeneratedKeys(tableName);
        if (keys == null) {
            return 0L;
        }
        final Long l = keys.get(new Long(oldKey));
        if (l == null) {
            return 0L;
        }
        return l;
    }
    
    public void zapMessages() {
        this.messageList = new MessageList();
    }
    
    public Value getUserId() {
        return this.getValue(AP.loggedInUserFieldName);
    }
    
    public Value getTimeStamp() {
        return this.timeStamp;
    }
    
    void toSpreadSheetXml(final StringBuilder sbf, final String sheetPrefix) {
        int n = this.values.size();
        if (n > 0) {
            final Grid grid = this.valuesToGrid();
            String sheetName = "_values";
            if (sheetPrefix != null) {
                sheetName = String.valueOf(sheetPrefix) + sheetName;
            }
            grid.toSpreadSheetXml(sbf, sheetName);
        }
        n = this.grids.size();
        if (n == 0) {
            return;
        }
        for (final String key : this.grids.keySet()) {
            final Grid grid2 = this.grids.get(key);
            final String sheetName = (sheetPrefix == null) ? key : (String.valueOf(sheetPrefix) + key);
            grid2.toSpreadSheetXml(sbf, sheetName);
        }
    }
    
    public boolean hasAllFieldsOf(final DataCollection dc) {
        boolean matched = true;
        for (final String fieldName : dc.values.keySet()) {
            final Value thisValue = this.getValue(fieldName);
            if (thisValue == null) {
                this.addError(String.valueOf(fieldName) + " is not found in the reslts");
                matched = false;
            }
            else {
                final Value otherValue = dc.getValue(fieldName);
                if (thisValue.equal(otherValue).getBooleanValue()) {
                    continue;
                }
                this.addError("Field " + fieldName + " is expected to have a value of " + otherValue.getTextValue() + " but its value is " + thisValue.getTextValue());
                matched = false;
            }
        }
        for (final String gridName : dc.grids.keySet()) {
            final Grid thisGrid = this.grids.get(gridName);
            if (thisGrid == null) {
                this.addError("Grid " + gridName + " not found in the reslts.");
                matched = false;
            }
            else {
                final Grid otherGrid = dc.grids.get(gridName);
                matched = thisGrid.equal(otherGrid, this);
            }
        }
        return matched;
    }
    
    public void gridToValues(final String gridName) {
        final Grid grid = this.getGrid(gridName);
        if (grid == null) {
            Spit.out(String.valueOf(gridName) + " is not a grid in dc. could not copy columns into values.");
            return;
        }
        String[] columnNames;
        for (int length = (columnNames = grid.getColumnNames()).length, i = 0; i < length; ++i) {
            final String columnName = columnNames[i];
            this.addValue(columnName, grid.getValue(columnName, 0));
        }
    }
    
    public Grid valuesToGrid() {
        final Grid grid = new Grid("_values");
        for (final String key : this.values.keySet()) {
            final Value value = this.values.get(key);
            final ValueList column = ValueList.newList(value.getValueType(), 1);
            column.setValue(value, 0);
            try {
                grid.addColumn(key, column);
            }
            catch (Exception e) {
                Spit.out("Column " + key + " could not be added with its value " + value.getTextValue());
            }
        }
        return grid;
    }
    
    public String[] getTextValueList(final String possibleGridName, final String fieldName) {
        ValueList list = null;
        if (possibleGridName != null) {
            final Grid grid = this.getGrid(possibleGridName);
            if (grid != null) {
                list = grid.getColumn(fieldName);
            }
            if (list == null) {
                return null;
            }
            return list.getTextList();
        }
        else {
            list = this.getValueList(fieldName);
            if (list != null) {
                return list.getTextList();
            }
            final String textValue = this.getTextValue(fieldName, "");
            if (textValue == null || textValue.length() == 0) {
                return null;
            }
            return textValue.split(",");
        }
    }
}
