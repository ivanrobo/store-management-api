package ro.robert.store.management.product.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Default implementation for unsupported update request types.
 * Used when Jackson encounters an unknown type in the "type" field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnsupportedUpdateRequest implements ProductUpdateRequest {
    
    private String type;
    
    @Override
    public String getType() {
        return type;
    }
}
