package com.example.onetour.dao;

import com.example.onetour.exception.TourNotFoundException;
import com.example.onetour.model.Tour;

import java.time.LocalDate;
import java.util.List;

public interface TourDAO {

    List<Tour> findTours(
            String cityName,
            LocalDate departureDate,
            LocalDate returnDate
    ) throws TourNotFoundException;

    Tour retrieveTourFromId(String tourID)
            throws TourNotFoundException;

    Tour retrieveTour(
            String tourName,
            LocalDate departureDate,
            LocalDate returnDate
    ) throws TourNotFoundException;
}
