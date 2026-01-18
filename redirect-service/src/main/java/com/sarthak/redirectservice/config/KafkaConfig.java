package com.sarthak.redirectservice.config;

import com.sarthak.common.constants.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic urlClickedTopic() {
        return TopicBuilder.name(KafkaTopics.URL_CLICKED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
