package com.Utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String getDayOfWeek(Date date) {
        return new SimpleDateFormat("EEEE").format(date);
    }
    
    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    
    public static String formatTime(Time time) {
        return new SimpleDateFormat("HH:mm").format(time);
    }
    
    public static String formatDateTimeForDisplay(Date date, Time time) {
        return new SimpleDateFormat("dd MMM yyyy hh:mm a").format(new java.util.Date(
            date.getTime() + time.getTime()
        ));
    }
}
