package com.example.onetour.model;

import com.example.onetour.enumeration.TicketState;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class Ticket {

    private String ticketID;
    private LocalDate bookingDate;
    private TicketState state;

    private String userEmail;
    private Tour tour;

    public Ticket(String ticketID, LocalDate bookingDate, TicketState state, String userEmail) {
        this.ticketID = ticketID;
        this.bookingDate = bookingDate;
        this.state = state;
        this.userEmail = userEmail;
        this.tour = null;
    }

    public Ticket(String userEmail, Tour tour) {
        this.userEmail = userEmail;
        this.tour = tour;
        this.bookingDate = LocalDate.now();
        this.state = TicketState.PENDING;
        this.ticketID = generateTicketID(userEmail, tour.getTourID());
    }

    public String getTicketID() {
        return ticketID;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;

        if (this.userEmail != null && !this.userEmail.isBlank()
                && this.tour != null && this.tour.getTourID() != null) {
            this.ticketID = generateTicketID(this.userEmail, this.tour.getTourID());
        }
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;

        if (this.tour != null && this.tour.getTourID() != null
                && this.userEmail != null && !this.userEmail.isBlank()) {
            this.ticketID = generateTicketID(this.userEmail, this.tour.getTourID());
        }
    }

    private static String generateTicketID(String userEmail, String tourID) {
        Objects.requireNonNull(userEmail, "userEmail cannot be null");
        Objects.requireNonNull(tourID, "tourID cannot be null");

        String normalizedEmail = userEmail.trim().toLowerCase(Locale.ROOT);
        String uniqueString = tourID + "::" + normalizedEmail;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(uniqueString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
