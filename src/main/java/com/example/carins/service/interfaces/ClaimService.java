package com.example.carins.service.interfaces;

import com.example.carins.model.Claim;
import com.example.carins.web.dto.request.ClaimCreateRequest;

public interface ClaimService {
    Claim createClaim( String vin, ClaimCreateRequest request);
}
