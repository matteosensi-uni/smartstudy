package com.smartstudy.domainModel;

import com.smartstudy.domainModel.enums.ReportStatus;
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
        checkId(reservationId, "Reservation");
        checkId(studentId, "Student");
        this.createdAt = LocalDateTime.now();
        this.reservationId = reservationId;
        this.description = description;
        this.studentId = studentId;
        this.adminId = null;
        this.status = ReportStatus.OPENED;
    }

    private AbandonmentReport(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status ,String description, long reservationId, long studentId, Long adminId) {
        super(id);
        checkId(reservationId, "Reservation");
        checkId(studentId, "Student");
        if(createdAt == null){
            throw new DomainViolationException("La data di creazione non può essere nulla");
        }
        if(status == null){
            throw new DomainViolationException("Lo stato non può essere nullo");
        }
        if(status == ReportStatus.OPENED && adminId != null){
            throw new DomainViolationException("Non è possibile caricare uno stato OPENED con un admin associato");
        }
        if(status != ReportStatus.OPENED && adminId == null){
            throw new DomainViolationException("Non è possibile caricare uno stato non OPENED senza un admin associato");
        }
        if((status == ReportStatus.CONFIRMED || status == ReportStatus.REJECTED) && resolvedAt == null){
            throw new DomainViolationException("Non è possibile caricare uno stato gestito senza una data di gestione");
        }
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
            checkId(adminId, "Admin");
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
        checkId(adminId, "Admin");
        this.adminId = adminId;
        status = finalState;
        resolvedAt = LocalDateTime.now();
    }


}