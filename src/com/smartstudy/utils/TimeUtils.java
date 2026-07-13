package com.smartstudy.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TimeUtils {
    private TimeUtils() {}

    public static LocalDateTime getLocalTime(Timestamp time){
        if(time == null) return null;
        else return time.toLocalDateTime();
    }
}
