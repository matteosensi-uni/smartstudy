package com.smartstudy.domainModel;

import com.smartstudy.exceptions.DomainViolationException;

public abstract class User extends BaseModel {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    User(long id, String name, String surname, String password, String email){
        super(id);
        if(name == null || name.isBlank()){
            throw new DomainViolationException("Il nome non può essere vuoto");
        }
        if(surname == null || surname.isBlank()){
            throw new DomainViolationException("Il cognome non può essere vuoto");
        }
        if(password == null || password.isBlank()){
            throw new DomainViolationException("La password non può essere vuota");
        }
        if(email == null || email.isBlank()){
            throw new DomainViolationException("L'email non può essere vuota");
        }
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public final String getEmail() {return email;}
    public final String getName() {return name;}
    public final String getSurname() {return surname;}
    public final String getPassword() {return password;}
    public abstract boolean isAdmin();
}