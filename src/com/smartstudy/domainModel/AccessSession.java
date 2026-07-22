package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Library library;
    private Student student;

    private AccessSession(Library library, Student student) {
        setLibrary(library);
        setStudent(student);
        setEntryTime(LocalDateTime.now());
    }

    private AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime,  Library library,  Student student) {
        super(id);
        setEntryTime(entryTime);
        setLibrary(library);
        setStudent(student);
        if(exitTime != null){
            setExitTime(exitTime);
        }
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

    private void setEntryTime(LocalDateTime entryTime) {
        if(entryTime == null)
            throw new DomainViolationException("la data di ingresso è nulla");
        if(exitTime != null &&  exitTime.isBefore(entryTime))
            throw new DomainViolationException("Inserire la data di entrata valida");
        this.entryTime = entryTime;
    }

    private void setLibrary(Library library) {
        if(library == null)
            throw new DomainViolationException("La biblioteca associata all'AccessSession è nulla");
        this.library = library;
    }

    private void setStudent(Student student) {
        if(student == null)
            throw new DomainViolationException("Lo studente associato all'AccessSession è nullo");
        this.student = student;
    }

    private void setExitTime(LocalDateTime exitTime) {
        if(exitTime == null)
            throw new DomainViolationException("La data di uscita è nulla");
        if(entryTime != null &&  exitTime.isBefore(entryTime)){
            throw new DomainViolationException("Inserire la data di uscita valida");
        }
        this.exitTime = exitTime;
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
        setExitTime(LocalDateTime.now());
    }
}