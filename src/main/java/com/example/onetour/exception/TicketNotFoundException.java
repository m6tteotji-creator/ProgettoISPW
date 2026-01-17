package com.example.onetour.exception;

public class TicketNotFoundException extends Exception {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(Throwable cause) {
        super(cause);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
