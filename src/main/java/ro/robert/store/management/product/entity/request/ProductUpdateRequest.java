package ro.robert.store.management.product.entity.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base interface for product update requests.
 * Uses Jackson annotations for polymorphic serialization/deserialization based on "type" field.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = UpdatePriceRequest.class, name = "UpdatePriceRequest"),
    @JsonSubTypes.Type(value = UpdateStockRequest.class, name = "UpdateStockRequest")
})
public interface ProductUpdateRequest {
    
    /**
     * Gets the type of update request.
     * 
     * @return the type identifier
     */
    String getType();
}
