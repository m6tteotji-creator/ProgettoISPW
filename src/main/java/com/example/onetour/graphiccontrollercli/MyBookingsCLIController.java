package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.util.Printer;

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
            Printer.printMessage("\n");
            Printer.printMessage("=== PRENOTAZIONI ===\n");

            List<BookingBean> bookings;
            try {
                bookings = bookTourController.getMyBookings(sessionId);
            } catch (Exception e) {
                Printer.printMessage(
                        "Errore nel recupero prenotazioni: " + e.getMessage() + "\n"
                );
                Printer.printMessage("0) Indietro\n");
                readInt("Seleziona: ", 0, 0);
                return;
            }

            if (bookings == null || bookings.isEmpty()) {
                Printer.printMessage("Nessuna prenotazione trovata.\n");
            } else {
                for (int i = 0; i < bookings.size(); i++) {
                    BookingBean b = bookings.get(i);

                    Printer.printMessage(String.format(
                            "%d) %s | Ticket: %s | Guida: %s | Data: %s | Stato: %s%n",
                            i + 1,
                            safe(b.getTourName()),
                            safe(b.getTicketID()),
                            safe(b.getGuideName()),
                            safeDate(b.getBookingDate()),
                            safeState(b.getState())
                    ));
                }
            }

            Printer.printMessage("\n");
            Printer.printMessage("1) Aggiorna\n");
            Printer.printMessage("0) Indietro\n");

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
