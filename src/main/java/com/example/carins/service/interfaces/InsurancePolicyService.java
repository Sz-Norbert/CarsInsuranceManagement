package com.example.carins.service.interfaces;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.web.dto.request.PolicyCreateRequest;

import java.time.LocalDate;
import java.util.List;

public interface InsurancePolicyService {

    boolean isValidForDate(Long carId, LocalDate date);
    List<InsurancePolicy> findExpiredPolicies();
    void markAsLogged(Long policyId);
    InsurancePolicy createPolicyByVin(PolicyCreateRequest request);
}
