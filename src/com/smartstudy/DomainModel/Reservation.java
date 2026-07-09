package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.ReservationStatus;

import java.time.LocalDateTime;

public class Reservation extends BaseModel{
    private final LocalDateTime start_time;
    private LocalDateTime end_time;
    private ReservationStatus status;
    private final long seat_id;
    private final long session_id;

    public Reservation(LocalDateTime start_time, ReservationStatus status, long session_id, long seat_id) {
        super();
        this.start_time = start_time;
        this.status = status;
        this.session_id = session_id;
        this.seat_id = seat_id;
    }

    public Reservation(long id, LocalDateTime start_time, LocalDateTime end_time, ReservationStatus status, long session_id, long seat_id) {
        super(id);
        this.start_time = start_time;
        this.end_time = end_time;
        this.status = status;
        this.session_id = session_id;
        this.seat_id = seat_id;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }
    public LocalDateTime getEnd_time() {
        return end_time;
    }
    public ReservationStatus getStatus() {
        return status;
    }
    public long getSeat() {
        return seat_id;
    }
    public long getSessionId() { return session_id; }
    public boolean isActive(){ return status == ReservationStatus.ACTIVE; }

    protected void close(){
        end_time = LocalDateTime.now();
        status = ReservationStatus.CLOSED;
    }

    public void markTemporariliLeft(){
        status = ReservationStatus.TEMPORARILY_LEFT;
    }

}
