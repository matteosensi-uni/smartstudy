package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final long libraryId;
    private final long studentId;

    private AccessSession(long libraryId, long studentId) {
        checkId(studentId, "Student");
        checkId(libraryId, "Library");
        this.entryTime = LocalDateTime.now();
        this.libraryId = libraryId;
        this.studentId = studentId;
    }

    private AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, long studentId) {
        super(id);
        checkId(studentId, "Student");
        checkId(libraryId, "Library");
        if(entryTime == null){
            throw new DomainViolationException("Il tempo di inizio è nullo");
        }
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.libraryId = libraryId;
        this.studentId = studentId;
    }

    public static AccessSession startSession(long libraryId, long studentId) {
        return new AccessSession(libraryId, studentId);
    }

    public static AccessSession valueOf(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, long user) {
        return new AccessSession(id, entryTime, exitTime, libraryId, user);
    }

    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getLibraryId() { return libraryId; }
    public long getStudentId() { return studentId; }
    public boolean isActive() { return exitTime == null; }

    public void closeSession(long studentId, long libraryId){
        if(exitTime != null){
            throw new DomainViolationException("La sessione è già stata chiusa");
        }
        checkId(studentId, "Student");
        checkId(libraryId, "Library");
        if(libraryId != this.libraryId){
            throw new DomainViolationException("La sessione non appartiene a questa biblioteca");
        }
        if(studentId != this.studentId){
            throw new DomainViolationException("L'utente non è lo stesso della sessione");
        }
        this.exitTime = LocalDateTime.now();
    }
}