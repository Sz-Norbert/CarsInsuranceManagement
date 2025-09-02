package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.exception.PolicyValidationException;
import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.interfaces.InsurancePolicyService;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    @Getter
    @Resource
    private  InsurancePolicyRepository insurancePolicyRepository;

    @Getter
    @Resource
    private  CarRepository carRepository;



    @Override
    @Transactional(readOnly = true)
    public boolean isValidForDate(Long carId, LocalDate date) {
        if (carId == null || date == null) {
            throw new PolicyValidationException("Car ID and date cannot be null");
        }
        return insurancePolicyRepository.existsActiveOnDate(carId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsurancePolicy> findExpiredPolicies() {
        return insurancePolicyRepository.findExpiredPolicies(LocalDate.now());
    }

    @Override
    public void markAsLogged(Long policyId) {
        if (policyId == null) {
            throw new PolicyValidationException("Policy ID cannot be null");
        }
        insurancePolicyRepository.markPolicyAsLogged(policyId);
    }

    @Override
    public InsurancePolicy createPolicy(Long carId, PolicyCreateRequest request) {
        validateCreatePolicyRequest(carId, request);
        
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new CarNotFoundException("Car not found with ID: " + carId));
        
        validatePolicyDates(request.getStartDate(), request.getEndDate());
        
        InsurancePolicy policy = new InsurancePolicy(
            car, 
            request.getProvider(), 
            request.getStartDate(), 
            request.getEndDate()
        );
        
        return insurancePolicyRepository.save(policy);
    }

    private void validateCreatePolicyRequest(Long carId, PolicyCreateRequest request) {
        if (carId == null) {
            throw new PolicyValidationException("Car ID cannot be null");
        }
        if (request == null) {
            throw new PolicyValidationException("Policy request cannot be null");
        }
    }

    private void validatePolicyDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new PolicyValidationException("Start date cannot be after end date");
        }
        if (startDate != null && startDate.isBefore(LocalDate.now().minusYears(1))) {
            throw new PolicyValidationException("Start date cannot be more than 1 year in the past");
        }
    }
}
