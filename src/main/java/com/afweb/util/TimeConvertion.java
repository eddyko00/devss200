package com.afweb.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class TimeConvertion {

    public TimeConvertion() {
    }

    public static Calendar getDefaultCalendar() {
        Calendar tcDate = getCalendar();
        tcDate.set(1970, 0, 1);  //1970
        return tcDate;
    }

    //2006-05-13 16:24:58.421
    public static Timestamp getDefaultTimeStamp() {
        Calendar tcDate = getDefaultCalendar();
        return new Timestamp(tcDate.getTimeInMillis());
    }

    private static Calendar getCalendar() {
        Calendar cDate = Calendar.getInstance();
        return cDate;
    }

    public static Calendar getCurrentCalendar() {
        Timestamp TS = new Timestamp(currentTimeMillis());
        Calendar cDate = Calendar.getInstance();
        cDate.setTimeInMillis(TS.getTime());
        return cDate;
    }

    public static Calendar getCurrentCalendar(long TimeMillis) {
        Timestamp TS = new Timestamp(TimeMillis);
        Calendar cDate = Calendar.getInstance();
        cDate.setTimeInMillis(TS.getTime());
        return cDate;
    }
    
    public static Timestamp getCurrentTimeStamp() {
        return new Timestamp(currentTimeMillis());
    }


    ///
    /// Main system time
    ///
    public static long currentTimeMillis() {
        ////////////////////////////////////////
        // setup time for simulation
        ////////////////////////////////////////
//        if (CKey.simulation == true) {
//            if (CKey.simCurrentDate != 0) {
//                return CKey.simCurrentDate;
//            }
//        }

        // normal flow
        Calendar cDate = getCalendar();
        return cDate.getTimeInMillis();
    }

    public void setDate(Timestamp fDate) {
        Calendar tcDate = getCalendar();
        tcDate.setTimeInMillis(fDate.getTime());
    }

    //2006-05-13 16:24:58.421
    public String getDate() {
        Calendar tcDate = getCalendar();
        String stdate = new Timestamp(tcDate.getTime().getTime()).toString();
        return stdate.substring(0, 10);
    }

    public String getDateTime() {
        Calendar tcDate = getCalendar();
        String stdate = new Timestamp(tcDate.getTime().getTime()).toString();
        int pos = stdate.indexOf(".");
        return stdate.substring(0, pos);
    }

    public static String getDate(Timestamp fDate) {
        return fDate.toString().substring(0, 10);
    }

    public static String getDateTime(Timestamp fDate) {
        String stDate = fDate.toString();
        int pos = stDate.indexOf(".");
        return stDate.substring(0, pos);
    }

    //2006-05-13
    public static Timestamp setDate(String stDate) {
        Calendar cDate = getDefaultCalendar();

        try {
            int yy, mm, dd;
            String[] dateArray = stDate.split(" ");
            String[] YYMMDD = dateArray[0].split("-");
            yy = Integer.parseInt(YYMMDD[0]);
            // month start with 0 for January
            mm = Integer.parseInt(YYMMDD[1]) - 1; // to set the month it start with 0 for jan
            dd = Integer.parseInt(YYMMDD[2]);
            cDate.set(yy, mm, dd);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return new Timestamp(cDate.getTimeInMillis());
    }

    //2006-05-13 16:24:58.421
    public static Timestamp setDateTime(String stDate) {
        Calendar cDate = getDefaultCalendar();
        try {
            int yy, mm, dd;
            String[] dateArray = stDate.split(" ");
            String[] YYMMDD = dateArray[0].split("-");
            if (YYMMDD.length == 1) {
                YYMMDD = dateArray[0].split("/");
            }
            yy = Integer.parseInt(YYMMDD[0]);
            // month start with 0 for January
            mm = Integer.parseInt(YYMMDD[1]) - 1; // to set the month it start with 0 for jan
            dd = Integer.parseInt(YYMMDD[2]);
            int h, m, s;
            String stHMSS = dateArray[1];

            int pos = dateArray[1].indexOf(".");
            if (pos != -1) {
                stHMSS = dateArray[1].substring(0, pos);
            }
            String[] HMS = stHMSS.split(":");
            h = Integer.parseInt(HMS[0]);
            m = Integer.parseInt(HMS[1]);
            s = Integer.parseInt(HMS[2]);

            cDate.set(yy, mm, dd, h, m, s);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Timestamp(cDate.getTimeInMillis());
    }

    //  5/13/2006 to 2006-05-13
    public static String getYahoo2Date(String stDate) {
        try {
            String[] YYMMDD = stDate.split("/");
            int mm = Integer.parseInt(YYMMDD[0]);
            int dd = Integer.parseInt(YYMMDD[1]);
            int yy = Integer.parseInt(YYMMDD[2]);
            return "" + yy + "-" + mm + "-" + dd;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String MonthArray[] = {
        " ", "JAN", "FEB", "MAR", "APR", "MAY",
        "JUN", "JUL", "AUG", "SEP", "OCT",
        "NOV", "DEC"
    };

    // Date Format (9-Jun-06) to 2006-05-13
    public static String getYahooHistorical2Date(String stDate) {
        try {
            if (stDate.length() == 0) {
                return null;
            }
            String[] strYYMMDD = stDate.split("-");
            int yy = Integer.parseInt(strYYMMDD[2]);
            int dd = Integer.parseInt(strYYMMDD[0]);
            if (yy < 1900) {
                if (yy > 60) {
                    yy += 1900;
                } else {
                    yy += 2000;
                }
            }
            int mm = 0;
            strYYMMDD[1] = strYYMMDD[1].toUpperCase();
            for (int i = 1; i <= 12; i++) {
                if (strYYMMDD[1].equals(MonthArray[i])) {
                    mm = i;
                    break;
                }
            }
            if (mm != 0) {
                return "" + yy + "-" + mm + "-" + dd;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
//http://www.koders.com/java/fid2C519CB34987730C8014A228942662C228599737.aspx

    /**
     * Returns the last millisecond of the specified date.
     *
     * @param date Date to calculate end of day from
     * @return Last millisecond of <code>date</code>
     */
    public static Date endOfDay(Date date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
            return calendar.getTime();
        }
    }

    /**
     * Returns a new Date with the hours, milliseconds, seconds and minutes
     * set to 0.
     *
     * @param date Date used in calculating start of day
     * @return Start of <code>date</code>
     */
    public static Date startOfDay(Date date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTime();
        }
    }

    /**
     * Returns day in millis with the hours, milliseconds, seconds and minutes
     * set to 0.
     *
     * @param date long used in calculating start of day
     * @return Start of <code>date</code>
     */
    public static long startOfDayInMillis(long date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the last millisecond of the specified date.
     *
     * @param date long to calculate end of day from
     * @return Last millisecond of <code>date</code>
     */
    public static long endOfDayInMillis(long date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MILLISECOND, 999);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the day after <code>date</code>.
     *
     * @param date Date used in calculating next day
     * @return Day after <code>date</code>.
     */
    public static Date nextDay(Date date) {
        return new Date(addDays(date.getTime(), 1));
    }

    /**
     * Adds <code>amount</code> days to <code>time</code> and returns
     * the resulting time.
     *
     * @param time Base time
     * @param amount Amount of increment.
     * 
     * @return the <var>time</var> + <var>amount</var> days
     */
    
    public static long addMonths(long time, int amount) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MONTH, amount);
            return calendar.getTimeInMillis();
        }
    }    
    public static long addDays(long time, int amount) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.DAY_OF_MONTH, amount);
            return calendar.getTimeInMillis();
        }
    }

    public static long addHours(long time, int amount) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.HOUR, amount);
            return calendar.getTimeInMillis();
        }
    }
    
    public static long addMinutes(long time, int amount) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MINUTE, amount);
            return calendar.getTimeInMillis();
        }
    }
    public static long addMiniSeconds(long time, int amount) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MILLISECOND, amount);
            return calendar.getTimeInMillis();
        }
    }    
    /**
     * Returns the day after <code>date</code>.
     *
     * @param date Date used in calculating next day
     * @return Day after <code>date</code>.
     */
    public static long nextDay(long date) {
        return addDays(date, 1);
    }

    /**
     * Returns the week after <code>date</code>.
     *
     * @param date Date used in calculating next week
     * @return week after <code>date</code>.
     */
    public static long nextWeek(long date) {
        return addDays(date, 7);
    }

    /**
     * Returns the number of days difference between <code>t1</code> and
     * <code>t2</code>.
     *
     * @param t1 Time 1
     * @param t2 Time 2
     * @param checkOverflow indicates whether to check for overflow
     * @return Number of days between <code>start</code> and <code>end</code>
     */
    public static int getDaysDiff(long t1, long t2, boolean checkOverflow) {
        if (t1 > t2) {
            long tmp = t1;
            t1 = t2;
            t2 = tmp;
        }
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(t1);
            int delta = 0;
            while (calendar.getTimeInMillis() < t2) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                delta++;
            }
            if (checkOverflow && (calendar.getTimeInMillis() > t2)) {
                delta--;
            }
            return delta;
        }
    }

    /**
     * Returns the number of days difference between <code>t1</code> and
     * <code>t2</code>.
     *
     * @param t1 Time 1
     * @param t2 Time 2
     * @return Number of days between <code>start</code> and <code>end</code>
     */
    public static int getDaysDiff(long t1, long t2) {
        return getDaysDiff(t1, t2, true);
    }

    /**
     * Check, whether the date passed in is the first day of the year.
     *
     * @param date date to check in millis
     * @return <code>true</code> if <var>date</var> corresponds to the first
     *         day of a year
     * @see Date#getTime() 
     */
    public static boolean isFirstOfYear(long date) {
        boolean ret = false;
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int currentYear = calendar.get(Calendar.YEAR);
            // Check yesterday
            calendar.add(Calendar.DATE, -1);
            int yesterdayYear = calendar.get(Calendar.YEAR);
            ret = (currentYear != yesterdayYear);
        }
        return ret;
    }

    /**
     * Check, whether the date passed in is the first day of the month.
     *
     * @param date date to check in millis
     * @return <code>true</code> if <var>date</var> corresponds to the first
     *         day of a month
     * @see Date#getTime() 
     */
    public static boolean isFirstOfMonth(long date) {
        boolean ret = false;
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int currentMonth = calendar.get(Calendar.MONTH);
            // Check yesterday
            calendar.add(Calendar.DATE, -1);
            int yesterdayMonth = calendar.get(Calendar.MONTH);
            ret = (currentMonth != yesterdayMonth);
        }
        return ret;
    }

    /**
     * Returns the day before <code>date</code>.
     *
     * @param date Date used in calculating previous day
     * @return Day before <code>date</code>.
     */
    public static long previousDay(long date) {
        return addDays(date, -1);
    }

    /**
     * Returns the week before <code>date</code>.
     *
     * @param date Date used in calculating previous week
     * @return week before <code>date</code>.
     */
    public static long previousWeek(long date) {
        return addDays(date, -7);
    }

    /**
     * Returns the first day before <code>date</code> that has the
     * day of week matching <code>startOfWeek</code>.  For example, if you
     * want to find the previous monday relative to <code>date</code> you
     * would call <code>getPreviousDay(date, Calendar.MONDAY)</code>.
     *
     * @param date Base date
     * @param startOfWeek Calendar constant correspoding to start of week.
     * @return start of week, return value will have 0 hours, 0 minutes,
     *         0 seconds and 0 ms.
     * 
     */
    public static long getPreviousDay(long date, int startOfWeek) {
        return getDay(date, startOfWeek, -1);
    }

    /**
     * Returns the first day after <code>date</code> that has the
     * day of week matching <code>startOfWeek</code>.  For example, if you
     * want to find the next monday relative to <code>date</code> you
     * would call <code>getPreviousDay(date, Calendar.MONDAY)</code>.
     *
     * @param date Base date
     * @param startOfWeek Calendar constant correspoding to start of week.
     * @return start of week, return value will have 0 hours, 0 minutes,
     *         0 seconds and 0 ms.
     * 
     */
    public static long getNextDay(long date, int startOfWeek) {
        return getDay(date, startOfWeek, 1);
    }

    private static long getDay(long date, int startOfWeek, int increment) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            // Normalize the view starting date to a week starting day
            while (day != startOfWeek) {
                calendar.add(Calendar.DATE, increment);
                day = calendar.get(Calendar.DAY_OF_WEEK);
            }
            return startOfDayInMillis(calendar.getTimeInMillis());
        }
    }

    /**
     * Returns the previous month.
     * 
     * @param date Base date
     * @return previous month
     */
    public static long getPreviousMonth(long date) {
        return incrementMonth(date, -1);
    }

    public static long getLastYear(long date) {
        return incrementMonth(date, -12);
    }

    public static long getLastMonth(long date, int iMonth) {
        return incrementMonth(date, -1*iMonth);
    }

    public static long getLastHalfYear(long date) {
        return incrementMonth(date, -6);
    }

    public static long getLast9Month(long date) {
        return incrementMonth(date, -9);
    }

    /**
     * Returns the next month.
     * 
     * @param date Base date
     * @return next month
     */
    public static long getNextMonth(long date) {
        return incrementMonth(date, 1);
    }

    private static long incrementMonth(long date, int increment) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            calendar.add(Calendar.MONTH, increment);
            return calendar.getTimeInMillis();
        }
    }

    /**
     * Returns the date corresponding to the start of the month.
     *
     * @param date Base date
     * @return Start of month.
     */
    public static long getStartOfMonth(long date) {
        return getMonth(date, -1);
    }

    /**
     * Returns the date corresponding to the end of the month.
     *
     * @param date Base date
     * @return End of month.
     */
    public static long getEndOfMonth(long date) {
        return getMonth(date, 1);
    }

    private static long getMonth(long date, int increment) {
        long result;
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            if (increment == -1) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                result = startOfDayInMillis(calendar.getTimeInMillis());
            } else {
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.add(Calendar.MILLISECOND, -1);
                result = calendar.getTimeInMillis();
            }
        }
        return result;
    }

    /**
     * Returns the day of the week.
     *
     * @param date date
     * @return day of week.
     */
    public static int getDayOfWeek(long date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            return (calendar.get(Calendar.DAY_OF_WEEK));
        }
    }

    public static int getDayOfMonth(long date) {
        Calendar calendar = getCalendar();
        synchronized (calendar) {
            calendar.setTimeInMillis(date);
            return (calendar.get(Calendar.DAY_OF_MONTH));
        }
    }


}
