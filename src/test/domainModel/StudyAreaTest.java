package test.domainModel;

import com.smartstudy.domainModel.*;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class StudyAreaTest {

    private Library library;
    private TimePolicy timePolicy;
    private StudyArea correctStudyArea;

    @Before
    public void setUp() {
        ArrayList<Admin> admins = new ArrayList<>();
        admins.add(Admin.valueOf(1, "Mario", "Rossi", "pwd", "email", false));
        LocalTime openingTime = LocalTime.of(1,0,0);
        LocalTime closingTime = LocalTime.of(23,0,0);
        library = Library.valueOf(1, "name", openingTime, closingTime, "street", "number", "city", admins);
        timePolicy = TimePolicy.valueOf(1, 5, 5, "Strict");
        correctStudyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, timePolicy, library);
    }

    @Test
    public void testValueOfSuccess() {
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, timePolicy, library);
        assertEquals(1, studyArea.getId());
        assertEquals("name", studyArea.getName());
        assertEquals(StudyAreaType.GROUP, studyArea.getType());
        assertEquals(0, studyArea.getFloor());
        assertEquals(library.getId(), studyArea.getLibrary().getId());
        assertEquals(timePolicy.getId(), studyArea.getTimePolicy().getId());
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(0, "name", 0, StudyAreaType.GROUP, timePolicy, library)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "", 0, StudyAreaType.GROUP, timePolicy, library)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, null, timePolicy, library)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, null, library)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, timePolicy, null)
        );
    }

    @Test
    public void testSetNameSuccess(){
        correctStudyArea.changeName("area");
        assertEquals("area", correctStudyArea.getName());
    }

    @Test
    public void testSetNameFailure(){
        assertThrows(DomainViolationException.class, () ->
            correctStudyArea.changeName("")
        );
    }

    @Test
    public void testSetTypeSuccess(){
        correctStudyArea.changeStudyAreaType(StudyAreaType.GROUP);
        assertEquals(StudyAreaType.GROUP, correctStudyArea.getType());
    }

    @Test
    public void testSetTypeFailure(){
        assertThrows(DomainViolationException.class, () ->
            correctStudyArea.changeStudyAreaType(null)
        );
    }

    @Test
    public void testSetPolicySuccess(){
        correctStudyArea.changePolicy(TimePolicy.valueOf(2, 5, 2, "Strict"));
        assertEquals(StudyAreaType.GROUP, correctStudyArea.getType());
    }

    @Test
    public void testSetPolicyFailure(){
        assertThrows(DomainViolationException.class, () ->
            correctStudyArea.changePolicy(null)
        );
    }
}
