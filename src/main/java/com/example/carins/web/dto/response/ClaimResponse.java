package com.example.carins.web.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    private Long id;
    private String carVin;
    private LocalDate claimDate;
    private String description;
    private BigDecimal amount;
    private String provider;
    private LocalDateTime createdAt;
}
