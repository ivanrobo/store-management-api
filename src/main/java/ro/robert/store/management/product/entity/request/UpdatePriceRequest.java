package ro.robert.store.management.product.entity.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request class for updating product price.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriceRequest implements ProductUpdateRequest {
    
    private String type = "UpdatePriceRequest";
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Override
    public String getType() {
        return type;
    }
}
