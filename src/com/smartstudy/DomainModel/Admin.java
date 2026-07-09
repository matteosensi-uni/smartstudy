package com.smartstudy.DomainModel;

public class Admin extends User {
    private boolean is_present;
    private long library_id;

    public Admin(String name, String surname, String email, long library_id){
        super(name, surname, email);
        this.library_id = library_id;
        this.is_present = false;
    }

    public Admin(int id, String name, String surname, String email){
        super(id, name, surname, email);
    }

    public long getLibrary_id() { return library_id; }
    public void accessLibrary(){ this.is_present = true; }
    public void leaveLibrary(){ this.is_present = false; }
    public boolean isPresent() {return this.is_present; }
}
