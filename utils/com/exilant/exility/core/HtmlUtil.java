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

public class HtmlUtil
{
    public static String rowsToTable(final String[][] rows) {
        final StringBuilder s = new StringBuilder();
        if (rows == null || rows.length == 0) {
            return "<div>No data</div>";
        }
        s.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"3\"><thead><tr>");
        final String[] header = rows[0];
        String[] array;
        for (int length = (array = header).length, j = 0; j < length; ++j) {
            final String cell = array[j];
            s.append("<th>").append(cell).append("</th>");
        }
        s.append("</tr></thead><tbody>");
        for (int i = 1; i < rows.length; ++i) {
            s.append("<tr>");
            String[] array2;
            for (int length2 = (array2 = rows[i]).length, k = 0; k < length2; ++k) {
                final String cell2 = array2[k];
                s.append("<td>").append(cell2).append("</td>");
            }
        }
        s.append("</tbody></table>");
        return s.toString();
    }
}
