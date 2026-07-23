package com.smartstudy.view;

import com.smartstudy.ORM.AbandonmentReportDAO;
import com.smartstudy.ORM.AccessSessionDAO;
import com.smartstudy.ORM.AdminDAO;
import com.smartstudy.ORM.LibraryDAO;
import com.smartstudy.ORM.ReservationDAO;
import com.smartstudy.ORM.SeatDAO;
import com.smartstudy.ORM.StudentDAO;
import com.smartstudy.ORM.StudyAreaDAO;
import com.smartstudy.ORM.TemporaryLeaveDAO;
import com.smartstudy.ORM.TimePolicyDAO;
import com.smartstudy.ORM.UserDAO;
import com.smartstudy.businessLogic.AuthenticationService;
import com.smartstudy.businessLogic.LibraryAccessService;
import com.smartstudy.controller.AuthenticationController;
import com.smartstudy.controller.LibraryAccessController;
import com.smartstudy.db.ConnectionManager;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.Student;
import com.smartstudy.domainModel.User;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;

public class SmartStudyApp extends Application {

    private Stage primaryStage;

    private AuthenticationController authenticationController;
    private LibraryAccessController libraryAccessController;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("SmartStudy");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        try {
            wireDependencies();
        } catch (RuntimeException e) {
            showFatalErrorAndExit(e);
            return;
        }

        showLogin();
        stage.show();
    }

    private void showFatalErrorAndExit(RuntimeException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di avvio");
        alert.setHeaderText("Impossibile avviare SmartStudy");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        Platform.exit();
    }

    private void wireDependencies() {
        Connection connection = ConnectionManager.getInstance().getConnection();

        UserDAO userDAO = new UserDAO(connection);
        StudentDAO studentDAO = new StudentDAO(connection);
        AdminDAO adminDAO = new AdminDAO(connection);
        LibraryDAO libraryDAO = new LibraryDAO(connection);
        TimePolicyDAO timePolicyDAO = new TimePolicyDAO(connection);
        StudyAreaDAO studyAreaDAO = new StudyAreaDAO(connection, libraryDAO, timePolicyDAO);
        SeatDAO seatDAO = new SeatDAO(connection, studyAreaDAO);
        AccessSessionDAO accessSessionDAO = new AccessSessionDAO(connection, libraryDAO, studentDAO);
        AbandonmentReportDAO abandonmentReportDAO = new AbandonmentReportDAO(connection);
        TemporaryLeaveDAO temporaryLeaveDAO = new TemporaryLeaveDAO(connection);
        ReservationDAO reservationDAO = new ReservationDAO(connection, seatDAO, accessSessionDAO, abandonmentReportDAO, temporaryLeaveDAO);

        AuthenticationService authenticationService = new AuthenticationService(userDAO, studentDAO, adminDAO);
        LibraryAccessService libraryAccessService = new LibraryAccessService(
                adminDAO, accessSessionDAO, studentDAO, reservationDAO, libraryDAO, seatDAO);

        authenticationController = new AuthenticationController(authenticationService);
        libraryAccessController = new LibraryAccessController(libraryAccessService);
    }

    private void showLogin() {
        LoginView loginView = new LoginView(
                authenticationController::handleLogin,
                this::onLoginSuccess,
                this::showLibraryAccessDialog);
        setScene(loginView, 900, 600);
    }

    private void onLoginSuccess(User user) {
        if (user instanceof Student student) {
            showStudentDashboard(student);
        } else if (user instanceof Admin) {
            showAdminDashboard();
        }
    }

    private void showStudentDashboard(Student student) {
        StudentView studentView = new StudentView(student, libraryAccessController, this::showLogin);
        setScene(studentView, 1100, 700);
    }

    private void showAdminDashboard() {
        AdminView adminView = new AdminView(this::showLogin);
        setScene(adminView, 1100, 700);
    }

    private void showLibraryAccessDialog() {
        LibraryAccessDialogView dialogView = new LibraryAccessDialogView(libraryAccessController);
        Scene scene = new Scene(dialogView, 360, 420);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Accesso Biblioteca");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setScene(javafx.scene.Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
