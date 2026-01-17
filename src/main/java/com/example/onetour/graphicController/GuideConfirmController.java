package com.example.onetour.graphicController;

import com.example.onetour.applicationController.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.enumeration.TicketState;
import com.example.onetour.model.Session;
import com.example.onetour.sessionManagement.SessionManagerSingleton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuideConfirmController {

    private static final Logger logger = Logger.getLogger(GuideConfirmController.class.getName());
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final BookTourController bookTourController = new BookTourController();

    @FXML private TableView<BookingBean> pendingTable;
    @FXML private TableColumn<BookingBean, String> pTourCol;
    @FXML private TableColumn<BookingBean, String> pUserCol;
    @FXML private TableColumn<BookingBean, String> pDateCol;
    @FXML private TableColumn<BookingBean, String> pActionCol;

    @FXML private TableView<BookingBean> historyTable;
    @FXML private TableColumn<BookingBean, String> hTourCol;
    @FXML private TableColumn<BookingBean, String> hUserCol;
    @FXML private TableColumn<BookingBean, String> hDateCol;
    @FXML private TableColumn<BookingBean, String> hStateCol;

    @FXML
    public void initialize() {
        NavigatorBase.refreshHeader();
        setupColumns();
        refreshTables();
    }

    private void setupColumns() {
        // PENDING
        pTourCol.setCellValueFactory(cd -> new SimpleStringProperty(getTourLabel(cd.getValue())));
        pUserCol.setCellValueFactory(cd -> new SimpleStringProperty(getUserLabel(cd.getValue())));
        pDateCol.setCellValueFactory(cd -> new SimpleStringProperty(getDateLabel(cd.getValue())));

        pActionCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                BookingBean b = getTableView().getItems().get(getIndex());

                Button acceptBtn = new Button("Accept");
                Button rejectBtn = new Button("Reject");

                acceptBtn.setOnAction(e -> onDecision(b, TicketState.CONFIRMED));
                rejectBtn.setOnAction(e -> onDecision(b, TicketState.REJECTED));

                setGraphic(new HBox(8, acceptBtn, rejectBtn));
            }
        });

        // HISTORY
        hTourCol.setCellValueFactory(cd -> new SimpleStringProperty(getTourLabel(cd.getValue())));
        hUserCol.setCellValueFactory(cd -> new SimpleStringProperty(getUserLabel(cd.getValue())));
        hDateCol.setCellValueFactory(cd -> new SimpleStringProperty(getDateLabel(cd.getValue())));
        hStateCol.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() != null && cd.getValue().getState() != null ? cd.getValue().getState().name() : "-"
        ));
    }

    private void refreshTables() {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getSessionID() == null || session.getSessionID().isBlank()
                    || session.getUser() == null || session.getUser().getUserEmail() == null) {
                showError("Sessione non valida. Effettua di nuovo il login.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            String sessionID = session.getSessionID();

            var allBeans = bookTourController.getAllRequestsByGuide(sessionID);

            var pending = allBeans.stream()
                    .filter(b -> b.getState() == TicketState.PENDING)
                    .toList();

            var history = allBeans.stream()
                    .filter(b -> b.getState() != TicketState.PENDING)
                    .toList();

            pendingTable.setItems(FXCollections.observableArrayList(pending));
            historyTable.setItems(FXCollections.observableArrayList(history));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading guide requests", e);
            showError("Errore durante il caricamento delle richieste.");
        }
    }

    private void onDecision(BookingBean booking, TicketState decision) {
        try {
            Session session = SessionManagerSingleton.getInstance().getCurrentSession();
            if (session == null || session.getSessionID() == null || session.getSessionID().isBlank()) {
                showError("Sessione non valida.");
                NavigatorBase.goTo("/fxml/pages/page2_login.fxml");
                return;
            }

            EmailBean bean = new EmailBean();
            bean.setSessionID(session.getSessionID());
            bean.setTicketID(booking.getTicketID());
            bean.setDecision(decision);

            bookTourController.guideDecision(bean);

            refreshTables();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during decision", e);
            showError("Errore durante la decisione.");
        }
    }

    private String getTourLabel(BookingBean b) {
        if (b == null) return "-";
        if (b.getTourName() != null && !b.getTourName().isBlank()) {
            return b.getTourName();
        }
        return "-";
    }

    private String getUserLabel(BookingBean b) {
        if (b == null) return "-";
        if (b.getGuideName() != null && !b.getGuideName().isBlank()) {
            return b.getGuideName();
        }
        return "-";
    }

    private String getDateLabel(BookingBean b) {
        if (b == null || b.getBookingDate() == null) return "-";
        return DF.format(b.getBookingDate());
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Guide area");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
