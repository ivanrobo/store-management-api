package ro.robert.store.management.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {

    // Topic Configuration
    @Value("${app.kafka.topic.product-events}")
    private String productEventsTopic;
    
    @Value("${app.kafka.topic.partitions}")
    private int topicPartitions;
    
    @Value("${app.kafka.topic.replication-factor}")
    private short replicationFactor;

    /**
     * Ensures product events topic exists, creates it if it doesn't exist
     */
    @Bean
    public NewTopic productEventsTopic() {
        log.info("Kafka is enabled - ensuring topic exists: {} with {} partitions", productEventsTopic, topicPartitions);
        return TopicBuilder.name(productEventsTopic)
                .partitions(topicPartitions)
                .replicas(replicationFactor)
                .build();
    }
}
