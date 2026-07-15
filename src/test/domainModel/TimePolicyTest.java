package test.domainModel;

import com.smartstudy.domainModel.TimePolicy;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimePolicyTest {
    @Test
    public void testValueOfSuccess() {
        TimePolicy timePolicy = TimePolicy.valueOf(1, 2, 2, "policy");
        assertEquals(1, timePolicy.getId());
        assertEquals(2, timePolicy.getMaxTemporaryLeaveTimes());
        assertEquals(2, timePolicy.getMaxTemporaryLeaveMin());
        assertEquals("policy", timePolicy.getName());
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            TimePolicy.valueOf(0, 2, 2, "policy")
        );
        assertThrows(DomainViolationException.class, () ->
            TimePolicy.valueOf(1, 0, 2, "policy")
        );
        assertThrows(DomainViolationException.class, () ->
            TimePolicy.valueOf(1, 2, 0, "policy")
        );
        assertThrows(DomainViolationException.class, () ->
            TimePolicy.valueOf(1, 2, 2, "")
        );
    }

    @Test
    public void testReachedLimit() {
        TimePolicy timePolicy = TimePolicy.valueOf(1, 2, 2, "policy");
        assertTrue(timePolicy.reachedLimit(3));
        assertFalse(timePolicy.reachedLimit(0));
    }
}
