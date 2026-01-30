package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.TourBean;
import com.example.onetour.util.Printer;

import java.time.LocalDate;
import java.util.List;

public class TourDetailsCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final String tourId;
    private final BookTourController bookTourController = new BookTourController();

    public TourDetailsCLIController(String sessionId, TourBean selected) {
        this.sessionId = sessionId;
        this.tourId = (selected == null) ? null : selected.getTourID();
    }

    public void start() {
        if (tourId == null || tourId.isBlank()) {
            Printer.printMessage("Errore: tour non valido.\n");
            return;
        }

        TourBean details;
        try {
            TourBean in = new TourBean();
            in.setSessionID(sessionId);
            in.setTourID(tourId);

            details = bookTourController.getTourDescription(in);
        } catch (Exception e) {
            Printer.printMessage("Errore nel caricamento dettagli tour: " + e.getMessage() + "\n");
            return;
        }

        while (true) {
            printDetails(details);

            Printer.printMessage("\n");
            Printer.printMessage("1) Prenota\n");
            Printer.printMessage("2) Vai a Prenotazioni\n");
            Printer.printMessage("0) Indietro\n");
            int choice = readInt("Seleziona: ", 0, 2);

            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> doBooking();
                case 2 -> new MyBookingsCLIController(sessionId).start();
                default -> Printer.printMessage("Scelta non valida.\n");
            }
        }
    }

    private void printDetails(TourBean t) {
        Printer.printMessage("\n");
        Printer.printMessage("=== DETTAGLI TOUR ===\n");
        Printer.printMessage("ID: " + safe(t.getTourID()) + "\n");
        Printer.printMessage("Nome tour: " + safe(t.getTourName()) + "\n");
        Printer.printMessage("Città: " + safe(t.getCityName()) + "\n");
        Printer.printMessage("Partenza: " + safeDate(t.getDepartureDate()) + "\n");
        Printer.printMessage("Ritorno: " + safeDate(t.getReturnDate()) + "\n");
        Printer.printMessage("Guida: " + safe(t.getGuideName()) + "\n");
        Printer.printMessage("Prezzo: " + t.getPrice() + "\n");

        List<String> attr = t.getAttractions();
        if (attr != null && !attr.isEmpty()) {
            Printer.printMessage("Attrazioni:\n");
            for (String a : attr) {
                Printer.printMessage(" - " + safe(a) + "\n");
            }
        }
    }

    private void doBooking() {
        try {
            BookingBean in = new BookingBean();
            in.setSessionID(sessionId);

            BookingBean out = bookTourController.createTicket(in);

            Printer.printMessage("Prenotazione creata!\n");
            Printer.printMessage("Ticket ID: " + safe(out.getTicketID()) + "\n");
            Printer.printMessage("Stato: " + safeState(out.getState()) + "\n");
        } catch (Exception e) {
            Printer.printMessage("Errore prenotazione: " + e.getMessage() + "\n");
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
