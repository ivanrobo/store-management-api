package ro.robert.store.management.product.control;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.robert.store.management.product.boundary.ProductRepository;
import ro.robert.store.management.product.entity.ProductCreateRequest;
import ro.robert.store.management.product.entity.ProductEntity;
import ro.robert.store.management.product.entity.ProductResponse;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse createProduct(ProductCreateRequest request) {
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity savedEntity = productRepository.save(entity);
        return productMapper.toResponse(savedEntity);
    }
}
