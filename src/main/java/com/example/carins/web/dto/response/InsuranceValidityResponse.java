package com.example.carins.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsuranceValidityResponse {
    private Long carId;
    private String date;
    private boolean valid;
}