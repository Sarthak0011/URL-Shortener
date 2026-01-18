package com.sarthak.urlservice.service;

import com.sarthak.urlservice.dto.CreateUrlRequest;
import com.sarthak.urlservice.dto.UrlResponse;
import com.sarthak.urlservice.exception.UrlExpiredException;
import com.sarthak.urlservice.exception.UrlNotFoundException;
import com.sarthak.urlservice.model.UrlMapping;
import com.sarthak.urlservice.repository.UrlMappingRepository;
import com.sarthak.urlservice.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    
    private final UrlMappingRepository urlMappingRepository;
    
    @Value("${app.url.default-expiry-days:30}")
    private int defaultExpiryDays;
    
    @Value("${app.url.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Override
    @Transactional
    public UrlResponse createShortUrl(CreateUrlRequest request) {
        log.info("Creating short URL for: {}", request.getLongUrl());
        
        // Calculate expiry
        int expiryDays = request.getExpiryDays() != null ? request.getExpiryDays() : defaultExpiryDays;
        LocalDateTime expiryAt = LocalDateTime.now().plusDays(expiryDays);
        
        String shortCode = generateUniqueShortCode();
        
        // Create and save URL mapping
        UrlMapping urlMapping = UrlMapping.builder()
                .longUrl(request.getLongUrl())
                .createdAt(LocalDateTime.now())
                .expiryAt(expiryAt)
                .shortCode(shortCode)
                .build();
        
        urlMapping = urlMappingRepository.save(urlMapping);
        
        log.info("Created short URL: {} -> {}", shortCode, request.getLongUrl());
        
        return mapToResponse(urlMapping);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        int retries = 0;
        do {
            shortCode = ShortCodeGenerator.generate();
            retries++;
            if (retries > 10) {
                throw new RuntimeException("Failed to generate unique short code after 10 attempts");
            }
        } while (urlMappingRepository.existsByShortCode(shortCode));
        return shortCode;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UrlResponse getUrl(String shortCode) {
        log.debug("Getting URL for short code: {}", shortCode);
        
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        
        return mapToResponse(urlMapping);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UrlResponse> getAllUrls() {
        log.debug("Getting all URLs");
        
        return urlMappingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public String getLongUrl(String shortCode) {
        log.debug("Getting long URL for redirect: {}", shortCode);
        
        UrlMapping urlMapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        
        // Check expiry
        if (urlMapping.isExpired()) {
            log.warn("URL expired for short code: {}", shortCode);
            throw new UrlExpiredException(shortCode);
        }
        
        return urlMapping.getLongUrl();
    }
    
    @Override
    @Transactional
    public void deleteUrl(String shortCode) {
        log.info("Deleting URL for short code: {}", shortCode);
        
        if (!urlMappingRepository.existsByShortCode(shortCode)) {
            throw new UrlNotFoundException(shortCode);
        }
        
        urlMappingRepository.deleteByShortCode(shortCode);
        log.info("Deleted URL for short code: {}", shortCode);
    }
    
    private UrlResponse mapToResponse(UrlMapping urlMapping) {
        return UrlResponse.builder()
                .shortCode(urlMapping.getShortCode())
                .shortUrl(baseUrl + "/" + urlMapping.getShortCode())
                .longUrl(urlMapping.getLongUrl())
                .createdAt(urlMapping.getCreatedAt())
                .expiryAt(urlMapping.getExpiryAt())
                .build();
    }
}
