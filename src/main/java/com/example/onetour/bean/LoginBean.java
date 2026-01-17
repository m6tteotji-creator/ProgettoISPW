package com.example.onetour.bean;

import com.example.onetour.exception.InvalidFormatException;

import java.io.Serializable;

public class LoginBean implements Serializable {

    private String email;
    private String password;
    private String sessionID;

    public LoginBean() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void checkFields() throws InvalidFormatException {
        if (email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            throw new InvalidFormatException("Please fill all the fields");
        }
    }
}
