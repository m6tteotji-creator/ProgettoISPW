package com.example.onetour.bean;

import com.example.onetour.exception.InvalidFormatException;

import java.io.Serializable;
import java.time.LocalDate;

public class SearchBean implements Serializable {

    private String sessionID;
    private String cityName;
    private LocalDate departureDate;
    private LocalDate returnDate;

    public SearchBean() {
        //Costruttore vuoto intenzionale per il Bean
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void checkFields() throws InvalidFormatException {
        if (cityName == null || cityName.isBlank()) {
            throw new InvalidFormatException("City name is required");
        }
        if (departureDate == null) {
            throw new InvalidFormatException("Departure date is required");
        }
        if (returnDate == null) {
            throw new InvalidFormatException("Return date is required");
        }

        if (departureDate.isBefore(LocalDate.now())) {
            throw new InvalidFormatException("Departure date cannot be in the past");
        }

        if (returnDate.isBefore(departureDate)) {
            throw new InvalidFormatException("Return date cannot be before the departure date");
        }
    }

}
