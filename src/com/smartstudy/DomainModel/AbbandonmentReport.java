package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.ReportStatus;

import java.time.LocalDateTime;

public class AbbandonmentReport extends BaseModel{
    private LocalDateTime created_at;
    private LocalDateTime resolved_at;
    private ReportStatus status;
    private String description;

    private long id_reservation;
    private long student_id;
    private long admin_id;

    public AbbandonmentReport(long id, LocalDateTime created_at, long id_reservation, String description, long student_id, long admin_id) {
        super(id);
        this.created_at = created_at;
        this.id_reservation = id_reservation;
        this.description = description;
        this.student_id = student_id;
        this.admin_id = admin_id;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getResolved_at() {
        return resolved_at;
    }

    public void setResolved_at(LocalDateTime resolved_at) {
        this.resolved_at = resolved_at;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(long id_reservation) {
        this.id_reservation = id_reservation;
    }

    public long getStudent_id() {
        return student_id;
    }

    public void setStudent_id(long student_id) {
        this.student_id = student_id;
    }

    public long getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(long admin_id) {
        this.admin_id = admin_id;
    }
}
