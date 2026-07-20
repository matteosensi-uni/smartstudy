package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final Library library;
    private final Student student;

    private AccessSession(Library library, Student student) {
        if(student == null || library == null){
            throw new DomainViolationException("I dati non possono essere nulli");
        }
        this.entryTime = LocalDateTime.now();
        this.library = library;
        this.student= student;
    }

    private AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime,  Library library,  Student student) {
        super(id);
        if(student == null || library == null){
            throw new DomainViolationException("I dati non possono essere nulli");
        }
        if(entryTime == null){
            throw new DomainViolationException("la data di ingresso non può essere nulla");
        }
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.library = library;
        this.student = student;
    }

    public static AccessSession startSession(Library library, Student student) {
        return new AccessSession(library, student);
    }

    public static AccessSession valueOf(long id, LocalDateTime entryTime, LocalDateTime exitTime, Library library, Student student) {
        return new AccessSession(id, entryTime, exitTime, library, student);
    }

    public static AccessSession copy(AccessSession accessSession) {
        if(accessSession == null){
            throw new DomainViolationException("AccessSession è nullo");
        }
        return new AccessSession(accessSession.getId(), accessSession.getEntryTime(), accessSession.getExitTime(), accessSession.getLibrary(), accessSession.getStudent());
    }

    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public Library getLibrary() {return library; }
    public Student getStudent() { return student; }
    public boolean isActive() { return exitTime == null; }

    public void closeSession(){
        if(exitTime != null){
            throw new DomainViolationException("La sessione è già stata chiusa");
        }
        this.exitTime = LocalDateTime.now();
    }
}