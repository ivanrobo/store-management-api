package ro.robert.store.management.product.control;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ro.robert.store.management.exception.entity.ServiceErrorType;
import ro.robert.store.management.exception.entity.ServiceException;
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

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductEntity> entityPage = productRepository.findAll(pageable);
        return entityPage.map(productMapper::toResponse);
    }
    
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id));
        return productMapper.toResponse(entity);
    }
}
