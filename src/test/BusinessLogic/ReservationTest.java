package test.BusinessLogic;

import com.smartstudy.businessLogic.ReservationService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.domainModel.Reservation;
import com.smartstudy.domainModel.Seat;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.*;

public class ReservationTest {
    private static ReservationService reservationService;

    @BeforeClass
    public static void setUpClass() {
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public void setUp() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        AdminDAO adminDAO = new AdminDAO(conn);
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(conn, adminDAO);
        StudentDAO studentDAO = new StudentDAO(conn);
        SeatDAO seatDAO = new SeatDAO(conn, adminDAO);
        AbandonmentReportDAO abandonmentReportDAO = new AbandonmentReportDAO(conn);
        ReservationDAO reservationDAO = new ReservationDAO(conn, seatDAO, abandonmentReportDAO, new TemporaryLeaveDAO(conn));
        reservationService = new ReservationService(accessSessionDAO, studentDAO, seatDAO, reservationDAO, abandonmentReportDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testScanSeatSuccess(){
        Seat s = reservationService.scanSeat("QR001", 1); // lo studente deve essere in biblioteca per scannerizzare il qr
        assertEquals("QR001", s.getQrCode());
    }

    @Test
    public void testScanSeatFail(){
        assertThrows(BusinessViolationException.class,()-> reservationService.scanSeat("", 1)); //QR nullo
        assertThrows(BusinessViolationException.class,()-> reservationService.scanSeat("test", 1)); //QR non valido
        assertThrows(BusinessViolationException.class,()-> reservationService.scanSeat("QR001", 3)); // studente non in biblioteca
    }

    @Test
    public void testCreateReservationSuccess(){
        Seat s = reservationService.scanSeat("QR002", 2); // lo studente deve essere in biblioteca per scannerizzare il qr
        reservationService.createReservation(2, s.getId());
    }

    @Test
    public void testCreateReservationFail(){
        SeatDAO seatDAO = new SeatDAO(ConnectionManager.getInstance().getConnection(), new AdminDAO(ConnectionManager.getInstance().getConnection()));
        Seat s1 = seatDAO.getSeatByQR("QR001").orElseThrow(AssertionError::new);
        Seat s2 = seatDAO.getSeatByQR("QR002").orElseThrow(AssertionError::new);
        Seat s3 = seatDAO.getSeatByQR("QR008").orElseThrow(AssertionError::new);
        Seat s4 = seatDAO.getSeatByQR("QR009").orElseThrow(AssertionError::new);
        assertThrows(BusinessViolationException.class,()-> reservationService.createReservation(2, s1.getId())); // posto occupato
        assertThrows(BusinessViolationException.class,()-> reservationService.createReservation(1, s2.getId())); // studente con prenotazione attiva
        assertThrows(BusinessViolationException.class,()-> reservationService.createReservation(2, s3.getId())); // posto non nella biblioteca acceduta
        assertThrows(BusinessViolationException.class,()-> reservationService.createReservation(2, s4.getId())); // posto rotto
    }

    @Test
    public void testActiveReservationSuccess(){
        Reservation res = reservationService.getActiveStudentReservation(1);
        assertNotNull(res);
        res = reservationService.getActiveStudentReservation(3);
        assertNull(res);
    }

    @Test
    public void testCloseReservationSuccess(){
        Reservation res = reservationService.getActiveStudentReservation(1);
        reservationService.closeReservation(res.getId(), 1);
    }

    @Test
    public void testCloseReservationFail(){
        Reservation res = reservationService.getActiveStudentReservation(1);
        assertThrows(BusinessViolationException.class,()-> reservationService.closeReservation(res.getId(), 2)); //studente diverso da quello della prenotazione
        assertThrows(BusinessViolationException.class,()-> reservationService.closeReservation(3, 1)); //reservation non trovata
        assertThrows(BusinessViolationException.class,()-> reservationService.closeReservation(res.getId(), 3)); //studente non in biblioteca

    }

    @Test
    public void testGetReservationHistorySuccess(){
        List<Reservation> res = reservationService.getReservationHistory(2);
        assertEquals(1,  res.size());
    }

    @Test
    public void testGetReservationHistoryFail(){
        assertThrows(BusinessViolationException.class,()-> reservationService.getReservationHistory(5));
    }

    @Test
    public void testGetReservationByReportSuccess(){
        Reservation res = reservationService.getReservationByReport(1);
        assertNotNull(res);
    }

    @Test
    public void testGetReservationByReportFail(){
        assertThrows(BusinessViolationException.class, ()-> reservationService.getReservationByReport(2)); //report non esistente
    }
}
