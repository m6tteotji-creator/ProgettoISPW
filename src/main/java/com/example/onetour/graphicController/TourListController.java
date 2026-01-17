package com.example.onetour.graphicController;

import com.example.onetour.bean.TourBean;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Session;
import com.example.onetour.sessionManagement.SessionManagerSingleton;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

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

    private void setupClickableCells() {
        tourListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TourBean> call(ListView<TourBean> listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TourBean item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                            setOnMouseClicked(null);
                            return;
                        }

                        String title = (item.getTourName() != null && !item.getTourName().isBlank())
                                ? item.getTourName()
                                : "Tour";

                        String city = item.getCityName() != null ? item.getCityName() : "";
                        setText(title + (city.isBlank() ? "" : " • " + city));

                        setOnMouseClicked(evt -> {
                            if (!isEmpty()) {
                                onTourSelected(item);
                            }
                        });
                    }
                };
            }
        });
    }

    private void onTourSelected(TourBean selected) {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getLastTourList() == null) {
                showErrorDialog("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            var tourModel = session.getLastTourList().stream()
                    .filter(t -> t.getTourID() != null && t.getTourID().equals(selected.getTourID()))
                    .findFirst()
                    .orElseThrow(() -> new TourNotFoundException("Tour non trovato."));

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
