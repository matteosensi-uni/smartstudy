package test.domainModel;

import com.smartstudy.domainModel.StudyArea;
import com.smartstudy.domainModel.enums.StudyAreaType;
import com.smartstudy.exceptions.DomainViolationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class StudyAreaTest {
    @Test
    public void testValueOfSuccess() {
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        assertEquals(1, studyArea.getId());
        assertEquals("name", studyArea.getName());
        assertEquals(StudyAreaType.GROUP, studyArea.getType());
        assertEquals(0, studyArea.getFloor());
        assertEquals(1, studyArea.getLibraryId());
        assertEquals(1, studyArea.getTimePolicyId());
    }

    @Test
    public void testValueOfFailure() {
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(0, "name", 0, StudyAreaType.GROUP, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "", 0, StudyAreaType.GROUP, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, null, 1, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 0, 1)
        );
        assertThrows(DomainViolationException.class, () ->
            StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 0)
        );
    }

    @Test
    public void testSetNameSuccess(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        studyArea.setName("area");
        assertEquals("area", studyArea.getName());
    }

    @Test
    public void testSetNameFailure(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        assertThrows(DomainViolationException.class, () ->
            studyArea.setName("")
        );
    }

    @Test
    public void testSetTypeSuccess(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        studyArea.changeStudyAreaType(StudyAreaType.GROUP);
        assertEquals(StudyAreaType.GROUP, studyArea.getType());
    }

    @Test
    public void testSetTypeFailure(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        assertThrows(DomainViolationException.class, () ->
            studyArea.changeStudyAreaType(null)
        );
    }

    @Test
    public void testSetPolicySuccess(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        studyArea.changePolicy(2);
        assertEquals(StudyAreaType.GROUP, studyArea.getType());
    }

    @Test
    public void testSetPolicyFailure(){
        StudyArea studyArea = StudyArea.valueOf(1, "name", 0, StudyAreaType.GROUP, 1, 1);
        assertThrows(DomainViolationException.class, () ->
            studyArea.changePolicy(0)
        );
    }
}
