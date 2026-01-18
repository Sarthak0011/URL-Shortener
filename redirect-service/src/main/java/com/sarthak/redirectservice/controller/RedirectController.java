package com.sarthak.redirectservice.controller;

import com.sarthak.redirectservice.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {
    
    private final RedirectService redirectService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        log.info("Redirect request for: {}", shortCode);
        
        // Get long URL (from cache or URL Service)
        String longUrl = redirectService.getLongUrl(shortCode);
        
        // Extract request info for analytics
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        
        // Record click asynchronously (non-blocking)
        redirectService.recordClick(shortCode, ipAddress, userAgent);
        
        log.info("Redirecting {} to {}", shortCode, longUrl);
        
        // Return 302 redirect
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl)
                .build();
    }
    

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
