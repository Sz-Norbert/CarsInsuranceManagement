package com.example.carins.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PolicyCreateRequest {

    @NotBlank(message = "Car VIN is required")
    private String carVin;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public boolean isValidDateOrder() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }

    public boolean isValidDateRange() {
        if (startDate != null && (startDate.isBefore(LocalDate.of(1900, 1, 1)) || 
                                  startDate.isAfter(LocalDate.of(2100, 12, 31)))) {
            return false;
        }
        if (endDate != null && (endDate.isBefore(LocalDate.of(1900, 1, 1)) || 
                                endDate.isAfter(LocalDate.of(2100, 12, 31)))) {
            return false;
        }
        return true;
    }
}
