package com.example.onetour.graphiccontrollerCLI;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;

import java.util.List;

public class MyBookingsCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final BookTourController bookTourController = new BookTourController();

    public MyBookingsCLIController(String sessionId) {
        this.sessionId = sessionId;
    }

    public void start() {
        while (true) {
            System.out.println();
            System.out.println("=== PRENOTAZIONI ===");

            List<BookingBean> bookings;
            try {
                bookings = bookTourController.getMyBookings(sessionId);
            } catch (Exception e) {
                System.out.println("Errore nel recupero prenotazioni: " + e.getMessage());
                System.out.println("0) Indietro");
                readInt("Seleziona: ", 0, 0);
                return;
            }

            if (bookings == null || bookings.isEmpty()) {
                System.out.println("Nessuna prenotazione trovata.");
            } else {
                for (int i = 0; i < bookings.size(); i++) {
                    BookingBean b = bookings.get(i);
                    System.out.printf(
                            "%d) %s | %s | Guida: %s | Data: %s | Stato: %s%n",
                            i + 1,
                            safe(b.getTourName()),
                            safe(b.getTicketID()),
                            safe(b.getGuideName()),
                            (b.getBookingDate() == null ? "-" : b.getBookingDate()),
                            (b.getState() == null ? "-" : b.getState())
                    );
                }
            }

            System.out.println();
            System.out.println("1) Aggiorna");
            System.out.println("0) Indietro");
            int choice = readInt("Seleziona: ", 0, 1);
            if (choice == 0) return;
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
