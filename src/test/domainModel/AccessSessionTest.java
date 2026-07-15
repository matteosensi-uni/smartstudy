package test.domainModel;

import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class AccessSessionTest {
    @Test
    public void testStartSessionFailure() {
        assertThrows(DomainViolationException.class, () ->
            AccessSession.startSession(1, 0)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.startSession(0, 1)
        );
    }

    @Test
    public void testStartSessionSuccess() {
        AccessSession accessSession = AccessSession.startSession(1, 1);
        assertEquals(1, accessSession.getLibraryId());
        assertEquals(1, accessSession.getStudentId());
        assertNull(accessSession.getExitTime());
        assertNotNull(accessSession.getEntryTime());
    }

    @Test
    public void testValueOfSuccess(){
        LocalDateTime entryTime = LocalDateTime.now();
        LocalDateTime exitTime = LocalDateTime.now().plusMinutes(5);
        AccessSession accessSession = AccessSession.valueOf(1, entryTime, exitTime, 1, 1);
        assertEquals(1, accessSession.getLibraryId());
        assertEquals(1, accessSession.getStudentId());
        assertEquals(exitTime, accessSession.getExitTime());
        assertEquals(entryTime, accessSession.getEntryTime());
    }

    @Test
    public void testValueOfFailure(){
        LocalDateTime entryTime = LocalDateTime.now();
        LocalDateTime exitTime = LocalDateTime.now().plusMinutes(5);
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, null, exitTime, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, entryTime, exitTime, 0, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, entryTime, exitTime, 1, 0)
        );
    }
    @Test
    public void testCloseSessionFailure(){
        AccessSession accessSession = AccessSession.startSession(1, 1);
        assertTrue(accessSession.isActive());
        accessSession.closeSession();
        assertFalse(accessSession.isActive());
    }
    @Test
    public void testCloseSessionSuccess(){
        assertThrows(DomainViolationException.class, () -> {
            LocalDateTime entryTime = LocalDateTime.now().plusMinutes(5);
            AccessSession as = AccessSession.valueOf(1, entryTime, entryTime, 1, 1);
            as.closeSession();
        });
    }
}
