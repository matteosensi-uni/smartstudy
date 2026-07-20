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

    public static Student copy(Student student){
        if(student == null){
            throw new  IllegalArgumentException("Lo studente non può essere nullo");
        }
        return new Student(student.getId(), student.getName(),  student.getSurname(), student.getPassword(), student.getEmail(), student.isCardActive());
    }

    public boolean isCardActive() {return cardActive;}

    public boolean isAdmin() {return false;}
}