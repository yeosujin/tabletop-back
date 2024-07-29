package com.example.tabletop.menu.exception;

public class InvalidMenuDataException extends RuntimeException {
    public InvalidMenuDataException(String message) {
        super(message);
    }
}