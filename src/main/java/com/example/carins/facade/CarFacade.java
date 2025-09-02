package com.example.carins.facade;

import com.example.carins.api.ApiResponse;
import com.example.carins.mappers.CarMapper;
import com.example.carins.model.Car;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.web.dto.response.CarResponse;
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

    public ApiResponse<List<CarResponse>> getAllCars() {
        List<Car> cars = carService.listCars();
        List<CarResponse> carResponses = carMapper.toResponseList(cars);
        return ApiResponse.success(carResponses);
    }
}
