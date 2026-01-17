package com.example.onetour.exception;

public class DuplicateTicketException extends Exception {

    public DuplicateTicketException(String message) {
        super(message);
    }

    public DuplicateTicketException(Throwable cause) {
        super(cause);
    }

    public DuplicateTicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
