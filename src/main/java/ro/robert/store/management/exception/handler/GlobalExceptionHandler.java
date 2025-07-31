package ro.robert.store.management.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ro.robert.store.management.exception.entity.ServiceErrorType;
import ro.robert.store.management.exception.entity.ServiceException;
import ro.robert.store.management.exception.entity.ServiceErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Store Management API.
 * Handles various types of exceptions and converts them to structured error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle custom ServiceException instances.
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceErrorResponse> handleServiceException(ServiceException ex) {
        log.warn("Service exception occurred: {} (Error Code: {})", ex.getMessage(), ex.getErrorCode());
        if (log.isDebugEnabled()) {
            log.debug("Service exception stack trace:", ex);
        }
        return new ResponseEntity<>(ex.getErrorResponse(), ex.getStatusCode());
    }

    /**
     * Handle validation errors from @Valid annotations on request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServiceErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Validation error occurred: Invalid request data");
        if (log.isDebugEnabled()) {
            log.debug("Validation exception details:", ex);
        }
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                fieldErrors.put("general", error.getDefaultMessage());
            }
        });
        
        String errorMessage = fieldErrors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        
        ServiceException serviceException = new ServiceException(ServiceErrorType.VALIDATION_ERROR, errorMessage);
        return new ResponseEntity<>(serviceException.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle constraint validation errors (e.g., from @NotNull, @Size, etc.).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ServiceErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Constraint violation occurred: {}", ex.getMessage());
        if (log.isDebugEnabled()) {
            log.debug("Constraint violation stack trace:", ex);
        }
        
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        ServiceException serviceException = new ServiceException(ServiceErrorType.VALIDATION_ERROR, errorMessage);
        return new ResponseEntity<>(serviceException.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle data integrity violations (e.g., constraint violations).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ServiceErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation occurred: {}", ex.getMessage());
        if (log.isDebugEnabled()) {
            log.debug("Data integrity violation stack trace:", ex);
        }
        
        ServiceException serviceException = new ServiceException(ServiceErrorType.DATABASE_CONSTRAINT_VIOLATION);
        return new ResponseEntity<>(serviceException.getErrorResponse(), serviceException.getStatusCode());
    }

    /**
     * Handle SQL exceptions
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ServiceErrorResponse> handleSQLException(SQLException ex) {
        log.error("SQL exception occurred: {}", ex.getMessage(), ex);
        
        ServiceException serviceException;
        
        String sqlMessage = ex.getMessage().toLowerCase();
        if (sqlMessage.contains("constraint") || sqlMessage.contains("check") || 
            sqlMessage.contains("violates") || sqlMessage.contains("invalid")) {
            serviceException = new ServiceException(ServiceErrorType.DATABASE_CONSTRAINT_VIOLATION);
        } else {
            serviceException = new ServiceException(ServiceErrorType.DATABASE_ERROR);
        }
        
        return new ResponseEntity<>(serviceException.getErrorResponse(), serviceException.getStatusCode());
    }

    /**
     * Handle malformed JSON requests.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ServiceErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        if (log.isDebugEnabled()) {
            log.debug("Malformed JSON stack trace:", ex);
        }
        
        String errorMessage = "Invalid JSON format or missing required fields";
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            errorMessage = ex.getCause().getMessage();
        }
        
        ServiceException serviceException = new ServiceException(ServiceErrorType.INVALID_PRODUCT_DATA, errorMessage);
        return new ResponseEntity<>(serviceException.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle any other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        ServiceException serviceException = new ServiceException(ServiceErrorType.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(serviceException.getErrorResponse(), serviceException.getStatusCode());
    }
}
