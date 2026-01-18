package com.example.onetour.graphiccontroller;

import com.example.onetour.enumeration.RoleEnum;
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

    private static final String PAGE_LANDING = "/fxml/pages/page1_landing.fxml";
    private static final String PAGE_LOGIN = "/fxml/pages/page2_login.fxml";
    private static final String PAGE_SIGNUP = "/fxml/pages/page3_signup.fxml";
    private static final String PAGE_BOOKINGS = "/fxml/pages/page7_bookings.fxml";

    @FXML private HBox headerBar;
    @FXML private AnchorPane contentPane;

    @FXML private Button loginButton;
    @FXML private Button bookingsButton;
    @FXML private Button userButton;
    @FXML private VBox userCard;
    @FXML private Label helloLabel;
    @FXML private Label emailLabel;

    @FXML
    public void initialize() {
        NavigatorBase.setNavigatorController(this);

        setCenter(PAGE_LANDING);
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

            setHeaderVisible(!shouldHideHeader(fxmlPath));
            hideUserCard();
            refreshHeader();

        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Errore nel caricamento FXML: " + fxmlPath);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Errore inatteso durante la navigazione verso: " + fxmlPath);
        }
    }

    private boolean shouldHideHeader(String fxmlPath) {
        return PAGE_LOGIN.equals(fxmlPath) || PAGE_SIGNUP.equals(fxmlPath);
    }

    private void hideUserCard() {
        if (userCard == null) return;
        userCard.setVisible(false);
        userCard.setManaged(false);
    }

    public void refreshHeader() {
        if (!isHeaderActive()) {
            return;
        }

        Session session = SessionManagerSingleton.getInstance().getCurrentSession();
        UserAccount user = (session != null) ? session.getUser() : null;

        if (user == null) {
            applyNotLoggedState();
            return;
        }

        applyLoggedState(user);
    }

    private boolean isHeaderActive() {
        return headerBar == null || (headerBar.isVisible() && headerBar.isManaged());
    }

    private void applyNotLoggedState() {
        setNodeVisible(loginButton, true);
        setNodeVisible(userButton, false);
        clearUserButtonText();
        setNodeVisible(userCard, false);
        setNodeVisible(bookingsButton, false);
    }

    private void applyLoggedState(UserAccount user) {
        setNodeVisible(loginButton, false);
        setNodeVisible(userButton, true);

        boolean isGuide = user.getRole() == RoleEnum.TOURISTGUIDE;
        setNodeVisible(bookingsButton, !isGuide);

        String fullName = safeFullName(user);
        String email = safeEmail(user);

        setUserTexts(fullName, email);
    }

    private void setNodeVisible(javafx.scene.Node node, boolean visible) {
        if (node == null) return;
        node.setVisible(visible);
        node.setManaged(visible);
    }

    private void clearUserButtonText() {
        if (userButton != null) {
            userButton.setText("");
        }
    }

    private String safeFullName(UserAccount user) {
        String fullName = user.getFullName();
        return (fullName == null || fullName.isBlank()) ? "User" : fullName;
    }

    private String safeEmail(UserAccount user) {
        String email = user.getUserEmail();
        return (email == null || email.isBlank()) ? "-" : email;
    }

    private void setUserTexts(String fullName, String email) {
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
        NavigatorBase.goTo(PAGE_LOGIN);
    }

    @FXML
    private void onBookingsClicked() {
        NavigatorBase.goTo(PAGE_BOOKINGS);
    }

    @FXML
    private void onLogoutClicked() {
        Session session = SessionManagerSingleton.getInstance().getCurrentSession();
        if (session != null) {
            SessionManagerSingleton.getInstance().removeSession(session.getSessionID());
        }
        SessionManagerSingleton.getInstance().clearCurrentSession();

        hideUserCard();
        refreshHeader();
        setCenter(PAGE_LOGIN);
    }
}
