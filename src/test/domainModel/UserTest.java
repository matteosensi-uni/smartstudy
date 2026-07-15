package test.domainModel;

import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testStudentValueOfSuccess() {
        Student student = Student.valueOf(1, "Mario", "Rossi", "1234", "mario@rossi", true);
        assertEquals(1, student.getId());
        assertEquals("Mario", student.getName());
        assertEquals("Rossi", student.getSurname());
        assertEquals("1234", student.getPassword());
        assertEquals("mario@rossi", student.getEmail());
        assertTrue(student.isCardActive());
        assertFalse(student.isAdmin());
    }
    @Test
    public void testAdminValueOfSuccess() {
        Admin admin = Admin.valueOf(1, "Mario", "Rossi", "1234", "mario@rossi", true, 1);
        assertEquals(1, admin.getId());
        assertEquals("Mario", admin.getName());
        assertEquals("Rossi", admin.getSurname());
        assertEquals("1234", admin.getPassword());
        assertEquals("mario@rossi", admin.getEmail());
        assertTrue(admin.isPresent());
        assertTrue(admin.isAdmin());
        assertEquals(1, admin.getLibraryId());
    }

    @Test
    public void togglePresenceSuccess(){
        Admin admin = Admin.valueOf(1, "Mario", "Rossi", "1234", "mario@rossi", true, 1);
        assertTrue(admin.isPresent());
        admin.togglePresence();
        assertFalse(admin.isPresent());
    }
    @Test
    public void testValueOfFailure() {
        //Per student non c'è bisogno di ripeterli in quanto fino ad 'email' dipendono dalla classe User in comune e l'ultimo parametro è un boolean
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(0, "Mario", "Rossi", "1234", "mario@rossi", true, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(1, "", "Rossi", "1234", "mario@rossi", true, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(1, "Mario", "", "1234", "mario@rossi", true, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(1, "Mario", "Rossi", "", "mario@rossi", true, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(1, "Mario", "Rossi", "1234", "", true, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            Admin.valueOf(1, "Mario", "Rossi", "1234", "", true, 0)
        );
    }

}
