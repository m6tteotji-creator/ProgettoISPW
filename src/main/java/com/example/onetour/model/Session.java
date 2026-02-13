package com.example.onetour.model;

import com.example.onetour.bean.TourBean;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private final String sessionID;
    private final UserAccount user;

    // 🔥 SOLO BEAN, NON ENTITY
    private TourBean actualTour;
    private List<TourBean> lastTourList = new ArrayList<>();

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

    public TourBean getActualTour() {
        return actualTour;
    }

    public void setActualTour(TourBean actualTour) {
        this.actualTour = actualTour;
    }

    public List<TourBean> getLastTourList() {
        return lastTourList;
    }

    public void setLastTourList(List<TourBean> lastTourList) {
        this.lastTourList = (lastTourList != null)
                ? lastTourList
                : new ArrayList<>();
    }
}
