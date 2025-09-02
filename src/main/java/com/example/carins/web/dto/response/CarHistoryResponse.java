package com.example.carins.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarHistoryResponse {
    private String vin;
    private String make;
    private String model;
    private int yearOfManufacture;
    private List<CarHistoryEvent> history;
}