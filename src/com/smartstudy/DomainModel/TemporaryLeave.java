package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class TemporaryLeave extends BaseModel {
    private final LocalDateTime start_time;
    private final LocalDateTime expected_end_time;
    private final long reservation_id;

    public TemporaryLeave(LocalDateTime start_time, LocalDateTime expected_end_time, long reservation_id) {
        super();
        this.start_time = start_time;
        this.expected_end_time = expected_end_time;
        this.reservation_id = reservation_id;
    }

    public TemporaryLeave(long id, LocalDateTime start_time, LocalDateTime expected_end_time, long reservation_id) {
        super(id);
        this.start_time = start_time;
        this.expected_end_time = expected_end_time;
        this.reservation_id = reservation_id;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }
    public LocalDateTime getExpected_end_time() {
        return expected_end_time;
    }
    public long getReservation_id() {
        return reservation_id;
    }
}

