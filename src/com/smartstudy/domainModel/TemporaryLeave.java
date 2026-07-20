package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private final LocalDateTime startTime;
    private final LocalDateTime expectedEndTime;

    private TemporaryLeave(int minutes) {
        super();
        if(minutes <= 0){
            throw new IllegalArgumentException("I minuti devono essere maggiori di 0");
        }
        this.startTime = LocalDateTime.now();
        this.expectedEndTime = startTime.plusMinutes(minutes);
    }

    private TemporaryLeave(long id, LocalDateTime startTime, LocalDateTime expectedEndTime) {
        super(id);
        if(startTime == null || expectedEndTime == null){
            throw new DomainViolationException("I tempi di inizio e fine non possono essere nulli");
        }
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
    }

    public static TemporaryLeave create(int minutes) {
        return new TemporaryLeave(minutes);
    }

    public static TemporaryLeave valueOf(long id, LocalDateTime startTime, LocalDateTime expectedEndTime) {
        return new TemporaryLeave(id, startTime, expectedEndTime);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getExpectedEndTime() {return expectedEndTime;}
    public boolean isValid(){
        return expectedEndTime.isBefore(LocalDateTime.now());
    }
}