package com.smartstudy.DomainModel;

import com.smartstudy.exceptions.DomainViolationException;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final long libraryId;
    private final long student_id;

    private AccessSession(long libraryId, long user) {
        this.entryTime = LocalDateTime.now();
        this.libraryId = libraryId;
        this.student_id = user;
    }

    private AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, long user) {
        super(id);
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.libraryId = libraryId;
        this.student_id = user;
    }

    public static AccessSession startSession(long libraryId, long user) {
        return new AccessSession(libraryId, user);
    }

    public static AccessSession valueOf(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, long user) {
        return new AccessSession(id, entryTime, exitTime, libraryId, user);
    }

    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getLibraryId() { return libraryId; }
    public long getStudent_id() { return student_id; }
    public boolean isActive() { return exitTime == null; }

    public void closeSession(long studentId, long libraryId){
        if(exitTime != null){
            throw new DomainViolationException("La sessione è già stata chiusa");
        }
        if(libraryId != this.libraryId){
            throw new DomainViolationException("La sessione non appartiene a questa biblioteca");
        }
        if(studentId != this.student_id){
            throw new DomainViolationException("L'utente non è lo stesso della sessione");
        }
        this.exitTime = LocalDateTime.now();
    }
}