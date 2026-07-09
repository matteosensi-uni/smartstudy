package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.ReservationStatus;

import java.time.LocalDateTime;

public class Reservation extends BaseModel{
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private final long seatId;
    private final long sessionId;

    public Reservation(LocalDateTime startTime, ReservationStatus status, long sessionId, long seatId) {
        super();
        this.startTime = startTime;
        this.status = status;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    public Reservation(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, long sessionId, long seatId) {
        super(id);
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public ReservationStatus getStatus() {
        return status;
    }
    public long getSeat() {
        return seatId;
    }
    public long getSessionId() { return sessionId; }
    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }

    protected void close(){
        endTime = LocalDateTime.now();
        status = ReservationStatus.CLOSED;
    }

    public void markTemporarilyLeft(){
        status = ReservationStatus.TEMPORARILY_LEFT;
    }

}