package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.exception.PolicyValidationException;
import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.interfaces.InsurancePolicyService;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final CarRepository carRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isValidForDate(Long carId, LocalDate date) {
        if (carId == null || date == null) {
            throw new PolicyValidationException("Car ID and date cannot be null");
        }
        return insurancePolicyRepository.existsActiveOnDate(carId, date);
    }

    @Override
    public InsurancePolicy createPolicyByVin(PolicyCreateRequest request) {
        if (!request.isValidDateOrder()) {
            throw new PolicyValidationException("Start date must be before or equal to end date");
        }
        
        if (!request.isValidDateRange()) {
            throw new PolicyValidationException("Dates must be between 1900-01-01 and 2100-12-31");
        }
        
        Car car = carRepository.findByVin(request.getCarVin())
            .orElseThrow(() -> new CarNotFoundException("Car not found with VIN: " + request.getCarVin()));
        
        InsurancePolicy policy = InsurancePolicy.builder()
            .car(car)
            .provider(request.getProvider())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .logged(false)
            .build();
        
        return insurancePolicyRepository.save(policy);
    }
}
