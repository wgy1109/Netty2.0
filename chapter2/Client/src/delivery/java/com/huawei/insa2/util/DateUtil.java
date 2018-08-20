package com.huawei.insa2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String GetTimeStamp() {
		String TimeStamp = "";
		Calendar now = Calendar.getInstance();
		TimeStamp = FormatInt(Integer.toString(now.MONTH + 1))
				+ FormatInt(Integer.toString(now.DAY_OF_MONTH + 1))
				+ FormatInt(Integer.toString(now.HOUR_OF_DAY + 1))
				+ FormatInt(Integer.toString(now.MINUTE + 1))
				+ FormatInt(Integer.toString(now.SECOND + 1));
		return TimeStamp;
	}
	public static Long getTimeStampL() {
		return (new java.util.Date()).getTime();
	}
	private static String FormatInt(String value) {
		if (value.length() == 1) {
			return "0" + value;
		} else {
			return value;
		}
	}
	
	public static String GetTimeString() {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		return sdf.format(new Date());
	}
	public static String GetTimeString2() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
	
	public static String getDeliverDateString(byte [] src, int srcPos,int destPos,int length){
	    	byte[] tmp=new byte[length];
	    	System.arraycopy(src, srcPos, tmp, destPos, length);
	    	return (new String(tmp).trim());
	}
	
	public static Date strToDate(String str,String pattern) {
		   SimpleDateFormat format = new SimpleDateFormat(pattern);
		   Date date = null;
		   try {
		    date = format.parse(str);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		   return date;
		}
	
	public static void main_(String[] args){
		Date dt = strToDate("1605052041","yyMMddHHmmss");
		System.out.println(dt);
		
	}
	
	
}
