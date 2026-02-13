package com.example.onetour.graphiccontroller;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.TourBean;
import com.example.onetour.exception.InvalidFormatException;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Session;
import com.example.onetour.sessionmanagement.SessionManagerSingleton;
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

    private final BookTourController bookTourController = new BookTourController();

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
        List<TourBean> beans = session.getLastTourList();

        tourListView.setItems(FXCollections.observableArrayList(beans));
    }


    private void setupClickableCells() {
        tourListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(TourBean item, boolean empty) {
                super.updateItem(item, empty);
                handleCellUpdate(this, item, empty);
            }
        });
    }

    private void handleCellUpdate(ListCell<TourBean> cell, TourBean item, boolean empty) {
        if (empty || item == null) {
            cell.setText(null);
            cell.setOnMouseClicked(null);
            return;
        }

        cell.setText(formatCellText(item));

        cell.setOnMouseClicked(evt -> {
            if (!cell.isEmpty()) {
                onTourSelected(item);
            }
        });
    }

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
            if (session == null || session.getSessionID() == null || session.getSessionID().isBlank()) {
                showErrorDialog("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            if (selected.getTourID() == null || selected.getTourID().isBlank()) {
                showErrorDialog("Tour non valido (manca tourID).");
                return;
            }

            TourBean in = new TourBean();
            in.setSessionID(session.getSessionID());
            in.setTourID(selected.getTourID());

            bookTourController.getTourDescription(in);

            NavigatorBase.goTo("/fxml/pages/page6_details.fxml");

        } catch (TourNotFoundException | InvalidFormatException e) {
            logger.log(Level.WARNING, e.getMessage());
            showErrorDialog(e.getMessage());

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            showErrorDialog("Errore durante la selezione del tour.");
        }

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
