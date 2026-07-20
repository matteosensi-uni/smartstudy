package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public class Admin extends User {
    private boolean present;

    private Admin(long id, String name, String surname, String password, String email, boolean present){
        super(id, name, surname, password, email);
        this.present = present;
    }

    public static Admin valueOf(long id, String name, String surname, String password, String email, boolean present){
        return  new Admin(id, name, surname, password, email, present);
    }

    public static Admin copy(Admin admin){
        if(admin == null){
            throw new DomainViolationException("L'addmin non può essere nullo");
        }
        return  new Admin(admin.getId(), admin.getName(), admin.getSurname(), admin.getPassword(), admin.getEmail(), admin.isPresent());
    }

    public void togglePresence(){
        this.present = !this.present;
    }
    public boolean isPresent() {return this.present;}
    public boolean isAdmin() {return true;}
}