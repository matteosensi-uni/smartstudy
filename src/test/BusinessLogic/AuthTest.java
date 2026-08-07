package test.BusinessLogic;

import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;
import com.smartstudy.domainModel.User;
import com.smartstudy.exceptions.BusinessViolationException;
import com.smartstudy.orm.AdminDAO;
import com.smartstudy.orm.StudentDAO;
import com.smartstudy.orm.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.*;

public class AuthTest {

    private AuthenticationService authenticationService;

    @BeforeClass
    public static void setUpClass() {
        DataBaseInitializer.prepareTestSchema();
    }

    @Before
    public void setUp() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        AdminDAO adminDAO = new AdminDAO(conn);
        UserDAO userDAO = new UserDAO(conn);
        StudentDAO studentDAO = new StudentDAO(conn);
        authenticationService = new AuthenticationService(userDAO, studentDAO, adminDAO);
    }

    @After
    public void tearDown() {
        ConnectionManager.getInstance().closeConnection();
        DataBaseInitializer.resetData();
    }

    @Test
    public void testAuthAdminSuccess(){
        User u =  authenticationService.authenticateUser(4, "admin123");
        assertTrue(u.isAdmin());
    }

    @Test
    public void testAuthStudentSuccess(){
        User u =  authenticationService.authenticateUser(1, "password");
        assertFalse(u.isAdmin());
    }

    @Test
    public void testAuthFailure(){
        assertThrows(BusinessViolationException.class ,() -> authenticationService.authenticateUser(0, "password"));
        assertThrows(BusinessViolationException.class ,() -> authenticationService.authenticateUser(1, ""));
        assertThrows(BusinessViolationException.class ,() -> authenticationService.authenticateUser(1, "asdf"));
    }

}
