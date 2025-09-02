package com.example.carins.facade;

import com.example.carins.api.ApiResponse;
import com.example.carins.mappers.PolicyMapper;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.interfaces.InsurancePolicyService;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import com.example.carins.web.dto.response.PolicyResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class PolicyFacade {
    @Resource
    @Getter
    private InsurancePolicyService policyService;

    @Resource
    @Getter
    private PolicyMapper policyMapper;


    public ApiResponse<PolicyResponse> createPolicyByVin(PolicyCreateRequest request) {
        InsurancePolicy policy = policyService.createPolicyByVin(request);
        PolicyResponse response = policyMapper.toResponse(policy);
        return ApiResponse.created(response);
    }
}
