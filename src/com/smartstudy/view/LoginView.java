package com.smartstudy.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView extends VBox {

    private final TextField userIdField;
    private final PasswordField passwordField;
    private final Label errorLabel;
    private final Button loginButton;

    public LoginView(Runnable onLoginSuccess) {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #f4f6f8;");

        Label title = new Label("SmartStudy");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitle = new Label("Accedi al tuo account");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        userIdField = new TextField();
        userIdField.setPromptText("ID utente");
        userIdField.setMaxWidth(280);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #c0392b;");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        loginButton = new Button("Accedi");
        loginButton.setMaxWidth(280);
        loginButton.setDefaultButton(true);
        loginButton.setStyle("-fx-background-color: #2d6cdf; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setOnAction(e -> onLoginSuccess.run());

        VBox form = new VBox(10, userIdField, passwordField, loginButton, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(280);

        getChildren().addAll(title, subtitle, form);
    }

    public TextField getUserIdField() {
        return userIdField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public Button getLoginButton() {
        return loginButton;
    }
}
