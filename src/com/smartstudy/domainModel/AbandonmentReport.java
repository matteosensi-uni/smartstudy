package com.smartstudy.domainModel;

import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AbandonmentReport extends BaseModel{
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private ReportStatus status;
    private String description;
    private Student author;
    private Admin admin;

    private AbandonmentReport(String description,  Student author) {
        super();
        setCreatedAt(LocalDateTime.now());
        setStatus(ReportStatus.OPENED);
        setDescription(description);
        setAuthor(author);
    }

    private AbandonmentReport(long id, LocalDateTime createdAt, LocalDateTime resolvedAt, ReportStatus status , String description, Student author, Admin admin) {
        super(id);
        setCreatedAt(createdAt);
        setResolvedAt(resolvedAt);
        setStatus(status);
        setDescription(description);
        setAuthor(author);
        setAdmin(admin);
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

    private void setCreatedAt(LocalDateTime createdAt) {
        if(createdAt == null){
            throw new DomainViolationException("La data di creazione non può essere nulla");
        }
        this.createdAt = createdAt;
    }

    private void setResolvedAt(LocalDateTime resolvedAt) {
        if(resolvedAt != null && createdAt != null && resolvedAt.isBefore(createdAt)){
            throw new DomainViolationException("Inserire una data corretta");
        }
        this.resolvedAt = resolvedAt;
    }

    private void setStatus(ReportStatus status) {
        if(status == null){
            throw new DomainViolationException("Lo stato del report non può essere nullo");
        }
        this.status = status;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setAuthor(Student author) {
        if(author == null)
            throw new DomainViolationException("L'autore non può essere nullo");
        this.author = author;
    }

    private void setAdmin(Admin admin) {
        if(admin == null && status != ReportStatus.OPENED && status != ReportStatus.CLOSED){
            throw new DomainViolationException("Inserire un admin valido");
        }
        if(admin != null && (status ==  ReportStatus.OPENED ||  status == ReportStatus.CLOSED)){
            throw new DomainViolationException("Non può essere inserito un admin in un report aperto e non gestito");
        }
        this.admin = admin == null ? null : Admin.copy(admin);
    }

    public LocalDateTime getCreatedAt() {return  createdAt;}
    public LocalDateTime getResolvedAt() {return resolvedAt;}
    public ReportStatus getStatus() {return status;}
    public String getDescription() {return description;}
    public Student getAuthor() {return author;}
    public boolean isActive() {
        return status == ReportStatus.OPENED || status == ReportStatus.PENDING;
    }

    public Admin getAdmin() {
        return admin == null ? null : Admin.copy(admin);
    }

    public void takeInCharge(Admin admin) {
        if(status != ReportStatus.OPENED){
            throw new DomainViolationException("Il report non può essere preso in carico");
        }
        setStatus(ReportStatus.PENDING);
        setAdmin(admin);
    }

    void close(){ //la prenotazione è stata chiusa prima che un admin abbia gestito la prenotazione
        if(status == ReportStatus.PENDING || status == ReportStatus.OPENED){
            setStatus(ReportStatus.CLOSED);
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
        setStatus(finalState);
        setResolvedAt(LocalDateTime.now());
    }


}