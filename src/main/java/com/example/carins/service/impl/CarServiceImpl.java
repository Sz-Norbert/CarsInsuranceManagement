package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.model.Car;
import com.example.carins.repo.CarRepository;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.service.interfaces.InsurancePolicyService;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    @Resource
    @Getter
    private CarRepository carRepository;



    @Resource
    @Getter
    private InsurancePolicyService insurancePolicyService;


    @Override
    public List<Car> listCars() {
        return carRepository.findAll();
    }

    @Override
    public Car findById(Long carId) {
        return null;
    }

    @Override
    public Car findByVin(String vin) {
        return carRepository.findByVin(vin)
                .orElseThrow(() -> new CarNotFoundException("Car not found with VIN: " + vin));
    }



    @Override
    public boolean isInsuranceValid(Long carId, LocalDate date) {
        findById(carId);
        return insurancePolicyService.isValidForDate(carId, date);
    }


}
