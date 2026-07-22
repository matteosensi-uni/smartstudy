package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public class TimePolicy extends BaseModel{
    private int maxTemporaryLeaveMin;
    private int maxTemporaryLeaveTimes;
    private String name;

    private TimePolicy(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        super(id);
        setMaxTemporaryLeaveMin(maxTemporaryLeaveMin);
        setMaxTemporaryLeaveTimes(maxTemporaryLeaveTimes);
        setName(name);
    }

    private static TimePolicy valueOf(long id, int maxTemporaryLeaveMin, int maxTemporaryLeaveTimes, String name) {
        return new TimePolicy(id, maxTemporaryLeaveMin, maxTemporaryLeaveTimes, name);
    }

    private void setMaxTemporaryLeaveMin(int maxTemporaryLeaveMin) {
        if(maxTemporaryLeaveMin <= 0){
            throw new DomainViolationException("Le regole devono essere maggiori di 0");
        }
        this.maxTemporaryLeaveMin = maxTemporaryLeaveMin;
    }

    private void setMaxTemporaryLeaveTimes(int maxTemporaryLeaveTimes) {
        if(maxTemporaryLeaveTimes <= 0){
            throw new DomainViolationException("Le regole devono essere maggiori di 0");
        }
        this.maxTemporaryLeaveTimes = maxTemporaryLeaveTimes;
    }

    private void setName(String name) {
        if(name == null || name.isBlank())
            throw new DomainViolationException("Il nome della regola è nullo");

        this.name = name;
    }

    public int getMaxTemporaryLeaveMin() {return maxTemporaryLeaveMin;}
    public int getMaxTemporaryLeaveTimes() {return maxTemporaryLeaveTimes;}
    public String getName() {return name;}

    public boolean reachedLimit(int times){
        return times >= maxTemporaryLeaveTimes;
    }
}