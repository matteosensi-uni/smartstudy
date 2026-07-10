package com.smartstudy.DomainModel;

public class Student extends User {
    public boolean cardActive;

    public Student(String name, String surname, String password, String email, boolean cardActive){
        super(name, surname, password, email);
        this.cardActive = cardActive;
    }

    public Student(long id, String name, String surname, String password, String email, boolean cardActive){
        super(id, name, surname, password, email);
        this.cardActive = cardActive;
    }

    public boolean isCardActive() {return cardActive;}
}