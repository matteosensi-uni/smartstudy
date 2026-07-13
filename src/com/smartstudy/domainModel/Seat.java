package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.exceptions.DomainViolationException;

public class Seat extends BaseModel{
    private final String qrCode;
    private SeatType type;
    private SeatStatus status;
    private final long studyAreaId;

    private Seat(long id, String qrCode, SeatType type, SeatStatus status, long studyAreaId) {
        super(id);
        this.qrCode = qrCode;
        this.type = type;
        this.status = status;
        this.studyAreaId = studyAreaId;
    }

    public static Seat valueOf(long id, String qrCode, SeatType type, SeatStatus status, long studyAreaId) {
        return new Seat(id, qrCode, type, status, studyAreaId);
    }

    public String getQrCode() {return qrCode;}
    public SeatStatus getStatus() {return status;}
    public long getStudyAreaId() {return studyAreaId;}
    public SeatType getType() {return type;}
    public boolean isAvailable(){ return status == SeatStatus.AVAILABLE;}
    public boolean isBroken() {return status == SeatStatus.BROKEN; }

    public void occupy() {
        if(status != SeatStatus.AVAILABLE){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.UNAVAILABLE;
    }
    public void free() {
        if(status != SeatStatus.UNAVAILABLE){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.AVAILABLE;
    }

    public void markBroken()  {
        if(status != SeatStatus.AVAILABLE){
            throw new IllegalStateException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.BROKEN;
    }

    public void changeSeatType(SeatType newType){
        type = newType;
    }

}