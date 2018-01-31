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

public class Util
{
    public static String[][] namesToGrid(final String[] names, final String header) {
        final String[][] grid = new String[names.length + 1][];
        final String[] hdr = { header };
        grid[0] = hdr;
        for (int i = 0; i < names.length; ++i) {
            final String[] row = { names[i] };
            grid[i + 1] = row;
        }
        return grid;
    }
    
    public static void sortGrid(final String[][] grid, final String columnToSort, final boolean sortDescending) {
        final int nbrRows = grid.length;
        if (nbrRows <= 1) {
            Spit.out("No data to sort on column " + columnToSort);
            return;
        }
        final String[] header = grid[0];
        final int nbrCols = header.length;
        int colIdx = -1;
        for (int j = 0; j < nbrCols; ++j) {
            if (header[j].equalsIgnoreCase(columnToSort)) {
                colIdx = j;
                break;
            }
        }
        if (colIdx == -1) {
            Spit.out("Design Error: " + columnToSort + " is not a column in grid that is supplied. Grid is Not Sorted.");
        }
        final DataValueType valueType = DataDictionary.getValueType(columnToSort);
        final Value[] values = new Value[nbrRows];
        for (int i = 1; i < nbrRows; ++i) {
            final String colValue = grid[i][colIdx];
            if (colValue == null || colValue.length() == 0) {
                values[i] = null;
            }
            else {
                values[i] = Value.newValue(colValue, valueType);
            }
        }
        int startAt = 1;
        int endAt = nbrRows;
        int incr = 1;
        if (sortDescending) {
            startAt = nbrRows - 1;
            endAt = 0;
            incr = -1;
        }
        for (int k = startAt; k != endAt; k += incr) {
            Value min = values[k];
            if (min != null) {
                int idx = k;
                for (int l = k + incr; l != endAt; l += incr) {
                    final Value value = values[l];
                    if (value == null) {
                        min = value;
                        idx = l;
                        break;
                    }
                    if (value.lessThan(min).getBooleanValue()) {
                        min = value;
                        idx = l;
                    }
                }
                if (idx != k) {
                    values[idx] = values[k];
                    values[k] = min;
                    final String[] row = grid[k];
                    grid[k] = grid[idx];
                    grid[idx] = row;
                }
            }
        }
    }
    
    public static int getColumnIndex(final String[][] grid, final String columnName) {
        final String[] header = grid[0];
        for (int j = 0; j < header.length; ++j) {
            if (header[j].equalsIgnoreCase(columnName)) {
                return j;
            }
        }
        Spit.out("Design Error : " + columnName + " is not column in the data grid.");
        return -1;
    }
    
    public static int getMatchingRow(final String[][] grid, final String columnName, final String value) {
        final int idx = getColumnIndex(grid, columnName);
        if (idx == -1) {
            return -1;
        }
        for (int i = 1; i < grid.length; ++i) {
            if (grid[i][idx].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;
    }
    
    public static String[] toTextArray(final String text, final String delimiter) {
        if (text == null || text.length() == 0) {
            return new String[0];
        }
        final String[] arr = text.split(delimiter);
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = arr[i].trim();
        }
        return arr;
    }
    
    public static String arrayToText(final String[] values, final String delimiter) {
        final StringBuilder sbf = new StringBuilder();
        for (final String value : values) {
            sbf.append(value).append(delimiter);
        }
        sbf.deleteCharAt(sbf.length() - 1);
        return sbf.toString();
    }
    
    public static String arrayToText(final long[] values, final String delimiter) {
        final StringBuilder sbf = new StringBuilder();
        for (final long value : values) {
            sbf.append(value).append(delimiter);
        }
        sbf.deleteCharAt(sbf.length() - 1);
        return sbf.toString();
    }
    
    public static long[] toIntegerArray(final String text, final String delimiter) {
        if (text == null || text.length() == 0) {
            return new long[0];
        }
        final String[] textArray = text.split(delimiter);
        final long[] arr = new long[textArray.length];
        for (int i = 0; i < arr.length; ++i) {
            try {
                arr[i] = Long.parseLong(textArray[i]);
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return arr;
    }
}
