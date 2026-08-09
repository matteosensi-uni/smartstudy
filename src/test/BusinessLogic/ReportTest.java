package test.BusinessLogic;

import com.smartstudy.businessLogic.ReportService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.domainModel.AbandonmentReport;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.enums.ReportStatus;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ReportTest {

    private ReportService reportService;

    @BeforeClass
    public static void setUpClass() {
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public void setUp() {
        Connection connection = ConnectionManager.getInstance().getConnection();
        StudentDAO studentDAO = new StudentDAO(connection);
        AdminDAO adminDAO = new AdminDAO(connection);
        SeatDAO seatDAO = new SeatDAO(connection, adminDAO);
        LibraryDAO libraryDAO = new LibraryDAO(connection, adminDAO);
        AbandonmentReportDAO abandonmentReportDAO = new AbandonmentReportDAO(connection);
        ReservationDAO reservationDAO = new ReservationDAO(connection, seatDAO, abandonmentReportDAO, new TemporaryLeaveDAO(connection));
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(connection, adminDAO);
        reportService = new ReportService(reservationDAO, studentDAO, abandonmentReportDAO, accessSessionDAO, libraryDAO, adminDAO, seatDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testCreateReportSuccess() {
        reportService.createReport(2, "", 1);
        ArrayList<AbandonmentReport> openReports = reportService.getOpenReportsByAdmin(4);
        assertEquals(1, openReports.size());
        assertEquals(ReportStatus.OPENED, openReports.getFirst().getStatus());
    }

    @Test
    public void testCreateReportFailure() {
        assertThrows(BusinessViolationException.class, ()-> reportService.createReport(2, "", 2)); //seat senza reservation
        assertThrows(BusinessViolationException.class, ()-> reportService.createReport(3, "", 1)); //studente non in biblioteca
        reportService.createReport(2, "", 1);
        assertThrows(BusinessViolationException.class, ()-> reportService.createReport(2, "", 1)); //posto con report già associato
    }

    @Test
    public void testCreateReportFailureValidLeave() {
        Connection connection = ConnectionManager.getInstance().getConnection();
        TemporaryLeaveDAO temporaryLeaveDAO = new TemporaryLeaveDAO(connection);
        ReservationDAO reservationDAO = new ReservationDAO(connection, new SeatDAO(connection, new AdminDAO(connection)), new AbandonmentReportDAO(connection), temporaryLeaveDAO);
        Reservation res  = reservationDAO.getActiveReservationBySeat(1).orElseThrow(AssertionError::new);
        temporaryLeaveDAO.insert(res.addTemporaryLeave(), res.getId());
        assertThrows(BusinessViolationException.class, ()-> reportService.createReport(2, "", 1));
    }

    @Test
    public void testTakeInChargeSuccess() {
        reportService.createReport(2, "", 1);
        reportService.takeInCharge(4, 2); // nel db di test c'è un solo report quindi quello creato sarà il secondo
    }

    @Test
    public void testTakeInChargeFailure() {
        reportService.createReport(2, "", 1);
        assertThrows(BusinessViolationException.class, () ->reportService.takeInCharge(5, 2));
    }

    @Test
    public void testConfirmReportSuccess() { //per il reject i test sono identici
        reportService.createReport(2, "", 1);
        reportService.takeInCharge(4, 2);
        reportService.confirm(4, 2);
    }

    @Test
    public void testConfirmReportFailure() {
        reportService.createReport(2, "", 1);
        reportService.takeInCharge(4, 2);
        assertThrows(BusinessViolationException.class, () -> reportService.confirm(0, 2)); // admin non trovato
        assertThrows(BusinessViolationException.class, () -> reportService.confirm(4, 0)); // report non trovato
    }

    @Test
    public void testGetOpenReportsByAdminSuccess() {
        // nel db di test l'unico report esistente è già CLOSED, quindi non deve comparire tra gli "aperti"
        ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByAdmin(4);
        assertTrue(reports.isEmpty());
    }

    @Test
    public void testGetOpenReportsByStudentSuccess() {
        // il report seed dello studente 2 è CLOSED e non deve comparire tra gli "aperti"
        reportService.createReport(2, "nuovo report", 1);
        ArrayList<AbandonmentReport> reports = reportService.getOpenReportsByStudent(2);
        assertEquals(1, reports.size());
        assertEquals(ReportStatus.OPENED, reports.getFirst().getStatus());
        assertEquals("nuovo report", reports.getFirst().getDescription());
    }

    @Test
    public void testGetReportsInChargeByAdminSuccess(){
        reportService.createReport(2, "", 1);
        reportService.takeInCharge(4, 2); // nel db di test c'è un solo report quindi quello creato sarà il secondo
        ArrayList<AbandonmentReport> reports = reportService.getReportsInChargeByAdmin(4);
        assertEquals(1, reports.size());
        assertEquals(ReportStatus.PENDING, reports.getFirst().getStatus());
    }

    @Test
    public void getClosedReportsByAdmin(){
        reportService.createReport(2, "", 1);
        reportService.takeInCharge(4, 2);
        reportService.confirm(4, 2);
        ArrayList<AbandonmentReport> reports = reportService.getClosedReportsByAdmin(4);
        assertEquals(1, reports.size());
        assertEquals(ReportStatus.CONFIRMED, reports.getFirst().getStatus());
    }

    @Test
    public void testGetFailure(){ // tutti i get del service falliscono per un id utente errato
        assertThrows(BusinessViolationException.class, () -> reportService.getOpenReportsByAdmin(0)); // admin errato
        assertThrows(BusinessViolationException.class, () -> reportService.getReportsInChargeByAdmin(0)); // admin errato
        assertThrows(BusinessViolationException.class, () -> reportService.getClosedReportsByAdmin(0)); // admin errato
        assertThrows(BusinessViolationException.class, () -> reportService.getOpenReportsByStudent(0)); //student errato

    }

}
