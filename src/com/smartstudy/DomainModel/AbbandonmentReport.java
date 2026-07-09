package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.ReportStatus;

import java.time.LocalDateTime;

public class AbbandonmentReport extends BaseModel{
    private final LocalDateTime created_at;
    private LocalDateTime resolved_at;
    private ReportStatus status;
    private final String description;

    private final long id_reservation;
    private final long student_id;
    private long admin_id;

    public AbbandonmentReport(LocalDateTime created_at, long id_reservation, String description, long student_id, long admin_id) {
        super();
        this.created_at = created_at;
        this.id_reservation = id_reservation;
        this.description = description;
        this.student_id = student_id;
        this.admin_id = admin_id;
    }

    public AbbandonmentReport(long id, LocalDateTime created_at, LocalDateTime resolved_at, long id_reservation, String description, long student_id, long admin_id) {
        super(id);
        this.created_at = created_at;
        this.resolved_at = resolved_at;
        this.id_reservation = id_reservation;
        this.description = description;
        this.student_id = student_id;
        this.admin_id = admin_id;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public LocalDateTime getResolved_at() {
        return resolved_at;
    }
    public ReportStatus getStatus() {
        return status;
    }
    public String getDescription() {
        return description;
    }
    public long getId_reservation() {
        return id_reservation;
    }
    public long getStudent_id() {
        return student_id;
    }
    public long getAdmin_id() {
        return admin_id;
    }

    public void takeInCharge(long admin_id){
        if(status == ReportStatus.SENDED){
            this.admin_id = admin_id;
            status = ReportStatus.PENDING;
        }
    }

    public void confirm(long admin_id) throws IllegalAccessException {
        handleReport(admin_id, ReportStatus.CONFIRMED);
    }

    public void reject(long admin_id) throws IllegalAccessException {
        handleReport(admin_id, ReportStatus.REJECTED);
    }

    private void handleReport(long admin_id, ReportStatus action) throws IllegalAccessException {
        if (status == ReportStatus.PENDING) {
            if (admin_id == this.admin_id) {
                status = action;
            } else {
                throw new IllegalAccessException("Il report è gestito da un admin diverso");
            }
        }else{
            throw new IllegalAccessException("Il report non può essere ancora gestito, è necessario che qualcuno lo prenda in carico!");
        }
    }


}
