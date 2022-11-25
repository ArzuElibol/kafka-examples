package com.egm.kafkaexample.infra;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.boostrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer-group-id}")
    private String consumerGroupId;

    @Value("${kafka.auto-offset-reset}")
    private String offsetReset;

    private static final String ERROR = ".error";
    private static final String RETRY = ".retry";
    private static final String DEAD_LETTER = ".dl";


    public ConsumerFactory<String, String> kafkaConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(SeekToCurrentErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setMessageConverter(new StringJsonMessageConverter());
        factory.setConsumerFactory(kafkaConsumerFactory());

        factory.setErrorHandler(kafkaErrorHandler);

        return factory;
    }

    @Bean
    public SeekToCurrentErrorHandler kafkaErrorHandler(DeadLetterPublishingRecoverer kafkaRecoverer) {
        return new SeekToCurrentErrorHandler(kafkaRecoverer, new FixedBackOff(0, 0));
    }

    @Bean
    public DeadLetterPublishingRecoverer kafkaRecoverer(KafkaTemplate<String, String> myKafkaRetryTemplate) {
        return new DeadLetterPublishingRecoverer(myKafkaRetryTemplate, (cr, ex) -> {
            log.error("error: {}", ex);

            // ssmdb.yazilim.user-created.0.error
            var topicName = cr.topic();
            if (topicName.contains(RETRY)) {
                topicName = topicName.replace(RETRY, ERROR);
            } else {
                topicName = topicName + ERROR;
            }

            if (ex.getCause() instanceof ArithmeticException || ex.getCause() instanceof MyException) {
                // ssmdb.yazilim.user-created.0.dl
                topicName = cr.topic() + DEAD_LETTER;
            }

            return new TopicPartition(topicName, -1);
        });
    }


}
