package test.domainModel;

import com.smartstudy.domainModel.*;
import com.smartstudy.domainModel.enums.*;
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
    private Reservation validReservation;

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
        Seat seat2 = Seat.valueOf(2, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        validReservation = Reservation.start(accessSession, seat2);
    }

    @Test
    public void testStartSuccess() {
        Reservation reservation = Reservation.start(accessSession, seat);
        assertNotNull(reservation.getStartTime());
        assertNull(reservation.getEndTime());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(seat.getId(), reservation.getSeat().getId());
        assertEquals(accessSession.getId(), reservation.getSession().getId());
        assertTrue(reservation.isActive());
        assertTrue(reservation.getTemporaryLeaves().isEmpty());
        assertTrue(reservation.getAbandonmentReports().isEmpty());
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

    @Test
    public void testAddLeaveSuccess(){
        validReservation.addTemporaryLeave();
        assertTrue(validReservation.hasValidTemporaryLeave());
        assertTrue(validReservation.isTemporarilyLeft());
        assertThrows(DomainViolationException.class, () ->
            validReservation.markActive()
        );
    }

    @Test
    public void testAddLeaveFailure(){
        validReservation.addTemporaryLeave();
        assertTrue(validReservation.hasValidTemporaryLeave());
        assertThrows(DomainViolationException.class, () ->
                validReservation.addTemporaryLeave() //non si può aggiungere due reservation nello stesso tempo
        );
    }

    @Test
    public void testAddReportSuccess(){
        Student s = Student.valueOf(2, "Mario", "Rossi", "1234", "m.r@test.it", true);
        AbandonmentReport report = AbandonmentReport.valueOf(1, LocalDateTime.now(), null, ReportStatus.OPENED, "", s, null);
        validReservation.addAbandonmentReport(report);
        assertTrue(validReservation.hasActiveReport());
    }

    @Test
    public void testAddReportFailure(){
        Student s = Student.valueOf(2, "Mario", "Rossi", "1234", "m.r@test.it", true);
        AbandonmentReport report = AbandonmentReport.valueOf(1, LocalDateTime.now(), null, ReportStatus.OPENED, "", s, null);
        validReservation.addAbandonmentReport(report);
        assertTrue(validReservation.hasActiveReport());
        assertThrows(DomainViolationException.class, () ->
                validReservation.addAbandonmentReport(report) //non è possibile aggiungere un report con lo stesso studente
        );

        assertThrows(DomainViolationException.class, () -> {
            Student s1 = Student.valueOf(3, "Mario", "Rossi", "1234", "m.r@test.it", true);
            AbandonmentReport report1 = AbandonmentReport.valueOf(2, LocalDateTime.now(), null, ReportStatus.OPENED, "", s1, null);
            validReservation.addAbandonmentReport(report1); //non è possibile aggiungere due report alla solita reservation
        });
    }

    @Test
    public void testCloseReservationSuccess(){
        Student s = Student.valueOf(2, "Mario", "Rossi", "1234", "m.r@test.it", true);
        AbandonmentReport report = AbandonmentReport.valueOf(1, LocalDateTime.now(), null, ReportStatus.OPENED, "", s, null);
        validReservation.addAbandonmentReport(report);
        assertEquals(1, validReservation.close().size());
        assertEquals(ReservationStatus.CLOSED, validReservation.getStatus());
        assertTrue(validReservation.getSeat().isAvailable());
        assertNotNull(validReservation.getEndTime());
    }
}
