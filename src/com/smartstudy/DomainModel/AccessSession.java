package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private final long libraryId;
    private final long userId;

    public AccessSession(LocalDateTime entryTime, long libraryId, int user) {
        this.entryTime = entryTime;
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

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public long getLibraryId() {
        return libraryId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isActive() {return exitTime == null;}

    public void closeSession(LocalDateTime exitTime) throws IllegalArgumentException{
        if(exitTime.isBefore(entryTime)){
            throw new IllegalArgumentException("Exit time non valido");
        }
        this.exitTime = exitTime;
    }
}