package com.smartstudy.view;

import com.smartstudy.dto.StudentSessionDTO;
import com.smartstudy.AppBootstrap;
import com.smartstudy.domainModel.Admin;
import com.smartstudy.domainModel.User;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SmartStudyApp extends Application {

    private Stage primaryStage;
    private AppBootstrap appBootstrap;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        appBootstrap = AppBootstrap.getInstance();
        stage.setTitle("SmartStudy");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        showLogin();
        stage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(this::showAdminDashboard,
                this::showStudentDashboard,
                this::showLibraryAccessDialog,
                appBootstrap.getAuthenticationController());
        setScene(loginView, 900, 600);
    }

    private void showStudentDashboard(StudentSessionDTO studentSession) {
        StudentView studentView = new StudentView(this::showLogin, appBootstrap.getStudentController(), studentSession);
        setScene(studentView, 1100, 700);
    }

    private void showAdminDashboard(User admin) {
        AdminView adminView = new AdminView(this::showLogin, appBootstrap.getAdminController(), (Admin) admin);
        setScene(adminView, 1100, 700);
    }

    private void showLibraryAccessDialog() {
        LibraryAccessDialogView dialogView = new LibraryAccessDialogView(appBootstrap.getLibraryController());
        Scene scene = new Scene(dialogView, 360, 420);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Accesso Biblioteca");
        dialogStage.initOwner(primaryStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void setScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }
}
