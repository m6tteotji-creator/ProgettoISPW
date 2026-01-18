package com.example.onetour.graphiccontrollerCLI;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.EmailBean;
import com.example.onetour.enumeration.TicketState;

import java.util.List;

public class GuideRequestsCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final BookTourController bookTourController = new BookTourController();

    public GuideRequestsCLIController(String sessionId) {
        this.sessionId = sessionId;
    }

    public void start() {
        while (true) {
            System.out.println();
            System.out.println("=== AREA GUIDA ===");
            System.out.println("1) Visualizza richieste pending");
            System.out.println("2) Logout");
            int choice = readInt("Seleziona: ", 1, 2);

            if (choice == 2) {
                return;
            }

            showPendingAndDecide();
        }
    }

    private void showPendingAndDecide() {
        try {
            List<BookingBean> pending = bookTourController.getPendingRequests(sessionId);

            if (pending == null || pending.isEmpty()) {
                System.out.println("Nessuna richiesta pending.");
                return;
            }

            System.out.println();
            System.out.println("Richieste pending:");
            int max = pending.size();

            for (int i = 0; i < max; i++) {
                BookingBean b = pending.get(i);

                System.out.printf(
                        "%d) %s | Tour: %s | Data: %s | Stato: %s%n",
                        i + 1,
                        safe(b.getGuideName()),
                        safe(b.getTourName()),
                        safeDate(b.getBookingDate()),
                        safeState(b.getState())
                );
                System.out.println("    TicketID: " + safe(b.getTicketID()));
            }

            System.out.println("0) Indietro");
            int idx = readInt("Seleziona una richiesta: ", 0, max);
            if (idx == 0) return;

            BookingBean selected = pending.get(idx - 1);

            System.out.println();
            System.out.println("1) Conferma");
            System.out.println("2) Rifiuta");
            System.out.println("0) Annulla");
            int decision = readInt("Scelta: ", 0, 2);
            if (decision == 0) return;

            boolean accept = (decision == 1);

            EmailBean eb = new EmailBean();
            eb.setSessionID(sessionId);
            eb.setTicketID(selected.getTicketID());
            eb.setDecision(accept ? TicketState.CONFIRMED : TicketState.REJECTED);

            bookTourController.guideDecision(eb);

            System.out.println("Decisione salvata (" + (accept ? "CONFERMATA" : "RIFIUTATA") + ").");

        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String safeDate(java.time.LocalDate d) {
        return (d == null) ? "-" : d.toString();
    }

    private String safeState(TicketState st) {
        return (st == null) ? "-" : st.name();
    }
}
