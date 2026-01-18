package com.sarthak.urlservice.repository;

import com.sarthak.urlservice.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for URL mapping operations.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    
    /**
     * Find URL mapping by short code.
     */
    Optional<UrlMapping> findByShortCode(String shortCode);
    
    /**
     * Check if a short code already exists.
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * Delete URL mapping by short code.
     */
    void deleteByShortCode(String shortCode);

    /**
     * Find all URL mappings ordered by creation date descending.
     */
    java.util.List<UrlMapping> findAllByOrderByCreatedAtDesc();
}
