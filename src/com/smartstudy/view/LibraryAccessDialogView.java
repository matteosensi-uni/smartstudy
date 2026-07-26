package com.smartstudy.view;

import com.smartstudy.controller.ControllerResult;
import com.smartstudy.controller.LibraryAccessController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LibraryAccessDialogView extends VBox {

    private final TextField studentIdField;
    private final TextField libraryIdField;
    private final Label resultLabel;

    private final LibraryAccessController libraryAccessController;

    public LibraryAccessDialogView(LibraryAccessController accessController) {
        this.libraryAccessController = accessController;
        getStyleClass().add("root");
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(30));

        Label title = new Label("Ingresso / Uscita Biblioteca");
        title.getStyleClass().add("login-title");
        title.setStyle("-fx-font-size: 22px;");

        Label subtitle = new Label("Inserisci il tuo ID studente e l'ID della biblioteca");
        subtitle.getStyleClass().add("login-subtitle");

        studentIdField = new TextField();
        studentIdField.setPromptText("ID studente");
        studentIdField.setMaxWidth(260);
        studentIdField.getStyleClass().add("field");

        libraryIdField = new TextField();
        libraryIdField.setPromptText("ID biblioteca");
        libraryIdField.setMaxWidth(260);
        libraryIdField.getStyleClass().add("field");

        resultLabel = new Label();
        resultLabel.setWrapText(true);
        resultLabel.setMaxWidth(260);
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);
        resultLabel.setStyle("-fx-font-weight: bold");

        Button toggleButton = new Button("Registra ingresso/uscita");
        toggleButton.setMaxWidth(260);
        toggleButton.setDefaultButton(true);
        toggleButton.getStyleClass().add("btn-primary");
        toggleButton.setOnAction(e -> handleToggle());

        VBox form = new VBox(10, studentIdField, libraryIdField, toggleButton, resultLabel);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(260);

        getChildren().addAll(title, subtitle, form);
    }

    private void handleToggle() {
        try {
            long studentId = Long.parseLong(studentIdField.getText());
            long libraryId = Long.parseLong(libraryIdField.getText());
            ControllerResult<Void> result = libraryAccessController.handleAccess(studentId, libraryId);
            showResult(result.getMessage(), result.isSuccess());
        }catch (IllegalArgumentException exception){
            showResult("Inserire dei dati corretti", false);
        }
    }

    private void showResult(String message, boolean success) {
        resultLabel.setText(message);
        resultLabel.getStyleClass().removeAll("error-label", "hint-label");
        resultLabel.getStyleClass().add(success ? "hint-label" : "error-label");
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }
}
