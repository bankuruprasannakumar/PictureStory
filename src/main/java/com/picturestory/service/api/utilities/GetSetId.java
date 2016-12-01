package com.picturestory.service.api.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bankuru on 7/7/16.
 */
public class GetSetId {

    public static long getSetIdForFeed(long startTime) {
        try {
            if (startTime <= 0) {
                return 0;
            }
            long absoluteStartTime = getStartOfDay(startTime);
            long diffInMilis = (System.currentTimeMillis()) - absoluteStartTime;
            if (diffInMilis < 0) {
                return 0;
            }
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);

            if (diffInDays == 0) {
                return 0;
            }
            Date currentDate = new Date(System.currentTimeMillis());
            //currentDate according to IST
            DateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            currentFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            String currentTimeFormatted = currentFormat.format(currentDate);

            String[] currentTimeTokens = currentTimeFormatted.split("-");
            int currentTimeHour = Integer.parseInt(currentTimeTokens[3]);

            if (currentTimeHour < 5) {
                return diffInDays-1;
            }
            else {
                return diffInDays;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static long getSetIdForWallPaper(long startTime) {
        try {
            if (startTime <= 0){
                return 0;
            }
            long absoluteStartTime = getStartOfDay(startTime);
//            System.out.println("currentTime: "+System.currentTimeMillis());
//            System.out.println("absoluteTime: "+absoluteStartTime);
            long diffInMilis = (System.currentTimeMillis()) - absoluteStartTime;
//            System.out.println("diffInDays:"+diffInMilis);
            if (diffInMilis < 0) {
                return 0;
            }
            long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
//            System.out.println("diffInDays:"+diffInDays);
            if (diffInDays == 0) {
                return 0;
            }
            Date currentDate = new Date(System.currentTimeMillis());
            //currentDate according to IST
            DateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            currentFormat.setTimeZone(TimeZone.getTimeZone("IST"));
            String currentTimeFormatted = currentFormat.format(currentDate);

            String[] currentTimeTokens = currentTimeFormatted.split("-");
            int currentTimeHour = Integer.parseInt(currentTimeTokens[3]);

            if (currentTimeHour < 3) {
                return diffInDays-1;
            }
            else {
                return diffInDays;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static long getStartOfDay(long epochTime) {
        Date currentDate = new Date(epochTime);
        DateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        currentFormat.setTimeZone(TimeZone.getTimeZone("IST"));
        String currentTimeFormatted = currentFormat.format(currentDate);

        String[] currentTimeTokens = currentTimeFormatted.split("-");

        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(currentTimeTokens[0]);
        int month = Integer.parseInt(currentTimeTokens[1]);
        int day = Integer.parseInt(currentTimeTokens[2]);
        calendar.set(year, month-1, day, 0, 0, 0);
//        System.out.println(calendar);
        long time = calendar.getTimeInMillis();
        return time;
    }

    public static void main (String[] args){
        System.out.println(getSetIdForFeed(1470748328000l));
    }
}
