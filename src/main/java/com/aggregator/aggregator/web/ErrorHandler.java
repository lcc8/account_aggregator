package com.aggregator.aggregator.web;

import com.aggregator.aggregator.core.exception.InvalidProviderException;
import com.aggregator.aggregator.core.exception.ValidateException;
import com.aggregator.aggregator.web.converter.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.concurrent.TimeoutException;

@ControllerAdvice
public class ErrorHandler {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ValidateException.class, ConversionException.class})
    public void handleValidateError(Exception e) {

    }

    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(TimeoutException.class)
    public void handleTimeoutError(Exception e) {

    }

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidProviderException.class)
    public ResponseEntity<String> handleInvalidProviderError(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
