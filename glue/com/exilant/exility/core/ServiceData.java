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
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class ServiceData
{
    public Map<String, String> values;
    public Map<String, String[]> lists;
    public Map<String, String[][]> grids;
    public MessageList messageList;
    
    public ServiceData() {
        this.values = new HashMap<String, String>();
        this.lists = new HashMap<String, String[]>();
        this.grids = new HashMap<String, String[][]>();
        this.messageList = new MessageList();
    }
    
    public void addValue(final String name, final String value) {
        this.values.put(name, value);
    }
    
    public String getValue(final String name) {
        return this.values.get(name);
    }
    
    public boolean hasValue(final String name) {
        return this.values.containsKey(name);
    }
    
    public void removeValue(final String name) {
        this.values.remove(name);
    }
    
    public void addGrid(final String name, final String[][] grid) {
        this.grids.put(name, grid);
    }
    
    public boolean hasGrid(final String name) {
        return this.grids.containsKey(name);
    }
    
    public String[][] getGrid(final String name) {
        return this.grids.get(name);
    }
    
    public String[][] getFirstGrid() {
        if (this.grids.size() == 0) {
            return null;
        }
        final String name = this.grids.keySet().iterator().next();
        return this.grids.get(name);
    }
    
    public void removeGrid(final String name) {
        this.grids.remove(name);
    }
    
    public void addList(final String name, final String[] list) {
        this.lists.put(name, list);
    }
    
    public boolean hasList(final String name) {
        return this.lists.containsKey(name);
    }
    
    public String[] getList(final String name) {
        return this.lists.get(name);
    }
    
    public void removeList(final String name) {
        this.lists.remove(name);
    }
    
    String[] getFieldNames() {
        return this.keysToArray(this.values.keySet());
    }
    
    String[] getListNames() {
        return this.keysToArray(this.lists.keySet());
    }
    
    public String[] getGridNames() {
        return this.keysToArray(this.grids.keySet());
    }
    
    public int getErrorStatus() {
        switch (this.messageList.getSevirity()) {
            case UNDEFINED:
            case IGNORE:
            case INFO: {
                return 0;
            }
            case WARNING: {
                return 1;
            }
            default: {
                return 2;
            }
        }
    }
    
    private String[] keysToArray(final Set<String> keys) {
        final String[] keysArray = new String[keys.size()];
        int i = 0;
        for (final String key : keys) {
            keysArray[i++] = key;
        }
        return keysArray;
    }
    
    public void addMessage(final String messageId, final String... params) {
        this.messageList.addMessage(messageId, params);
    }
    
    public void extractData(final String inputText) throws ExilityException {
        final String[] tables = inputText.split("\u001c");
        boolean valuesFound = false;
        String[] array;
        for (int length = (array = tables).length, j = 0; j < length; ++j) {
            final String gridText = array[j];
            if (gridText.trim().length() != 0) {
                final String[] data = gridText.split("\u001d");
                if (data.length != 2) {
                    throw new ExilityException("Invalid data serialization: name-table has " + data.length + " parts.");
                }
                final String gridName = data[0];
                final String[] rows = data[1].split("\u001e", -1);
                final String[][] grid = new String[rows.length][];
                final String[] header = rows[0].split("\u001f", -1);
                grid[0] = header;
                for (int i = 1; i < grid.length; ++i) {
                    final String[] row = rows[i].split("\u001f", -1);
                    if (row.length != header.length) {
                        throw new ExilityException("Invalid data serialization: " + gridName + " does has different number of cells in its rows.");
                    }
                    grid[i] = row;
                }
                if (!valuesFound && gridName.equals("values")) {
                    valuesFound = true;
                    String[][] array2;
                    for (int length2 = (array2 = grid).length, k = 0; k < length2; ++k) {
                        final String[] pair = array2[k];
                        this.values.put(pair[0], pair[1]);
                    }
                }
                else {
                    this.addGrid(gridName, grid);
                }
            }
        }
    }
    
    public void extractData(final ServiceData inData) {
        if (inData.values.size() > 0) {
            for (final String key : inData.values.keySet()) {
                this.values.put(key, inData.getValue(key));
            }
        }
        if (inData.grids.size() > 0) {
            for (final String key : inData.grids.keySet()) {
                final String[][] grid = inData.grids.get(key);
                final int n = grid.length;
                final int m = grid[0].length;
                final String[][] newGrid = new String[n][];
                this.grids.put(key, newGrid);
                for (int i = 0; i < n; ++i) {
                    final String[] newRow = new String[m];
                    final String[] row = grid[i];
                    newGrid[i] = newRow;
                    for (int j = 0; j < m; ++j) {
                        newRow[j] = row[j];
                    }
                }
            }
        }
        if (inData.lists.size() > 0) {
            for (final String key : inData.lists.keySet()) {
                final String[] list = inData.lists.get(key);
                final String[] newList = new String[list.length];
                this.lists.put(key, newList);
                for (int k = 0; k < list.length; ++k) {
                    newList[k] = list[k];
                }
            }
        }
    }
    
    public String toSerializedData() {
        final StringBuilder sbf = new StringBuilder();
        sbf.append("values").append("\u001d");
        this.appendValues(sbf);
        if (this.messageList.size() > 0) {
            final String[][] msgs = this.messageList.toGrid();
            sbf.append("\u001c").append("_messages").append("\u001d");
            this.appendGrid(sbf, msgs);
        }
        for (final String key : this.grids.keySet()) {
            final String[][] grid = this.grids.get(key);
            if (grid.length > 0 && grid[0].length > 0) {
                sbf.append("\u001c").append(key).append("\u001d");
                this.appendGrid(sbf, grid);
            }
        }
        sbf.append("\u001c");
        return sbf.toString();
    }
    
    private void appendGrid(final StringBuilder sbf, final String[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            Spit.out("Grid has no data. Not added to buffer.");
            return;
        }
        boolean firstRow = true;
        for (final String[] row : grid) {
            if (firstRow) {
                firstRow = false;
            }
            else {
                sbf.append("\u001e");
            }
            boolean firstCol = true;
            String[] array;
            for (int length2 = (array = row).length, j = 0; j < length2; ++j) {
                final String col = array[j];
                if (firstCol) {
                    firstCol = false;
                }
                else {
                    sbf.append("\u001f");
                }
                sbf.append(col);
            }
        }
    }
    
    private void appendValues(final StringBuilder sbf) {
        sbf.append("name").append("\u001f").append("value").append("\u001e");
        final String success = this.messageList.hasError() ? "0" : "1";
        sbf.append("_success").append("\u001f").append(success);
        for (final String key : this.values.keySet()) {
            sbf.append("\u001e").append(key).append("\u001f").append(this.values.get(key));
        }
    }
    
    public boolean hasError() {
        return this.getErrorStatus() >= 3;
    }
    
    public String getMessages() {
        final String[] msgs = this.messageList.getMessageTexts();
        String msg = "";
        String[] array;
        for (int length = (array = msgs).length, i = 0; i < length; ++i) {
            final String m = array[i];
            msg = String.valueOf(msg) + '\n' + m;
        }
        return msg;
    }
}
