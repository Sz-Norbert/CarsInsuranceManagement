package com.example.carins.service.interfaces;

import com.example.carins.model.Car;
import com.example.carins.web.dto.response.CarHistoryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarService {
    List<Car> listCars();

    Optional<Car> findById(Long carId);

    Optional<Car> findByVin(String vin);

    boolean isInsuranceValid(Long carId, LocalDate date);

    Optional<CarHistoryResponse> getCarHistory(String vin);

}
