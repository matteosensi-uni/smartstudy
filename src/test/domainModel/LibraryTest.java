package test.domainModel;

import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Library;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class LibraryTest {
    private ArrayList<Admin> admins;
    private LocalTime openingTime;
    private LocalTime closingTime;
    @Before
    public void setUp() {
        admins = new ArrayList<>();
        admins.add(Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false));
        openingTime = LocalTime.of(1,0,0);
        closingTime = LocalTime.of(23,0,0);
    }

    @Test
    public void testValueOfSuccess(){
        Library library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city", admins);
        assertEquals(1, library.getId());
        assertEquals("name", library.getName());
        assertEquals(openingTime, library.getOpeningTime());
        assertEquals(closingTime, library.getClosingTime());
        assertEquals("street", library.getStreet());
        assertEquals("number", library.getNumber());
        assertEquals("city", library.getCity());
        assertTrue(library.hasAdmin(admins.getFirst().getId()));
    }
    @Test
    public void testValueOfFailure(){
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(0,  "name", openingTime, closingTime, "street", "number", "city", admins));
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "", openingTime, closingTime, "street", "number", "city", admins));
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", null, closingTime, "street", "number", "city", admins)
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, null, "street", "number", "city", admins)
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "", "number", "city", admins)
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "street", "", "city", admins)
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "street", "number", "", admins)
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "street", "number", "city", new ArrayList<>())
        );
    }
}
