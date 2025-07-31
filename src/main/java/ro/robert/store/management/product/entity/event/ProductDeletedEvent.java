package ro.robert.store.management.product.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeletedEvent implements ProductEvent {
    
    private String eventType = "ProductDeletedEvent";
    private Long productId;
    private String productName;
    private LocalDateTime eventTimestamp;
}
