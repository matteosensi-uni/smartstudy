package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private final LocalDateTime startTime;
    private final LocalDateTime expectedEndTime;
    private final long reservationId;

    private TemporaryLeave(int minutes, long reservationId) {
        super();
        checkId(reservationId, "Reservation");
        if(minutes <= 0)
            throw new DomainViolationException("I minuti della pausa non possono essere negativi o zero");
        this.startTime = LocalDateTime.now();
        this.expectedEndTime = startTime.plusMinutes(minutes);
        this.reservationId = reservationId;
    }

    private TemporaryLeave(long id, LocalDateTime startTime, LocalDateTime expectedEndTime, long reservationId) {
        super(id);
        checkId(reservationId, "Reservation");
        if(startTime == null || expectedEndTime == null){
            throw new DomainViolationException("I tempi di inizio e fine non possono essere nulli");
        }
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
        this.reservationId = reservationId;
    }

    public static TemporaryLeave create(int minutes, long reservationId) {
        return new TemporaryLeave(minutes, reservationId);
    }

    public static TemporaryLeave valueOf(long id, LocalDateTime startTime, LocalDateTime expectedEndTime, long reservationId) {
        return new TemporaryLeave(id, startTime, expectedEndTime, reservationId);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getExpectedEndTime() {return expectedEndTime;}
    public long getReservationId() {return reservationId;}
}