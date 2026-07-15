package test.domainModel;

import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.enums.ReservationStatus;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReservationTest {
    @Test
    public void testStartSuccess() {
        Reservation reservation = Reservation.start(1, 1);
        assertNotNull(reservation.getStartTime());
        assertNull(reservation.getEndTime());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(1, reservation.getSeatId());
        assertEquals(1, reservation.getSessionId());
    }

    @Test
    public void testStartFailure() {
        assertThrows(DomainViolationException.class, () ->
            Reservation.start(-1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.start(1, -1)
        );
    }

    @Test
    public void testValueOfSuccess() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);
        Reservation reservation = Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, 1, 1);
        assertEquals(startTime, reservation.getStartTime());
        assertEquals(endTime, reservation.getEndTime());
        assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
        assertEquals(1, reservation.getSeatId());
        assertEquals(1, reservation.getSessionId());
    }

    @Test
    public void testValueOfFailure() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(-1, startTime, endTime, ReservationStatus.CLOSED, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(1, null, endTime, ReservationStatus.CLOSED, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(1, startTime, endTime, ReservationStatus.ACTIVE, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, -1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, 1, -1)
        );
        assertThrows(DomainViolationException.class, () ->
            Reservation.valueOf(1, startTime, null, ReservationStatus.CLOSED, 1, -1)
        );
    }

    @Test
    public void testCloseSuccess() {
        Reservation reservation = Reservation.start(1, 1);
        reservation.close();
        assertEquals(ReservationStatus.CLOSED, reservation.getStatus());
        assertNotNull(reservation.getEndTime());
    }

    @Test
    public void testCloseFailureEndAlreadyClosed() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);
        Reservation reservation = Reservation.valueOf(1, startTime, endTime, ReservationStatus.CLOSED, 1, 1);
        assertThrows(DomainViolationException.class, reservation::close);
    }

    @Test
    public void testMarkTemporarilyLeftSuccess() {
        Reservation reservation = Reservation.start(1, 1);
        reservation.markTemporarilyLeft();
        assertEquals(ReservationStatus.TEMPORARILY_LEFT, reservation.getStatus());
    }

    @Test
    public void testMarkTemporarilyLeftFailure() {
        Reservation reservation = Reservation.start(1, 1);
        reservation.markTemporarilyLeft();
        assertThrows(DomainViolationException.class, reservation::markTemporarilyLeft);
        reservation.close();
        assertThrows(DomainViolationException.class, reservation::markTemporarilyLeft);
    }

    @Test
    public void testMarkActiveSuccess() {
        Reservation reservation = Reservation.start(1, 1);
        reservation.markTemporarilyLeft();
        reservation.markActive();
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
    }

    @Test
    public void testMarkActiveFailure() {
        Reservation reservation = Reservation.start(1, 1);
        assertThrows(DomainViolationException.class, reservation::markActive);
        reservation.close();
        assertThrows(DomainViolationException.class, reservation::markActive);
    }
}
