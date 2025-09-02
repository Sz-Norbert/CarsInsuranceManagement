package com.example.carins.web.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimCreationResponse {
    private ClaimResponse claim;
    private String locationUrl;
}