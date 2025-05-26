package com.example.projetSpring.exception;
// src/main/java/com/example/projetSpring/exception/AlreadyExistsException.java

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
