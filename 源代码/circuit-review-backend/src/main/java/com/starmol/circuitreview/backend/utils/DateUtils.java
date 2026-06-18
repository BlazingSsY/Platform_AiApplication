package com.starmol.circuitreview.backend.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private final static String[] CN_Digits = { "〇", "一", "二", "三", "四", "五",
            "六", "七", "八", "九", "十" };
    public static final long daySpan = 24 * 60 * 60 * 1000;
    /**yyyy-MM-dd HH:mm:ss*/
    public static final String TIME_FORMAT_NORMAL = "yyyy-MM-dd HH:mm:ss";
    /**yyyy-MM-dd*/
    public static final String DATE_FORMAT_NORMAL = "yyyy-MM-dd";
    /**yyyy.MM.dd*/
    public static final String DATE_FORMAT_DOT = "yyyy.MM.dd";
    /**yyyyMMdd*/
    public static final String DATE_FORMAT_NO_MINUS = "yyyyMMdd";
    /**yyMMdd*/
    public static final String DATE_FORMAT_NO_MINUS_SHORT = "yyMMdd";
    /**yyyy-MM*/
    public static final String MONTH_FORMAT_NORMAL = "yyyy-MM";
    /**MM-dd*/
    public static final String MONTH_DAY_FORMAT = "MM-dd";
    /**yyyyMMdd*/
    public static final String DATE_FORMAT_SHORT = "yyyyMMdd";
    /**yyyyMM*/
    public static final String MONTH_FORMAT = "yyyyMM";
    /**yyyy.MM*/
    public static final String MONTH_FORMAT_DOT = "yyyy.MM";
    /**yyyyMMddHHmm*/
    public static final String DATE_FORMAT_MINUTE = "yyyyMMddHHmm";
    /**yyyyMMddHHmmss*/
    private static final String TIME_FORMAT_SHORT = "yyyyMMddHHmmss";
    /**MM/dd/yyyy HH:mm:ss**/
    public static final String MONTH_DAY_YEAR_HOUR_MINUTE  ="MM/dd/yyyy HH:mm:ss";

    /**
     * 判断参数year、month、day能否组成一个合法的日期。
     *
     * @param month
     *            月份，合法月份范围是 1-12
     * @param day
     *            日数
     * @param year
     *            年份，必须大于1900
     * @return
     */
    public static boolean isDate(int month, int day, int year) {
        if (year < 1900) {
            return false;
        }
        if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }

        // 判断年份是否为闰年
        @SuppressWarnings("unused")
        boolean leapYear = isLeapYear(year);
        // 获得该年该月的最大日期
        int maxD = getMaxDay(year, month);
        if (day > maxD) {
            return false;
        }

        return true;
    }

    /**
     * 给定一个年份和月份，可以得到该月的最大日期。 例如 2009年1月，最大日期为31。
     *
     * @param year
     *            年份，必须大于1900
     * @param month
     *            月份，合法月份范围是 1-12
     * @return
     */
    public static int getMaxDay(int year, int month) {
        if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
            return 30;
        }
        if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        }
        return 31;
    }

    /**
     * 判断年份是否为闰年。
     *
     * @param year
     *            年份，必须大于1900
     * @return
     */
    public static boolean isLeapYear(int year) {
        boolean leapYear = ((year % 400) == 0);
        if (!leapYear) {
            leapYear = ((year % 4) == 0) && ((year % 100) != 0);
        }
        return leapYear;
    }


    public static String getCurrentStringTime(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        String formatDate=format.format(date);
        return formatDate;
    }

    public static String getCurrentTimeSecond(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate=format.format(date);
        return formatDate;
    }
    /**
     * yyyy-MM-dd HH:mm:ss格式串转换为日期
     * @return Date日期
     */
    public static Date parseDate(String formatDate){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        try {
            date = format.parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String getCurrentTimeMilliSecond(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String formatDate=format.format(date);
        return formatDate;
    }

    public static String getCurrentMonth(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM");
        String formatDate=format.format(date);
        return formatDate;
    }
    /**
     * 获取当前日期（格式为20110802）
     * @return
     */
    public static String getCurrentDay(){
        Date date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        String formatDate=format.format(date);
        return formatDate;
    }
    /**
     * 获取当前时间
     * @param format 时间格式，例如：yyyy-MM-dd
     * @param count 增加或减少的天数
     * @return
     */
    public static String getCurrentDate(String format,Integer count) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, count);//增加或减少的天数
        String currentDate = df.format(cal.getTime());
        return currentDate;
    }

    /**
     * 增加月份后的日期数
     * @return
     */
    public static String getDateAddMoney(String dateStr, int m) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = df.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, m);
            return df.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取下个月的第一天
     * @param format
     * @return
     */
    public static String getNextMonthFirstDay(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);// 当前月＋1，即下个月
        cal.set(Calendar.DATE, 1);// 将下个月1号作为日期初始值
        String currentMonth = df.format(cal.getTime());
        return currentMonth;
    }

    /**
     * 获取下个月的最后一天
     * @param format
     * @return
     */
    public static String getNextMonthLastDay(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);// 将下2个月1号作为日期初始值
        cal.add(Calendar.MONTH, 2);// 当前月＋2，即下2个月
        cal.add(Calendar.DATE, -1);// 下2个月1号减去一天，即得到下1一个月最后一天
        String currentMonth = df.format(cal.getTime());
        return currentMonth;
    }

    public static String format(Date date) {
        if(null==date){
            return null;
        }
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate=format.format(date);
        return formatDate;
    }

    public static String format(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        String formatDate=format.format(date);
        return formatDate;
    }

    public static String format4Null(Date date, String formatStr) {
        if(date == null) {
            return null;
        } else {
            return format(date, formatStr);
        }
    }

    /**
     * 得到2个字符串日期之间的日期差,返回结果以秒为单位
     * @param beginTime
     * @param endTime
     * @return
     * @throws ParseException
     */
    public static Long getOffTime(String beginTime, String endTime)  {
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            Date begin = dfs.parse(beginTime);
            Date end = dfs.parse(endTime);
            long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
            return between;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getYesterday(){
        return getCurrentDate("yyyy-MM-dd",-1)+" 00:00:00";
    }

    public static String getToday(){
        return getCurrentDate("yyyy-MM-dd",0)+" 00:00:00";
    }

    /**
     * 计算两个日期之间相差的月数
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 两个日期之间相差的月数
     */
    public static int dateIntervalMonth(Date startDate, Date endDate) {

        int intervalMonth = 0;
        try {
            if(startDate==null || endDate==null){
                return intervalMonth;
            }
            Calendar starCal = Calendar.getInstance();
            starCal.setTime(startDate);

            int sYear = starCal.get(Calendar.YEAR);
            int sMonth = starCal.get(Calendar.MONTH);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate);
            int eYear = endCal.get(Calendar.YEAR);
            int eMonth = endCal.get(Calendar.MONTH);

            intervalMonth = ((eYear - sYear) * 12 + (eMonth - sMonth));
        } catch (Exception e) {
            intervalMonth = 0;
        }

        return intervalMonth;
    }

    /***
     * 根据所选格式将字符串转换为日期类型
     * @param dateStr  待转换的字符串
     * @param formateRule 转换格式
     * @return 日期类型结果
     */
    public static Date convertAsDate(String dateStr, String formateRule) {
        DateFormat fmt = new SimpleDateFormat(formateRule);
        try {
            if(dateStr == null) {
                return null;
            }
            return fmt.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }


    public static SimpleDateFormat newTimeFormater(){
        return new SimpleDateFormat(TIME_FORMAT_NORMAL);
    }
    /****
     * 计算时间差，返回秒
     * @param start
     * @param end
     * @return
     */
    public static long timeDifference(long start,long end){
        return (end-start)/1000;
    }


    /* 判断输入的字符串是否是合法的生日 生日不能大于当前日期,支持  yyyy-MM-dd ,yyyyMMdd  MM-dd-yyyy  ,yyyy年MM月dd日等
     * @param birthday 一个日期格式的字符串
     * @param formats  期望转换后的日期格式数组
     * @return
     * */
    public static boolean isRightDate(String birthday,String[] formats){

        //记录传入的日期字符串，转换成日期类型
        Date birth=null;

        //判断格式是否正确，默认值为false
        boolean isRight=false;
        for(String f:formats){
            try {
                birth =new SimpleDateFormat(f).parse(birthday);
                //校验日期转换后是和传入的值不相同，说明日期传入有问题
                if(!new SimpleDateFormat(f).format(birth).equals(birthday)){
                    return false;
                }
                isRight=true;
                break;
            } catch (ParseException e) {}
        }

        if(isRight){
            //获取当前日期的毫秒数
            long now =System.currentTimeMillis();
            //获取生日的毫秒数
            long birthTime = birth.getTime();
            //如果当前时间小于生日，生日不合法。反之合法
            return birthTime<=now;
        }else{
            //输入的参数类型不是日期类型，或者类型和过滤中设置的类型不匹配
            return false;
        }
    }
    /***
     * 计算时间差，返回天数
     * @param b_date 大时间
     * @param s_date 小时间
     * @return 天数
     */
    public static long dateSubtract(Date b_date,Date s_date){
        if((null == s_date) || (null == b_date)){
            return 0;
        }else{
            long dateLong1 = b_date.getTime();
            long dateLong2 = s_date.getTime();
            long day = (dateLong1-dateLong2)/1000/60/60/24;
            return day;
        }
    }

}
