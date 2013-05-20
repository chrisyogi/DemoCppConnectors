package com.ericsson.ecut.collector.cpp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateUtil {
	private final static Logger log = Logger.getLogger(DateUtil.class);	
	
	public static Calendar createCalendar(String date, String format) throws ParseException{
		SimpleDateFormat sf = new SimpleDateFormat(format);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(sf.parse(date));
		return cal; 
	}
	
	public static Calendar createCalendar(String date) throws ParseException{
		if(date.indexOf('-')>0){
			try{
				if(date.length() == 16){
					Calendar cal = createCalendar(date, "yyyy-MM-dd HH:mm");
					cal.set(Calendar.SECOND, 0);
					return cal;
				}
				else if(date.length() == 19)
					return createCalendar(date, "yyyy-MM-dd HH:mm:ss");
				else{
					log.error("Could not convert date " + date);
					return null;
				}
	        }catch(Exception e){
	        	log.error("Could not convert date " + date);
	        	return null;
	        }
		}else{
			try{
				Calendar cal = Calendar.getInstance();
				int time = Integer.parseInt(date); 
				cal.setTimeInMillis(time);
				return cal;
			}catch(Exception e){
				log.error("Could not convert time " + date);
			}
		}
		
		return null;
//		if(date.length() < 19)
//			return createCalendar(date, "yyyy-MM-dd HH:mm");
//		else
//			return createCalendar(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getDayOfWeek(int dayNr){
		switch (dayNr) {
		case Calendar.MONDAY:
			return "Monday";
		case Calendar.TUESDAY:
			return "Tueday";
		case Calendar.WEDNESDAY:
			return "Wednesday";
		case Calendar.THURSDAY:
			return "Thursday";
		case Calendar.FRIDAY:
			return "Friday";
		case Calendar.SATURDAY:
			return "Saturday";
		case Calendar.SUNDAY:
			return "Sunday";
		default:
			return "Monday";
		}
	}
	
	public static String getMonthStringShort(int month){
		switch (month) {
		case 1:
			return "Jan";
		case 2:
			return "Feb";
		case 3:
			return "Mar";
		case 4:
			return "Apr";
		case 5:
			return "May";
		case 6:
			return "Jun";
		case 7:
			return "Jul";
		case 8:
			return "Aug";
		case 9:
			return "Sep";
		case 10:
			return "Oct";
		case 11:
			return "Nov";
		case 12:
			return "Dec";
		default:
			return "";
		}
	}
	
	public static String getDayOfWeek(int y, int m, int d){
		Calendar cal = Calendar.getInstance();
		cal.set(y, m-1, d);
		return getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK));
	}
	
	/** Using Calendar - THE CORRECT WAY**/  
	//assert: startDate must be before endDate  
	public static int getDaysBetween(Calendar startDate, Calendar endDate) {
		if(startDate.after(endDate))
			return 0;
		Calendar date = (Calendar) startDate.clone();  
		int daysBetween = 0;  
	  
		while (date.before(endDate) || date.equals(endDate)) {  
			date.add(Calendar.DAY_OF_MONTH, 1);  
			daysBetween++;
		}  
		return daysBetween;  
	}
	
	public static String getDateString(Calendar cal, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
	
	public static String getDateString(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(cal.getTime());
	}
	
	public static String getDateString(Date cal, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
	
	public static String getDateString(Date cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    return sdf.format(cal.getTime());
	}
	
	public static int getFirstMonthInQuarter(int quarter){
		return (quarter == 1) ?  1 :
			   (quarter == 2) ?  4 :
			   (quarter == 3) ?  7 :
				                10 ;
	}
	
	public static int getQuarter(Calendar cal){
		int month = cal.get(Calendar.MONTH);
		return (month >= Calendar.JANUARY && month <= Calendar.MARCH)     ? 1 :
		       (month >= Calendar.APRIL && month <= Calendar.JUNE)        ? 2 :
		       (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)    ? 3 :
		                                                                    4 ;
	}
	
	public static int getQuarterBefore(Calendar cal){
		int month = cal.get(Calendar.MONTH);
		return (month >= Calendar.JANUARY && month <= Calendar.MARCH)     ? 4 :
		       (month >= Calendar.APRIL && month <= Calendar.JUNE)        ? 1 :
		       (month >= Calendar.JULY && month <= Calendar.SEPTEMBER)    ? 2 :
		                                                                    3 ;
	}

	public static int getQuarter(int month){
		return (month >= 1 && month <= 3) ? 1 :
		       (month >= 2 && month <= 6) ? 2 :
		       (month >= 7 && month <= 9) ? 3 :
		                                    4 ;

	}
	
	static final long ONE_HOUR = 60 * 60 * 1000L;

	public static long daysBetween(Calendar startDate, Calendar endDate) {
		return ((endDate.getTimeInMillis() - startDate.getTimeInMillis() + ONE_HOUR) / (ONE_HOUR * 24));
	}
	
	public static int getUTCOffset(){
		Calendar cal = Calendar.getInstance();
		return (cal.get(Calendar.DST_OFFSET)/1000/60/60) + (cal.get(Calendar.ZONE_OFFSET)/1000/60/60);
	}
	
	public static String formatCalendar(Calendar cal){
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + " " + (cal.get(Calendar.HOUR_OF_DAY)<10?"0":"") +  cal.get(Calendar.HOUR_OF_DAY) + ":" + (cal.get(Calendar.MINUTE)<10?"0":"") +  cal.get(Calendar.MINUTE) + ":" + (cal.get(Calendar.SECOND)<10?"0":"") +  cal.get(Calendar.SECOND);
	}
	
	public static int getHourDiff(Calendar cal1, Calendar cal2){
		boolean forward = true;
		if(cal1.after(cal2))
			forward = false;
		
		Calendar c1 = (Calendar) cal1.clone();
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		Calendar c2 = (Calendar) cal2.clone();
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		int diff = 0;
		if(forward){
			while(c1.before(c2) && !c1.equals(c2)){
				c1.add(Calendar.HOUR_OF_DAY, 1);
				diff++;
			}
		}else{
			while(c1.after(c2) && !c1.equals(c2)){
				c1.add(Calendar.HOUR_OF_DAY, -1);
				diff--;
			}
		}
		return diff;
	}
	
	public static int getMinutesDiff(Calendar cal1, Calendar cal2){
		boolean forward = true;
		if(cal1.after(cal2))
			forward = false;
		
		Calendar c1 = (Calendar) cal1.clone();
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);
		Calendar c2 = (Calendar) cal2.clone();
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		
		int diff = 0;
		if(forward){
			while(c1.before(c2) && !c1.equals(c2)){
				c1.add(Calendar.MINUTE, 1);
				diff++;
			}
		}else{
			while(c1.after(c2) && !c1.equals(c2)){
				c1.add(Calendar.MINUTE, -1);
				diff--;
			}
		}
		return diff;
	}
	
	public static int getSecondsDiff(Calendar cal1, Calendar cal2){
		boolean forward = true;
		if(cal1.after(cal2))
			forward = false;
		
		Calendar c1 = (Calendar) cal1.clone();
		c1.set(Calendar.MILLISECOND, 0);
		Calendar c2 = (Calendar) cal2.clone();
		c2.set(Calendar.MILLISECOND, 0);
		
		int diff = 0;
		if(forward){
			while(c1.before(c2) && !c1.equals(c2)){
				c1.add(Calendar.SECOND, 1);
				diff++;
			}
		}else{
			while(c1.after(c2) && !c1.equals(c2)){
				c1.add(Calendar.SECOND, -1);
				diff--;
			}
		}
		return diff;
	}
}
