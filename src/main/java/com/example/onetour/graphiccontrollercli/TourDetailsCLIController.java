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
            CLIPrinter.println("Errore: tour non valido.");
            return;
        }

        TourBean details;
        try {
            TourBean in = new TourBean();
            in.setSessionID(sessionId);
            in.setTourID(tourId);

            details = bookTourController.getTourDescription(in);

        } catch (Exception e) {
            CLIPrinter.println("Errore nel caricamento dettagli tour: " + e.getMessage());
            return;
        }

        while (true) {
            printDetails(details);

            CLIPrinter.println();
            CLIPrinter.println("1) Prenota");
            CLIPrinter.println("2) Vai a Prenotazioni");
            CLIPrinter.println("0) Indietro");
            int choice = readInt("Seleziona: ", 0, 2);

            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> doBooking();
                case 2 -> new MyBookingsCLIController(sessionId).start();
                default -> CLIPrinter.println("Scelta non valida.");
            }
        }
    }

    private void printDetails(TourBean t) {
        CLIPrinter.println();
        CLIPrinter.println("=== DETTAGLI TOUR ===");
        CLIPrinter.println("ID: " + safe(t.getTourID()));
        CLIPrinter.println("Nome tour: " + safe(t.getTourName()));
        CLIPrinter.println("Città: " + safe(t.getCityName()));
        CLIPrinter.println("Partenza: " + safeDate(t.getDepartureDate()));
        CLIPrinter.println("Ritorno: " + safeDate(t.getReturnDate()));
        CLIPrinter.println("Guida: " + safe(t.getGuideName()));
        CLIPrinter.println("Prezzo: " + t.getPrice());

        List<String> attr = t.getAttractions();
        if (attr != null && !attr.isEmpty()) {
            CLIPrinter.println("Attrazioni:");
            for (String a : attr) {
                CLIPrinter.println(" - " + safe(a));
            }
        }
    }

    private void doBooking() {
        try {
            BookingBean in = new BookingBean();
            in.setSessionID(sessionId);

            BookingBean out = bookTourController.createTicket(in);

            CLIPrinter.println("Prenotazione creata!");
            CLIPrinter.println("Ticket ID: " + safe(out.getTicketID()));
            CLIPrinter.println("Stato: " + safeState(out.getState()));
        } catch (Exception e) {
            CLIPrinter.println("Errore prenotazione: " + e.getMessage());
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
