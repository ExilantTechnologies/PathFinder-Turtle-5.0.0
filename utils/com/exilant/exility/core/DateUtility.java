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

import java.util.Calendar;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DateUtility
{
    public static final String SERVER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String SERVER_DATE_TIME_FORMAT_WITH_NO_MS = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormatter;
    private static final SimpleDateFormat dateTimeFormatter;
    private static final SimpleDateFormat dateTimeFormatterShort;
    public static final long MILLIS_IN_A_DAY = 86400000L;
    
    static {
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateTimeFormatterShort = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateUtility.dateFormatter.setLenient(false);
    }
    
    public static Date parseDate(final String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            final int n = value.length();
            if (n == "yyyy-MM-dd".length()) {
                return DateUtility.dateFormatter.parse(value);
            }
            if (n == "yyyy-MM-dd HH:mm:ss".length()) {
                return DateUtility.dateTimeFormatterShort.parse(value);
            }
            if (n == "yyyy-MM-dd HH:mm:ss.SSS".length()) {
                return DateUtility.dateTimeFormatter.parse(value);
            }
            return DateUtility.dateFormatter.parse(value);
        }
        catch (ParseException ex) {
            final Date d = parseShortcut(value);
            if (d == null) {
                Spit.out("ERROR " + value + " is not a valid date. You may get null related issue in your transaction.");
            }
            return d;
        }
    }
    
    private static Date parseShortcut(final String value) {
        if (value.equals(".")) {
            return new Date();
        }
        final String upperValue = value.toUpperCase();
        if (upperValue.equals("SYSDATE") || upperValue.equals("SYSTEMDATE")) {
            return new Date();
        }
        try {
            final int i = Integer.parseInt(value);
            return addDays(null, i);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static Date parseDateTime(final String value) {
        try {
            final int n = value.length();
            if (n > "yyyy-MM-dd HH:mm:ss".length()) {
                return DateUtility.dateTimeFormatter.parse(value);
            }
            if (n == "yyyy-MM-dd HH:mm:ss".length()) {
                return DateUtility.dateTimeFormatterShort.parse(value);
            }
            return DateUtility.dateFormatter.parse(value);
        }
        catch (ParseException e) {
            final Date d = parseShortcut(value);
            if (d == null) {
                Spit.out("ERROR: " + value + " is not a valid date-time. You may get null related issues in your transactions.");
            }
            return d;
        }
    }
    
    public static String formatDate(final Date datePossiblyWithTime) {
        if (datePossiblyWithTime == null) {
            return "";
        }
        final Calendar c = Calendar.getInstance();
        c.setTime(datePossiblyWithTime);
        if (c.get(14) > 0 || c.get(13) > 0 || c.get(12) > 0 || c.get(11) > 0) {
            return DateUtility.dateTimeFormatter.format(datePossiblyWithTime);
        }
        return DateUtility.dateFormatter.format(datePossiblyWithTime);
    }
    
    public static String formatDateTime(final Date dateWithTime) {
        if (dateWithTime == null) {
            return "";
        }
        return DateUtility.dateTimeFormatter.format(dateWithTime);
    }
    
    public static int subtractDates(final Date fromDate, final Date toDate) {
        final long ms = fromDate.getTime() - toDate.getTime();
        return (int)(ms / 86400000L);
    }
    
    public static Date getToday() {
        final Calendar c = Calendar.getInstance();
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date addDays(final Date date, final long days) {
        return addDays(date, (int)days);
    }
    
    public static Date addDays(final Date date, final int days) {
        final Calendar d1 = Calendar.getInstance();
        if (date != null) {
            d1.setTime(date);
        }
        d1.add(6, days);
        return d1.getTime();
    }
    
    public static Date getNow() {
        return new Date();
    }
}
