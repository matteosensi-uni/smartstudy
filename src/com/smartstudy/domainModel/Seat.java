package com.smartstudy.domainModel;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.exceptions.DomainViolationException;

public class Seat extends BaseModel{
    private final String qrCode;
    private SeatType type;
    private SeatStatus status;
    private final StudyArea area;

    private Seat(long id, String qrCode, SeatType type, SeatStatus status, StudyArea area) {
        super(id);
        if(qrCode == null || qrCode.isBlank())
            throw new DomainViolationException("il qrCode è nullo");
        if(type == null)
            throw new DomainViolationException("il type è nullo");
        if(status == null)
            throw new DomainViolationException("Lo status è nullo");
        if(area == null){
            throw new DomainViolationException("Il posto deve essere associato ad un'area studio");
        }
        this.qrCode = qrCode;
        this.type = type;
        this.status = status;
        this.area = area;
    }

    public static Seat valueOf(long id, String qrCode, SeatType type, SeatStatus status, StudyArea area) {
        return new Seat(id, qrCode, type, status, area);
    }

    public static Seat copy(Seat seat) {
        return new Seat(seat.getId(), seat.getQrCode(), seat.getType(), seat.getStatus(), seat.getStudyArea());
    }

    public StudyArea getStudyArea(){ return StudyArea.copy(area); }
    public String getQrCode() {return qrCode;}
    public SeatStatus getStatus() {return status;}
    public SeatType getType() {return type;}
    public boolean isAvailable(){ return status == SeatStatus.AVAILABLE;}
    public boolean isBroken() {return status == SeatStatus.BROKEN; }
    public boolean isUnavailable(){ return status == SeatStatus.UNAVAILABLE;}

    void occupy() {
        if(!isAvailable()){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.UNAVAILABLE;
    }
    void free() {
        if(!isUnavailable()){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.AVAILABLE;
    }

    public void repair() {
        if(!isBroken()){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.AVAILABLE;
    }

    public void markBroken()  {
        if(!isAvailable()){
            throw new DomainViolationException("Lo stato del posto non può essere modificato");
        }
        status = SeatStatus.BROKEN;
    }

    public void changeSeatType(SeatType newType){
        if(newType == null){
            throw new DomainViolationException("Il nuovo tipo non può essere nullo");
        }
        if(!isUnavailable() || isBroken()){
            throw new DomainViolationException("Non è possibile cambiare il tipo di un posto occupato");
        }
        type = newType;
    }

}