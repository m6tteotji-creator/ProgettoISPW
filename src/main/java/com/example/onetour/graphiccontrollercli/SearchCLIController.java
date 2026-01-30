package com.example.onetour.graphiccontrollercli;

import com.example.onetour.applicationcontroller.BookTourController;
import com.example.onetour.bean.SearchBean;
import com.example.onetour.bean.TourBean;
import com.example.onetour.util.Printer;

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
            Printer.printMessage("\n");
            Printer.printMessage("=== RICERCA TOUR ===\n");
            Printer.printMessage("1) Cerca tour\n");
            Printer.printMessage("2) Le mie prenotazioni\n");
            Printer.printMessage("3) Logout\n");

            int choice = readInt("Seleziona: ", 1, 3);

            switch (choice) {
                case 1 -> doSearch();
                case 2 -> new MyBookingsCLIController(sessionID).start();
                case 3 -> {
                    return;
                }
                default -> Printer.printMessage("Scelta non valida.\n");
            }
        }
    }

    private void doSearch() {
        while (true) {
            try {
                String city = readLine("Citta (cityName): ").trim();

                LocalDate dep = readRequiredLocalDate("Departure date (YYYY-MM-DD): ");
                LocalDate ret = readRequiredLocalDate("Return date (YYYY-MM-DD): ");

                SearchBean sb = new SearchBean();
                sb.setSessionID(sessionID);
                sb.setCityName(city);
                sb.setDepartureDate(dep);
                sb.setReturnDate(ret);

                List<TourBean> results = bookTourController.searchTours(sb);

                if (results == null || results.isEmpty()) {
                    Printer.printMessage("Nessun tour trovato.\n");
                    return;
                }

                new TourListCLIController(sessionID, results).start();
                return;

            } catch (Exception e) {
                Printer.printMessage("Errore durante la ricerca: " + e.getMessage() + "\n");
                Printer.printMessage("Riprova.\n");
            }
        }
    }


    private LocalDate readRequiredLocalDate(String prompt) {
        while (true) {
            String s = readLine(prompt).trim();
            if (s.isBlank()) {
                Printer.printMessage("La data e' obbligatoria.\n");
                continue;
            }
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException ex) {
                Printer.printMessage("Formato data non valido. Usa YYYY-MM-DD.\n");
            }
        }
    }
}
