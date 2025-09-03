package com.example.carins.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DateFormatInvalidException extends IllegalArgumentException{
    public DateFormatInvalidException(LocalDate date)
    {
        super("Date:" + date.toString() + " has invalid format");
    }
}
