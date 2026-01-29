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

        errorLabel.setText(
                "Sign up not implemented in this version.\n" +
                        "Use DEMO credentials or JDBC accounts."
        );
    }

    @FXML
    private void onBackToLoginClicked() {
        NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
    }

    @FXML
    private void onSignUpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign Up");
        alert.setHeaderText("Not implemented");
        alert.setContentText(
                "Sign up is not implemented in this version.\n" +
                        "Please go back and login using DEMO credentials or JDBC accounts."
        );
        alert.showAndWait();
    }
}
