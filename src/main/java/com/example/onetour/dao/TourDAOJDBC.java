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
                    out.add(mapRow(rs));
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
                return mapRow(rs);
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
                return mapRow(rs);
            }
        }
    }

    private Tour mapRow(ResultSet rs) throws SQLException {
        String tourID = rs.getString("tour_id");
        String nameTour = rs.getString("name_tour");
        String cityName = rs.getString("city_name");
        LocalDate departure = rs.getDate("departure_date").toLocalDate();
        LocalDate ret = rs.getDate("return_date").toLocalDate();
        double price = rs.getDouble("price");

        Tour t = new Tour(tourID, nameTour, cityName, departure, ret, price);

        String guideEmail = rs.getString("guide_email");
        if (guideEmail != null && !guideEmail.isBlank()) {
            t.setTouristGuide(new TouristGuide(null, "", "", guideEmail));
        }

        return t;
    }
}
