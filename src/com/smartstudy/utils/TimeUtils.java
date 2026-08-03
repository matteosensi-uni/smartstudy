package com.smartstudy.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    private TimeUtils() {}

    public static LocalDateTime getLocalTime(Timestamp time){
        if(time == null) return null;
        else return time.toLocalDateTime();
    }

    public static String format(LocalDateTime date){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(date);
    }

}
