package com.example.onetour.bean;

import com.example.onetour.enumeration.TicketState;

import java.io.Serializable;

public class EmailBean implements Serializable {

    private String ticketID;
    private String guideEmail;
    private String userEmail;
    private String tourID;
    private TicketState decision;
    private String sessionID;

    public EmailBean() {}

    public EmailBean(String ticketID,
                     String guideEmail,
                     String userEmail,
                     String tourID,
                     TicketState decision,
                     String sessionID) {
        this.ticketID = ticketID;
        this.guideEmail = guideEmail;
        this.userEmail = userEmail;
        this.tourID = tourID;
        this.decision = decision;
        this.sessionID = sessionID;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getGuideEmail() {
        return guideEmail;
    }

    public void setGuideEmail(String guideEmail) {
        this.guideEmail = guideEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTourID() {
        return tourID;
    }

    public void setTourID(String tourID) {
        this.tourID = tourID;
    }

    public TicketState getDecision() {
        return decision;
    }

    public void setDecision(TicketState decision) {
        this.decision = decision;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
