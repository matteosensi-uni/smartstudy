package com.smartstudy.DomainModel;

public class Admin extends User {
    private boolean present;
    private final long libraryId;

    public Admin(String name, String password, String surname, String email, boolean present, long libraryId){
        super(name, password, surname, email);
        this.libraryId = libraryId;
        this.present = present;
    }

    public Admin(int id, String name, String password, String surname, String email, boolean present, long libraryId){
        super(id, name, password, surname, email);
        this.libraryId = libraryId;
        this.present = present;
    }

    public long getLibraryId() { return libraryId; }
    public void accessLibrary(){ this.present = true; }
    public void leaveLibrary(){ this.present = false; }
    public boolean isPresent() {return this.present; }
}