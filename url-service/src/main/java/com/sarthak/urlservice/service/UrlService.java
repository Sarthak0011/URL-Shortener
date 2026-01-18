package com.sarthak.urlservice.service;

import com.sarthak.urlservice.dto.CreateUrlRequest;
import com.sarthak.urlservice.dto.UrlResponse;

import java.util.List;


public interface UrlService {
    

    UrlResponse createShortUrl(CreateUrlRequest request);

    UrlResponse getUrl(String shortCode);
    
    List<UrlResponse> getAllUrls();

    String getLongUrl(String shortCode);

    void deleteUrl(String shortCode);
}
