package com.example.onetour.graphiccontroller;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.model.Session;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchTourController {

    private final Logger logger = Logger.getLogger(SearchTourController.class.getName());
    private final BookTourController bookTourController = new BookTourController();

    @FXML
    private TextField destinationField;   // fx:id nel tuo page4_home.fxml

    @FXML
    private DatePicker fromDatePicker;    // fx:id nel tuo page4_home.fxml

    @FXML
    private DatePicker toDatePicker;      // fx:id nel tuo page4_home.fxml

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
    }

    @FXML
    private void onSearchClicked() {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getUser() == null || session.getSessionID() == null || session.getSessionID().isBlank()) {
                showErrorDialog("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            String destination = destinationField != null ? destinationField.getText() : null;
            LocalDate from = fromDatePicker != null ? fromDatePicker.getValue() : null;
            LocalDate to = toDatePicker != null ? toDatePicker.getValue() : null;

            SearchBean sb = new SearchBean();
            sb.setSessionID(session.getSessionID());
            sb.setCityName(destination);
            sb.setDepartureDate(from);
            sb.setReturnDate(to);
            sb.checkFields();

            bookTourController.searchTours(sb);

            NavigatorBase.goTo("/fxml/pages/page5_results.fxml");

        } catch (InvalidFormatException e) {
            logger.log(Level.WARNING, e.getMessage());
            showErrorDialog(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            showErrorDialog("Errore durante la ricerca.");
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search Tour");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
