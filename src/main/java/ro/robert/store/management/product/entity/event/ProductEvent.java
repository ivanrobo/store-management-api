package ro.robert.store.management.product.entity.event;

import java.time.LocalDateTime;

/**
 * Base interface for all product-related events
 */
public interface ProductEvent {
    
    /**
     * Gets the type of event (e.g., "ProductCreatedEvent", "ProductUpdatedEvent", "ProductDeletedEvent")
     * 
     * @return the event type
     */
    String getEventType();
    
    /**
     * Gets the ID of the product this event relates to
     * 
     * @return the product ID
     */
    Long getProductId();
    
    /**
     * Gets the timestamp when this event occurred
     * 
     * @return the event timestamp
     */
    LocalDateTime getEventTimestamp();
}
