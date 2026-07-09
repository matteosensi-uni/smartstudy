package com.smartstudy.DomainModel;

import java.time.LocalTime;

public class TimePolicy extends BaseModel{
    private int max_temporary_leave_min;
    private int max_temporary_leave_times;

    public TimePolicy(long id, int max_temporary_leave_min, int max_temporary_leave_times) {
        super(id);
        this.max_temporary_leave_min = max_temporary_leave_min;
        this.max_temporary_leave_times = max_temporary_leave_times;
    }

    public TimePolicy(int max_temporary_leave_min, int max_temporary_leave_times) {
        super();
        this.max_temporary_leave_min = max_temporary_leave_min;
        this.max_temporary_leave_times = max_temporary_leave_times;
    }

    public int getMax_temporary_leave_min() {
        return max_temporary_leave_min;
    }

    public void setMax_temporary_leave_min(int max_temporary_leave_min) {
        this.max_temporary_leave_min = max_temporary_leave_min;
    }

    public int getMax_temporary_leave_times() {
        return max_temporary_leave_times;
    }

    public void setMax_temporary_leave_times(int max_temporary_leave_times) {
        this.max_temporary_leave_times = max_temporary_leave_times;
    }

    public LocalTime calculateEnd(LocalTime startTime){
        return startTime.plusMinutes(max_temporary_leave_min);
    }

    public boolean reachedLimit(int times){
        return times >= max_temporary_leave_times;
    }
}
