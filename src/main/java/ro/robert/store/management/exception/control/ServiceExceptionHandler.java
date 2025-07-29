package ro.robert.store.management.exception.control;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ro.robert.store.management.exception.entity.ServiceException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for Service exceptions.
 * Handles ServiceException and returns appropriate HTTP status codes.
 */
@ControllerAdvice
public class ServiceExceptionHandler {

    /**
     * Handles ServiceException and returns the appropriate HTTP status code
     * defined in the ServiceErrorType enum.
     *
     * @param ex the ServiceException
     * @return ResponseEntity with error details and proper HTTP status
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, Object>> handleServiceException(ServiceException ex) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", ex.getStatusCode().value());
        errorResponse.put("error", ex.getStatusCode().getReasonPhrase());
        errorResponse.put("errorCode", ex.getErrorCode());
        errorResponse.put("message", ex.getMessage());
        
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }
}
