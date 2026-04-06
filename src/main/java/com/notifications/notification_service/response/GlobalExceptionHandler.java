package com.notifications.notification_service.response;

import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFoundException(UserNotFoundException ex) {
       ApiResponse response = new ApiResponse<>(
        false,
        ex.getMessage(),
        null,
        "USER_NOT_FOUND"
       );
       return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
     @ExceptionHandler(RateLimitExceedException.class)
    public ResponseEntity<ApiResponse> handleRateLimitExceedException(RateLimitExceedException ex) {
       ApiResponse response = new ApiResponse<>(
        false,
        ex.getMessage(),
        null,
        "RATE_LIMIT_EXCEEDED"
       );
       return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
        ApiResponse response = new ApiResponse<>(
            false,
            "Something went wrong: ",
            null,
            "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    
}
}
