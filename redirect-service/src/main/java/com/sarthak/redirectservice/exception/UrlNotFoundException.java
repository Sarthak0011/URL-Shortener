package com.sarthak.redirectservice.exception;

/**
 * Exception thrown when a URL mapping is not found.
 */
public class UrlNotFoundException extends RuntimeException {
    
    public UrlNotFoundException(String shortCode) {
        super("URL not found for short code: " + shortCode);
    }
}
