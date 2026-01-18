package com.example.onetour.graphiccontroller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpGraphicController {

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
        errorLabel.setText("");
    }

    @FXML
    private void onBackToLoginClicked() {
        NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
    }

    @FXML
    private void onSignUpClicked() {
        errorLabel.setText("");

        String name = safe(nameField.getText());
        String surname = safe(surnameField.getText());
        String email = safe(emailField.getText()).toLowerCase();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank()) {
            errorLabel.setText("Fill in all fields.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            errorLabel.setText("Invalid email format.");
            return;
        }
        if (password.length() < 4) {
            errorLabel.setText("Password too short.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText(null);
        alert.setContentText("Registration completed. Now login.");
        alert.showAndWait();

        NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
