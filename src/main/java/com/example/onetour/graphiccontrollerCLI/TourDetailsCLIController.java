package com.example.onetour.graphiccontrollerCLI;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.BookingBean;
import com.example.onetour.bean.TourBean;

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
            System.out.println("Errore: tour non valido.");
            return;
        }

        TourBean details;
        try {
            TourBean in = new TourBean();
            in.setSessionID(sessionId);
            in.setTourID(tourId);

            details = bookTourController.getTourDescription(in);

        } catch (Exception e) {
            System.out.println("Errore nel caricamento dettagli tour: " + e.getMessage());
            return;
        }

        while (true) {
            printDetails(details);

            System.out.println();
            System.out.println("1) Prenota");
            System.out.println("2) Vai a Prenotazioni");
            System.out.println("0) Indietro");
            int choice = readInt("Seleziona: ", 0, 2);

            switch (choice) {
                case 0 -> { return; }
                case 1 -> doBooking();
                case 2 -> new MyBookingsCLIController(sessionId).start();
            }
        }
    }

    private void printDetails(TourBean t) {
        System.out.println();
        System.out.println("=== DETTAGLI TOUR ===");
        System.out.println("ID: " + safe(t.getTourID()));
        System.out.println("Nome tour: " + safe(t.getTourName()));
        System.out.println("Città: " + safe(t.getCityName()));
        System.out.println("Partenza: " + (t.getDepartureDate() == null ? "-" : t.getDepartureDate()));
        System.out.println("Ritorno: " + (t.getReturnDate() == null ? "-" : t.getReturnDate()));
        System.out.println("Guida: " + safe(t.getGuideName()));
        System.out.println("Prezzo: " + t.getPrice());

        List<String> attr = t.getAttractions();
        if (attr != null && !attr.isEmpty()) {
            System.out.println("Attrazioni:");
            for (String a : attr) System.out.println(" - " + a);
        }
    }

    private void doBooking() {
        try {
            BookingBean in = new BookingBean();
            in.setSessionID(sessionId);

            BookingBean out = bookTourController.createTicket(in);

            System.out.println("Prenotazione creata!");
            System.out.println("Ticket ID: " + safe(out.getTicketID()));
            System.out.println("Stato: " + (out.getState() == null ? "-" : out.getState()));
        } catch (Exception e) {
            System.out.println("Errore prenotazione: " + e.getMessage());
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
