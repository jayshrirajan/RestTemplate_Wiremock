package com.example.RestTemplate.exception;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import static org.springframework.http.HttpStatus.*;
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Error> handleItemNotFoundException(ItemNotFoundException ex) {
        Error errorResponse = new Error(ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemOutOfStockException.class)
    public ResponseEntity<Error> handleItemOutOfStockException(ItemOutOfStockException ex) {
        Error errorResponse = new Error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Error> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        Error errorResponse = new Error(ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UnableToReturnBalanceException.class)
    public ResponseEntity<Error> handleUnableToReturnBalanceException(UnableToReturnBalanceException ex) {
        Error errorResponse = new Error(ex.getMessage());
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

}
