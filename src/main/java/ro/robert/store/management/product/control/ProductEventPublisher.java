package ro.robert.store.management.product.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ro.robert.store.management.product.entity.event.ProductEvent;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final boolean kafkaEnabled;
    private final String productEventsTopic;

    public ProductEventPublisher(
            @Value("${app.kafka.enabled:false}") boolean kafkaEnabled,
            @Value("${app.kafka.topic.product-events:product-events}") String productEventsTopic,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaEnabled = kafkaEnabled;
        this.productEventsTopic = productEventsTopic;
        this.kafkaTemplate = kafkaTemplate;
        
        log.info("ProductEventPublisher initialized - Kafka enabled: {}", kafkaEnabled);
    }

    /**
     * Publishes any product event to Kafka if enabled
     * 
     * @param event the product event to publish
     */
    public void publishEvent(ProductEvent event) {
        if (!kafkaEnabled) {
            log.info("Kafka is disabled - skipping event publishing for {} with product ID: {}", 
                    event.getEventType(), event.getProductId());
            return;
        }

        try {
            String key = "product-" + event.getProductId(); // Use product ID as message key for partitioning
            
            log.info("Publishing {} event for product ID: {} to topic: {}", 
                    event.getEventType(), event.getProductId(), productEventsTopic);
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(productEventsTopic, key, event);
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.warn("Failed to publish {} event for product ID: {} - Kafka may not be available: {}", 
                            event.getEventType(), event.getProductId(), throwable.getMessage());
                } else {
                    log.info("Successfully published {} event for product ID: {} to partition: {}, offset: {}", 
                            event.getEventType(),
                            event.getProductId(), 
                            result.getRecordMetadata().partition(), 
                            result.getRecordMetadata().offset());
                }
            });
            
        } catch (Exception e) {
            log.warn("Could not publish {} event for product ID: {} - Kafka may not be available: {}", 
                    event.getEventType(), event.getProductId(), e.getMessage());
        }
    }
}
