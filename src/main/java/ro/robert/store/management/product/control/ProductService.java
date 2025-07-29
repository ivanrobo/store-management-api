package ro.robert.store.management.product.control;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.robert.store.management.exception.entity.ServiceErrorType;
import ro.robert.store.management.exception.entity.ServiceException;
import ro.robert.store.management.product.boundary.ProductRepository;
import ro.robert.store.management.product.entity.ProductEntity;
import ro.robert.store.management.product.entity.request.ProductCreateRequest;
import ro.robert.store.management.product.entity.request.ProductUpdateRequest;
import ro.robert.store.management.product.entity.request.UpdatePriceRequest;
import ro.robert.store.management.product.entity.request.UpdateStockRequest;
import ro.robert.store.management.product.entity.response.ProductResponse;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity savedEntity = productRepository.save(entity);
        return productMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        Page<ProductEntity> entityPage = productRepository.findAll(pageable);
        return entityPage.map(productMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id));
        return productMapper.toResponse(entity);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id));
        
        applyUpdate(entity, request);
        
        ProductEntity savedEntity = productRepository.save(entity);
        return productMapper.toResponse(savedEntity);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id));
        
        productRepository.delete(entity);
    }
    
    /**
     * Applies the appropriate update to the product entity based on the request type.
     *
     * @param entity the product entity to update
     * @param request the update request
     */
    private void applyUpdate(ProductEntity entity, ProductUpdateRequest request) {
        if (request instanceof UpdatePriceRequest priceRequest) {
            entity.setPrice(priceRequest.getPrice());
        } else if (request instanceof UpdateStockRequest stockRequest) {
            entity.setQuantity(stockRequest.getQuantity());
        } else {
            throw new ServiceException(ServiceErrorType.UNSUPPORTED_UPDATE_TYPE, request.getType());
        }
    }
}
