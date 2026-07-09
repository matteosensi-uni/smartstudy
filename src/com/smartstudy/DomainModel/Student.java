package com.smartstudy.DomainModel;

public class Student extends User {
    public boolean cardActive;

    public Student(String name, String password, String surname, String email, boolean cardActive){
        super(name,password, surname, email);
        this.cardActive = cardActive;
    }

    public Student(int id, String name, String password, String surname, String email, boolean cardActive){
        super(id, name, password, surname, email);
        this.cardActive = cardActive;
    }

    public boolean isCardActive() {
        return cardActive;
    }
}