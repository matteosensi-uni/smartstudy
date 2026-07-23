package test.domainModel;

import com.smartstudy.domainModel.TemporaryLeave;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class TemporaryLeaveTest {
    @Test
    public void testStartSuccess(){
        TemporaryLeave temporaryLeave = TemporaryLeave.create(5);
        assertNotNull(temporaryLeave.getStartTime());
        assertNotNull(temporaryLeave.getExpectedEndTime());
        assertEquals(temporaryLeave.getStartTime(), temporaryLeave.getExpectedEndTime().minusMinutes(5));
    }

    @Test
    public void testStartFailure(){
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.create(-1)
        );

    }

    @Test
    public void testValueOfSuccess(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expectedEndTime = LocalDateTime.now().plusMinutes(5);
        TemporaryLeave temporaryLeave = TemporaryLeave.valueOf(1, startTime, expectedEndTime);
        assertEquals(startTime, temporaryLeave.getStartTime());
        assertEquals(expectedEndTime, temporaryLeave.getExpectedEndTime());
        assertEquals(1, temporaryLeave.getId());
        assertTrue(temporaryLeave.isValid());
    }

    @Test
    public void testValueOfFailure(){
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime expectedEndTime = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(0,  startTime, expectedEndTime)
        );
        assertThrows(DomainViolationException.class, () ->
                TemporaryLeave.valueOf(0,  startTime, expectedEndTime)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(1, expectedEndTime, startTime)
        );
        assertThrows(DomainViolationException.class, () ->
            TemporaryLeave.valueOf(1, startTime, null)
        );
        assertThrows(DomainViolationException.class, () ->
                TemporaryLeave.valueOf(1, null, expectedEndTime)
        );
    }
}
