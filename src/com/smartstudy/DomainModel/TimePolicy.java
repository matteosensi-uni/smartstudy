package com.smartstudy.DomainModel;

import java.time.LocalTime;

public class TimePolicy extends BaseModel{
    private final int maxTemporaryLeaveMin;
    private final int maxTemporaryLeaveTimes;
    private final String name;

    public TimePolicy(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        super(id);
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
        this.name = name;
    }

    public TimePolicy(int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        super();
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
        this.name = name;
    }

    public int getMaxTemporaryLeaveMin() {return maxTemporaryLeaveMin;}
    public int getMaxTemporaryLeaveTimes() {return maxTemporaryLeaveTimes;}
    public String getPolicyName() {return name;}

    public LocalTime calculateEnd(LocalTime startTime){
        return startTime.plusMinutes(maxTemporaryLeaveMin);
    }
    public boolean reachedLimit(int times){
        return times >= maxTemporaryLeaveTimes;
    }
}