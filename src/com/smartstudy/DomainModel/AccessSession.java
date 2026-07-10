package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final long libraryId;
    private final long student_id;

    public AccessSession(long libraryId, int user) {
        this.entryTime = LocalDateTime.now();
        this.libraryId = libraryId;
        this.student_id = user;
    }

    public AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, long user) {
        super(id);
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.libraryId = libraryId;
        this.student_id = user;
    }

    public LocalDateTime getEntryTime() {return entryTime;}

    public LocalDateTime getExitTime() {return exitTime;}

    public long getLibraryId() {return libraryId;}

    public long getStudent_id() {return student_id;}

    public boolean isActive() {return exitTime == null;}

    public void closeSession(){
        this.exitTime = LocalDateTime.now();
    }
}