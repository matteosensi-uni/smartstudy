package com.smartstudy.DomainModel;

public abstract class User extends BaseModel {
    private String name;
    private String surname;
    private String email;

    public User(int id, String name, String surname, String email){
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public boolean is_admin() {
        return false;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

