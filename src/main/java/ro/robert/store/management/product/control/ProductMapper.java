package ro.robert.store.management.product.control;

import org.springframework.stereotype.Component;

import ro.robert.store.management.product.entity.ProductEntity;
import ro.robert.store.management.product.entity.event.ProductCreatedEvent;
import ro.robert.store.management.product.entity.event.ProductDeletedEvent;
import ro.robert.store.management.product.entity.event.ProductUpdatedEvent;
import ro.robert.store.management.product.entity.request.ProductCreateRequest;
import ro.robert.store.management.product.entity.response.ProductResponse;

import java.time.LocalDateTime;

@Component
public class ProductMapper {
    
    public ProductEntity toEntity(ProductCreateRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setCategory(request.getCategory());
        entity.setPrice(request.getPrice());
        entity.setQuantity(request.getQuantity());
        return entity;
    }
    
    public ProductResponse toResponse(ProductEntity entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setCategory(entity.getCategory());
        response.setPrice(entity.getPrice());
        response.setQuantity(entity.getQuantity());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
    
    public ProductCreatedEvent toCreatedEvent(ProductEntity entity) {
        ProductCreatedEvent event = new ProductCreatedEvent();
        event.setProductId(entity.getId());
        event.setProductName(entity.getName());
        event.setDescription(entity.getDescription());
        event.setCategory(entity.getCategory());
        event.setPrice(entity.getPrice());
        event.setQuantity(entity.getQuantity());
        event.setCreatedAt(entity.getCreatedAt());
        event.setEventTimestamp(LocalDateTime.now());
        return event;
    }
    
    public ProductUpdatedEvent toUpdatedEvent(ProductEntity entity, String fieldUpdated, String oldValue, String newValue) {
        ProductUpdatedEvent event = new ProductUpdatedEvent();
        event.setProductId(entity.getId());
        event.setProductName(entity.getName());
        event.setFieldUpdated(fieldUpdated);
        event.setOldValue(oldValue);
        event.setNewValue(newValue);
        event.setEventTimestamp(LocalDateTime.now());
        return event;
    }

    public ProductDeletedEvent toDeletedEvent(ProductEntity entity) {
        ProductDeletedEvent event = new ProductDeletedEvent();
        event.setProductId(entity.getId());
        event.setProductName(entity.getName());
        event.setEventTimestamp(LocalDateTime.now());
        return event;
    }
}
