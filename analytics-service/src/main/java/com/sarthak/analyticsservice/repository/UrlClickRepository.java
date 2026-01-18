package com.sarthak.analyticsservice.repository;

import com.sarthak.analyticsservice.model.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

    long countByShortCode(String shortCode);

    List<UrlClick> findByShortCodeOrderByClickedAtDesc(String shortCode);

    List<UrlClick> findByShortCodeAndClickedAtBetweenOrderByClickedAtDesc(
            String shortCode, LocalDateTime start, LocalDateTime end);
    

    @Query("SELECT DATE(u.clickedAt) as date, COUNT(u) as count " +
           "FROM UrlClick u WHERE u.shortCode = :shortCode " +
           "GROUP BY DATE(u.clickedAt) ORDER BY DATE(u.clickedAt) DESC")
    List<Object[]> getClickCountByDate(@Param("shortCode") String shortCode);

    @Query("SELECT COUNT(DISTINCT u.ipAddress) FROM UrlClick u WHERE u.shortCode = :shortCode")
    long countUniqueIpsByShortCode(@Param("shortCode") String shortCode);

    @Query("SELECT u.shortCode as shortCode, COUNT(u) as totalClicks FROM UrlClick u GROUP BY u.shortCode")
    List<java.util.Map<String, Object>> findAllClickCounts();

    @Query("SELECT u.shortCode as shortCode, COUNT(DISTINCT u.ipAddress) as uniqueVisitors FROM UrlClick u GROUP BY u.shortCode")
    List<java.util.Map<String, Object>> findAllUniqueVisitors();
}
