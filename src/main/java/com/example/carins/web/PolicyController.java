package com.example.carins.web;

import com.example.carins.api.ApiResponse;
import com.example.carins.facade.PolicyFacade;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import com.example.carins.web.dto.response.PolicyResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api")
@RestController
public class PolicyController {

    @Resource
    @Getter
    private PolicyFacade policyFacade;



    @PostMapping("/policies")
    public ResponseEntity<ApiResponse<PolicyResponse>> createPolicyByVin(
            @Valid @RequestBody PolicyCreateRequest request) {
        ApiResponse<PolicyResponse> response = policyFacade.createPolicyByVin(request);
        PolicyResponse policyData = response.getData();
        URI location = URI.create("/api/policies/" + policyData.getId());
        return ResponseEntity.created(location).body(response);
    }

}
