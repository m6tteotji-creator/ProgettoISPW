package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;

import java.time.LocalDate;
import java.util.List;

public class MyBookingsCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final BookTourController bookTourController = new BookTourController();

    public MyBookingsCLIController(String sessionId) {
        this.sessionId = sessionId;
    }

    public void start() {
        while (true) {
            CLIPrinter.println();
            CLIPrinter.println("=== PRENOTAZIONI ===");

            List<BookingBean> bookings;
            try {
                bookings = bookTourController.getMyBookings(sessionId);
            } catch (Exception e) {
                CLIPrinter.println("Errore nel recupero prenotazioni: " + e.getMessage());
                CLIPrinter.println("0) Indietro");
                readInt("Seleziona: ", 0, 0);
                return;
            }

            if (bookings == null || bookings.isEmpty()) {
                CLIPrinter.println("Nessuna prenotazione trovata.");
            } else {
                for (int i = 0; i < bookings.size(); i++) {
                    BookingBean b = bookings.get(i);

                    CLIPrinter.printf(
                            "%d) %s | Ticket: %s | Guida: %s | Data: %s | Stato: %s%n",
                            i + 1,
                            safe(b.getTourName()),
                            safe(b.getTicketID()),
                            safe(b.getGuideName()),
                            safeDate(b.getBookingDate()),
                            safeState(b.getState())
                    );
                }
            }

            CLIPrinter.println();
            CLIPrinter.println("1) Aggiorna");
            CLIPrinter.println("0) Indietro");
            int choice = readInt("Seleziona: ", 0, 1);
            if (choice == 0) {
                return;
            }
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String safeDate(LocalDate d) {
        return (d == null) ? "-" : d.toString();
    }

    private String safeState(Object st) {
        return (st == null) ? "-" : st.toString();
    }
}
