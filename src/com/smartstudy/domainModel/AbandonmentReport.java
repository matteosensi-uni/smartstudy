package com.smartstudy.domainModel;

import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AbandonmentReport extends BaseModel{
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private ReportStatus status;
    private final String description;
    private final Student author;
    private Admin admin;

    private AbandonmentReport(String description,  Student author) {
        super();
        if(author == null)
            throw new DomainViolationException("L'autore non può essere nullo");
        this.createdAt = LocalDateTime.now();
        this.description = description;
        this.author = author;
        this.admin = null;
        this.status = ReportStatus.OPENED;
    }

    private AbandonmentReport(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status , String description, Student author, Admin admin) {
        super(id);
        if(createdAt == null){
            throw new DomainViolationException("La data di creazione non può essere nulla");
        }
        if(status == null){
            throw new DomainViolationException("Lo stato del report non può essere nullo");
        }
        if(author == null){
            throw new DomainViolationException("L'autore del report non può essere nullo");
        }
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.description = description;
        this.author = author;
        this.status = status;
        this.admin = Admin.copy(admin);
    }

    public static AbandonmentReport open(String description, Student student) {
        return new AbandonmentReport(description, student);
    }

    public static AbandonmentReport valueOf(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status , String description, Student student, Admin admin){
        return new AbandonmentReport(id, createdAt, resolvedAt, status, description, student, admin);
    }

    public static AbandonmentReport copy(AbandonmentReport abandonmentReport) {
        return new AbandonmentReport(abandonmentReport.getId(), abandonmentReport.getCreatedAt(), abandonmentReport.getResolvedAt(), abandonmentReport.getStatus(), abandonmentReport.getDescription(),  abandonmentReport.getAuthor(), abandonmentReport.getAdmin());
    }

    public LocalDateTime getCreatedAt() {return  createdAt;}
    public LocalDateTime getResolvedAt() {return resolvedAt;}
    public ReportStatus getStatus() {return status;}
    public String getDescription() {return description;}
    public Student getAuthor() {return author;}
    public boolean isActive() {
        return status == ReportStatus.OPENED;
    }
    public Admin getAdmin() {
        return Admin.copy(admin);
    }

    public void takeInCharge(Admin admin) {
        if(admin == null){
            throw new DomainViolationException("Inserire un admin valido");
        }
        if(status == ReportStatus.OPENED){
            this.admin = Admin.copy(admin);
            status = ReportStatus.PENDING;
        }else{
            throw new DomainViolationException("Lo stato è già stato gestito");
        }
    }

    void close(){ //la prenotazione è stata chiusa prima che un admin abbia gestito la prenotazione
        if(status == ReportStatus.PENDING || status == ReportStatus.OPENED){
            this.status = ReportStatus.CLOSED;
        }else {
            throw new DomainViolationException("La prenotazione è già stata gestita");
        }
    }

    public void confirm(Admin admin) {
        handleReport(admin, ReportStatus.CONFIRMED);
    }

    public void reject(Admin admin) {
        handleReport(admin, ReportStatus.REJECTED);
    }

    private void handleReport(Admin admin, ReportStatus finalState) {
        if (status != ReportStatus.PENDING) {
            throw new DomainViolationException("Il report non può essere gestito");
        }
        if(admin == null){
            throw new DomainViolationException("Inserire un admin valido");
        }
        if (admin.getId() != this.admin.getId()) {
            throw new DomainViolationException("Il report è gestito da un admin diverso");
        }
        status = finalState;
        resolvedAt = LocalDateTime.now();
    }


}