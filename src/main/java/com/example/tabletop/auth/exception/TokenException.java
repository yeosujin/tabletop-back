package com.example.tabletop.auth.exception;

public class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}