package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.ReservationStatus;

import java.time.LocalDateTime;

public class Reservation extends BaseModel{
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private final long seatId;
    private final long sessionId;

    private Reservation(ReservationStatus status, long sessionId, long seatId) {
        super();
        this.startTime = LocalDateTime.now();
        this.status = status;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    private Reservation(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, long sessionId, long seatId) {
        super(id);
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    public static Reservation valueOf(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, long sessionId, long seatId){
        return new Reservation(id, startTime, endTime, status, sessionId, seatId);
    }

    public static Reservation start(ReservationStatus status, long sessionId, long seatId){
        return new Reservation(status, sessionId, seatId);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    public ReservationStatus getStatus() {return status;}
    public long getSeat() {return seatId;}
    public long getSessionId() {return sessionId;}
    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }

    public void close() throws IllegalAccessException {
        if(endTime != null || status == ReservationStatus.CLOSED)
            throw new IllegalAccessException("La prenotazione è già stata chiusa");
        endTime =  LocalDateTime.now();
        status = ReservationStatus.CLOSED;
    }

    public void markTemporarilyLeft(){
        if(status == ReservationStatus.CLOSED){
            throw new IllegalStateException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.TEMPORARILY_LEFT;
    }

    public void markActive(){
        if(status == ReservationStatus.CLOSED){
            throw new IllegalStateException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.ACTIVE;
    }

}