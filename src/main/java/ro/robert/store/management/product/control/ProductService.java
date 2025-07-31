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
import ro.robert.store.management.product.entity.response.ProductPagedResponse;
import ro.robert.store.management.product.entity.response.ProductResponse;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductEventPublisher productEventPublisher;

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating new product with name: {}", request.getName());
        
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity savedEntity = productRepository.save(entity);
        
        log.info("Successfully created product with ID: {} and name: {}", savedEntity.getId(), savedEntity.getName());
        
        productEventPublisher.publishEvent(productMapper.toCreatedEvent(savedEntity));
        
        return productMapper.toResponse(savedEntity);
    }

    @Transactional(readOnly = true)
    public ProductPagedResponse getAllProducts(Pageable pageable) {
        log.info("Retrieving products - Page: {}, Size: {}, Sort: {}", 
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        
        Page<ProductEntity> entityPage = productRepository.findAll(pageable);
        
        log.info("Retrieved {} products out of {} total products", 
                entityPage.getNumberOfElements(), entityPage.getTotalElements());
        
        List<ProductResponse> content = entityPage.getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();
        
        return new ProductPagedResponse(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages(),
                entityPage.isFirst(),
                entityPage.isLast(),
                entityPage.getNumberOfElements(),
                entityPage.isEmpty()
        );
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
        
        String oldValue = getFieldValue(entity, request);
        
        applyUpdate(entity, request);
        
        ProductEntity savedEntity = productRepository.save(entity);
        log.info("Successfully updated product: {} with ID: {}", savedEntity.getName(), id);
        
        String newValue = getFieldValue(savedEntity, request);
        String fieldName = getFieldName(request);
        productEventPublisher.publishEvent(
            productMapper.toUpdatedEvent(savedEntity, fieldName, oldValue, newValue)
        );
        
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
        
        productEventPublisher.publishEvent(
            productMapper.toDeletedEvent(entity)
        );
        
        productRepository.delete(entity);
        log.info("Successfully deleted product with ID: {}", id);
    }
    
    /**
     * Gets the field name being updated based on the request type
     */
    private String getFieldName(ProductUpdateRequest request) {
        if (request instanceof UpdatePriceRequest) {
            return "PRICE";
        } else if (request instanceof UpdateStockRequest) {
            return "QUANTITY";
        }
        return "UNKNOWN";
    }
    
    /**
     * Gets the current value of the field being updated
     */
    private String getFieldValue(ProductEntity entity, ProductUpdateRequest request) {
        if (request instanceof UpdatePriceRequest) {
            return entity.getPrice().toString();
        } else if (request instanceof UpdateStockRequest) {
            return entity.getQuantity().toString();
        }
        return "UNKNOWN";
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
