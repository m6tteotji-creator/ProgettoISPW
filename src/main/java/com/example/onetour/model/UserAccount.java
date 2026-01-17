package com.example.onetour.model;

import com.example.onetour.enumeration.RoleEnum;

public class UserAccount {

    private String userID;
    private String name;
    private String surname;     // NEW
    private String userEmail;
    private RoleEnum role;

    public UserAccount(String userID, String name, String surname, String userEmail, RoleEnum role) {
        this.userID = userID;
        this.name = name;
        this.surname = surname;
        this.userEmail = userEmail;
        this.role = role;
    }

    public UserAccount(String userID, String name, String userEmail, RoleEnum role) {
        this(userID, name, null, userEmail, role);
    }

    public UserAccount(String userID, String name, String userEmail) {
        this(userID, name, null, userEmail, RoleEnum.USER);
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public RoleEnum getRole() { return role; }
    public void setRole(RoleEnum role) { this.role = role; }

    public String getFullName() {
        String n = name == null ? "" : name.trim();
        String s = surname == null ? "" : surname.trim();
        String full = (n + " " + s).trim();
        return full.isEmpty() ? "User" : full;
    }
}
