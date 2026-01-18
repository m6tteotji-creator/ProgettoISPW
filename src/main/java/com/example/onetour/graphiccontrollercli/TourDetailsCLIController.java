package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.TourBean;

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
            CLIPrinter.printMessage("Errore: tour non valido.\n");
            return;
        }

        TourBean details;
        try {
            TourBean in = new TourBean();
            in.setSessionID(sessionId);
            in.setTourID(tourId);

            details = bookTourController.getTourDescription(in);
        } catch (Exception e) {
            CLIPrinter.printMessage("Errore nel caricamento dettagli tour: " + e.getMessage() + "\n");
            return;
        }

        while (true) {
            printDetails(details);

            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("1) Prenota\n");
            CLIPrinter.printMessage("2) Vai a Prenotazioni\n");
            CLIPrinter.printMessage("0) Indietro\n");
            int choice = readInt("Seleziona: ", 0, 2);

            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> doBooking();
                case 2 -> new MyBookingsCLIController(sessionId).start();
                default -> CLIPrinter.printMessage("Scelta non valida.\n");
            }
        }
    }

    private void printDetails(TourBean t) {
        CLIPrinter.printMessage("\n");
        CLIPrinter.printMessage("=== DETTAGLI TOUR ===\n");
        CLIPrinter.printMessage("ID: " + safe(t.getTourID()) + "\n");
        CLIPrinter.printMessage("Nome tour: " + safe(t.getTourName()) + "\n");
        CLIPrinter.printMessage("Città: " + safe(t.getCityName()) + "\n");
        CLIPrinter.printMessage("Partenza: " + safeDate(t.getDepartureDate()) + "\n");
        CLIPrinter.printMessage("Ritorno: " + safeDate(t.getReturnDate()) + "\n");
        CLIPrinter.printMessage("Guida: " + safe(t.getGuideName()) + "\n");
        CLIPrinter.printMessage("Prezzo: " + t.getPrice() + "\n");

        List<String> attr = t.getAttractions();
        if (attr != null && !attr.isEmpty()) {
            CLIPrinter.printMessage("Attrazioni:\n");
            for (String a : attr) {
                CLIPrinter.printMessage(" - " + safe(a) + "\n");
            }
        }
    }

    private void doBooking() {
        try {
            BookingBean in = new BookingBean();
            in.setSessionID(sessionId);

            BookingBean out = bookTourController.createTicket(in);

            CLIPrinter.printMessage("Prenotazione creata!\n");
            CLIPrinter.printMessage("Ticket ID: " + safe(out.getTicketID()) + "\n");
            CLIPrinter.printMessage("Stato: " + safeState(out.getState()) + "\n");
        } catch (Exception e) {
            CLIPrinter.printMessage("Errore prenotazione: " + e.getMessage() + "\n");
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
