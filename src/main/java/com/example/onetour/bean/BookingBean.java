package com.example.onetour.bean;

import com.example.onetour.enumeration.TicketState;
import com.example.onetour.model.Ticket;
import com.example.onetour.model.Tour;
import com.example.onetour.model.TouristGuide;

import java.io.Serializable;
import java.time.LocalDate;

public class BookingBean implements Serializable {

    private String sessionID;

    private String ticketID;
    private LocalDate bookingDate;
    private TicketState state;

    private String guideName;
    private String tourName;

    public BookingBean() {}

    public BookingBean(String sessionID, String ticketID, LocalDate bookingDate, TicketState state,
                       String guideName, String tourName) {
        this.sessionID = sessionID;
        this.ticketID = ticketID;
        this.bookingDate = bookingDate;
        this.state = state;
        this.guideName = guideName;
        this.tourName = tourName;
    }

    public static BookingBean fromModel(Ticket ticket) {
        BookingBean b = new BookingBean();
        b.setTicketID(ticket.getTicketID());
        b.setBookingDate(ticket.getBookingDate());
        b.setState(ticket.getState());

        Tour tour = ticket.getTour();
        if (tour != null) {
            b.setTourName(tour.getNameTour());

            TouristGuide guide = tour.getTouristGuide();
            if (guide != null) {
                b.setGuideName(guide.getName());
            }
        }
        return b;
    }

    public String getSessionID() { return sessionID; }
    public void setSessionID(String sessionID) { this.sessionID = sessionID; }

    public String getTicketID() { return ticketID; }
    public void setTicketID(String ticketID) { this.ticketID = ticketID; }

    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }

    public TicketState getState() { return state; }
    public void setState(TicketState state) { this.state = state; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
}
