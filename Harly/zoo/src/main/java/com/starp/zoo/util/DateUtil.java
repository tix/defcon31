package com.starp.zoo.util;

import com.starp.zoo.constant.DateConstant;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @Author vic
 * @Date 18:12 2018/12/18
 * @param
 * @return
 **/
@Slf4j
public class DateUtil {

	private static final String  DF = "yyyy-MM-dd HH:mm:ss";
    private static final String DAY_DF = "yyyy-MM-dd";
    private static final String MON_DAY_FORMAT = "MMdd";
    private static final String HOUR_FORMAT = "yyyy-MM-dd HH";
    private static final String FULL_FORMAT = "yyyy-MM-dd-HH-mm-ss";
    private static final String HOUR_FORMAT_HH = "yyyy-MM-dd-HH";
    private static final String MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String MONTH_DF = "yyyy-MM";
	private static final String YEAR_MONTH = "yyyyMM";
    private static final String DAY_MIN_DF = "MMddHH-mmss";


	/**
	 * 返回年月：202201
	 * @param begin
	 * @return
	 */
	public static String formatYearMonthTime(Date begin) {
		return new SimpleDateFormat(YEAR_MONTH).format(begin);
	}

	/**
	 * 根据时区获取小时
	 * @param date
	 * @param timeZone
	 * @return
	 */
	public static String getTimeByTimeZone(Date date,String timeZone){
		TimeZone zone = TimeZone.getTimeZone(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DAY_DF);
		simpleDateFormat.setTimeZone(zone);
		return simpleDateFormat.format(date);
	}

	/**
	 * String 转换为 long 类型时间戳
	 */
	public static long timeStrToLong(String timeStr, String timeZone) {
		SimpleDateFormat sdf = new SimpleDateFormat(DF);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		long time = 0;
		try {
			time = sdf.parse(timeStr).getTime();
		} catch (ParseException e) {
			return time;
		}
		return time;
	}

    /**
     * 格式化字符串为日期
     * @param time 字符串格式为 yyyy-MM-dd HH:mm:ss
     * @return 日期 Date对象
     */
    public static Date formatTime(String time){
        try {
            return new SimpleDateFormat(DF).parse(time);
        } catch (ParseException ex) {
            ex.printStackTrace();
            return new Date();
        }
    }

	/**
	 * 返回 MMddHH-mmss 字符串样式
	 * @param date
	 * @return
	 */
	public static String formatDayMinString(Date date){
		return new SimpleDateFormat(DAY_MIN_DF).format(date);
	}

	/**
	 * 返回 yyyy-MM-dd-HH 字符串样式
	 * @param date
	 * @return
	 */
	public static String formatDayHourString(Date date){
		return new SimpleDateFormat(HOUR_FORMAT_HH).format(date);
	}

    /**
	 * 格式化字符串为日期
	 * @param day 字符串格式为yyyy-MM-dd
	 * @return 日期 Date对象
	 */
	public static Date formatDayTime(String day){
		try {
			return new SimpleDateFormat(DAY_DF).parse(day);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return new Date();
		}
	}


	/**
	 * 格式化字符串为日期
	 * @param hourTime 字符串格式为yyyy-MM-dd HH
	 * @return 日期 Date对象
	 */
	public static Date formatHourTime(String hourTime){
		try {
			return new SimpleDateFormat(HOUR_FORMAT).parse(hourTime);
		} catch (ParseException ex) {
			ex.printStackTrace();
			return new Date();
		}
	}



    /**
     * 格式字符串为某年某月的第一天。
     * @param yearmonth 格式为2008-10
     * @return 某年某月的第一天
     */
    public static Date formatMonthTime(String yearmonth){
        try {
            return new SimpleDateFormat(DAY_DF).parse(yearmonth + "-01");
        } catch (ParseException ex) {
            ex.printStackTrace();
            return new Date();
        }
    }

    public static boolean isBefore(Date time,Date checkTime){
		long timeLong  = time.getTime();
		long checkTimeLong = checkTime.getTime();
		if(timeLong <= checkTimeLong){
			return true;
		}
		return false;
	}

    /**
     *返回自1970年1月1日00:00:00GMT以来此日期对象表示的毫秒数
     *@param str 格式为yyyy-MM-dd
     */
    public static long parseDayByYYYYMMDD(String str){
        try {
            return new SimpleDateFormat(DAY_DF).parse(str).getTime();
        } catch (Exception ex) {
            return 0L;
        }
    }
    /**
     *返回自1970年1月1日00:00:00GMT以来此时间对象表示的秒数
     *@param str 格式为yyyy-MM-dd HH:mm:ss
     */
    public static int parseTimeByYYYYMMDDHHMMSS(String str){
        if(str == null || str.length() != DateConstant.DATE_LEN) {
			return 0;
		}
        try {
            return (int)(new SimpleDateFormat(DF).parse(str).getTime()/1000L);
        } catch (Exception ex) {
            return 0;
        }
    }
    /**
     * 得到 yyyy-MM-dd 格式的指定日期的前一天
     */
    public static String foreDay(String day){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(parseDayByYYYYMMDD(day));
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return new SimpleDateFormat(DAY_DF).format(cal.getTime());
    }
    /**
     * 根据时间值构造日期
     */
    public static String parseDay(int time){
        return new SimpleDateFormat(DAY_DF).format(new Date(time*1000L));
    }
    /**
     * 显示时间
     * @param millseconds 毫秒数
     * @return 时间显示
     */
    public static String displayTime(long millseconds) {
        if(millseconds < DateConstant.DATE_MILS) {
			return millseconds+" 毫秒";
		}
        int seconds = (int)(millseconds/1000);
        if(seconds < DateConstant.DATE_SECONDS) {
			return seconds+" 秒";
		}
        if(seconds < DateConstant.DATE_MINS*DateConstant.DATE_SECONDS) {
			return seconds/60+"分"+seconds%60+"秒";
		}
        int m = seconds -(seconds/3600)*3600;
        if(seconds < DateConstant.DATE_HOURS*DateConstant.DATE_MINS*DateConstant.DATE_SECONDS) {
			return seconds/3600+"小时"+m/60+"分"+m%60+"秒";
		}
        return millseconds+" 毫秒";
    }


    /**
     * 根据时区获取小时
     * @param date
     * @param timeZone
     * @return
     */
    public static String getHourByTimeZone(Date date,String timeZone){
        TimeZone zone = TimeZone.getTimeZone(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(HOUR_FORMAT);
        simpleDateFormat.setTimeZone(zone);
        return simpleDateFormat.format(date);
    }

	/**
	 * 根据时区获取小时
	 * @param date
	 * @param timeZone
	 * @return
	 */
	public static String getHourByTimeZoneDay(Date date,String timeZone){
		TimeZone zone = TimeZone.getTimeZone(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DAY_DF);
		simpleDateFormat.setTimeZone(zone);
		return simpleDateFormat.format(date);
	}


    /**
     * 转换成yyyy-MM-dd格式的日期字符串
     * @param d Date对象
     */
    public static String formatDay(Date d){
        return new SimpleDateFormat(DAY_DF).format(d);
    }

	/**
	 * 转换为yyyy-MM 格式的月份年份字符串
	 * @param d
	 * @return
	 */
	public static String formatMonth(Date d){
    	return new SimpleDateFormat(MONTH_DF).format(d);
	}

	public static String formatDayHour(Date d){
		return new SimpleDateFormat(HOUR_FORMAT).format(d);
	}

    /**
     * 转换成yyyy-MM-dd格式的日期字符串
     * @param d Calendar对象
     */
    public static String formatDay(Calendar d){
        return new SimpleDateFormat(DAY_DF).format(d.getTime());
    }

    /**
     * 两个日期相减，返回差值（单位天）
     * @param date1 被减数
     * @param date2 减数
     * @return
     */
    public static int subDate1ToDate2(String date1, String date2){
    	int days = 0;
    	try {
    		Date d1 = formatDayTime(date1);
        	Date d2 = formatDayTime(date2);
        	long result = (d1.getTime() - d2.getTime())/(1000*24*60*60);
        	days = (int) result;
    	} catch (Exception e) {
    		return days;
		}
    	return days;
    }

    /**
     *
     * @param date1 被减数
     * @param date2 减数
     * @return
     */
    public static String[] fromDate1ToDate2(String date1, String date2){
    	String[] array = null;
    	try {
    		int days = subDate1ToDate2(date1, date2);
    		if (days < 0){
    			return array;
    		}
    		if (days == 0){
    			return new String[]{date1};
    		}

        	array = new String[days + 1];
        	for(int i=0; i<=days; i++){
        		array[i] = DateUtil.addDate(date1, "D", -i);
        	}
    	} catch (Exception e) {
		}
    	return array;
    }

    /**
     * 两个日期相减，返回差值（单位秒）
     * @param date1 被减数
     * @param date2 减数
     * @return
     */
    public static Long subDate1ToDate2(Date date1, Date date2){
    	if(date1 == null || date2 == null){
    		return 0L;
    	}
    	long result = (date1.getTime() - date2.getTime())/1000;
    	if(result < 0){
    		result = Math.abs(result);
    	}
    	return result;
    }


    /**
     * 转换成yyyy-MM-dd HH:mm:ss格式的时间
     * @param time 毫秒数
     */
    public static String formatyyyyMMddHHmmss(long time){
        return new SimpleDateFormat(DF).format(new Date(time));
    }


    public static Date formateDateByLongTime(long time){
    	return new Date(time);
	}
    /**
     * 将时间转换成yyyy-MM-dd HH:mm:ss的格式字符串。
     * @param time 时间对象
     * @return 格式化后的字符串,当输入为null时输出为""
     */
    public static String formatyyyyMMddHHmmss(Date time){
        if(time==null){
            return "";
        }
        try{
            return new SimpleDateFormat(DF).format(time);
        }
        catch(Exception ex){
            return "";
        }
    }

	/**
	 * 将时间转换成yyyy-MM-dd HH:mm的格式字符串。
	 * @param time 时间对象
	 * @return 格式化后的字符串,当输入为null时输出为""
	 */
	public static String formatyyyyMMddHHmm(Date time){
		if(time==null){
			return "";
		}
		try{
			return new SimpleDateFormat(MINUTE_FORMAT).format(time);
		}
		catch(Exception ex){
			return "";
		}
	}


	/**
	 * 将时间转换成yyyy-MM-dd HH:mm的格式字符串。
	 * @param time 时间对象
	 * @return 格式化后的字符串,当输入为null时输出为""
	 */
	public static String formatyyyyMMddHH(Date time){
		if(time==null){
			return "";
		}
		try{
			return new SimpleDateFormat(HOUR_FORMAT).format(time);
		}
		catch(Exception ex){
			return "";
		}
	}


    /**
     * 当前日期
     * @return yyyy-MM-dd格式的当前日期
     */
    public static String today() {
        return new SimpleDateFormat(DAY_DF).format(new Date());
    }

	/**
	 * 根据时区，刷新时间判断日期
	 * @param timeZone
	 * @param time
	 * @return
	 */
    public static String dayByTimeZone(String timeZone,String time,String offerId){
    	if(StringUtils.isEmpty(timeZone) || StringUtils.isEmpty(time)){
    		timeZone = "GMT+08:00";
    		time = "00:00:00";
		}
		SimpleDateFormat format = new SimpleDateFormat(HOUR_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date newDate = new Date();
		String dateStr = format.format(newDate);
		String hour = dateStr.split(" ")[1];
		Integer hourInt = Integer.valueOf(StringUtils.isEmpty(hour) ? "00" : hour);
		Integer dbHourInt = Integer.valueOf(time.split(":")[0]);
		if(hourInt >= dbHourInt){
			return dateStr.split(" ")[0];
		}else {
			return yesterday();
		}
	}

	public static boolean needRefresh(String timeZone,String time){
		SimpleDateFormat format = new SimpleDateFormat(HOUR_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date newDate = new Date();
		String dateStr = format.format(newDate);
		String hour = dateStr.split(" ")[1];
		Integer hourInt = Integer.valueOf(StringUtils.isEmpty(hour) ? "00" : hour);
		Integer dbHourInt = Integer.valueOf(time.split(":")[0]);
		if(hourInt.equals(dbHourInt)){
			return true;
		}else {
			return false;
		}
	}
    /**
	 * 返回yyyyMMdd格式的当前日期
	 */
	public static String otherdateofnow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date());
	}

    /**
     * 当前日期的前一天
     * @return 当前日期的前一天
     */
    public static String yesterday(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return new SimpleDateFormat(DAY_DF).format(cal.getTime());
    }

	/**
	 * 当前时间前一个小时
	 * @return 当前时间前一个小时
	 */
	public static String beforeHour(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, (cal.get(Calendar.HOUR_OF_DAY) - 1));
		return new SimpleDateFormat(HOUR_FORMAT).format(cal.getTime());
	}

	/**
	 * 当前时间的上个月
	 * @return
	 */
	public static String beforeMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat(MONTH_DF);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		// 设置为当前时间
		calendar.setTime(date);
		calendar.add(Calendar.MONTH,-1);
		date = calendar.getTime();
		return sdf.format(date);
	}

    /**
     * 当前日期的下一天
     * @return 当前日期的下一天
     */
    public static String tomorrow(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat(DAY_DF).format(cal.getTime());
    }

    /**
     * 返回本月1号
     * @return 返回本月1号
     */
    public static String currmonth1day(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return new SimpleDateFormat(DAY_DF).format(cal.getTime());
    }

    /**
     * 返回本周第一天日期
     * @return
     */
    public static String currweek1day(){
        Calendar cal = Calendar.getInstance();
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int mondayPlus = 0;
        if (dayOfWeek != 1) {
        	mondayPlus = 1 - dayOfWeek;
        }
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        return new SimpleDateFormat(DAY_DF).format(currentDate.getTime());
    }

    /**
     * 返回当前日期周的第一天
     * @return
     */
    public static String currweek1day(String day){
        Calendar cal = Calendar.getInstance();

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int mondayPlus = 0;
        if (dayOfWeek != 1) {
        	mondayPlus = 1 - dayOfWeek;
        }
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        return new SimpleDateFormat(DAY_DF).format(currentDate.getTime());
    }

    /**
	 * 返回本月最后一天
	 */
	public static String lastdayofmonth(){
		Calendar calendar=Calendar.getInstance();
        Calendar cpcalendar=(Calendar)calendar.clone();
        cpcalendar.set(Calendar.DAY_OF_MONTH,1);
        cpcalendar.add(Calendar.MONTH, 1);
        cpcalendar.add(Calendar.DATE, -1);

        String date = new SimpleDateFormat(DAY_DF).format( new Date(cpcalendar.getTimeInMillis()));
        return date;
	}



	public static String getFirstDay(int year, int month) {
		// 获取Calendar类的实例
		Calendar c = Calendar.getInstance();
		// 设置年份
		c.set(Calendar.YEAR, year);
		// 设置月份，因为月份从0开始，所以用month - 1
		c.set(Calendar.MONTH, month - 1);
		// 设置日期
		c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        String time = DateUtil.formatDay(c.getTime()) + " 00:00:00";
		return time;
	}

	public static String getLastDay(int year, int month) {
		// 获取Calendar类的实例
		Calendar c = Calendar.getInstance();
		// 设置年份
		c.set(Calendar.YEAR, year);
		// 设置月份，因为月份从0开始，所以用month - 1
		c.set(Calendar.MONTH, month - 1);
		// 获取当前时间下，该月的最大日期的数字
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 将获取的最大日期数设置为Calendar实例的日期数
		c.set(Calendar.DAY_OF_MONTH, lastDay);
		String time = DateUtil.formatDay(c.getTime()) + " 23:59:59";
		return time;
	}

	/**
	 * 返回本年第一天
	 */
	public static String firstdayofyear(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(new Date());
        return year+"-01-01";
	}


	/**
	 * 返回当前秒数
	 * @return
	 */
	@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
	public static int getCurrentTimeSeconds(){
		String timeStamp = String.valueOf(System.currentTimeMillis()/1000);
		return Integer.valueOf(timeStamp);
	}

	/**
	 * 返回本年最后一天
	 */
	public static String lastdayofyear(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String year = sdf.format(new Date());
        return year+"-12-31";
	}

	public static String addMonth(String month,int num){
		String date = month + "-01 00:00:00";
		String time = addTime(date,DateConstant.MONTH_STR,num);
		return time.substring(0,7);
	}

	/**
	 * 给指定时间加上一个数值
	 * @param time1 要加上一数值的时间，为null即为当前时间，格式为yyyy-MM-dd HH:mm:ss
	 * @param addpart 要加的部分：年月日时分秒分别为：YMDHFS
	 * @param num 要加的数值
	 * @return 新时间，格式为yyyy-MM-dd HH:mm:ss
	 */
	public static String addTime(String time1,String addpart,int num){
		try{
			String now = new SimpleDateFormat(DF).format(new Date());
			time1 = (time1 == null) ? now : time1;
			if (time1.length() < DateConstant.DATE_LEN){
				time1 += " 00:00:00";
			}
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(new SimpleDateFormat(DF).parse(time1));
			if (DateConstant.YEAR_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.YEAR,num);
			}
			else if (DateConstant.MONTH_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.MONTH,num);
			}
			else if (DateConstant.DAY_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.DATE,num);
			}
			else if (DateConstant.HOUR_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.HOUR,num);
			}
			else if (DateConstant.MIN_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.MINUTE,num);
			}
			else if (DateConstant.SECOND_STR.equalsIgnoreCase(addpart)){
				cal.add(Calendar.SECOND,num);
			}
			return new SimpleDateFormat(DF).format(cal.getTime());
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 给指定日期加上一个数值
	 * @param date1 要加上一数值的日期，为null即为当前日期，格式为yyyy-MM-dd
	 * @param addpart 要加的部分：年月日分别为：YMD
	 * @param num 要加的数值
	 * @return 新日期，格式为yyyy-MM-dd
	 */
	public static String addDate(String date1,String addpart,int num){
		return addTime(date1,addpart,num).substring(0,10);
	}

    /**
     * 当前日期
     * @return yyyy-MM-dd HH:mm:ss格式的当前日期
     */
    public static String now() {
        return new SimpleDateFormat(DF).format(new Date());
    }
    /**
	 * 返回当前时间
	 */
	public static String timeofnow(){
		Calendar curcal = Calendar.getInstance();
		return new SimpleDateFormat(DF).format(curcal.getTime());
	}
	/**
	 * 返回yyyyMMddHHmmss格式的当前时间
	 */
	public static String othertimeofnow(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar curcal = Calendar.getInstance();
		return sdf.format(curcal.getTime());
	}

    /**
     * 得到距离当前天几天的日期表达，格式为1985-12-20。
     * @param step 天数。例如-10表示十天前
     * @return
     */
    public static String dateofSepcial(int step) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, step);
        return new SimpleDateFormat(DAY_DF).format(cal.getTime());
    }

	public static Date getCurrentDate(){
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 格式化 MMdd
	 * @param date
	 * @return java.lang.String
	 * @author Curry
	 * @date 2023/8/1
	 */
	public static String formatMonDayString(Date date) {
	    return new SimpleDateFormat(MON_DAY_FORMAT).format(date);
	}

	public static String getDateStr(long date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date newDate = new Date(date);
		String dateStr = format.format(newDate);
		return dateStr;
	}

	/**
	 * 根据时区获取小时
	 * @param time
	 * @param timeZone
	 * @return
	 */
	public static Date getTimeByTimeZone(String time, String timeZone){
		TimeZone zone = TimeZone.getTimeZone(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DF);
		simpleDateFormat.setTimeZone(zone);
		try {
			return simpleDateFormat.parse(time);
		} catch (ParseException e) {
			return new Date();
		}
	}

	/*public static String getGMT0DateStr(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		Date newDate = new Date(System.currentTimeMillis());
		String dateStr = format.format(newDate);
		return dateStr;
	}*/

	public static Long getGMT0DateLong24() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		Date newDate = new Date(System.currentTimeMillis());
		String dateStr = format.format(newDate);
		long nowDateLong = format.parse(dateStr).getTime();
		return nowDateLong;
	}

	public static Long getGMT0DateLong12() throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		Date newDate = new Date(System.currentTimeMillis());
		String dateStr = format.format(newDate);
		long nowDateLong = format.parse(dateStr).getTime();
		return nowDateLong;
	}

	/*public static boolean isCurrentGMT0Match(String timestamp) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		format.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));

		Date newDate = new Date(System.currentTimeMillis());
		String dateStr = format.format(newDate);
		long nowDate = format.parse(dateStr).getTime();

		long apinowDate = format.parse(timestamp).getTime();

		if((nowDate - apinowDate) < 10*60*1000 && (nowDate - apinowDate) >= -1){
			return true;
		}
		return false;
	}*/

	public static boolean isCurrentGMT0Match(Long timestamp) throws ParseException{
		//24小时制判断
		long nowDate = System.currentTimeMillis();
		long apiDate = timestamp;
		if((nowDate - apiDate) < (DateConstant.DATE_GMT*DateConstant.DATE_MINS*DateConstant.DATE_HOURS* DateConstant.DATE_MONTH) && (nowDate - apiDate) >= -(DateConstant.DATE_GMT*DateConstant.DATE_MINS*DateConstant.DATE_HOURS* DateConstant.DATE_MONTH)){
			return true;
		}
		return false;
	}

	public static int daysBetween(String beginDay, String endDay) throws ParseException{
		if(StringUtils.isEmpty(beginDay)){
			return 0;
		}
		Date beginDate = new SimpleDateFormat(DAY_DF).parse(beginDay);
		Date endDate = new SimpleDateFormat(DAY_DF).parse(endDay);
		Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        long beginTime = cal.getTimeInMillis();
        cal.setTime(endDate);
        long endTime = cal.getTimeInMillis();
        long betweenDays=(endTime-beginTime)/(1000*3600*24)+1;
        return Integer.parseInt(String.valueOf(betweenDays));
	}

    public static int getMonthSpace(String date1, String date2) {
        int result = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

	    try {
		    c1.setTime(sdf.parse(date1));
		    c2.setTime(sdf.parse(date2));
	    } catch (ParseException e) {
		    e.printStackTrace();
	    }
	    result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return Math.abs(result);
}



	public static List<String> getDayStrs(String beginDay, int dayCount) throws ParseException{
		List<String> result = new ArrayList<String>();
		Date beginDate = new SimpleDateFormat(DAY_DF).parse(beginDay);
		Calendar cal = Calendar.getInstance();
		cal.setTime(beginDate);
		for(int i=0; i<dayCount; i++){
			result.add(new SimpleDateFormat(DAY_DF).format(cal.getTime()));
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return result;
	}

	public static String incrDay(String day, int incrNum){
		//取时间
		Date date = formatDayTime(day);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		//把日期往后增加一天.整数往后推,负数往前移动
		calendar.add(Calendar.DATE,incrNum);

		return formatDay(calendar.getTime());
	}

	public static long times2tamptoLong(String timestamp,String timeZone)throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat(DF);
		sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		long time = sdf.parse(timestamp).getTime();
		return time;
	}

	public static Date getDateByTimeZone(long date,String timeZone) {
		SimpleDateFormat format = new SimpleDateFormat(DF);
		format.setTimeZone(TimeZone.getTimeZone(timeZone));
		Date newDate = new Date(date);
		String dateStr = format.format(newDate);
		Date date1 = formatTime(dateStr);
		return date1;
	}

	public static String newFormatyyyyMMddHHmmss(Date time){
		if(time==null){
			return "";
		}
		try{
			return new SimpleDateFormat(FULL_FORMAT).format(time);
		}
		catch(Exception ex){
			return "";
		}
	}

	/**
	 * 获取两个日期之间的所有日期 (年月日),不包含当天的日期
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getBetweenDate(String startTime, String endTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 声明保存日期集合
		List<String> list = new ArrayList<String>();
		try {
			// 转化成日期类型
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);

			//用Calendar 进行日期比较判断
			Calendar calendar = Calendar.getInstance();
			while (startDate.getTime() < endDate.getTime()){
				// 把日期添加到集合
				list.add(sdf.format(startDate));
				// 设置日期
				calendar.setTime(startDate);
				//把日期增加一天
				calendar.add(Calendar.DATE, 1);
				// 获取增加后的日期
				startDate=calendar.getTime();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}
}
