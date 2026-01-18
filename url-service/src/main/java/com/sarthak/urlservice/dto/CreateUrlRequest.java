package com.sarthak.urlservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUrlRequest {
    
    @NotBlank(message = "Long URL is required")
    @URL(message = "Invalid URL format")
    private String longUrl;

    private Integer expiryDays;
}
