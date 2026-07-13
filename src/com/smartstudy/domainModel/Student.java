package com.smartstudy.domainModel;

public class Student extends User {
    private final boolean cardActive;

    private Student(long id, String name, String surname, String password, String email, boolean cardActive){
        super(id, name, surname, password, email);
        this.cardActive = cardActive;
    }
    public static Student valueOf(long id, String name, String surname, String password, String email, boolean cardActive){
        return new Student(id, name, surname, password, email, cardActive);
    }

    public boolean isCardActive() {return cardActive;}

    public boolean isAdmin() {return false;}
}