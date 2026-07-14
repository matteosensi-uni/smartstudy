package com.smartstudy.domainModel;

public class Admin extends User {
    private boolean present;
    private final long libraryId;

    private Admin(long id, String name, String surname, String password, String email, boolean present, long libraryId){
        super(id, name, surname, password, email);
        checkId(libraryId, "Library");
        this.libraryId = libraryId;
        this.present = present;
    }

    public static Admin valueOf(long id, String name, String surname, String password, String email, boolean present, long libraryId){
        return  new Admin(id, name, surname, password, email, present, libraryId);
    }

    public long getLibraryId() {return libraryId;}
    public void togglePresence(){
        this.present = !this.present;
    }
    public boolean isPresent() {return this.present;}

    public boolean isAdmin() {return true;}
}