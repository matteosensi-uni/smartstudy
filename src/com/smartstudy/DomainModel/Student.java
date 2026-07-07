package com.smartstudy.DomainModel;

public class Student extends User {
    public boolean card_active;

    public Student(int id, String name, String surname, String email, boolean card_active){
        super(id, name, surname, email);
        this.card_active = card_active;
    }

    public boolean isCard_active() {
        return card_active;
    }

    public void setCard_active(boolean card_active) {
        this.card_active = card_active;
    }
}
