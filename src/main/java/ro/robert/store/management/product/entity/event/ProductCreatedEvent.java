package ro.robert.store.management.product.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent implements ProductEvent {
    
    private String eventType = "ProductCreatedEvent";
    private Long productId;
    private String productName;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime eventTimestamp;
}
