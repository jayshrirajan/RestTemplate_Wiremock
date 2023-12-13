package com.example.RestTemplate.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {

        super(message);
    }
}
