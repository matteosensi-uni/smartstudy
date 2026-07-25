package DTO;

import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Student;

public class StudentSession {
    private final Student user;
    private final boolean presence;
    private Reservation reservation;
    private StudentSession(Student user, boolean presence) {
        this.user = user;
        this.presence = presence;
    }

    public static StudentSession start(Student user, boolean presence) {
        return new StudentSession(user, presence);
    }

    public Student getUser() { return user; }
    public boolean isPresent() { return presence; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
}
