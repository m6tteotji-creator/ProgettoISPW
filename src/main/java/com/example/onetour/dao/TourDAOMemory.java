package com.example.onetour.dao;

import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourDAOMemory extends TourDAO {

    private static final List<Tour> SEED = new ArrayList<>();
    private static boolean initialized = false;

    private static void initSeed() {
        if (initialized) return;

        TouristGuide g1 = new TouristGuide(
                "G001", "Marco", "Rossi", "guide1@guide.com",
                List.of("IT", "EN")
        );

        TouristGuide g2 = new TouristGuide(
                "G002", "Luca", "Bianchi", "guide2@guide.com",
                List.of("IT", "FR", "EN")
        );

        // --- TOURS ---
        Tour t1 = new Tour(
                "T001", "Colosseo Experience", "Roma",
                LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12), 25.0
        );
        t1.setAttractions(List.of("Colosseo", "Foro Romano", "Palatino"));
        t1.setTouristGuide(g1);

        Tour t2 = new Tour(
                "T002", "Vatican Museums Tour", "Roma",
                LocalDate.of(2026, 2, 15), LocalDate.of(2026, 2, 16), 30.0
        );
        t2.setAttractions(List.of("Musei Vaticani", "Cappella Sistina", "Basilica San Pietro"));
        t2.setTouristGuide(g1);

        Tour t3 = new Tour(
                "T003", "Duomo & Centro Storico", "Milano",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2), 20.0
        );
        t3.setAttractions(List.of("Duomo", "Galleria Vittorio Emanuele", "Castello Sforzesco"));
        t3.setTouristGuide(g2);

        Tour t4 = new Tour(
                "T004", "Canal Cruise & Night Walk", "Venezia",
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 12), 35.0
        );
        t4.setAttractions(List.of("Canal Grande", "Rialto", "San Marco"));
        t4.setTouristGuide(g2);

        SEED.add(t1);
        SEED.add(t2);
        SEED.add(t3);
        SEED.add(t4);

        initialized = true;
    }

    @Override
    public List<Tour> findTours(String cityName, LocalDate departureDate, LocalDate returnDate)
            throws SQLException, TourNotFoundException {

        initSeed();

        if (cityName == null || cityName.isBlank()) throw new IllegalArgumentException("cityName null/blank");
        if (departureDate == null || returnDate == null) throw new IllegalArgumentException("dates null");

        List<Tour> out = new ArrayList<>();
        for (Tour t : SEED) {
            if (t.getCityName() != null
                    && t.getCityName().equalsIgnoreCase(cityName)
                    && !t.getDepartureDate().isBefore(departureDate)
                    && !t.getReturnDate().isAfter(returnDate)) {
                out.add(t);
            }
        }

        if (out.isEmpty()) throw new TourNotFoundException("No tours available");
        return out;
    }

    @Override
    public Tour retrieveTourFromId(String tourID) throws SQLException, TourNotFoundException {

        initSeed();

        if (tourID == null || tourID.isBlank()) throw new IllegalArgumentException("tourID null/blank");

        for (Tour t : SEED) {
            if (t.getTourID() != null && t.getTourID().equals(tourID)) return t;
        }
        throw new TourNotFoundException("Tour not found");
    }

    @Override
    public Tour retrieveTour(String tourName, LocalDate departureDate, LocalDate returnDate)
            throws SQLException, TourNotFoundException {

        initSeed();

        if (tourName == null || tourName.isBlank()) throw new IllegalArgumentException("tourName null/blank");
        if (departureDate == null || returnDate == null) throw new IllegalArgumentException("dates null");

        for (Tour t : SEED) {
            if (t.getNameTour() != null
                    && t.getNameTour().equalsIgnoreCase(tourName)
                    && t.getDepartureDate().equals(departureDate)
                    && t.getReturnDate().equals(returnDate)) {
                return t;
            }
        }
        throw new TourNotFoundException("Tour not found");
    }
}