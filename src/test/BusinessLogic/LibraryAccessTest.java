package test.BusinessLogic;
import com.smartstudy.businessLogic.LibraryAccessService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class LibraryAccessTest {

    private LibraryAccessService libraryAccessService;

    @BeforeClass
    public static void setUpClass() {
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public void setUp() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        AdminDAO adminDAO = new AdminDAO(conn);
        StudentDAO studentDAO = new StudentDAO(conn);
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(conn, adminDAO);
        LibraryDAO libraryDAO = new LibraryDAO(conn, adminDAO);
        ReservationDAO reservationDAO = new ReservationDAO(conn, new SeatDAO(conn, adminDAO), new AbandonmentReportDAO(conn), new TemporaryLeaveDAO(conn));
        libraryAccessService = new LibraryAccessService(adminDAO, accessSessionDAO, studentDAO, libraryDAO, reservationDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testToggleStudentPresenceSuccess() {
        long studentId = 2L; //studente con un'accesssession
        long libraryId = 1L; //biblioteca sempre aperta (opening_time == closing_time)
        boolean res = libraryAccessService.toggleUserPresence(studentId, libraryId);
        assertFalse(res); //lo studente esce dalla biblioteca
        res = libraryAccessService.toggleUserPresence(studentId, libraryId);
        assertTrue(res); //lo studente è entrato nuovamente in biblioteca
    }

    @Test
    public void testToggleStudentPresenceFailure() {
        long studentId = 1; //studente con una sessione di accesso attiva
        long studentId2 = 3; //studente con una card non attiva
        long libraryId = 2; //biblioteca diversa da quella dell'accessSession dello studente
        assertThrows(BusinessViolationException.class, () -> libraryAccessService.toggleUserPresence(studentId, libraryId));
        assertThrows(BusinessViolationException.class, () -> libraryAccessService.toggleUserPresence(studentId2, libraryId));
    }

    @Test
    public void testToggleAdminPresenceSuccess() {
        long adminId = 4; //id di un admin non presente nella biblioteca
        long libraryId = 1; //biblioteca associata all'admin
        boolean res = libraryAccessService.toggleUserPresence(adminId, libraryId);
        assertFalse(res); //admin entrato
        res = libraryAccessService.toggleUserPresence(adminId, libraryId);
        assertTrue(res); // admin uscito
    }

    @Test
    public void testToggleAdminPresenceFailure() {
        long adminId = 5; //id di un admin non presente nella biblioteca
        long libraryId = 1; //biblioteca non associata all'admin
        assertThrows(BusinessViolationException.class, () -> libraryAccessService.toggleUserPresence(adminId, libraryId));
    }
    @Test
    public void testToggleLibraryPresenceFailure() { //fail generali: utente sbagliato, libreria sbagliata
        assertThrows(BusinessViolationException.class, () -> libraryAccessService.toggleUserPresence(0, 1));
        assertThrows(BusinessViolationException.class, () -> libraryAccessService.toggleUserPresence(1, 0));
    }

}
