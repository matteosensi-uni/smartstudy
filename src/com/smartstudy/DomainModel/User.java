package com.smartstudy.DomainModel;

public abstract class User extends BaseModel {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    User(long id, String name, String surname, String password, String email){
        super(id);
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public final String getEmail() {return email;}
    public final String getName() {return name;}
    public final String getSurname() {return surname;}
    public final String getPassword() {return password;}
}