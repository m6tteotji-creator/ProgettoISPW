package com.example.onetour.graphicController;

import com.example.onetour.applicationController.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.model.Session;
import com.example.onetour.model.Tour;
import com.example.onetour.sessionManagement.SessionManagerSingleton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuidedTourController {

    private static final Logger logger =
            Logger.getLogger(GuidedTourController.class.getName());

    private final BookTourController bookTourController =
            new BookTourController();

    @FXML private Label tourNameLabel;
    @FXML private Label cityLabel;
    @FXML private Label datesLabel;
    @FXML private Label priceLabel;
    @FXML private Label guideLabel;
    @FXML private ListView<String> attractionsListView;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
        statusLabel.setText("");

        Session session = SessionManagerSingleton
                .getInstance()
                .getCurrentSession();

        if (session == null ||
                session.getSessionID() == null ||
                session.getSessionID().isBlank()) {

            goBackToLogin();
            return;
        }

        Tour actual = session.getActualTour();
        if (actual == null) {
            NavigatorBase.goTo("/fxml/pages/page5_results.fxml");
            return;
        }

        renderTour(actual);
    }

    private void renderTour(Tour t) {
        tourNameLabel.setText(safe(t.getNameTour(), "Tour"));
        cityLabel.setText(safe(t.getCityName(), "-"));

        LocalDate from = t.getDepartureDate();
        LocalDate to = t.getReturnDate();

        if (from != null && to != null) {
            datesLabel.setText(from + "  →  " + to);
        } else if (from != null) {
            datesLabel.setText(from.toString());
        } else {
            datesLabel.setText("-");
        }

        priceLabel.setText(String.format("%.2f €", t.getPrice()));

        if (t.getTouristGuide() != null &&
                t.getTouristGuide().getName() != null) {
            guideLabel.setText(t.getTouristGuide().getName());
        } else {
            guideLabel.setText("-");
        }

        List<String> attractions = t.getAttractions();
        if (attractions == null) attractions = List.of();

        attractionsListView.setItems(
                FXCollections.observableArrayList(attractions)
        );
    }

    @FXML
    private void onBookClicked() {
        statusLabel.setText("");

        try {
            Session session = SessionManagerSingleton
                    .getInstance()
                    .getCurrentSession();

            if (session == null ||
                    session.getSessionID() == null ||
                    session.getSessionID().isBlank()) {

                goBackToLogin();
                return;
            }

            BookingBean bookingBean = new BookingBean();
            bookingBean.setSessionID(session.getSessionID());

            BookingBean created =
                    bookTourController.createTicket(bookingBean);

            showInfoDialog(
                    "Prenotazione inviata!\nStato: " + created.getState()
            );

            NavigatorBase.goTo("/fxml/pages/page7_bookings.fxml");

        } catch (InvalidFormatException e) {
            logger.log(Level.WARNING, e.getMessage());
            statusLabel.setText(e.getMessage());
            showErrorDialog(e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            statusLabel.setText("Errore durante la prenotazione.");
            showErrorDialog("Errore durante la prenotazione.");
        }
    }


    private void goBackToLogin() {
        showErrorDialog(
                "Sessione non valida. Effettua di nuovo il login."
        );
        NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
    }

    private void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking created");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Booking error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String safe(String s, String fallback) {
        if (s == null) return fallback;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}
