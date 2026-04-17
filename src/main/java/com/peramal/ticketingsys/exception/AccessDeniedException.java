package com.peramal.ticketingsys.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
        super("Access denied: you do not have permission to perform this action");
    }
}
