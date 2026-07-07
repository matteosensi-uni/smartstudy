package com.smartstudy.DomainModel;

public class Admin extends User {

    public Admin(int id, String name, String surname, String email){
        super(id, name, surname, email);
    }

    @Override
    public boolean is_admin() {
        return true;
    }
}
