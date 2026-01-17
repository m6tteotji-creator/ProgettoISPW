package com.example.onetour.graphiccontroller;

import com.example.onetour.model.Session;
import com.example.onetour.model.UserAccount;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavigatorController {

    private static final Logger logger = Logger.getLogger(NavigatorController.class.getName());

    @FXML
    private Button loginButton;

    @FXML
    private Button bookingsButton;

    @FXML
    private MenuButton userMenu;

    @FXML
    private MenuItem userInfoItem;

    @FXML
    private AnchorPane contentPane;

    @FXML
    public void initialize() {
        NavigatorBase.setNavigatorController(this);

        setCenter("/fxml/pages/page1_landing.fxml");
        refreshHeader();
    }

    public void setCenter(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(page);

            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 0.0);
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);

            refreshHeader();

        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Errore nel caricamento FXML: " + fxmlPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Errore inatteso durante la navigazione verso: " + fxmlPath);
        }
    }

    public void refreshHeader() {
        Session session = SessionManagerSingleton.getInstance().getCurrentSession();

        if (session == null || session.getUser() == null) {
            loginButton.setVisible(true);
            loginButton.setManaged(true);

            userMenu.setVisible(false);
            userMenu.setManaged(false);

            if (bookingsButton != null) {
                bookingsButton.setVisible(false);
                bookingsButton.setManaged(false);
            }
            return;
        }

        UserAccount user = session.getUser();

        loginButton.setVisible(false);
        loginButton.setManaged(false);

        userMenu.setVisible(true);
        userMenu.setManaged(true);

        if (bookingsButton != null) {
            bookingsButton.setVisible(true);
            bookingsButton.setManaged(true);
        }

        String fullName = user.getFullName();
        if (fullName == null || fullName.isBlank()) fullName = "User";

        String email = (user.getUserEmail() != null && !user.getUserEmail().isBlank())
                ? user.getUserEmail()
                : "-";

        userMenu.setText(fullName);
        userInfoItem.setText(fullName + " • " + email);
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

        refreshHeader();
        setCenter("/fxml/pages/page2_login.fxml");
    }
}