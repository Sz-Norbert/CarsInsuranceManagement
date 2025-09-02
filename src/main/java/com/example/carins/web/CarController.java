package com.example.carins.web;

import com.example.carins.api.ApiResponse;
import com.example.carins.facade.CarFacade;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.web.dto.request.ClaimCreateRequest;
import com.example.carins.web.dto.response.CarResponse;
import com.example.carins.web.dto.response.CarHistoryResponse;
import com.example.carins.web.dto.response.ClaimResponse;
import com.example.carins.web.dto.response.InsuranceValidityResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;
    private final CarFacade carFacade;

    public CarController(CarService service, CarFacade carFacade) {
        this.service = service;
        this.carFacade = carFacade;
    }

    @GetMapping("/cars")
    public ApiResponse<List<CarResponse>> getCars() {
        return carFacade.getAllCars();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }



    @PostMapping("/cars/{vin}/claims")
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(
            @PathVariable String vin, 
            @Valid @RequestBody ClaimCreateRequest request) {
        ApiResponse<ClaimResponse> response = carFacade.createClaim(vin,request);
        return ResponseEntity.status(201)
                .header("Location", "/api/cars/" + vin + "/claims/" + response.getData().getId())
                .body(response);
    }

    @GetMapping("/cars/{vin}/history")
    public ResponseEntity<ApiResponse<CarHistoryResponse>> getCarHistory(@PathVariable String vin) {
        ApiResponse<CarHistoryResponse> response = carFacade.getCarHistory(vin);
        return response.isSuccess() 
            ? ResponseEntity.ok(response)
            : ResponseEntity.status(404).body(response);
    }



}
