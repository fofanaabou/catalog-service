package com.sinignaci.catalogservice.handler;

public class BookAlreadyExistsException extends RuntimeException {
    public BookAlreadyExistsException(String message) {
        super("A book with ISBN " + message + " already exists.");
    }
}
