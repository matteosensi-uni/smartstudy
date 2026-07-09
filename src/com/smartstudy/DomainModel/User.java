package com.smartstudy.DomainModel;

public abstract class User extends BaseModel {
    private final String name;
    private final String surname;
    private final String email;
    private final String password;

    public User(String name, String password, String surname, String email){
        super();
        this.name = name;
        this.password = password;
        this.surname = surname;
        this.email = email;
    }

    public User(int id, String name, String password, String surname, String email){
        super(id);
        this.name = name;
        this.password = password;
        this.surname = surname;
        this.email = email;
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
    public String getPassword() {return password; }

}