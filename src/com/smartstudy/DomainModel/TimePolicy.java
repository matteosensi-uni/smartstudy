package com.smartstudy.DomainModel;

public class TimePolicy extends BaseModel{
    private final int maxTemporaryLeaveMin;
    private final int maxTemporaryLeaveTimes;
    private final String name;

    private TimePolicy(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        super(id);
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
        this.name = name;
    }

    public static TimePolicy valueOf(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        return new TimePolicy(id, maxTemporaryLeaveMin, maxTemporaryLeaveTimes, name);
    }

    public int getMaxTemporaryLeaveMin() {return maxTemporaryLeaveMin;}
    public int getMaxTemporaryLeaveTimes() {return maxTemporaryLeaveTimes;}
    public String getPolicyName() {return name;}

    public boolean reachedLimit(int times){
        return times >= maxTemporaryLeaveTimes;
    }
}