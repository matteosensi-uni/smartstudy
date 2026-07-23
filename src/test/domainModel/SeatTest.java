package test.domainModel;

import com.smartstudy.domainModel.*;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SeatTest {
    private StudyArea studyArea;

    @Before
    public void setUp(){
        ArrayList<Admin> admins = new ArrayList<>();
        admins.add(Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false));
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        Library library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city", admins);
        TimePolicy timePolicy = TimePolicy.valueOf(1, 5, 5, "Strict");
        studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, timePolicy, library);

    }

    @Test
    public void testValueOfSuccess() {
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        assertEquals(1, seat.getId());
        assertEquals("qr", seat.getQrCode());
        assertEquals(SeatType.GROUP, seat.getType());
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertEquals(studyArea.getId(), seat.getStudyArea().getId());
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(0, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", null, SeatStatus.AVAILABLE, studyArea)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", SeatType.GROUP, null, studyArea)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, null)
        );
    }

    @Test
    public void testFreeSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, studyArea);
        seat.free();
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
    }

    @Test
    public void testFreeFailure(){
        Seat brokenSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, studyArea);
        assertThrows(DomainViolationException.class, brokenSeat::free);
        Seat availableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        assertThrows(DomainViolationException.class, availableSeat::free);
    }

    @Test
    public void testRepairSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, studyArea);
        seat.repair();
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
    }

    @Test
    public void testRepairFailure(){
        Seat unavailableSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, studyArea);
        assertThrows(DomainViolationException.class, unavailableSeat::repair);
        Seat availableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        assertThrows(DomainViolationException.class, availableSeat::repair);
    }

    @Test
    public void testMarkBrokenSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        seat.markBroken();
        assertEquals(SeatStatus.BROKEN, seat.getStatus());
    }

    @Test
    public void testMarkBrokenFailure(){
        Seat brokenSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, studyArea);
        assertThrows(DomainViolationException.class, brokenSeat::markBroken);
        Seat unavailableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, studyArea);
        assertThrows(DomainViolationException.class, unavailableSeat::markBroken);
    }

    @Test
    public void testChangeSeatTypeSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, studyArea);
        seat.changeSeatType(SeatType.INDIVIDUAL);
        assertEquals(SeatType.INDIVIDUAL, seat.getType());
    }

    @Test
    public void testChangeSeatTypeSuccessFailureNullType(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, studyArea);
        assertThrows(DomainViolationException.class, () ->
            seat.changeSeatType(null)
        );
    }

    @Test
    public void testChangeSeatTypeSuccessFailureSeatUnavailable(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, studyArea);
        assertThrows(DomainViolationException.class, () ->
            seat.changeSeatType(SeatType.INDIVIDUAL)
        );
    }
}
