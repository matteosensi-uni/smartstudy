package test.domainModel;

import com.smartstudy.domainModel.*;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ReservationTest {
    private AccessSession accessSession;
    private Seat seat;

    @Before
    public void setUp() {
        ArrayList<Admin> admins = new ArrayList<>();
        admins.add(Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false));
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        Library library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city", admins);
        TimePolicy timePolicy = TimePolicy.valueOf(1, 5, 5, "Strict");
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, timePolicy, library);
        seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        Student s = Student.valueOf(1, "Mario", "Rossi", "1234", "m.r@test.it", true);
        accessSession = AccessSession.valueOf(1, LocalDateTime.now(), null, library, s);
    }

    @Test
    public void testStartSuccess() {
        Reservation reservation = Reservation.start(accessSession, seat);
        assertNotNull(reservation.getStartTime());
        assertNull(reservation.getEndTime());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(seat.getId(), reservation.getSeat().getId());
        assertEquals(accessSession.getId(), reservation.getSession().getId());
    }

    @Test
    public void testStartFailure() {
        assertThrows(DomainViolationException.class, () ->
            Reservation.start(null, seat)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.start(accessSession, null)
        );
    }

    @Test
    public void testValueOfSuccess() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);
        Reservation reservation = Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, new ArrayList<>(), accessSession, seat, new ArrayList<>());
        assertEquals(startTime, reservation.getStartTime());
        assertEquals(endTime, reservation.getEndTime());
        assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
        assertEquals(seat.getId(), reservation.getSeat().getId());
        assertEquals(accessSession.getId(), reservation.getSession().getId());
    }

    @Test
    public void testValueOfFailure() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(-1, startTime, endTime, ReservationStatus.CLOSED, new ArrayList<>(), accessSession, seat, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, null, endTime, ReservationStatus.CLOSED, new ArrayList<>(), accessSession, seat, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, startTime, endTime, ReservationStatus.ACTIVE, new ArrayList<>(), accessSession, seat, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, null, accessSession, seat, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, new ArrayList<>(), null, seat, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, new ArrayList<>(), accessSession, null, new ArrayList<>())
        );
        assertThrows(DomainViolationException.class, () ->
                Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, new ArrayList<>(), accessSession, seat, null)
        );
    }

}
