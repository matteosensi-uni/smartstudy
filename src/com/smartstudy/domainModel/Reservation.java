package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class Reservation extends BaseModel{
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private final long seatId;
    private final long sessionId;

    private Reservation(long sessionId, long seatId) {
        super();
        checkId(sessionId, "AccessSession");
        checkId(seatId, "Seat");
        this.startTime = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    private Reservation(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, long sessionId, long seatId) {
        super(id);
        checkId(sessionId, "AccessSession");
        checkId(seatId, "Seat");
        if(startTime == null){
            throw new DomainViolationException("la data di inizio prenotazione non può essere nulla");
        }
        if(status == null){
            throw new DomainViolationException("Lo stato della prenotazione non può essere nullo");
        }
        if((status == ReservationStatus.ACTIVE || status == ReservationStatus.TEMPORARILY_LEFT) && endTime != null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        if(status == ReservationStatus.CLOSED && endTime == null){
            throw new DomainViolationException("Una prenotazione attiva non può avere un tempo di fine");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.sessionId = sessionId;
        this.seatId = seatId;
    }

    public static Reservation valueOf(long id, LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status, long sessionId, long seatId){
        return new Reservation(id, startTime, endTime, status, sessionId, seatId);
    }

    public static Reservation start(long sessionId, long seatId){
        return new Reservation(sessionId, seatId);
    }

    public LocalDateTime getStartTime() {return startTime;}
    public LocalDateTime getEndTime() {return endTime;}
    public ReservationStatus getStatus() {return status;}
    public long getSeatId() {return seatId;}
    public long getSessionId() {return sessionId;}
    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }

    public void close() {
        if(status == ReservationStatus.CLOSED)
            throw new DomainViolationException("La prenotazione è già stata chiusa");
        endTime =  LocalDateTime.now();
        status = ReservationStatus.CLOSED;
    }

    public void markTemporarilyLeft(){
        if(status == ReservationStatus.CLOSED || status == ReservationStatus.TEMPORARILY_LEFT){
            throw new DomainViolationException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.TEMPORARILY_LEFT;
    }

    public void markActive(){
        if(status == ReservationStatus.CLOSED || status == ReservationStatus.ACTIVE){
            throw new DomainViolationException("La prenotazione non può essere modificata");
        }
        status = ReservationStatus.ACTIVE;
    }

}