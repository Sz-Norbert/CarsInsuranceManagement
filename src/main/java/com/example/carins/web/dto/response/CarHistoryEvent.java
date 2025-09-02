package com.example.carins.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarHistoryEvent {
    private String eventType;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String description;
    private BigDecimal amount;
    private String provider;
}