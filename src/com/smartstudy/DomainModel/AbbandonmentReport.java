package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.ReportStatus;

import java.time.LocalDateTime;

public class AbbandonmentReport extends BaseModel{
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private ReportStatus status;
    private final String description;
    private final long reservationId;
    private final long studentId;
    private Long adminId;

    public AbbandonmentReport(long reservationId, String description, long studentId, long adminId) {
        super();
        this.createdAt = LocalDateTime.now();
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.adminId = adminId;
        this.status = ReportStatus.OPENED;
    }

    public AbbandonmentReport(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, long reservationId, String description, long studentId, Long adminId) {
        super(id);
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.adminId = adminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    public ReportStatus getStatus() {
        return status;
    }
    public String getDescription() {
        return description;
    }
    public long getReservationId() {
        return reservationId;
    }
    public long getStudentId() {
        return studentId;
    }
    public long getAdminId() {
        return adminId;
    }

    public void takeInCharge(long adminId){
        if(status == ReportStatus.OPENED){
            this.adminId = adminId;
            status = ReportStatus.PENDING;
        }
    }

    public void confirm(long adminId) throws IllegalAccessException {
        handleReport(adminId, ReportStatus.CONFIRMED);
    }

    public void reject(long adminId) throws IllegalAccessException {
        handleReport(adminId, ReportStatus.REJECTED);
    }

    private void handleReport(long adminId, ReportStatus action) throws IllegalAccessException {
        if (status == ReportStatus.PENDING) {
            if (adminId == this.adminId) {
                status = action;
            } else {
                throw new IllegalAccessException("Il report è gestito da un admin diverso");
            }
        }else{
            throw new IllegalAccessException("Il report non può essere ancora gestito, è necessario che qualcuno lo prenda in carico!");
        }
    }


}