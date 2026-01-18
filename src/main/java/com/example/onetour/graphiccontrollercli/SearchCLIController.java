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
            CLIPrinter.printMessage("\n");
            CLIPrinter.printMessage("=== RICERCA TOUR ===\n");
            CLIPrinter.printMessage("1) Cerca tour\n");
            CLIPrinter.printMessage("2) Le mie prenotazioni\n");
            CLIPrinter.printMessage("3) Logout\n");

            int choice = readInt("Seleziona: ", 1, 3);

            switch (choice) {
                case 1 -> doSearch();
                case 2 -> new MyBookingsCLIController(sessionID).start();
                case 3 -> {
                    return;
                }
                default -> CLIPrinter.printMessage("Scelta non valida.\n");
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
                CLIPrinter.printMessage("Nessun tour trovato.\n");
                return;
            }

            new TourListCLIController(sessionID, results).start();

        } catch (Exception e) {
            CLIPrinter.printMessage("Errore durante la ricerca: " + e.getMessage() + "\n");
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
                CLIPrinter.printMessage("Formato data non valido. Usa YYYY-MM-DD oppure invio per saltare.\n");
            }
        }
    }
}
