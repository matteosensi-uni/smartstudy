package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public class TimePolicy extends BaseModel{
    private final int maxTemporaryLeaveMin;
    private final int maxTemporaryLeaveTimes;
    private final String name;

    private TimePolicy(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        super(id);
        if(name == null || name.isBlank())
            throw new DomainViolationException("Il nome della regola è vuoto");
        if(maxTemporaryLeaveMin <= 0 || maxTemporaryLeaveTimes <= 0){
            throw new DomainViolationException("Le regole devono essere maggiori di 0");
        }
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