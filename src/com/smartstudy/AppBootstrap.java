package com.smartstudy;

import com.smartstudy.orm.*;
import com.smartstudy.businessLogic.*;
import com.smartstudy.controller.*;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.db.DataBaseInitializer;

import java.sql.Connection;

public class AppBootstrap {
    private static AppBootstrap instance;
    private final AdminDashboardController adminController;
    private final AuthenticationController authenticationController;
    private final LibraryAccessController libraryController;
    private final StudentDashboardController studentController;

    private AppBootstrap() {
        Connection conn = ConnectionManager.getInstance().getConnection();
        DataBaseInitializer initializer = new DataBaseInitializer(conn);
        initializer.prepareTestSchema();
        //DAO
        UserDAO userDAO = new UserDAO(conn);
        LibraryDAO libraryDAO = new LibraryDAO(conn);
        TimePolicyDAO timePolicyDAO = new TimePolicyDAO(conn);
        AdminDAO adminDAO = new AdminDAO(conn);
        StudentDAO studentDAO = new StudentDAO(conn);
        StudyAreaDAO studyAreaDAO = new StudyAreaDAO(conn, libraryDAO, timePolicyDAO);
        SeatDAO seatDAO = new SeatDAO(conn, studyAreaDAO);
        AbandonmentReportDAO abandonmentReportDAO = new AbandonmentReportDAO(conn, adminDAO,  studentDAO);
        TemporaryLeaveDAO temporaryLeaveDAO = new TemporaryLeaveDAO(conn);
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(conn, libraryDAO, studentDAO);
        ReservationDAO reservationDAO = new ReservationDAO(conn, seatDAO, accessSessionDAO, abandonmentReportDAO, temporaryLeaveDAO);

        //Service
        AuthenticationService authenticationService = new AuthenticationService(userDAO, studentDAO, adminDAO);
        LibraryAccessService libraryAccessService = new LibraryAccessService(adminDAO,
                accessSessionDAO,
                studentDAO,
                libraryDAO);
        LibraryConfigService libraryConfigService = new LibraryConfigService(seatDAO, studyAreaDAO, adminDAO, libraryDAO, timePolicyDAO);
        ReportService reportService = new ReportService(reservationDAO,
                studentDAO,
                abandonmentReportDAO,
                accessSessionDAO,
                libraryDAO,
                adminDAO,
                seatDAO);
        ReservationService reservationService = new ReservationService(accessSessionDAO,
                studentDAO,
                seatDAO,
                reservationDAO,
                abandonmentReportDAO);
        TemporaryLeaveService temporaryLeaveService = new TemporaryLeaveService(temporaryLeaveDAO, reservationDAO, accessSessionDAO);

        adminController = new AdminDashboardController(reportService, libraryConfigService, reservationService);
        studentController = new StudentDashboardController(reservationService, reportService, temporaryLeaveService);
        libraryController = new LibraryAccessController(libraryAccessService);
        authenticationController = new AuthenticationController(authenticationService, reservationService, libraryController);
    }

    public static AppBootstrap getInstance() {
        if(instance == null){
            instance = new AppBootstrap();
        }
        return instance;
    }

    public AdminDashboardController getAdminController() {
        return adminController;
    }

    public AuthenticationController getAuthenticationController() {
        return authenticationController;
    }

    public LibraryAccessController getLibraryController() {
        return libraryController;
    }
    public StudentDashboardController getStudentController() {
        return studentController;
    }
}
