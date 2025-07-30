package ro.robert.store.management.product.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Custom pagination response wrapper that contains only relevant pagination fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPagedResponse {
    
    private List<ProductResponse> content;
    
    private int page;

    private int size;

    private long totalElements;
    
    private int totalPages;
    
    private boolean first;
    
    private boolean last;
    
    private int numberOfElements;
    
    private boolean empty;
}
