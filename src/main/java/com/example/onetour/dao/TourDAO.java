package com.example.onetour.dao;

import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Tour;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public abstract class TourDAO {

    public abstract List<Tour> findTours(
            String cityName,
            LocalDate departureDate,
            LocalDate returnDate
    ) throws SQLException, TourNotFoundException;

    public abstract Tour retrieveTourFromId(String tourID)
            throws SQLException, TourNotFoundException;

    public abstract Tour retrieveTour(
            String tourName,
            LocalDate departureDate,
            LocalDate returnDate
    ) throws SQLException, TourNotFoundException;
}
