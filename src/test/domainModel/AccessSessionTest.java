package test.domainModel;

import com.smartstudy.domainModel.AccessSession;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Library;
import com.smartstudy.domainModel.Student;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class AccessSessionTest {
    private Student s;
    private Library library;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    @Before
    public void setUp(){
        ArrayList<Admin> admins = new ArrayList<>();
        admins.add(Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false));
        s = Student.valueOf(1, "Mario", "Rossi", "1234", "m.r@test.it", true);
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city", admins);
        entryTime = LocalDateTime.now();
        exitTime = LocalDateTime.now().plusMinutes(5);
    }
    @Test
    public void testStartSessionFailure() {
        assertThrows(DomainViolationException.class, () ->
            AccessSession.startSession(library, null)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.startSession(null, s)
        );
    }

    @Test
    public void testStartSessionSuccess() {
        AccessSession accessSession = AccessSession.startSession(library, s);
        assertEquals(library.getId(), accessSession.getLibrary().getId());
        assertEquals(s.getId(), accessSession.getStudent().getId());
        assertNull(accessSession.getExitTime());
        assertNotNull(accessSession.getEntryTime());
    }

    @Test
    public void testValueOfSuccess(){
        AccessSession accessSession = AccessSession.valueOf(1, entryTime, exitTime, library, s);
        assertEquals(library.getId(), accessSession.getLibrary().getId());
        assertEquals(s.getId(), accessSession.getStudent().getId());
        assertEquals(exitTime, accessSession.getExitTime());
        assertEquals(entryTime, accessSession.getEntryTime());
    }

    @Test
    public void testValueOfFailure(){
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, null, exitTime, library, s)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, entryTime, exitTime, null, s)
        );
        assertThrows(DomainViolationException.class, () ->
            AccessSession.valueOf(1, entryTime, exitTime, library, null)
        );
    }
    @Test
    public void testCloseSessionSuccess(){
        AccessSession accessSession = AccessSession.startSession(library, s);
        assertTrue(accessSession.isActive());
        accessSession.closeSession();
        assertFalse(accessSession.isActive());
    }
    @Test
    public void testCloseSessionFailure(){
        assertThrows(DomainViolationException.class, () -> {
            AccessSession as = AccessSession.valueOf(1, entryTime, exitTime, library, s);
            as.closeSession();
        });
    }
}
