package com.smartstudy.DomainModel;

import com.smartstudy.DomainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AbandonmentReport extends BaseModel{
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private ReportStatus status;
    private final String description;
    private final long reservationId;
    private final long studentId;
    private Long adminId;

    private AbandonmentReport(String description,long reservationId,  long studentId) {
        super();
        this.createdAt = LocalDateTime.now();
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.adminId = null;
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

    public static AbandonmentReport open(String description, long reservationId, long studentId) {
        return new AbandonmentReport(description, reservationId, studentId);
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
    public Long getAdminId() {return adminId;}

    public void takeInCharge(long adminId) {
        if(status == ReportStatus.OPENED && this.adminId == null){
            this.adminId = adminId;
            status = ReportStatus.PENDING;
        }else{
            throw new DomainViolationException("Lo stato è già stato gestito");
        }
    }

    public void confirm(long adminId) {
        handleReport(adminId, ReportStatus.CONFIRMED);
    }

    public void reject(long adminId) {
        handleReport(adminId, ReportStatus.REJECTED);
    }

    private void handleReport(long adminId, ReportStatus finalState) {
        if (status != ReportStatus.PENDING) {
            throw new DomainViolationException("Il report non può essere gestito");
        }
        if (this.adminId == null || adminId != this.adminId) {
            throw new DomainViolationException("Il report è gestito da un admin diverso");
        }
        status = finalState;
    }


}