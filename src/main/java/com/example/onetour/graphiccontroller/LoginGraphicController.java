package com.example.onetour.graphiccontroller;

import com.example.onetour.applicationcontroller.LoginController;
import com.example.onetour.bean.LoginBean;
import com.example.onetour.enumeration.RoleEnum;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.exception.UserNotFoundException;
import com.example.onetour.model.Session;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginGraphicController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void onSignUpClicked() {
        errorLabel.setText("");
        NavigatorBase.goTo("/fxml/pages/page3_signup.fxml");
    }

    @FXML
    private void onLoginClicked() {
        errorLabel.setText("");

        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci username e password.");
            return;
        }

        try {
            LoginBean lb = new LoginBean();
            lb.setEmail(username);
            lb.setPassword(password);
            LoginController app = new LoginController();
            LoginBean result = app.login(lb);

            String sessionID = result.getSessionID();
            if (sessionID == null || sessionID.isBlank()) {
                errorLabel.setText("Errore: sessione non creata.");
                return;
            }

            Session session = SessionManagerSingleton.getInstance().getSession(sessionID);
            if (session == null || session.getUser() == null) {
                errorLabel.setText("Errore: sessione/utente non trovati.");
                return;
            }

            UserAccount user = session.getUser();
            NavigatorBase.refreshHeader();

            if (user.getRole() == RoleEnum.TOURISTGUIDE) {
                NavigatorBase.goTo("/fxml/pages/page9_guide.fxml");
            } else {
                NavigatorBase.goTo("/fxml/pages/page4_home.fxml");
            }

        } catch (InvalidFormatException e) {
            errorLabel.setText(e.getMessage());
        } catch (UserNotFoundException e) {
            errorLabel.setText("Credenziali non valide.");
        } catch (SQLException e) {
            errorLabel.setText("Errore database.");
        } catch (Exception e) {
            errorLabel.setText("Errore inatteso.");
        }
    }
}
