package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.enumeration.TicketState;

import java.time.LocalDate;
import java.util.List;

public class GuideRequestsCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final BookTourController bookTourController = new BookTourController();

    public GuideRequestsCLIController(String sessionId) {
        this.sessionId = sessionId;
    }

    public void start() {
        while (true) {
            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("=== AREA GUIDA ===\n");
            CLIPrinter.printMessage("1) Visualizza richieste\n");
            CLIPrinter.printMessage("2) Visualizza storico\n");
            CLIPrinter.printMessage("3) Logout\n");

            int choice = readInt("Seleziona: ", 1, 3);

            if (choice == 3) {
                return;
            }

            if (choice == 1) {
                showPendingAndDecide();
            } else {
                showAllRequestsHistory();
            }
        }
    }

    private void showPendingAndDecide() {
        try {
            List<BookingBean> pending = bookTourController.getPendingRequests(sessionId);

            if (pending == null || pending.isEmpty()) {
                CLIPrinter.printMessage("Nessuna richiesta.\n");
                return;
            }

            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("Richieste:\n");
            int max = pending.size();

            for (int i = 0; i < max; i++) {
                BookingBean b = pending.get(i);

                CLIPrinter.printMessage(String.format(
                        "%d) Guida: %s | Tour: %s | Data: %s | Stato: %s%n",
                        i + 1,
                        safe(b.getGuideName()),
                        safe(b.getTourName()),
                        safeDate(b.getBookingDate()),
                        safeState(b.getState())
                ));

                CLIPrinter.printMessage("   TicketID: " + safe(b.getTicketID()) + "\n");
            }

            CLIPrinter.printMessage("0) Indietro\n");
            int idx = readInt("Seleziona una richiesta: ", 0, max);
            if (idx == 0) {
                return;
            }

            BookingBean selected = pending.get(idx - 1);

            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("1) Conferma\n");
            CLIPrinter.printMessage("2) Rifiuta\n");
            CLIPrinter.printMessage("0) Annulla\n");
            int decision = readInt("Scelta: ", 0, 2);
            if (decision == 0) {
                return;
            }

            boolean accept = (decision == 1);

            EmailBean eb = new EmailBean();
            eb.setSessionID(sessionId);
            eb.setTicketID(selected.getTicketID());
            eb.setDecision(accept ? TicketState.CONFIRMED : TicketState.REJECTED);

            bookTourController.guideDecision(eb);

            CLIPrinter.printMessage("Decisione salvata (" + (accept ? "CONFERMATA" : "RIFIUTATA") + ").\n");

        } catch (Exception e) {
            CLIPrinter.printMessage("Errore: " + e.getMessage() + "\n");
        }
    }

    private void showAllRequestsHistory() {
        try {
            List<BookingBean> all = bookTourController.getAllRequestsByGuide(sessionId);

            if (all == null || all.isEmpty()) {
                CLIPrinter.printMessage("Nessuna richiesta trovata.\n");
                return;
            }

            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("Storico richieste:\n");

            for (int i = 0; i < all.size(); i++) {
                BookingBean b = all.get(i);

                CLIPrinter.printMessage(String.format(
                        "%d) Tour: %s | Data: %s | Stato: %s | TicketID: %s%n",
                        i + 1,
                        safe(b.getTourName()),
                        safeDate(b.getBookingDate()),
                        safeState(b.getState()),
                        safe(b.getTicketID())
                ));
            }

        } catch (Exception e) {
            CLIPrinter.printMessage("Errore: " + e.getMessage() + "\n");
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String safeDate(LocalDate d) {
        return (d == null) ? "-" : d.toString();
    }

    private String safeState(TicketState st) {
        return (st == null) ? "-" : st.name();
    }
}
