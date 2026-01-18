package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.bean.TourBean;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SearchCLIController extends NavigatorCLIController {

    private final String sessionID;
    private final BookTourController bookTourController = new BookTourController();

    public SearchCLIController(String sessionID) {
        this.sessionID = sessionID;
    }

    public void start() {
        while (true) {
            CLIPrinter.println();
            CLIPrinter.println("=== RICERCA TOUR ===");
            CLIPrinter.println("1) Cerca tour");
            CLIPrinter.println("2) Le mie prenotazioni");
            CLIPrinter.println("3) Logout");
            int choice = readInt("Seleziona: ", 1, 3);

            switch (choice) {
                case 1 -> doSearch();
                case 2 -> new MyBookingsCLIController(sessionID).start();
                case 3 -> {
                    return;
                }
                default -> CLIPrinter.println("Scelta non valida.");
            }
        }
    }

    private void doSearch() {
        try {
            String city = readLine("Citta (cityName): ");

            LocalDate dep = readLocalDateOrNull("Departure date (YYYY-MM-DD) [invio per skip]: ");
            LocalDate ret = readLocalDateOrNull("Return date (YYYY-MM-DD) [invio per skip]: ");

            SearchBean sb = new SearchBean();
            sb.setSessionID(sessionID);
            sb.setCityName(city);
            sb.setDepartureDate(dep);
            sb.setReturnDate(ret);

            List<TourBean> results = bookTourController.searchTours(sb);

            if (results == null || results.isEmpty()) {
                CLIPrinter.println("Nessun tour trovato.");
                return;
            }

            new TourListCLIController(sessionID, results).start();

        } catch (Exception e) {
            CLIPrinter.println("Errore durante la ricerca: " + e.getMessage());
        }
    }

    private LocalDate readLocalDateOrNull(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (s.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException ex) {
                CLIPrinter.println("Formato data non valido. Usa YYYY-MM-DD oppure invio per saltare.");
            }
        }
    }
}
