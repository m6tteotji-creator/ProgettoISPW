package com.example.onetour.graphicController;

import com.example.onetour.applicationController.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.exception.TicketNotFoundException;
import com.example.onetour.model.Session;
import com.example.onetour.sessionManagement.SessionManagerSingleton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBookingsController {

    private static final Logger logger = Logger.getLogger(MyBookingsController.class.getName());
    private final BookTourController bookTourController = new BookTourController();

    @FXML
    private ListView<BookingBean> bookingsListView;

    @FXML
    private Label hintLabel;

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
        setupCellRendering();
        loadMyBookings();
    }

    private void loadMyBookings() {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getUser() == null || session.getSessionID() == null || session.getSessionID().isBlank()) {
                showErrorDialog("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            List<BookingBean> bookings = bookTourController.getMyBookings(session.getSessionID());
            bookingsListView.setItems(FXCollections.observableArrayList(bookings));

            if (bookings.isEmpty()) {
                hintLabel.setText("No bookings found.");
            } else {
                hintLabel.setText("");
            }

        } catch (TicketNotFoundException e) {
            logger.log(Level.INFO, e.getMessage());
            bookingsListView.setItems(FXCollections.observableArrayList());
            hintLabel.setText("No bookings found.");

        } catch (InvalidFormatException e) {
            logger.log(Level.WARNING, e.getMessage());
            showErrorDialog(e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            showErrorDialog("Errore nel caricamento delle prenotazioni.");
        }
    }

    private void setupCellRendering() {
        bookingsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(BookingBean item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String tour = safe(item.getTourName());
                String guide = safe(item.getGuideName());

                LocalDate bookingDate = item.getBookingDate();
                String dateStr = (bookingDate == null) ? "" : bookingDate.toString();

                String stateStr = formatState(item.getState());

                String line1 = tour.isBlank() ? "Tour" : tour;
                if (!guide.isBlank()) {
                    line1 += " • " + guide;
                }

                String line2 = "";
                if (!dateStr.isBlank()) {
                    line2 += dateStr;
                }
                if (!stateStr.isBlank()) {
                    line2 += (line2.isBlank() ? "" : " • ") + stateStr;
                }

                setText(line1 + (line2.isBlank() ? "" : "\n" + line2));
            }
        });
    }

    private String formatState(TicketState state) {
        if (state == null) return "";
        return switch (state) {
            case PENDING -> "In attesa";
            case CONFIRMED -> "Confermato";
            case REJECTED -> "Rifiutato";
        };
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("My bookings");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
