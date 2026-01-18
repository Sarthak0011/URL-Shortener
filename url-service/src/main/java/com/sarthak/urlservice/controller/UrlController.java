package com.sarthak.urlservice.controller;

import com.sarthak.urlservice.dto.CreateUrlRequest;
import com.sarthak.urlservice.dto.UrlResponse;
import com.sarthak.urlservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {
    
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        log.info("Received request to create short URL for: {}", request.getLongUrl());
        UrlResponse response = urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrl(@PathVariable String shortCode) {
        log.info("Received request to get URL for: {}", shortCode);
        UrlResponse response = urlService.getUrl(shortCode);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<java.util.List<UrlResponse>> getAllUrls() {
        log.info("Received request to get all URLs");
        return ResponseEntity.ok(urlService.getAllUrls());
    }
    

    @GetMapping("/{shortCode}/redirect")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortCode) {
        log.debug("Received redirect request for: {}", shortCode);
        String longUrl = urlService.getLongUrl(shortCode);
        return ResponseEntity.ok(longUrl);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        log.info("Received request to delete URL for: {}", shortCode);
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}
