package test.domainModel;

import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.enums.SeatStatus;
import com.smartstudy.domainModel.enums.SeatType;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SeatTest {
    @Test
    public void testValueOfSuccess() {
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1);
        assertEquals(1, seat.getId());
        assertEquals("qr", seat.getQrCode());
        assertEquals(SeatType.GROUP, seat.getType());
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
        assertEquals(1, seat.getStudyAreaId());
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(0, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "", SeatType.GROUP, SeatStatus.AVAILABLE, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", null, SeatStatus.AVAILABLE, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", SeatType.GROUP, null, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 0)
        );
    }

    @Test
    public void testOccupySuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1);
        seat.occupy();
        assertEquals(SeatStatus.UNAVAILABLE, seat.getStatus());
    }

    @Test
    public void testOccupyFailure(){
        Seat brokenSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        assertThrows(DomainViolationException.class, brokenSeat::occupy);
        Seat unavailableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, 1);
        assertThrows(DomainViolationException.class, unavailableSeat::occupy);
    }

    @Test
    public void testFreeSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, 1);
        seat.free();
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
    }

    @Test
    public void testFreeFailure(){
        Seat brokenSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        assertThrows(DomainViolationException.class, brokenSeat::free);
        Seat availableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1);
        assertThrows(DomainViolationException.class, availableSeat::free);
    }

    @Test
    public void testRepairSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        seat.repair();
        assertEquals(SeatStatus.AVAILABLE, seat.getStatus());
    }

    @Test
    public void testRepairFailure(){
        Seat unavailableSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, 1);
        assertThrows(DomainViolationException.class, unavailableSeat::repair);
        Seat availableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1);
        assertThrows(DomainViolationException.class, availableSeat::repair);
    }

    @Test
    public void testMarkBrokenSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.AVAILABLE, 1);
        seat.markBroken();
        assertEquals(SeatStatus.BROKEN, seat.getStatus());
    }

    @Test
    public void testMarkBrokenFailure(){
        Seat brokenSeat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        assertThrows(DomainViolationException.class, brokenSeat::markBroken);
        Seat unavailableSeat  = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, 1);
        assertThrows(DomainViolationException.class, unavailableSeat::markBroken);
    }

    @Test
    public void testChangeSeatTypeSuccess(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        seat.changeSeatType(SeatType.INDIVIDUAL);
        assertEquals(SeatType.INDIVIDUAL, seat.getType());
    }

    @Test
    public void testChangeSeatTypeSuccessFailureNullType(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.BROKEN, 1);
        assertThrows(DomainViolationException.class, () ->
            seat.changeSeatType(null)
        );
    }

    @Test
    public void testChangeSeatTypeSuccessFailureSeatUnavailable(){
        Seat seat = Seat.valueOf(1, "qr", SeatType.GROUP, SeatStatus.UNAVAILABLE, 1);
        assertThrows(DomainViolationException.class, () ->
            seat.changeSeatType(SeatType.INDIVIDUAL)
        );
    }
}
