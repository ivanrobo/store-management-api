package ro.robert.store.management.exception.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response structure for API errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceErrorResponse {
    
    private String errorCode;
    
    private String message;
    
    private long timestamp;
}
