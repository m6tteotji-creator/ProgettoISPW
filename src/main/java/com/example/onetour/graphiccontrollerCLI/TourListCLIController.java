package com.example.onetour.graphiccontrollerCLI;

import com.example.onetour.bean.TourBean;

import java.time.LocalDate;
import java.util.List;

public class TourListCLIController extends NavigatorCLIController {

    private final String sessionId;
    private final List<TourBean> tours;

    public TourListCLIController(String sessionId, List<TourBean> tours) {
        this.sessionId = sessionId;
        this.tours = tours;
    }

    public void start() {
        while (true) {
            System.out.println();
            System.out.println("=== RISULTATI TOUR ===");

            if (tours == null || tours.isEmpty()) {
                System.out.println("Nessun risultato.");
                return;
            }

            int max = Math.min(tours.size(), 10);
            for (int i = 0; i < max; i++) {
                TourBean t = tours.get(i);

                String tourName = safe(t.getTourName());
                String cityName = safe(t.getCityName());
                String dep = formatDate(t.getDepartureDate());
                String ret = formatDate(t.getReturnDate());
                String price = formatPrice(t.getPrice());
                String guide = safe(t.getGuideName());

                System.out.printf(
                        "%d) %s | %s | %s -> %s | %s | Guida: %s%n",
                        i + 1,
                        tourName,
                        cityName,
                        dep,
                        ret,
                        price,
                        guide
                );
            }

            System.out.println("0) Indietro");
            int choice = readInt("Seleziona un tour: ", 0, max);

            if (choice == 0) return;

            TourBean selected = tours.get(choice - 1);
            new TourDetailsCLIController(sessionId, selected).start();
        }
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private String formatDate(LocalDate d) {
        return (d == null) ? "-" : d.toString();
    }

    private String formatPrice(double p) {
        if (p <= 0.0) return "-";
        return String.format("%.2f EUR", p);
    }
}
