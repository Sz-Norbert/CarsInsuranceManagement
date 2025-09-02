package com.example.carins.web;

import com.example.carins.api.ApiResponse;
import com.example.carins.facade.CarFacade;
import com.example.carins.web.dto.request.ClaimCreateRequest;
import com.example.carins.web.dto.request.InsuranceValidityRequest;
import com.example.carins.web.dto.response.CarResponse;
import com.example.carins.web.dto.response.CarHistoryResponse;
import com.example.carins.web.dto.response.ClaimCreationResponse;
import com.example.carins.web.dto.response.InsuranceValidityResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CarController {

    private final CarFacade carFacade;


    @GetMapping("/cars")
    public ApiResponse<List<CarResponse>> getCars() {
        return carFacade.getAllCars();
    }

    @GetMapping("/cars/{vin}/insurance-valid")
    public ResponseEntity<ApiResponse<InsuranceValidityResponse>> isInsuranceValid(
            @PathVariable String vin, 
            @RequestParam @Valid String date) {
        ApiResponse<InsuranceValidityResponse> response = carFacade.checkInsuranceValidity(vin, date);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    @PostMapping("/cars/{vin}/claims")
    public ResponseEntity<ApiResponse<ClaimCreationResponse>> createClaim(
            @PathVariable String vin, 
            @Valid @RequestBody ClaimCreateRequest request) {
        return carFacade.createClaimWithLocationAndHeaders(vin, request);
    }

    @GetMapping("/cars/{vin}/history")
    public ResponseEntity<ApiResponse<CarHistoryResponse>> getCarHistory(@PathVariable String vin) {
        ApiResponse<CarHistoryResponse> response = carFacade.getCarHistoryWithStatus(vin);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



}
