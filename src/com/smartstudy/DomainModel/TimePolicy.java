package com.smartstudy.DomainModel;

import java.time.LocalTime;

public class TimePolicy extends BaseModel{
    private final int maxTemporaryLeaveMin;
    private final int maxTemporaryLeaveTimes;

    public TimePolicy(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes) {
        super(id);
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
    }

    public TimePolicy(int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes) {
        super();
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
    }

    public int getMaxTemporaryLeaveMin() {
        return maxTemporaryLeaveMin;
    }
    public int getMaxTemporaryLeaveTimes() {
        return maxTemporaryLeaveTimes;
    }

    public LocalTime calculateEnd(LocalTime startTime){
        return startTime.plusMinutes(maxTemporaryLeaveMin);
    }
    public boolean reachedLimit(int times){
        return times >= maxTemporaryLeaveTimes;
    }
}