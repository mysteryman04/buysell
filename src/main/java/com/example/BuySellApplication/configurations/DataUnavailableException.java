package com.example.BuySellApplication.configurations;

public class DataUnavailableException extends RuntimeException {
    public DataUnavailableException(String message) {
        super(message);
    }
}
