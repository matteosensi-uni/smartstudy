package com.smartstudy.view;

import com.smartstudy.domainModel.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LoginView extends VBox {

    private final TextField userIdField;
    private final PasswordField passwordField;
    private final Label errorLabel;
    private final Button loginButton;

    public LoginView(BiFunction<Long, String, Optional<User>> loginHandler,
                      Consumer<User> onLoginSuccess,
                      Runnable onOpenLibraryAccess) {
        getStyleClass().add("root");
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(40));

        Label title = new Label("SmartStudy");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Accedi al tuo account");
        subtitle.getStyleClass().add("login-subtitle");

        userIdField = new TextField();
        userIdField.setPromptText("ID utente");
        userIdField.setMaxWidth(280);
        userIdField.getStyleClass().add("field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);
        passwordField.getStyleClass().add("field");

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        loginButton = new Button("Accedi");
        loginButton.setMaxWidth(280);
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setOnAction(e -> attemptLogin(loginHandler, onLoginSuccess));

        VBox form = new VBox(10, userIdField, passwordField, loginButton, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(280);

        Separator separator = new Separator();
        separator.setMaxWidth(280);

        Label accessHint = new Label("Devi solo registrare l'ingresso o l'uscita dalla biblioteca?");
        accessHint.getStyleClass().add("hint-label");

        Button libraryAccessButton = new Button("Ingresso / Uscita Biblioteca");
        libraryAccessButton.setMaxWidth(280);
        libraryAccessButton.getStyleClass().add("btn-secondary");
        libraryAccessButton.setOnAction(e -> onOpenLibraryAccess.run());

        VBox accessSection = new VBox(8, accessHint, libraryAccessButton);
        accessSection.setAlignment(Pos.CENTER);
        accessSection.setMaxWidth(280);

        getChildren().addAll(title, subtitle, form, separator, accessSection);
    }

    private void attemptLogin(BiFunction<Long, String, Optional<User>> loginHandler, Consumer<User> onLoginSuccess) {
        Long userId = parseUserId(userIdField.getText());
        if (userId == null) {
            showError("Inserisci un ID utente valido");
            return;
        }
        Optional<User> user = loginHandler.apply(userId, passwordField.getText());
        if (user.isPresent()) {
            hideError();
            onLoginSuccess.accept(user.get());
        } else {
            showError("ID o password non validi");
        }
    }

    private Long parseUserId(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
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
