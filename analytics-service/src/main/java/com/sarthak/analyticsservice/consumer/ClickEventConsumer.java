package com.sarthak.analyticsservice.consumer;

import com.sarthak.analyticsservice.service.AnalyticsService;
import com.sarthak.common.constants.KafkaTopics;
import com.sarthak.common.event.UrlClickEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickEventConsumer {
    
    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = KafkaTopics.URL_CLICKED,
            groupId = KafkaTopics.ANALYTICS_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeClickEvent(UrlClickEvent event) {
        try {
            log.info("Received click event for: {}", event.getShortCode());
            analyticsService.saveClickEvent(event);
            log.debug("Successfully processed click event for: {}", event.getShortCode());
        } catch (Exception e) {
            log.error("Error processing click event for {}: {}", event.getShortCode(), e.getMessage(), e);
            // In production, consider sending to DLQ
            throw e;
        }
    }
}
