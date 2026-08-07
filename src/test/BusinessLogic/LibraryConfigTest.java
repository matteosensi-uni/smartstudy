package test.BusinessLogic;

import com.smartstudy.businessLogic.LibraryConfigService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.domainModel.StudyArea;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.*;

public class LibraryConfigTest {

    private LibraryConfigService libraryConfigService;

    @BeforeClass
    public static void setUpClass(){
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public  void setUp() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        AdminDAO adminDAO = new AdminDAO(conn);
        SeatDAO seatDAO = new SeatDAO(conn, adminDAO);
        StudyAreaDAO studyAreaDAO = new StudyAreaDAO(conn, adminDAO);
        LibraryDAO libraryDAO = new LibraryDAO(conn, adminDAO);
        TimePolicyDAO timePolicyDAO = new TimePolicyDAO(conn);
        libraryConfigService = new LibraryConfigService(seatDAO, studyAreaDAO, adminDAO, libraryDAO, timePolicyDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testGetAllPolicies(){
        List<String> policies = libraryConfigService.getAllPolicies();
        assertEquals(2, policies.size());
        assertTrue(policies.contains("Standard"));
        assertTrue(policies.contains("Extended"));
    }

    @Test
    public void testGetAllStudyAreas(){
        List<StudyArea> sa = libraryConfigService.getLibraryStudyAreasByAdmin(4);
        assertEquals(2, sa.size());
    }


    @Test
    public void testGetAllSeats(){
        List<Seat> seats = libraryConfigService.getLibrarySeatsByAdmin(4);
        assertEquals(5, seats.size());
    }

    @Test
    public void testUpdateStudyAreaSuccess(){
        long adminId = 4;
        StudyArea studyArea = libraryConfigService.getLibraryStudyAreasByAdmin(adminId).getFirst();
        libraryConfigService.updateStudyAreaPolicy(studyArea.getId(), adminId, "Extended");
        libraryConfigService.updateStudyAreaName(studyArea.getId(), adminId, "a");
        libraryConfigService.updateStudyAreaType(studyArea.getId(), adminId, "GROUP");
        for(StudyArea sa: libraryConfigService.getLibraryStudyAreasByAdmin(adminId)){
            if(sa.getId() == studyArea.getId()){
                assertEquals("Extended", sa.getTimePolicy().getName());
                assertEquals("a", sa.getName());
                assertEquals("GROUP", sa.getType().name());
            }
        }
    }

    @Test
    public void testUpdateSeatSuccess(){
        long adminId = 4;
        Seat seat = libraryConfigService.getLibrarySeatsByAdmin(adminId).stream()
                .filter(Seat::isAvailable)
                .findFirst().orElseThrow(AssertionError::new);
        libraryConfigService.updateSeatType(seat.getId(), adminId, "GROUP");
        libraryConfigService.updateSeatStatus(seat.getId(), adminId, "BROKEN");
        for(Seat s: libraryConfigService.getLibrarySeatsByAdmin(adminId)){
            if(s.getId() == seat.getId()){
                assertEquals("BROKEN", s.getStatus().name());
                assertEquals("GROUP", s.getType().name());
            }
        }
    }

    @Test
    public void testUpdateFailure(){ // i controlli di dominio sono praticamente gli stessi sia per seat che studyarea
                                    // ovvero viene controllato che l'admin sia uno di quelli che possono gestire la biblioteca e che sia presente
        StudyArea studyArea = libraryConfigService.getLibraryStudyAreasByAdmin(4).getFirst();
        assertThrows(BusinessViolationException.class, () -> libraryConfigService.updateStudyAreaType(studyArea.getId(), 5, "GROUP")); //admin non della biblioteca
        StudyArea studyArea2 = libraryConfigService.getLibraryStudyAreasByAdmin(5).getFirst();
        assertThrows(BusinessViolationException.class, () -> libraryConfigService.updateStudyAreaType(studyArea2.getId(), 5, "GROUP")); //admin non presente
        //altri controlli sono sugli input: admin non trovato
        assertThrows(BusinessViolationException.class, () -> libraryConfigService.updateStudyAreaType(studyArea.getId(), 0, "GROUP")); //admin non della biblioteca
        //studyArea non trovata
        assertThrows(BusinessViolationException.class, () -> libraryConfigService.updateStudyAreaType(0, 5, "GROUP"));
        //stato posto non valido
        Seat seat = libraryConfigService.getLibrarySeatsByAdmin(4).stream()
                .filter(Seat::isAvailable)
                .findFirst().orElseThrow(AssertionError::new);
        assertThrows(BusinessViolationException.class, () -> libraryConfigService.updateSeatStatus(seat.getId(), 4, "UNAVAILABLE"));
    }
}
