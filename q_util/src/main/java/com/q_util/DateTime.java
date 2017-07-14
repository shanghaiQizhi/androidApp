package com.q_util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTime {

    public DateTime() {

    }

    public String GetNowDate(int type){
        String temp_str="";
        Date dt = new Date();
        SimpleDateFormat sdf;
        if(type==1){
            //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }else if (type==2){
            sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        }else if (type==3){
            sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }else if (type==4){
            return(String.valueOf(dt.getHours()));
        }else if (type==5){
            sdf = new SimpleDateFormat("ddHHmmss", Locale.US);
        }else if (type==6){
            sdf = new SimpleDateFormat("HH:mm:ss",Locale.US);
        }else if (type==7){
            sdf = new SimpleDateFormat("ss",Locale.US);
        }else if (type==8){
            sdf = new SimpleDateFormat("EEEE", Locale.US);
        }else if (type==9){
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        }else{
            sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        }
        try{
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        }catch(Exception e){
            e.printStackTrace();
        }


        temp_str=sdf.format(dt);
        if (type==5){
            int i=(int)(Math.random()*900)+100;
            temp_str = temp_str + String.valueOf(i);
        }

        return temp_str;
    }

    /**
     * 获取两日期相差天数
     * @param beforeDate 早的日期
     * @param afterDate 晚的日期
     * @return 相差天数
     */
    public int getDayBetweenTwo(Date beforeDate, Date afterDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(beforeDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(afterDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }

    /**
     * 获取特殊日期格式,三天内显示dd HH:mm,否则显示yyyy-MM-dd
     * @param datetime 日期
     * @return 特殊日期格式
     */
    public String getSpecialDateTime(String datetime){
        if(datetime==null || datetime.equals("")){
            return "";
        }
        String result = datetime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try{
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        }catch(Exception e){
            e.printStackTrace();
        }
        Date date=null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date nowDate = new Date();
        if(datetime.length()>=15){
            if(getDayBetweenTwo(date,nowDate)<=2){
                result = datetime.substring(8,16);//dd HH:mm
                result = result.replace(" ","日");
            }else{
                result = sdf2.format(date);//yyyy-MM-dd
            }
        }
        return result;
    }
}
