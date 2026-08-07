package test.BusinessLogic;

import com.smartstudy.businessLogic.TemporaryLeaveService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertThrows;

public class TemporaryLeaveServiceTest {
    private TemporaryLeaveService temporaryLeaveService;

    @BeforeClass
    public static void setUpClass() {
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public void setUp() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        AdminDAO adminDAO = new AdminDAO(conn);
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(conn, adminDAO);
        TemporaryLeaveDAO temporaryLeaveDAO = new TemporaryLeaveDAO(conn);
        ReservationDAO reservationDAO = new ReservationDAO(conn, new SeatDAO(conn, adminDAO), new AbandonmentReportDAO(conn), temporaryLeaveDAO);
        temporaryLeaveService = new TemporaryLeaveService(temporaryLeaveDAO, reservationDAO, accessSessionDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testCreateLeaveSuccess(){
        long reservationId = 2; //reservation nel db associata alla sessione dello studente 1 nella biblioteca 1
        long studentId = 1;
        temporaryLeaveService.createTemporaryLeave(reservationId, studentId);
    }

    @Test
    public void testCreateLeaveFailure(){
        long reservationId = 2; //reservation nel db associata alla sessione dello studente 1 nella biblioteca 1
        long studentId = 1;
        long studentId2 = 2; //studente con un'altra accessSession
        temporaryLeaveService.createTemporaryLeave(reservationId, studentId);
        assertThrows(BusinessViolationException.class, () -> temporaryLeaveService.createTemporaryLeave(0, studentId));
        assertThrows(BusinessViolationException.class, () -> temporaryLeaveService.createTemporaryLeave(reservationId, 0));
        assertThrows(BusinessViolationException.class, () -> temporaryLeaveService.createTemporaryLeave(reservationId, studentId2));
    }
}
