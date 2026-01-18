package com.sarthak.urlservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "url_mapping", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "short_code", unique = true, nullable = false, length = 10)
    private String shortCode;
    
    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expiry_at", nullable = false)
    private LocalDateTime expiryAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryAt);
    }
}
