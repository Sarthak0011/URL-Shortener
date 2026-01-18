package com.sarthak.redirectservice.service;

import com.sarthak.redirectservice.exception.UrlExpiredException;
import com.sarthak.redirectservice.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Service for handling URL redirects with Redis caching.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedirectService {
    
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final ClickEventProducer clickEventProducer;
    
    @Value("${app.url-service.base-url}")
    private String urlServiceBaseUrl;
    
    @Value("${app.redis.key-prefix:short:}")
    private String redisKeyPrefix;
    
    @Value("${app.redis.default-ttl-hours:24}")
    private int defaultTtlHours;

    public String getLongUrl(String shortCode) {
        log.debug("Looking up long URL for: {}", shortCode);
        
        // Try Redis first
        String redisKey = redisKeyPrefix + shortCode;
        String longUrl = redisTemplate.opsForValue().get(redisKey);
        
        if (longUrl != null) {
            log.debug("Cache HIT for: {}", shortCode);
            return longUrl;
        }
        
        log.debug("Cache MISS for: {}, fetching from URL Service", shortCode);
        
        // Fallback to URL Service
        longUrl = fetchFromUrlService(shortCode);
        
        // Cache the result
        cacheUrl(shortCode, longUrl);
        
        return longUrl;
    }
    

    public void recordClick(String shortCode, String ipAddress, String userAgent) {
        clickEventProducer.publishClickEvent(shortCode, ipAddress, userAgent);
    }

    private String fetchFromUrlService(String shortCode) {
        String url = urlServiceBaseUrl + "/api/urls/" + shortCode + "/redirect";
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            throw new UrlNotFoundException(shortCode);
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UrlNotFoundException(shortCode);
            } else if (e.getStatusCode() == HttpStatus.GONE) {
                throw new UrlExpiredException(shortCode);
            }
            throw new RuntimeException("Error fetching URL from URL Service", e);
        }
    }
    
    /**
     * Cache URL in Redis with TTL.
     */
    private void cacheUrl(String shortCode, String longUrl) {
        try {
            String redisKey = redisKeyPrefix + shortCode;
            redisTemplate.opsForValue().set(redisKey, longUrl, Duration.ofHours(defaultTtlHours));
            log.debug("Cached URL for: {}", shortCode);
        } catch (Exception e) {
            // Log but don't fail - caching is optional
            log.error("Failed to cache URL for {}: {}", shortCode, e.getMessage());
        }
    }
    
    /**
     * Invalidate cache for a short code.
     */
    public void invalidateCache(String shortCode) {
        try {
            String redisKey = redisKeyPrefix + shortCode;
            redisTemplate.delete(redisKey);
            log.debug("Invalidated cache for: {}", shortCode);
        } catch (Exception e) {
            log.error("Failed to invalidate cache for {}: {}", shortCode, e.getMessage());
        }
    }
}
