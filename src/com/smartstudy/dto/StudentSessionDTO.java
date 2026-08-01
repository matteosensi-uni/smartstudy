package com.smartstudy.dto;

import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Student;

public class StudentSessionDTO {
    private final Student user;
    private final boolean presence;
    private Reservation reservation;
    private StudentSessionDTO(Student user, boolean presence, Reservation reservation) {
        this.user = user;
        this.presence = presence;
        this.reservation = reservation;
    }

    public static StudentSessionDTO start(Student user, boolean presence, Reservation reservation) {
        return new StudentSessionDTO(user, presence, reservation);
    }

    public Student getUser() { return user; }
    public boolean isPresent() { return presence; }
    public Reservation getReservation() {
        if (reservation != null)
            reservation.refreshState();
        return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
