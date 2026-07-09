package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.ReservationStatus;
import com.smartstudy.DomainModel.enums.SeatStatus;
import com.smartstudy.DomainModel.enums.SeatType;

import javax.management.RuntimeErrorException;
import java.time.LocalDateTime;

public class Seat extends BaseModel{
    private final String qr_code;
    private SeatType type;
    private SeatStatus status;
    private long studyArea_id;

    public Seat(long id, String qr_code, SeatType type, SeatStatus status, long studyArea_id) {
        super(id);
        this.qr_code = qr_code;
        this.type = type;
        this.status = status;
        this.studyArea_id = studyArea_id;
    }

    public Seat(String qr_code, SeatType type, SeatStatus status, long studyArea_id) {
        super();
        this.qr_code = qr_code;
        this.type = type;
        this.status = status;
        this.studyArea_id = studyArea_id;
    }

    public String getQr_code() {
        return qr_code;
    }
    public SeatStatus getStatusId() {
        return status;
    }
    public long getStudyAreaId() {
        return studyArea_id;
    }
    public SeatType getType() {
        return type;
    }
    public boolean isAvailable(){ return status == SeatStatus.AVAILABLE;}
    public void Occupy(AccessSession accessSession){
        if(status == SeatStatus.AVAILABLE){
            status = SeatStatus.UNAVAILABLE;
        }else{
            throw new RuntimeErrorException(new Error("Il posto "));
        }
    }
    public void release(){
        status = SeatStatus.AVAILABLE;
    }

    public void changeSeatType(SeatType new_type){
        type = new_type;
    }
    public void changeStudyArea(int studyArea_id){
        this.studyArea_id = studyArea_id;
    }
}
