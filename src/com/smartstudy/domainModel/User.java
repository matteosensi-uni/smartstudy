package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public abstract class User extends BaseModel {
    private String name;
    private String surname;
    private String email;
    private String password;

    User(long id, String name, String surname, String password, String email){
        super(id);
        setEmail(email);
        setName(name);
        setSurname(surname);
        setPassword(password);
    }

    private void setName(String name) {
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nome non può essere vuoto");
        }
        this.name = name;
    }

    private void setSurname(String surname) {
        if(surname == null || surname.isBlank()){
            throw new DomainViolationException("Il cognome non può essere vuoto");
        }
        this.surname = surname;
    }

    private void setEmail(String email) {
        if(email == null || email.isBlank()){
            throw new DomainViolationException("L'email non può essere vuota");
        }
        this.email = email;
    }

    private void setPassword(String password) {
        if(password == null || password.isBlank()){
            throw new DomainViolationException("La password non può essere vuota");
        }
        this.password = password;
    }

    public final String getEmail() {return email;}
    public final String getName() {return name;}
    public final String getSurname() {return surname;}
    public final String getPassword() {return password;}
    public abstract boolean isAdmin();
}