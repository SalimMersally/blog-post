package com.taskflow.api;

import com.taskflow.core.user.InvalidRegistrationException;
import com.taskflow.core.user.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidRegistration(InvalidRegistrationException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUsernameAlreadyExists(UsernameAlreadyExistsException exception) {
        return Map.of("message", exception.getMessage());
    }
}
