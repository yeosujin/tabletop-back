package com.example.tabletop.auth.exception;

import jakarta.mail.MessagingException;

public class CustomMessagingException extends MessagingException {
    public CustomMessagingException(String message) {
        super(message);
    }
}