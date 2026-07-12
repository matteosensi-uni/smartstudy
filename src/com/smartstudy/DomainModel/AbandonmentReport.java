package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.ReportStatus;

import java.time.LocalDateTime;

public class AbandonmentReport extends BaseModel{
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private ReportStatus status;
    private final String description;
    private final long reservationId;
    private final long studentId;
    private Long adminId;

    private AbandonmentReport(String description,long reservationId,  long studentId, long adminId) {
        super();
        this.createdAt = LocalDateTime.now();
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.adminId = adminId;
        this.status = ReportStatus.OPENED;
    }

    private AbandonmentReport(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status ,String description, long reservationId, long studentId, Long adminId) {
        super(id);
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.status = status;
        this.adminId = adminId;
    }

    public static AbandonmentReport open(String description, long reservationId, long studentId, long adminId) {
        return new AbandonmentReport(description, reservationId, studentId, adminId);
    }

    public static AbandonmentReport valueOf(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status , String description, long reservationId, long studentId, Long adminId){
        return new AbandonmentReport(id, createdAt, resolvedAt, status, description, reservationId, studentId, adminId);
    }

    public LocalDateTime getCreatedAt() {return  createdAt;}
    public LocalDateTime getResolvedAt() {return resolvedAt;}
    public ReportStatus getStatus() {return status;}
    public String getDescription() {return description;}
    public long getReservationId() {return reservationId;}
    public long getStudentId() {return studentId;}
    public long getAdminId() {return adminId;}

    public void takeInCharge(long adminId) throws IllegalStateException{
        if(status == ReportStatus.OPENED && this.adminId == null){
            this.adminId = adminId;
            status = ReportStatus.PENDING;
        }else{
            throw new IllegalStateException("Lo stato è già stato gestito");
        }
    }

    public void confirm(long adminId) throws IllegalAccessException, IllegalStateException {
        handleReport(adminId, ReportStatus.CONFIRMED);
    }

    public void reject(long adminId) throws IllegalAccessException, IllegalStateException {
        handleReport(adminId, ReportStatus.REJECTED);
    }

    private void handleReport(long adminId, ReportStatus finalState) throws IllegalAccessException, IllegalStateException {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException("Il report non può essere gestito");
        }
        if (adminId != this.adminId) {
            throw new IllegalAccessException("Il report è gestito da un admin diverso");
        }
        status = finalState;
    }


}