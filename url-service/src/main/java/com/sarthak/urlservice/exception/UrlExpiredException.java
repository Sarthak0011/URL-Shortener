package com.sarthak.urlservice.exception;

/**
 * Exception thrown when trying to access an expired URL.
 */
public class UrlExpiredException extends RuntimeException {
    
    public UrlExpiredException(String shortCode) {
        super("URL has expired for short code: " + shortCode);
    }
}
