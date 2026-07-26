package DTO;

import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Student;

public class StudentSession {
    private final Student user;
    private final boolean presence;
    private Reservation reservation;
    private StudentSession(Student user, boolean presence, Reservation reservation) {
        this.user = user;
        this.presence = presence;
        this.reservation = reservation;
    }

    public static StudentSession start(Student user, boolean presence, Reservation reservation) {
        return new StudentSession(user, presence, reservation);
    }

    public Student getUser() { return user; }
    public boolean isPresent() { return presence; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
