package ro.robert.store.management.exception.entity;

import org.springframework.http.HttpStatus;

/**
 * Custom runtime exception for Store Management Service operations.
 * Uses ServiceErrorType enum to define error characteristics.
 */
public class ServiceException extends RuntimeException {
    
    private final ServiceErrorType errorType;
    private final Throwable rootCause;
    
    /**
     * Constructor with error type and optional root cause.
     *
     * @param errorType the error type containing error code, message, and status code
     * @param cause the underlying exception that caused this exception (optional)
     * @param messageArgs arguments to format the error message template
     */
    public ServiceException(ServiceErrorType errorType, Throwable cause, Object... messageArgs) {
        super(errorType.formatMessage(messageArgs), cause);
        this.errorType = errorType;
        this.rootCause = cause;
    }
    
    /**
     * Constructor with error type only (no root cause).
     *
     * @param errorType the error type containing error code, message, and status code
     * @param messageArgs arguments to format the error message template
     */
    public ServiceException(ServiceErrorType errorType, Object... messageArgs) {
        this(errorType, null, messageArgs);
    }
    
    /**
     * Gets the error type.
     *
     * @return the error type
     */
    public ServiceErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets the custom error code.
     *
     * @return the error code from the error type
     */
    public String getErrorCode() {
        return errorType.getErrorCode();
    }
    
    /**
     * Gets the HTTP status code.
     *
     * @return the HTTP status code from the error type
     */
    public HttpStatus getStatusCode() {
        return errorType.getStatusCode();
    }
    
    /**
     * Gets the root cause exception.
     *
     * @return the root cause exception, or null if not available
     */
    public Throwable getRootCause() {
        return rootCause;
    }
    
    /**
     * Creates a ServiceErrorResponse from this exception.
     *
     * @return the error response
     */
    public ServiceErrorResponse getErrorResponse() {
        return new ServiceErrorResponse(
            getErrorCode(),
            getMessage(),
            System.currentTimeMillis()
        );
    }
}
