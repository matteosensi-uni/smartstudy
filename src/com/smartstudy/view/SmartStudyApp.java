package com.smartstudy.view;

import javafx.application.Application;
import javafx.scene.Scene;
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
        LoginView loginView = new LoginView(this::showStudentDashboard);
        primaryStage.setScene(new Scene(loginView, 900, 600));
    }

    private void showStudentDashboard() {
        StudentView studentView = new StudentView(this::showLogin);
        primaryStage.setScene(new Scene(studentView, 1100, 700));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
