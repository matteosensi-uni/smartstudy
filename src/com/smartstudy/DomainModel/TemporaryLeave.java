package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private LocalDateTime expected_end_time;
    private long reservation_id;

    public TemporaryLeave(long id, LocalDateTime start_time, LocalDateTime expected_end_time, long reservation_id) {
        super(id);
        this.start_time = start_time;
        this.expected_end_time = expected_end_time;
        this.reservation_id = reservation_id;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getExpected_end_time() {
        return expected_end_time;
    }

    public void setExpected_end_time(LocalDateTime expected_end_time) {
        this.expected_end_time = expected_end_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public long getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(long reservation_id) {
        this.reservation_id = reservation_id;
    }
}

