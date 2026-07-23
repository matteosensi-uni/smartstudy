package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;

    private TemporaryLeave(int minutes) {
        super();
        if(minutes <= 0){
            throw new DomainViolationException("I minuti devono essere maggiori di 0");
        }
        setStartTime(LocalDateTime.now());
        setExpectedEndTime(startTime.plusMinutes(minutes));
    }

    private TemporaryLeave(long id, LocalDateTime startTime, LocalDateTime expectedEndTime) {
        super(id);
        setStartTime(startTime);
        setExpectedEndTime(expectedEndTime);
    }

    public static TemporaryLeave create(int minutes) {
        return new TemporaryLeave(minutes);
    }

    public static TemporaryLeave valueOf(long id, LocalDateTime startTime, LocalDateTime expectedEndTime) {
        return new TemporaryLeave(id, startTime, expectedEndTime);
    }

    private void setStartTime(LocalDateTime startTime) {
        if(startTime == null){
            throw new DomainViolationException("Il tempo di inizio è nullo");
        }
        if(expectedEndTime != null &&  expectedEndTime.isBefore(startTime)){
            throw new DomainViolationException("Inserire un tempo di inizio valido");
        }
        this.startTime = startTime;
    }

    private void setExpectedEndTime(LocalDateTime expectedEndTime) {
        if(expectedEndTime == null){
            throw new DomainViolationException("Il tempo di fine è nullo");
        }
        if(startTime != null && expectedEndTime.isBefore(startTime)){
            throw new DomainViolationException("Inserire un tempo di fine valido");
        }
        this.expectedEndTime = expectedEndTime;
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getExpectedEndTime() {return expectedEndTime;}
    public boolean isValid(){
        return expectedEndTime.isAfter(LocalDateTime.now());
    }
}