package com.example.onetour.dao;

import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourDAOMemory extends TourDAO {

    private static final String CITY_BARCELLONA = "Barcellona";
    private static final String CITY_VIENNA = "Vienna";
    private static final String CITY_PARIGI = "Parigi";
    private static final String CITY_LONDRA = "Londra";

    private static final List<Tour> SEED = new ArrayList<>();
    private static boolean initialized = false;

    private static void initSeed() {
        if (initialized) return;

        // Guides
        TouristGuide gTest = new TouristGuide(
                "G000", "Guida", "Test", "guide@test.com",
                List.of("IT", "EN")
        );

        TouristGuide g1 = new TouristGuide(
                "G001", "Marco", "Rossi", "guide1@guide.com",
                List.of("IT", "EN")
        );

        TouristGuide g2 = new TouristGuide(
                "G002", "Luca", "Bianchi", "guide2@guide.com",
                List.of("IT", "FR", "EN")
        );

        TouristGuide g3 = new TouristGuide(
                "G003", "Anna", "Verdi", "guide3@guide.com",
                List.of("IT", "EN", "ES")
        );

        TouristGuide g4 = new TouristGuide(
                "G004", "Sofia", "Neri", "guide4@guide.com",
                List.of("IT", "EN", "DE")
        );

        // =========================
        // BARCELLONA (4 tour)
        // =========================
        Tour b1 = new Tour("T_BCN_01", "Sagrada Familia & Modernismo", CITY_BARCELLONA,
                LocalDate.of(2026, 2, 20), LocalDate.of(2026, 2, 22), 39.0
        );
        b1.setAttractions(List.of("Sagrada Familia", "Casa Batlló", "Passeig de Gràcia"));
        b1.setTouristGuide(g3);

        Tour b2 = new Tour("T_BCN_02", "Gothic Quarter Walk", CITY_BARCELLONA,
                LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 6), 22.0
        );
        b2.setAttractions(List.of("Barri Gòtic", "Cattedrale", "Plaça Reial"));
        b2.setTouristGuide(g3);

        Tour b3 = new Tour("T_BCN_03", "Park Güell & Views", CITY_BARCELLONA,
                LocalDate.of(2026, 3, 18), LocalDate.of(2026, 3, 19), 27.0
        );
        b3.setAttractions(List.of("Park Güell", "Bunkers del Carmel", "Gràcia"));
        b3.setTouristGuide(g3);

        Tour b4 = new Tour("T_BCN_04", "Tapas & Seafront Evening", CITY_BARCELLONA,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 11), 29.0
        );
        b4.setAttractions(List.of("La Barceloneta", "Port Vell", "Tapas tasting"));
        b4.setTouristGuide(g3);

        // =========================
        // VIENNA (4 tour)
        // =========================
        Tour v1 = new Tour("T_VIE_01", "Imperial Vienna: Schönbrunn", CITY_VIENNA,
                LocalDate.of(2026, 2, 25), LocalDate.of(2026, 2, 27), 45.0
        );
        v1.setAttractions(List.of("Schönbrunn", "Gloriette", "Giardini"));
        v1.setTouristGuide(g4);

        Tour v2 = new Tour("T_VIE_02", "Historic Center & MuseumsQuartier", CITY_VIENNA,
                LocalDate.of(2026, 3, 12), LocalDate.of(2026, 3, 13), 28.0
        );
        v2.setAttractions(List.of("Stephansdom", "Graben", "MuseumsQuartier"));
        v2.setTouristGuide(g4);

        Tour v3 = new Tour("T_VIE_03", "Belvedere & Art Walk", CITY_VIENNA,
                LocalDate.of(2026, 4, 2), LocalDate.of(2026, 4, 3), 32.0
        );
        v3.setAttractions(List.of("Belvedere", "Klimt area", "Palace gardens"));
        v3.setTouristGuide(g4);

        Tour v4 = new Tour("T_VIE_04", "Prater Evening & Danube Canal", CITY_VIENNA,
                LocalDate.of(2026, 4, 18), LocalDate.of(2026, 4, 19), 26.0
        );
        v4.setAttractions(List.of("Prater", "Riesenrad", "Danube Canal"));
        v4.setTouristGuide(g4);

        // =========================
        // PARIGI (4 tour)
        // =========================
        Tour p1 = new Tour("T_PAR_01", "Louvre Essentials", CITY_PARIGI,
                LocalDate.of(2026, 2, 8), LocalDate.of(2026, 2, 9), 34.0
        );
        p1.setAttractions(List.of("Louvre", "Tuileries", "Pont des Arts"));
        p1.setTouristGuide(g2);

        Tour p2 = new Tour("T_PAR_02", "Eiffel & Seine Cruise", CITY_PARIGI,
                LocalDate.of(2026, 3, 22), LocalDate.of(2026, 3, 23), 38.0
        );
        p2.setAttractions(List.of("Torre Eiffel", "Champ de Mars", "Crociera Senna"));
        p2.setTouristGuide(g2);

        Tour p3 = new Tour("T_PAR_03", "Montmartre & Sacré-Cœur", CITY_PARIGI,
                LocalDate.of(2026, 4, 6), LocalDate.of(2026, 4, 7), 25.0
        );
        p3.setAttractions(List.of("Montmartre", "Sacré-Cœur", "Place du Tertre"));
        p3.setTouristGuide(g2);

        Tour p4 = new Tour("T_PAR_04", "Île de la Cité & Latin Quarter", CITY_PARIGI,
                LocalDate.of(2026, 5, 2), LocalDate.of(2026, 5, 3), 27.0
        );
        p4.setAttractions(List.of("Notre-Dame area", "Sainte-Chapelle", "Quartiere Latino"));
        p4.setTouristGuide(g2);

        // =========================
        // LONDRA (4 tour)
        // =========================
        Tour l1 = new Tour("T_LON_01", "Westminster Highlights", CITY_LONDRA,
                LocalDate.of(2026, 2, 14), LocalDate.of(2026, 2, 15), 29.0
        );
        l1.setAttractions(List.of("Westminster", "Big Ben", "Buckingham Palace"));
        l1.setTouristGuide(g1);

        Tour l2 = new Tour("T_LON_02", "British Museum & Bloomsbury", CITY_LONDRA,
                LocalDate.of(2026, 3, 8), LocalDate.of(2026, 3, 9), 24.0
        );
        l2.setAttractions(List.of("British Museum", "Bloomsbury", "Covent Garden"));
        l2.setTouristGuide(g1);

        Tour l3 = new Tour("T_LON_03", "Tower Bridge & City Walk", CITY_LONDRA,
                LocalDate.of(2026, 4, 12), LocalDate.of(2026, 4, 13), 31.0
        );
        l3.setAttractions(List.of("Tower Bridge", "Tower of London", "St Paul’s area"));
        l3.setTouristGuide(g1);

        Tour l4 = new Tour("T_LON_04", "Camden & Street Food", CITY_LONDRA,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 11), 23.0
        );
        l4.setAttractions(List.of("Camden Market", "Regent’s Canal", "Street food"));
        l4.setTouristGuide(g1);

        // =========================
        // ROMA (4 tour)
        // =========================
        Tour r1 = new Tour("T_ROM_01", "Colosseo & Fori Imperiali", "Roma",
                LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 12), 25.0
        );
        r1.setAttractions(List.of("Colosseo", "Foro Romano", "Palatino"));
        r1.setTouristGuide(g1);

        Tour r2 = new Tour("T_ROM_02", "Vaticano in un giorno", "Roma",
                LocalDate.of(2026, 2, 15), LocalDate.of(2026, 2, 16), 30.0
        );
        r2.setAttractions(List.of("Musei Vaticani", "Cappella Sistina", "Basilica San Pietro"));
        r2.setTouristGuide(g1);

        Tour r3 = new Tour("T_ROM_03", "Trastevere Night Walk", "Roma",
                LocalDate.of(2026, 3, 14), LocalDate.of(2026, 3, 15), 19.0
        );
        r3.setAttractions(List.of("Trastevere", "Isola Tiberina", "Piazza Santa Maria"));
        r3.setTouristGuide(g2);

        Tour r4 = new Tour("T_ROM_04", "Appia Antica & Catacombe", "Roma",
                LocalDate.of(2026, 5, 16), LocalDate.of(2026, 5, 17), 33.0
        );
        r4.setAttractions(List.of("Via Appia Antica", "Catacombe", "Parco degli Acquedotti"));
        r4.setTouristGuide(g2);

        // tour test
        Tour tTest = new Tour("T_TEST_1", "Tour Test", "Roma",
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 25), 99.90
        );
        tTest.setAttractions(List.of("Colosseo", "Centro Storico"));
        tTest.setTouristGuide(gTest);

        // --- SEED ---
        SEED.add(tTest);
        SEED.add(b1); SEED.add(b2); SEED.add(b3); SEED.add(b4);
        SEED.add(v1); SEED.add(v2); SEED.add(v3); SEED.add(v4);
        SEED.add(p1); SEED.add(p2); SEED.add(p3); SEED.add(p4);
        SEED.add(l1); SEED.add(l2); SEED.add(l3); SEED.add(l4);
        SEED.add(r1); SEED.add(r2); SEED.add(r3); SEED.add(r4);

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
