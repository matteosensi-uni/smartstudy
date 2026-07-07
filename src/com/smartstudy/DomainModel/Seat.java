package com.smartstudy.DomainModel;
import com.smartstudy.DomainModel.enums.SeatStatus;
import com.smartstudy.DomainModel.enums.SeatType;

public class Seat extends BaseModel{
    private String qr_code;
    private SeatType type;
    private long status_id;
    private long studyArea_id;

    public Seat(long id, String qr_code, SeatType type, long status_id, long studyArea_id) {
        super(id);
        this.qr_code = qr_code;
        this.type = type;
        this.status_id = status_id;
        this.studyArea_id = studyArea_id;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public long getStatusId() {
        return status_id;
    }

    public void setStatusId(long status_id) {
        this.status_id = status_id;
    }

    public long getStudyAreaId() {
        return studyArea_id;
    }

    public void setStudyAreaId(long studyArea_id) {
        this.studyArea_id = studyArea_id;
    }

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        this.type = type;
    }
}
