package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final long libraryId;
    private final long userId;

    public AccessSession(long libraryId, int user) {
        this.entryTime = LocalDateTime.now();
        this.libraryId = libraryId;
        this.userId = user;
    }

    public AccessSession(long id, LocalDateTime entryTime, LocalDateTime exitTime, long libraryId, int user) {
        super(id);
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.libraryId = libraryId;
        this.userId = user;
    }

    public LocalDateTime getEntryTime() {return entryTime;}

    public LocalDateTime getExitTime() {return exitTime;}

    public long getLibraryId() {return libraryId;}

    public long getUserId() {return userId;}

    public boolean isActive() {return exitTime == null;}

    public void closeSession(){
        this.exitTime = LocalDateTime.now();
    }
}