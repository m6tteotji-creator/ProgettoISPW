package com.example.onetour.model;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final String sessionID;
    private final UserAccount user;

    private Tour actualTour;
    private List<Tour> lastTourList = new ArrayList<>();

    public Session(String sessionID, UserAccount user) {
        this.sessionID = sessionID;
        this.user = user;
    }



    public String getSessionID() {
        return sessionID;
    }

    public UserAccount getUser() {
        return user;
    }


    public Tour getActualTour() {
        return actualTour;
    }

    public void setActualTour(Tour actualTour) {
        this.actualTour = actualTour;
    }

    public List<Tour> getLastTourList() {
        return lastTourList;
    }

    public void setLastTourList(List<Tour> lastTourList) {
        this.lastTourList = (lastTourList != null)
                ? lastTourList
                : new ArrayList<>();
    }
}
