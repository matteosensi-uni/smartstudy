package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private final LocalDateTime startTime;
    private final LocalDateTime expectedEndTime;
    private final long reservationId;

    public TemporaryLeave(LocalDateTime startTime, LocalDateTime expectedEndTime, long reservationId) {
        super();
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
        this.reservationId = reservationId;
    }

    public TemporaryLeave(long id, LocalDateTime startTime, LocalDateTime expectedEndTime, long reservationId) {
        super(id);
        this.startTime = startTime;
        this.expectedEndTime = expectedEndTime;
        this.reservationId = reservationId;
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getExpectedEndTime() {return expectedEndTime;}
    public long getReservationId() {return reservationId;}
}