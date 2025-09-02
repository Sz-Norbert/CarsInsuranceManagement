package com.example.carins.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponse {
    private Long id;
    private String vin;
    private String make;
    private String model;
    private int yearOfManufacture;
    private String ownerName;
    private String ownerEmail;
}
