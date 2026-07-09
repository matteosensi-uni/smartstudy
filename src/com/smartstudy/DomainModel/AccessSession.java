package com.smartstudy.DomainModel;

import java.time.LocalDateTime;

public class AccessSession extends BaseModel{
    private final LocalDateTime entry_time;
    private LocalDateTime exit_time;
    private final long library_id;
    private final long user_id;

    public AccessSession(LocalDateTime entry_time, long library_id, int user) {
        this.entry_time = entry_time;
        this.library_id = library_id;
        this.user_id = user;
    }

    public AccessSession(long id, LocalDateTime entry_time, LocalDateTime exit_time, long library_id, int user) {
        super(id);
        this.entry_time = entry_time;
        this.exit_time = exit_time;
        this.library_id = library_id;
        this.user_id = user;
    }

    public LocalDateTime getEntry_time() {
        return entry_time;
    }

    public LocalDateTime getExit_time() {
        return exit_time;
    }

    public long getLibraryId() {
        return library_id;
    }

    public long getUserId() {
        return user_id;
    }

    public boolean isActive() {return exit_time == null;}

    public void closeSession(LocalDateTime exitTime) throws IllegalArgumentException{
        if(exitTime.isBefore(entry_time)){
            throw new IllegalArgumentException("Exit time non valido");
        }
        this.exit_time = exitTime;
    }
}
