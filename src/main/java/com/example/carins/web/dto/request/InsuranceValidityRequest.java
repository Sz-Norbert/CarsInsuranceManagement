package com.example.carins.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceValidityRequest {
    
    @NotBlank(message = "VIN cannot be blank")
    private String vin;
    
    @NotBlank(message = "Date cannot be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in ISO format YYYY-MM-DD")
    private String date;
    
    public LocalDate getParsedDate() {
        return LocalDate.parse(date);
    }
    
    public boolean isValidDateRange() {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return !parsedDate.isBefore(LocalDate.of(1900, 1, 1)) && 
                   !parsedDate.isAfter(LocalDate.of(2100, 12, 31));
        } catch (Exception e) {
            return false;
        }
    }
}