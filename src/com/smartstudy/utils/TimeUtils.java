package com.smartstudy.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeUtils {
    private TimeUtils() {}
    public static LocalTime getLocalTime(Time time){
        if(time == null) return null;
        else return time.toLocalTime();
    }

    public static LocalDateTime getLocalTime(Timestamp time){
        if(time == null) return null;
        else return time.toLocalDateTime();
    }
}
