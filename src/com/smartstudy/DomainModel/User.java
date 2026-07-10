package com.smartstudy.DomainModel;

public abstract class User extends BaseModel {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    public User(String name, String surname, String password, String email){
        super();
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public User(long id, String name, String surname, String password, String email){
        super(id);
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.email = email;
    }

    public String getEmail() {return email;}
    public String getName() {return name;}
    public String getSurname() {return surname;}
    public String getPassword() {return password;}

}