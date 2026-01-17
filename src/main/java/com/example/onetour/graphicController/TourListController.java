package com.example.onetour.graphicController;

import com.example.onetour.bean.TourBean;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Session;
import com.example.onetour.model.Tour;
import com.example.onetour.sessionManagement.SessionManagerSingleton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TourListController {

    private static final Logger logger = Logger.getLogger(TourListController.class.getName());

    @FXML
    private ListView<TourBean> tourListView;

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
        loadToursIntoList();
        setupClickableCells();
    }

    private void loadToursIntoList() {
        Session session = SessionManagerSingleton.getInstance().getCurrentSession();
        if (session == null || session.getLastTourList() == null || session.getLastTourList().isEmpty()) {
            showInfoDialog();
            NavigatorBase.goTo("/fxml/pages/page4_home.fxml");
            return;
        }

        List<TourBean> beans = session.getLastTourList().stream()
                .map(TourBean::fromModel)
                .toList();

        tourListView.setItems(FXCollections.observableArrayList(beans));
    }

    // *** MODIFICA QUI: Ho rimosso l'annidamento profondo. ***
    // Invece di scrivere tutta la logica dentro new ListCell, delego a un metodo helper.
    private void setupClickableCells() {
        tourListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(TourBean item, boolean empty) {
                super.updateItem(item, empty);
                handleCellUpdate(this, item, empty);
            }
        });
    }

    // *** MODIFICA QUI: Nuovo metodo helper per gestire la logica della cella ***
    // Questo riduce drasticamente la "Cognitive Complexity" perché non è più annidato.
    private void handleCellUpdate(ListCell<TourBean> cell, TourBean item, boolean empty) {
        if (empty || item == null) {
            cell.setText(null);
            cell.setOnMouseClicked(null);
            return;
        }

        cell.setText(formatCellText(item));

        // Assegno l'evento click
        cell.setOnMouseClicked(evt -> {
            if (!cell.isEmpty()) {
                onTourSelected(item);
            }
        });
    }

    // *** MODIFICA QUI: Estratta anche la logica di formattazione del testo ***
    private String formatCellText(TourBean item) {
        String title = (item.getTourName() != null && !item.getTourName().isBlank())
                ? item.getTourName()
                : "Tour";

        String city = item.getCityName() != null ? item.getCityName() : "";
        return title + (city.isBlank() ? "" : " • " + city);
    }

    private void onTourSelected(TourBean selected) {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getLastTourList() == null) {
                showErrorDialog("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            // *** MODIFICA QUI: Ho estratto la ricerca in un metodo separato per pulire onTourSelected ***
            Tour tourModel = findTourInSession(session, selected.getTourID());

            session.setActualTour(tourModel);
            NavigatorBase.goTo("/fxml/pages/page6_details.fxml");

        } catch (TourNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            showErrorDialog(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            showErrorDialog("Errore durante la selezione del tour.");
        }
    }

    // *** MODIFICA QUI: Helper per la ricerca stream, riduce la complessità del metodo chiamante ***
    private Tour findTourInSession(Session session, String tourId) throws TourNotFoundException {
        return session.getLastTourList().stream()
                .filter(t -> t.getTourID() != null && t.getTourID().equals(tourId))
                .findFirst()
                .orElseThrow(() -> new TourNotFoundException("Tour non trovato."));
    }

    private void showInfoDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search results");
        alert.setHeaderText(null);
        alert.setContentText("Nessun risultato da mostrare. Rifai la ricerca.");
        alert.showAndWait();
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Search results");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}