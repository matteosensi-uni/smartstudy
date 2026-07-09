package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.SeatStatus;
import com.smartstudy.DomainModel.enums.SeatType;
import javax.management.RuntimeErrorException;

public class Seat extends BaseModel{
    private final String qrCode;
    private SeatType type;
    private SeatStatus status;
    private final long studyAreaId;

    public Seat(long id, String qrCode, SeatType type, SeatStatus status, long studyAreaId) {
        super(id);
        this.qrCode = qrCode;
        this.type = type;
        this.status = status;
        this.studyAreaId = studyAreaId;
    }

    public Seat(String qrCode, SeatType type, SeatStatus status, long studyAreaId) {
        super();
        this.qrCode = qrCode;
        this.type = type;
        this.status = status;
        this.studyAreaId = studyAreaId;
    }

    public String getQrCode() {
        return qrCode;
    }
    public SeatStatus getStatusId() {
        return status;
    }
    public long getStudyAreaId() {
        return studyAreaId;
    }
    public SeatType getType() {
        return type;
    }
    public boolean isAvailable(){ return status == SeatStatus.AVAILABLE;}
    public void occupy(AccessSession accessSession){
        if(status == SeatStatus.AVAILABLE){
            status = SeatStatus.UNAVAILABLE;
        }else{
            throw new RuntimeErrorException(new Error("Il posto "));
        }
    }
    public void setStatusAvailable(){
        status = SeatStatus.AVAILABLE;
    }

    public void changeSeatType(SeatType newType){
        type = newType;
    }
    public void setSeatBroken() { status = SeatStatus.BROKEN; }
    public boolean isBroken() {return status == SeatStatus.BROKEN; }

}