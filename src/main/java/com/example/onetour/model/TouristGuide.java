package com.example.onetour.model;

import java.util.List;

public class TouristGuide {

    private String guideID;
    private String name;
    private String surname;
    private String email;
    private List<String> spokenLanguages;

    public TouristGuide(String guideID, String name, String surname, String email) {
        this.guideID = guideID;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public TouristGuide(String guideID, String name, String surname, String email,
                        List<String> spokenLanguages) {
        this(guideID, name, surname, email);
        this.spokenLanguages = spokenLanguages;
    }

    public String getGuideID() {
        return guideID;
    }

    public void setGuideID(String guideID) {
        this.guideID = guideID;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<String> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }
}

