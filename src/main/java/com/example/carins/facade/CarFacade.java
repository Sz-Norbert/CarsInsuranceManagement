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
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarFacade {

    @Resource
    @Getter
    private CarService carService;

    @Resource
    @Getter
    private CarMapper carMapper;

    @Resource
    @Getter
    private ClaimService claimService;


    @Resource
    @Getter
    private ClaimMapper claimMapper;

    public ApiResponse<List<CarResponse>> getAllCars() {
        List<Car> cars = getCarService().listCars();
        List<CarResponse> carResponses = getCarMapper().toResponseList(cars);
        return ApiResponse.success(carResponses);
    }

    public ApiResponse<ClaimResponse> createClaim(String vin, ClaimCreateRequest request) {
        Claim claim = getClaimService().createClaim(vin, request);
        ClaimResponse response = claimMapper.toResponse(claim);
        return ApiResponse.created(response);
    }

    public ApiResponse<CarHistoryResponse> getCarHistory(String vin) {
        return getCarService().getCarHistory(vin)
                .map(history -> ApiResponse.success(history))
                .orElse(ApiResponse.notFound("Car not found with VIN: " + vin));
    }
}
