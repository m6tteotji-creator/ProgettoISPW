package com.example.onetour.dao;

import com.example.onetour.dao.db.DBConnection;
import com.example.onetour.dao.db.Queries;
import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourDAOJDBC extends TourDAO {

    @Override
    public List<Tour> findTours(String cityName,
                                LocalDate departureDate,
                                LocalDate returnDate)
            throws SQLException, TourNotFoundException {

        List<Tour> out = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(Queries.SELECT_TOURS_BY_CITY_AND_DATES)) {

            ps.setString(1, cityName);
            ps.setDate(2, Date.valueOf(departureDate));
            ps.setDate(3, Date.valueOf(returnDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(conn, rs)); // <-- passa conn
                }
            }
        }

        if (out.isEmpty()) {
            throw new TourNotFoundException("No tours found for given criteria");
        }

        return out;
    }

    @Override
    public Tour retrieveTourFromId(String tourID)
            throws SQLException, TourNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(Queries.SELECT_TOUR_BY_ID)) {

            ps.setString(1, tourID);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new TourNotFoundException(
                            "Tour not found with id: " + tourID
                    );
                }
                return mapRow(conn, rs); // <-- passa conn
            }
        }
    }

    @Override
    public Tour retrieveTour(String tourName,
                             LocalDate departureDate,
                             LocalDate returnDate)
            throws SQLException, TourNotFoundException {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(Queries.SELECT_TOUR_BY_NAME_AND_DATES)) {

            ps.setString(1, tourName);
            ps.setDate(2, Date.valueOf(departureDate));
            ps.setDate(3, Date.valueOf(returnDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new TourNotFoundException(
                            "Tour not found: " + tourName
                    );
                }
                return mapRow(conn, rs); // <-- passa conn
            }
        }
    }

    // -------------------------
    // MAPPING
    // -------------------------
    private Tour mapRow(Connection conn, ResultSet rs) throws SQLException {
        String tourID = rs.getString("tour_id");
        String nameTour = rs.getString("name_tour");
        String cityName = rs.getString("city_name");
        LocalDate departure = rs.getDate("departure_date").toLocalDate();
        LocalDate ret = rs.getDate("return_date").toLocalDate();
        double price = rs.getDouble("price");

        Tour t = new Tour(tourID, nameTour, cityName, departure, ret, price);

        String guideEmail = rs.getString("guide_email");
        if (guideEmail != null && !guideEmail.isBlank()) {
            TouristGuide guide = retrieveGuideByEmail(conn, guideEmail);

            // fallback: almeno email (ma con questa query di solito lo trovi sempre)
            if (guide == null) {
                guide = new TouristGuide(null, "", "", guideEmail);
            }
            t.setTouristGuide(guide);
        }

        return t;
    }

    private TouristGuide retrieveGuideByEmail(Connection conn, String guideEmail) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(Queries.FIND_GUIDE_BY_EMAIL)) {
            ps.setString(1, guideEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String guideId = rs.getString("guide_id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String email = rs.getString("guide_email");

                return new TouristGuide(
                        guideId,
                        name == null ? "" : name,
                        surname == null ? "" : surname,
                        email == null ? guideEmail : email
                );
            }
        }
    }
}
