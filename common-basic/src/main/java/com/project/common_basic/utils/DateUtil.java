package com.project.common_basic.utils;

import android.text.TextUtils;

import com.project.common_basic.utils.DateFormatSuit.DateFormatTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.project.common_basic.utils.DateFormatSuit.TEMPLATE1;
import static com.project.common_basic.utils.DateFormatSuit.TEMPLATE2;


/**
 * 时间工具类
 * <p>
 * Created by yamlee on 15/11/18.
 */
@SuppressWarnings("HardCodedStringLiteral")
public class DateUtil {
    private static long ONE_DAY_MILLI_SECONDS = 24 * 60 * 60 * 1000;

    /**
     * 获取指定格式的当前系统时间
     *
     * @param formatTemplate 时间格式模板, {@link DateFormatSuit}
     */
    public static String getNowDateStr(@DateFormatTemplate String formatTemplate) {
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        return simpleDateFormat.format(nowDate);
    }

    /**
     * 获取当前系统时间,返时间对象
     *
     * @return
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 将短时间格式时间转换为字符串
     */
    public static String dateToStr(Date date, @DateFormatTemplate String formatTemplate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        return formatter.format(date);
    }

    /**
     * 将时间字符串转换为时间
     */
    public static Date strToDate(String time, @DateFormatTemplate String formatTemplate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(time, pos);
    }

    /**
     * 将string类型时间转换为long型时间
     *
     * @param time           时间
     * @param formatTemplate 时间对应的格式模板,{@link DateFormatSuit}
     */
    public static long strToLong(String time, @DateFormatTemplate String formatTemplate) {
        return strToDate(time, formatTemplate).getTime();
    }

    /**
     * 将long型时间转换为Date对象
     *
     * @param time           long型时间
     * @param formatTemplate 需要转换
     */
    public static Date longToDate(long time, @DateFormatTemplate String formatTemplate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        String formatTime = formatter.format(new Date(time));
        // 转换为标准时间对象
        return strToDate(formatTime, TEMPLATE2);
    }

    /**
     * 将long型时间转换为Date对象
     *
     * @param time long型时间
     */
    public static Date longToDate(long time) {
        return new Date(time);
    }

    /**
     * 将long型时间转换为string类型时间
     *
     * @param time           long型时间
     * @param formatTemplate 时间格式模板
     */
    public static String longToStr(long time, @DateFormatTemplate String formatTemplate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        return formatter.format(new Date(time));
    }

    /**
     * 将string类型时间转换为另一格式的string类型时间
     *
     * @param time               string类型时间
     * @param fromFormatTemplate 当前的时间格式
     * @param toFormatTemplate   转换的另一时间格式
     */
    public static String transferFormat(String time,
                                        @DateFormatTemplate String fromFormatTemplate,
                                        @DateFormatTemplate String toFormatTemplate) {
        Date date = strToDate(time, fromFormatTemplate);
        return dateToStr(date, toFormatTemplate);
    }


    /**
     * 获取{@code monthCount}个月后的今天日期
     *
     * @param dateStr      当前string类型的日期
     * @param timeTemplate 日期模板
     * @param monthCount   月数
     */
    public static String getNextMonthOfDay(String dateStr,
                                           @DateFormatTemplate String timeTemplate,
                                           int monthCount) {
        Date date = strToDate(dateStr, timeTemplate);
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.MONTH, monthCount);
        long timeInMillis = calendar.getTimeInMillis();
        return longToStr(timeInMillis, TEMPLATE1);
    }

    /**
     * 获取{@code monthCount}个月后的今天日期
     *
     * @param currentTimeMills 当前的时间毫秒数
     * @param monthCount       月数
     */
    public static long getNextMonthOfDayToLong(long currentTimeMills,
                                               int monthCount) {
        Date date = longToDate(currentTimeMills);
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.MONTH, monthCount);
        return calendar.getTimeInMillis();
    }


    /**
     * 返回指定时间之后的日期
     *
     * @param currentLongTime 当前的时间
     * @param timeCount       需要增加的数量
     * @param dateUnit        单位是年,天,或月
     */
    public static long getNextDay(long currentLongTime, int timeCount,
                                  @DateUnit.DateUnitDef String dateUnit) {
        Date date = longToDate(currentLongTime);
        Calendar calendar = getCalendar(date);
        if (DateUnit.DAYS.equals(dateUnit)) {
            return getNextDayLong(date, timeCount);
        } else if (DateUnit.MONTHS.equals(dateUnit)) {
            calendar.add(Calendar.MONTH, timeCount);
            return calendar.getTimeInMillis();
        } else if (DateUnit.YEARS.equals(dateUnit)) {
            calendar.add(Calendar.YEAR, timeCount);
            return calendar.getTimeInMillis();
        }
        return 0;
    }


    /**
     * 得到一个时间延后或前移几天的时间,最后返回string类型时间
     *
     * @param date         当前时间,string类型
     * @param delay        当前日期前或后的位移天数,后移为正数,前移为负数
     * @param timeTemplate 当前时间的格式,返回结果也是此格式
     * @return 返回string类型
     */
    public static String getNextDayString(String date, long delay,
                                          @DateFormatTemplate String timeTemplate) {
        try {
            long nextDayLong = getNextDayLong(date, delay, timeTemplate);
            SimpleDateFormat format = new SimpleDateFormat(timeTemplate, Locale.CHINA);
            Date tmpDate = new Date();
            tmpDate.setTime(nextDayLong);
            return format.format(tmpDate);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 得到一个时间延后或前移几天的时间,最后返回long型时间
     *
     * @param date               string类型时间
     * @param delay              当前日期前或后的位移天数,后移为正数,前移为负数
     * @param timeFormatTemplate 当前时间的格式
     * @return 时间数long型
     */
    public static long getNextDayLong(String date, long delay,
                                      @DateFormatTemplate String timeFormatTemplate) {
        try {
            Date d = strToDate(date, timeFormatTemplate);
            return getNextDayLong(d, delay);
        } catch (Exception e) {
            return getNextDayLong(getNowDate(), delay);
        }
    }

    /**
     * 得到一个时间延后或前移几天的时间,最后返回long型时间
     *
     * @param date  Date类型时间
     * @param delay 当前日期前或后的位移天数,后移为正数,前移为负数
     * @return 时间数long型
     */
    public static long getNextDayLong(Date date, long delay) {
        //delay是int可能会超出字节数,转为long型
        return date.getTime() + delay * ONE_DAY_MILLI_SECONDS;
    }

    /**
     * 得到一个时间延后或前移几天的时间,最后返回long型时间
     *
     * @param date  long类型时间
     * @param delay 当前日期前或后的位移天数,后移为正数,前移为负数
     * @return 时间数long型
     */
    public static long getNextDayLong(long date, long delay) {
        //delay是int可能会超出字节数,转为long型
        return date + delay * ONE_DAY_MILLI_SECONDS;
    }

    /**
     * 将当前时间转化为当天最小的时间,如:当前时间2015-12-14 21:12:30转化为2015-12-14 00:00:00
     *
     * @return 2015-12-14 00:00:00类型的时间
     */
    public static String strToDayMinTime(String dateStr, @DateFormatTemplate String dateFormatTemplate) {
        String result = transferFormat(dateStr, dateFormatTemplate, DateFormatSuit.TEMPLATE2);
        return result + " 00:00:00";

    }

    /**
     * 将当前long时间转化为当天最小的long型时间,如:当前时间2015-12-14 21:12:30 转化为 2015-12-14 00:00:00
     */
    public static long longToDayMinLong(long timeLong) {
        String str = longToStr(timeLong, TEMPLATE1);
        return strToDayMinTimeLong(str, TEMPLATE1);
    }

    /**
     * 将当前时间转化为当天最小的long型时间,如:当前时间2015-12-14 21:12:30 转化为 2015-12-14 00:00:00
     */
    public static long strToDayMinTimeLong(String dateStr, @DateFormatTemplate String dateFormatTemplate) {
        String result = strToDayMinTime(dateStr, dateFormatTemplate);
        return strToLong(result, DateFormatSuit.TEMPLATE1);
    }


    /**
     * 将当前时间转化为当天最大的时间,如:当前时间2015-12-14 21:12:30转化为2015-12-14 23:59:59
     *
     * @return 2015-12-14 23:59:59字符类型的时间
     */
    public static String strToDayMaxTime(String dateStr, @DateFormatTemplate String dateFormatTemplate) {
        String result = transferFormat(dateStr, dateFormatTemplate, DateFormatSuit.TEMPLATE2);
        return result + " 23:59:59";

    }

    /**
     * 将当前时间转化为当天最大的long型时间,如:当前时间2015-12-14 21:12:30 转化为 2015-12-14 23:59:59
     */
    public static long strToDayMaxTimeLong(String dateStr, @DateFormatTemplate String dateFormatTemplate) {
        String result = strToDayMaxTime(dateStr, dateFormatTemplate);
        return strToLong(result, DateFormatSuit.TEMPLATE1);
    }


    /**
     * 将当前long时间转化为当天最大的long型时间,如:当前时间2015-12-14 21:12:30 转化为 2015-12-14 23:59:59
     */
    public static long longToDayMaxLong(long timeLong) {
        String str = longToStr(timeLong, TEMPLATE1);
        return strToDayMaxTimeLong(str, TEMPLATE1);
    }

    /**
     * 得到两个日期之间所有的月份列表
     *
     * @param p_start 开始时间
     * @param p_end   结束时间
     * @return 时间列表
     */
    public static List<Date> getDateMonthList(Calendar p_start, Calendar p_end) {
        List<Date> result = new ArrayList<>();
        Calendar temp = (Calendar) p_start.clone();
        while (temp.before(p_end) || temp.get(Calendar.MONTH) == p_end.get(Calendar.MONTH)) {
            result.add(temp.getTime());
            temp.add(Calendar.MONTH, 1);
        }
        return result;
    }


    /**
     * 获取当前时间为第几天
     *
     * @param time     当前字符串时间
     * @param template 时间格式模板
     */
    public static int getDayOfTime(String time, @DateFormatTemplate String template) {
        return getDayOfTime(strToDate(time, template));
    }

    /**
     * 获取当前时间为第几天
     */
    public static int getDayOfTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getDayOfTime(calendar);
    }

    /**
     * 获取当前时间为第几天
     *
     * @param calendar 时间日历
     */
    public static int getDayOfTime(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    /**
     * 获取当前时间为小时
     *
     * @param calendar 时间日历
     */
    public static int getHourOfDay(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前时间为分
     *
     * @param calendar 时间日历
     */
    public static int getMinuteOfTime(Calendar calendar) {
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取指定日期 月份最后一天
     *
     * @param date 指定日期
     */
    public static long getMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime().getTime();
    }

    /**
     * 获取指定时间的月份
     */
    public static int getMonthOfTime(String time, @DateFormatTemplate String formatTemplate) {
        return getMonthOfTime(strToDate(time, formatTemplate));
    }

    /**
     * 获取指定时间的月份
     */
    public static int getYearOfTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getYearOfTime(calendar);
    }

    /**
     * 获取指定时间的月份
     */
    public static int getMonthOfTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getMonthOfTime(calendar);
    }

    /**
     * 获取指定时间的月份
     */
    public static int getMonthOfTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMonthOfTime(calendar);
    }


    /**
     * 获取当前时间为第几个月
     */
    public static int getMonthOfTime(Calendar calendar) {
        //月份从0计数
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定时间的年份
     */
    public static int getYearOfTime(String time, @DateFormatTemplate String template) {
        return getYearOfTime(strToDate(time, template));
    }

    /**
     * 获取指定时间的年份
     */
    public static int getYearOfTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getYearOfTime(calendar);
    }

    /**
     * 获取当前时间的年数
     */
    public static int getYearOfTime(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }


    /**
     * 将date对象转化为calendar对象
     */
    public static Calendar getCalendar(Date modelDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(modelDate);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return calendar;
    }

    /**
     * 根据年,月,日获取calendar对象
     */
    public static Calendar getCalendar(int year, int monthOfYear, int dayOfMonth) {
        //获取一个日历对象
        Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
        //修改日历控件的年，月，日
        //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
        dateAndTime.set(Calendar.YEAR, year);
        dateAndTime.set(Calendar.MONTH, monthOfYear);
        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return dateAndTime;
    }

    /**
     * 计算两个日期相差的天数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     */
    public static float getDaysOfDates(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return (endTime - startTime) / ONE_DAY_MILLI_SECONDS;
    }

    /**
     * 计算两个日期相差的天数
     *
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param formatTemplate string类型时间格式模板
     */
    public static float getDaysOfDates(String startTime, String endTime,
                                       @DateFormatTemplate String formatTemplate) {
        if (startTime == null || startTime.equals("") || endTime == null || endTime.equals("")) {
            return 0;
        }
        Date startDate = strToDate(startTime, formatTemplate);
        Date endDate = strToDate(endTime, formatTemplate);
        return getDaysOfDates(startDate, endDate);
    }

    /**
     * 返回当前年份和月份的天数
     *
     * @param year  年份
     * @param month 月份
     * @return 天数
     */
    public static int calculateDays(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 时间比较大小
     *
     * @param time1          时间
     * @param time2          时间
     * @param formatTemplate 时间格式
     * @return 0表示两个时间相等, 1 表示time1大于time2, -1 表示time1小于time2
     */
    public static int compareTime(String time1, String time2,
                                  @DateFormatTemplate String formatTemplate) {
        DateFormat df = new SimpleDateFormat(formatTemplate, Locale.CHINA);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(df.parse(time1));
            c2.setTime(df.parse(time2));
        } catch (ParseException e) {
            System.err.println("格式不正确");
        }
        return c1.compareTo(c2);
    }

    /**
     * 将时间转换为距离当前时间的描述性语言,如"?天前","刚刚","?小时前"
     *
     * @param timestamp   需要转换的时间
     * @param showMinHour 是否显示小时,分钟,秒信息
     * @param serverTime  当前正确的时间(服务器时间)
     * @return
     */
    public static String convertDateToDescStr(String timestamp, boolean showMinHour, String serverTime) {
        String timeDes = "";
        try {
            Date now;
            SimpleDateFormat df;
            if (showMinHour) {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            }
            if (TextUtils.isEmpty(serverTime)) {
                now = new Date(System.currentTimeMillis());
            } else {
                now = df.parse(serverTime);
            }
            Date modelDate = df.parse(timestamp);
            Calendar nowCalendar = Calendar.getInstance();
            Calendar modelCalendar = getCalendar(modelDate);
            int dayX = nowCalendar.get(Calendar.DAY_OF_YEAR) - modelCalendar.get(Calendar.DAY_OF_YEAR);//DateUtil.getDayOfTime(nowCalendar) - DateUtil.getDayOfTime(modelCalendar);
            int yearX = getYearOfTime(nowCalendar) - getYearOfTime(modelCalendar);
            int monthX = getMonthOfTime(nowCalendar) - getMonthOfTime(modelCalendar);
            //仅对1天以内的时间做显示的特殊处理
            if (dayX < 1 && yearX == 0 && monthX == 0) {
                if (showMinHour) {
                    long l = now.getTime() - modelDate.getTime();
                    long day = l / (24 * 60 * 60 * 1000);
                    long hour = (l / (60 * 60 * 1000) - day * 24);
                    long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                    if (hour < 1) {
                        if (min == 0) {
                            timeDes = "刚刚";
                        } else {
                            timeDes = min + "分钟前";
                        }
                    } else {
                        timeDes = hour + "小时前";
                    }
                } else {
                    timeDes = "今天";
                }
            } else if (dayX == 1 && yearX == 0) {
                timeDes = "昨天";
            } else if (yearX == 0) {
//                timeDes = DateUtil.getMonthOfTime(modelCalendar) + "月" + DateUtil.getDayOfTime(modelCalendar) + "日  周" + DateUtil.getDayOfWeekCustom(modelCalendar);
                timeDes = getMonthOfTime(modelCalendar) + "月" + getDayOfTime(modelCalendar) + "日";
            } else {
                timeDes = getYearOfTime(modelCalendar) + "年" +
                        getMonthOfTime(modelCalendar) + "月" + getDayOfTime(modelCalendar) + "日";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDes;
    }

    public static boolean before(String startTime, @DateFormatTemplate String startFormatTemplate, String endTime, @DateFormatTemplate String endFormatTemplate) {
        Date startDate = strToDate(startTime, startFormatTemplate);
        Date endDate = strToDate(endTime, endFormatTemplate);
        return startDate.before(endDate);
    }
}
