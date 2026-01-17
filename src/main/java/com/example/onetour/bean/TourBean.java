package com.example.onetour.bean;

import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourBean implements Serializable {

    private String sessionID;

    private String tourID;
    private String tourName;
    private String cityName;

    private LocalDate departureDate;
    private LocalDate returnDate;

    private double price;
    private List<String> attractions = new ArrayList<>();

    private String guideName;
    private byte[] photo;

    public TourBean() {}

    public static TourBean fromModel(Tour tour) {
        TourBean b = new TourBean();
        b.setTourID(tour.getTourID());
        b.setTourName(tour.getNameTour());
        b.setCityName(tour.getCityName());
        b.setDepartureDate(tour.getDepartureDate());
        b.setReturnDate(tour.getReturnDate());
        b.setPrice(tour.getPrice());
        b.setAttractions(tour.getAttractions());
        b.setPhoto(tour.getPhoto());

        TouristGuide guide = tour.getTouristGuide();
        if (guide != null) {
            b.setGuideName(guide.getName());
        }
        return b;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
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

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}
