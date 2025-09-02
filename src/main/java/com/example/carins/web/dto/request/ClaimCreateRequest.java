package com.example.carins.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClaimCreateRequest {

    @NotNull(message = "Claim date is required")
    private LocalDate claimDate;

    @NotBlank
    private String description;


    @NotNull
    @Positive
    private BigDecimal amount;


}
