package com.example.carins.service.interfaces;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.web.dto.request.PolicyCreateRequest;

import java.time.LocalDate;

public interface InsurancePolicyService {

    boolean isValidForDate(Long carId, LocalDate date);
    InsurancePolicy createPolicyByVin(PolicyCreateRequest request);
}
