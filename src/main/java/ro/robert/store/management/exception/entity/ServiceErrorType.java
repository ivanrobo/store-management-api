package ro.robert.store.management.exception.entity;

import org.springframework.http.HttpStatus;

/**
 * Enum containing error types for Store Management Service operations.
 * Each error type defines its custom error code, message template, and HTTP status code.
 */
public enum ServiceErrorType {
    
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found with id: %d", HttpStatus.NOT_FOUND),
    UNSUPPORTED_UPDATE_TYPE("UNSUPPORTED_UPDATE_TYPE", "Unsupported update request type: %s", HttpStatus.BAD_REQUEST),
    
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed: %s", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_DATA("INVALID_PRODUCT_DATA", "Invalid product data: %s", HttpStatus.BAD_REQUEST),
    
    DATABASE_CONSTRAINT_VIOLATION("DATABASE_CONSTRAINT_VIOLATION", "Database constraint violation - please check your input data", HttpStatus.BAD_REQUEST),
    DATABASE_ERROR("DATABASE_ERROR", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String errorCode;
    private final String messageTemplate;
    private final HttpStatus statusCode;
    
    /**
     * Constructor for ServiceErrorType enum.
     *
     * @param errorCode the custom error code
     * @param messageTemplate the message template (can contain placeholders)
     * @param statusCode the HTTP status code
     */
    ServiceErrorType(String errorCode, String messageTemplate, HttpStatus statusCode) {
        this.errorCode = errorCode;
        this.messageTemplate = messageTemplate;
        this.statusCode = statusCode;
    }
    
    /**
     * Gets the custom error code.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the message template.
     *
     * @return the message template
     */
    public String getMessageTemplate() {
        return messageTemplate;
    }
    
    /**
     * Gets the HTTP status code.
     *
     * @return the HTTP status code
     */
    public HttpStatus getStatusCode() {
        return statusCode;
    }
    
    /**
     * Formats the message template with the provided arguments.
     *
     * @param args arguments to format the message template
     * @return the formatted message
     */
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
