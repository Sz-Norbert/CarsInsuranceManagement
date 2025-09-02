package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.exception.ClaimValidationException;
import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.repo.ClaimRepository;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.service.interfaces.ClaimService;
import com.example.carins.web.dto.request.ClaimCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final CarService carService;


    @Override
    public Claim createClaim(String vin, ClaimCreateRequest request) {

        Car car = carService.findByVin(vin).
                orElseThrow(() -> new CarNotFoundException("Car not found with VIN: " + vin));

        if (request.getClaimDate().isAfter(LocalDate.now())) {
            throw new ClaimValidationException("Claim date cannot be in the future");
        }

        Claim claim = Claim.builder()
                .car(car)
                .claimDate(request.getClaimDate())
                .description(request.getDescription())
                .amount(request.getAmount())
                .provider(request.getProvider())
                .build();

        return claimRepository.save(claim);
    }
}
