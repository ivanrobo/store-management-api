package ro.robert.store.management.product.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent implements ProductEvent {
    
    private String eventType = "ProductUpdatedEvent";
    private Long productId;
    private String productName;
    private String fieldUpdated;
    private String oldValue;
    private String newValue;
    private LocalDateTime eventTimestamp;
}
