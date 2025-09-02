package com.example.carins.facade;

import com.example.carins.api.ApiResponse;
import com.example.carins.mappers.PolicyMapper;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.interfaces.InsurancePolicyService;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import com.example.carins.web.dto.response.PolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PolicyFacade {
    
    private final InsurancePolicyService policyService;
    private final PolicyMapper policyMapper;

    public ApiResponse<PolicyResponse> createPolicyByVin(PolicyCreateRequest request) {
        InsurancePolicy policy = policyService.createPolicyByVin(request);
        PolicyResponse response = policyMapper.toResponse(policy);
        return ApiResponse.created(response);
    }
}
