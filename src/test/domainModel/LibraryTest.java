package test.domainModel;

import com.smartstudy.domainModel.Library;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class LibraryTest {
    @Test
    public void testValueOfSuccess(){
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        Library library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city");
        assertEquals(1, library.getId());
        assertEquals("name", library.getName());
        assertEquals(openingTime, library.getOpeningTime());
        assertEquals(closingTime, library.getClosingTime());
        assertEquals("street", library.getStreet());
        assertEquals("number", library.getNumber());
        assertEquals("city", library.getCity());
    }
    @Test
    public void testValueOfFailure(){
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(0,  "name", openingTime, closingTime, "street", "number", "city"));
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "", openingTime, closingTime, "street", "number", "city"));
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", null, closingTime, "street", "number", "city")
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, null, "street", "number", "city")
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "", "number", "city")
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "street", "", "city")
        );
        assertThrows(DomainViolationException.class, () ->
                Library.valueOf(1,  "name", openingTime, closingTime, "street", "number", "")
        );
    }
}
