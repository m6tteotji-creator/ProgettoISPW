package com.example.onetour.graphiccontroller;

import com.example.onetour.model.Session;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavigatorController {

    private static final Logger logger = Logger.getLogger(NavigatorController.class.getName());

    @FXML private HBox headerBar;
    @FXML private AnchorPane contentPane;

    @FXML private Button loginButton;
    @FXML private Button bookingsButton;

    // User area (icon + card)
    @FXML private Button userButton;
    @FXML private VBox userCard;
    @FXML private Label helloLabel;
    @FXML private Label emailLabel;

    @FXML
    public void initialize() {
        NavigatorBase.setNavigatorController(this);

        setCenter("/fxml/pages/page1_landing.fxml");
        refreshHeader();
    }

    public void setHeaderVisible(boolean visible) {
        if (headerBar == null) return;
        headerBar.setVisible(visible);
        headerBar.setManaged(visible);
    }

    public void setCenter(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
            boolean hideHeader =
                    "/fxml/pages/page2_login.fxml".equals(fxmlPath) ||
                            "/fxml/pages/page3_signup.fxml".equals(fxmlPath);

            setHeaderVisible(!hideHeader);
            if (userCard != null) {
                userCard.setVisible(false);
                userCard.setManaged(false);
            }

            refreshHeader();

        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Errore nel caricamento FXML: " + fxmlPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Errore inatteso durante la navigazione verso: " + fxmlPath);
        }
    }

    public void refreshHeader() {
        if (headerBar != null && (!headerBar.isVisible() || !headerBar.isManaged())) {
            return;
        }

        Session session = SessionManagerSingleton.getInstance().getCurrentSession();

        // NOT LOGGED
        if (session == null || session.getUser() == null) {
            if (loginButton != null) {
                loginButton.setVisible(true);
                loginButton.setManaged(true);
            }

            if (userButton != null) {
                userButton.setVisible(false);
                userButton.setManaged(false);
                userButton.setText(""); // pulizia
            }

            if (userCard != null) {
                userCard.setVisible(false);
                userCard.setManaged(false);
            }

            if (bookingsButton != null) {
                bookingsButton.setVisible(false);
                bookingsButton.setManaged(false);
            }
            return;
        }

        // LOGGED
        UserAccount user = session.getUser();

        if (loginButton != null) {
            loginButton.setVisible(false);
            loginButton.setManaged(false);
        }

        if (userButton != null) {
            userButton.setVisible(true);
            userButton.setManaged(true);
        }

        boolean isGuide = (user.getRole() == com.example.onetour.enumeration.RoleEnum.TOURISTGUIDE);
        if (bookingsButton != null) {
            bookingsButton.setVisible(!isGuide);
            bookingsButton.setManaged(!isGuide);
        }

        String fullName = user.getFullName();
        if (fullName == null || fullName.isBlank()) fullName = "User";

        String email = (user.getUserEmail() != null && !user.getUserEmail().isBlank())
                ? user.getUserEmail()
                : "-";

        if (userButton != null) {
            userButton.setText(fullName);
        }

        if (helloLabel != null) {
            helloLabel.setText("Hello, " + fullName);
        }
        if (emailLabel != null) {
            emailLabel.setText(email);
        }
    }

    @FXML
    private void onUserButtonClicked() {
        if (userCard == null) return;
        boolean show = !userCard.isVisible();
        userCard.setVisible(show);
        userCard.setManaged(show);
    }

    @FXML
    private void onLoginHeaderClicked() {
        NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
    }

    @FXML
    private void onBookingsClicked() {
        NavigatorBase.goTo("/fxml/pages/page7_bookings.fxml");
    }

    @FXML
    private void onLogoutClicked() {
        Session session = SessionManagerSingleton.getInstance().getCurrentSession();
        if (session != null) {
            SessionManagerSingleton.getInstance().removeSession(session.getSessionID());
        }
        SessionManagerSingleton.getInstance().clearCurrentSession();

        if (userCard != null) {
            userCard.setVisible(false);
            userCard.setManaged(false);
        }

        refreshHeader();
        setCenter("/fxml/pages/page2_login.fxml");
    }
}
