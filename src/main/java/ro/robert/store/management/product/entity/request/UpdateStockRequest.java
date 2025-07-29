package ro.robert.store.management.product.entity.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for updating product stock quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStockRequest implements ProductUpdateRequest {
    
    private String type = "UpdateStockRequest";
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;
    
    @Override
    public String getType() {
        return type;
    }
}
