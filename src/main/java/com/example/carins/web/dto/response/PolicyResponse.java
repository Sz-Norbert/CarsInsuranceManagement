package com.example.carins.web.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PolicyResponse {
    private Long id;
    private String carVin;
    private String provider;
    private LocalDate startDate;
    private LocalDate endDate;
}
