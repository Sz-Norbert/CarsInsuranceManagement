package com.example.carins.service.interfaces;

import com.example.carins.model.Car;

import java.time.LocalDate;
import java.util.List;

public interface CarService {
    List<Car> listCars();

    Car findById(Long carId);

    Car findByVin(String vin);

    boolean isInsuranceValid(Long carId, LocalDate date);

    com.example.carins.repo.CarRepository getCarRepository();
}
