package com.example.carins.facade;

import com.example.carins.api.ApiResponse;
import com.example.carins.mappers.CarMapper;
import com.example.carins.mappers.ClaimMapper;
import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.service.interfaces.ClaimService;
import com.example.carins.web.dto.request.ClaimCreateRequest;
import com.example.carins.web.dto.response.CarResponse;
import com.example.carins.web.dto.response.CarHistoryResponse;
import com.example.carins.web.dto.response.ClaimResponse;
import com.example.carins.web.dto.response.ClaimCreationResponse;
import com.example.carins.web.dto.response.InsuranceValidityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CarFacade {

    private final CarService carService;
    private final CarMapper carMapper;
    private final ClaimService claimService;
    private final ClaimMapper claimMapper;

    public ApiResponse<List<CarResponse>> getAllCars() {
        List<Car> cars = carService.listCars();
        List<CarResponse> carResponses = carMapper.toResponseList(cars);
        return ApiResponse.success(carResponses);
    }

    public ApiResponse<ClaimResponse> createClaim(String vin, ClaimCreateRequest request) {
        Claim claim = claimService.createClaim(vin, request);
        ClaimResponse response = claimMapper.toResponse(claim);
        return ApiResponse.created(response);
    }

    public ApiResponse<CarHistoryResponse> getCarHistory(String vin) {
        return carService.getCarHistory(vin)
                .map(history -> ApiResponse.success(history))
                .orElse(ApiResponse.notFound("Car not found with VIN: " + vin));
    }

    public ApiResponse<InsuranceValidityResponse> checkInsuranceValidity(String vin, String dateString) {
        return carService.findByVin(vin)
            .map(car -> {
                LocalDate parsedDate = LocalDate.parse(dateString);
                boolean valid = carService.isInsuranceValid(car.getId(), parsedDate);
                return ApiResponse.success(new InsuranceValidityResponse(vin, parsedDate.toString(), valid));
            })
            .orElse(ApiResponse.notFound("Car not found with VIN: " + vin));
    }

    public ApiResponse<ClaimCreationResponse> createClaimWithLocation(String vin, ClaimCreateRequest request) {
        ApiResponse<ClaimResponse> claimResponse = createClaim(vin, request);
        if (claimResponse.isSuccess()) {
            String locationUrl = "/api/cars/" + vin + "/claims/" + claimResponse.getData().getId();
            ClaimCreationResponse creationResponse = new ClaimCreationResponse(claimResponse.getData(), locationUrl);
            return ApiResponse.created(creationResponse);
        } else {
            return ApiResponse.<ClaimCreationResponse>builder()
                .statusCode(claimResponse.getStatusCode())
                .success(claimResponse.isSuccess())
                .message(claimResponse.getMessage())
                .data(null)
                .build();
        }
    }

    public ApiResponse<CarHistoryResponse> getCarHistoryWithStatus(String vin) {
        return getCarHistory(vin);
    }

    public ResponseEntity<ApiResponse<ClaimCreationResponse>> createClaimWithLocationAndHeaders(String vin, ClaimCreateRequest request) {
        ApiResponse<ClaimCreationResponse> response = createClaimWithLocation(vin, request);
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(response.getStatusCode());
        if (response.getData() != null && response.getData().getLocationUrl() != null) {
            builder.header("Location", response.getData().getLocationUrl());
        }
        return builder.body(response);
    }
}
