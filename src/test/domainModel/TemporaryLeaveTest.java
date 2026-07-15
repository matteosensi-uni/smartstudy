package test.domainModel;

import com.smartstudy.domainModel.TemporaryLeave;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TemporaryLeaveTest {
    @Test
    public void testStartSuccess(){
        TemporaryLeave temporaryLeave = TemporaryLeave.create(5, 1);
        assertNotNull(temporaryLeave.getStartTime());
        assertNotNull(temporaryLeave.getExpectedEndTime());
        assertEquals(temporaryLeave.getStartTime(), temporaryLeave.getExpectedEndTime().minusMinutes(5));
        assertEquals(1, temporaryLeave.getReservationId());
    }

    @Test
    public void testStartFailure(){
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.create(-1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.create(5, 0)
        );
    }

    @Test
    public void testValueOfSuccess(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expectedEndTime = LocalDateTime.now().plusMinutes(5);
        TemporaryLeave temporaryLeave = TemporaryLeave.valueOf(1, startTime, expectedEndTime, 2);
        assertEquals(startTime, temporaryLeave.getStartTime());
        assertEquals(expectedEndTime, temporaryLeave.getExpectedEndTime());
        assertEquals(2, temporaryLeave.getReservationId());
        assertEquals(1, temporaryLeave.getId());
    }

    @Test
    public void testValueOfFailure(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expectedEndTime = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(0,  startTime, expectedEndTime, 2)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(1, null, expectedEndTime, 2)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(1, startTime, null, 2)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(1, startTime, expectedEndTime, 0)
        );
    }
}
