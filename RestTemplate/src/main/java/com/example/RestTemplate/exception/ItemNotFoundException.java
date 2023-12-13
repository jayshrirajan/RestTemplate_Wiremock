package com.example.RestTemplate.exception;

public class ItemNotFoundException extends  RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
