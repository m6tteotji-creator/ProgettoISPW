package com.example.onetour.model;

import java.time.LocalDate;
import java.util.List;

public class Tour {

    private String tourID;
    private String nameTour;
    private String cityName;

    private LocalDate departureDate;
    private LocalDate returnDate;

    private double price;
    private List<String> attractions;

    private TouristGuide touristGuide;
    private byte[] photo;

    public Tour(String tourID, String nameTour, String cityName,
                LocalDate departureDate, LocalDate returnDate, double price) {
        this.tourID = tourID;
        this.nameTour = nameTour;
        this.cityName = cityName;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.price = price;
    }


    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getNameTour() {
        return nameTour;
    }

    public void setNameTour(String nameTour) {
        this.nameTour = nameTour;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<String> attractions) {
        this.attractions = attractions;
    }

    public TouristGuide getTouristGuide() {
        return touristGuide;
    }

    public void setTouristGuide(TouristGuide touristGuide) {
        this.touristGuide = touristGuide;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}