package ro.robert.store.management.product.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating new product with name: {}", request.getName());
        
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity savedEntity = productRepository.save(entity);
        
        log.info("Successfully created product with ID: {} and name: {}", savedEntity.getId(), savedEntity.getName());
        return productMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Retrieving products - Page: {}, Size: {}, Sort: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        Page<ProductEntity> entityPage = productRepository.findAll(pageable);
        
        log.info("Retrieved {} products out of {} total products", 
                entityPage.getNumberOfElements(), entityPage.getTotalElements());
        
        return entityPage.map(productMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Retrieving product with ID: {}", id);
        
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id);
                });
        
        log.info("Successfully retrieved product: {} with ID: {}", entity.getName(), id);
        return productMapper.toResponse(entity);
    }
    
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {} using request type: {}", id, request.getType());
        
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update - Product not found with ID: {}", id);
                    return new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id);
                });
        
        applyUpdate(entity, request);
        
        ProductEntity savedEntity = productRepository.save(entity);
        log.info("Successfully updated product: {} with ID: {}", savedEntity.getName(), id);
        
        return productMapper.toResponse(savedEntity);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete - Product not found with ID: {}", id);
                    return new ServiceException(ServiceErrorType.PRODUCT_NOT_FOUND, id);
                });
        
        productRepository.delete(entity);
        log.info("Successfully deleted product with ID: {}", id);
    }
    
    /**
     * Applies the appropriate update to the product entity based on the request type.
     *
     * @param entity the product entity to update
     * @param request the update request
     */
    private void applyUpdate(ProductEntity entity, ProductUpdateRequest request) {
        if (request instanceof UpdatePriceRequest priceRequest) {
            log.info("Updating price for product ID: {} from {} to {}", 
                    entity.getId(), entity.getPrice(), priceRequest.getPrice());
            entity.setPrice(priceRequest.getPrice());
        } else if (request instanceof UpdateStockRequest stockRequest) {
            log.info("Updating stock for product ID: {} from {} to {}", 
                    entity.getId(), entity.getQuantity(), stockRequest.getQuantity());
            entity.setQuantity(stockRequest.getQuantity());
        } else {
            log.error("Unsupported update type: {} for product ID: {}", request.getType(), entity.getId());
            throw new ServiceException(ServiceErrorType.UNSUPPORTED_UPDATE_TYPE, request.getType());
        }
    }
}
