package com.example.onetour.exception;

public class TourNotFoundException extends Exception {

    public TourNotFoundException(String message) {
        super(message);
    }

    public TourNotFoundException(Throwable cause) {
        super(cause);
    }

    public TourNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
