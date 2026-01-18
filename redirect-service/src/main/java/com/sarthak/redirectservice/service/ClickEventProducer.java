package com.sarthak.redirectservice.service;

import com.sarthak.common.constants.KafkaTopics;
import com.sarthak.common.event.UrlClickEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class ClickEventProducer {
    
    private final KafkaTemplate<String, UrlClickEvent> kafkaTemplate;
    

    @Async
    public void publishClickEvent(String shortCode, String ipAddress, String userAgent) {
        try {
            UrlClickEvent event = UrlClickEvent.builder()
                    .shortCode(shortCode)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .clickedAt(LocalDateTime.now())
                    .build();
            
            kafkaTemplate.send(KafkaTopics.URL_CLICKED, shortCode, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish click event for {}: {}", shortCode, ex.getMessage());
                        } else {
                            log.debug("Published click event for {}", shortCode);
                        }
                    });
        } catch (Exception e) {
            // Don't let analytics failures affect redirects
            log.error("Error creating click event for {}: {}", shortCode, e.getMessage());
        }
    }
}
