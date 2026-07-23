package com.smartstudy.view;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SmartStudyApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("SmartStudy");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        showLogin();
        stage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(this::showStudentDashboard, this::showLibraryAccessDialog);
        setScene(loginView, 900, 600);
    }

    private void showStudentDashboard() {
        StudentView studentView = new StudentView(this::showLogin);
        setScene(studentView, 1100, 700);
    }

    private void showLibraryAccessDialog() {
        LibraryAccessDialogView dialogView = new LibraryAccessDialogView();
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

    public static void main(String[] args) {
        launch(args);
    }
}
