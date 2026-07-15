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
        checkId(studyAreaId, "StudyArea");
        if(qrCode == null || qrCode.isBlank())
            throw new DomainViolationException("il qrCode è nullo");
        if(type == null)
            throw new DomainViolationException("il type è nullo");
        if(status == null)
            throw new DomainViolationException("Lo status è nullo");
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

    public void repair() {
        if(status != SeatStatus.BROKEN){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.AVAILABLE;
    }

    public void markBroken()  {
        if(status != SeatStatus.AVAILABLE){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.BROKEN;
    }

    public void changeSeatType(SeatType newType){
        if(newType == null){
            throw new DomainViolationException("Il nuovo tipo non può essere nullo");
        }
        if(status == SeatStatus.UNAVAILABLE){
            throw new DomainViolationException("Non è possibile cambiare il tipo di un posto occupato");
        }
        type = newType;
    }

}