package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.web.dto.response.ClaimResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-02T15:47:59+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class ClaimMapperImpl implements ClaimMapper {

    @Override
    public ClaimResponse toResponse(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimResponse claimResponse = new ClaimResponse();

        claimResponse.setCarVin( claimCarVin( claim ) );
        claimResponse.setId( claim.getId() );
        claimResponse.setClaimDate( claim.getClaimDate() );
        claimResponse.setDescription( claim.getDescription() );
        claimResponse.setAmount( claim.getAmount() );
        claimResponse.setProvider( claim.getProvider() );
        claimResponse.setCreatedAt( claim.getCreatedAt() );

        return claimResponse;
    }

    private String claimCarVin(Claim claim) {
        if ( claim == null ) {
            return null;
        }
        Car car = claim.getCar();
        if ( car == null ) {
            return null;
        }
        String vin = car.getVin();
        if ( vin == null ) {
            return null;
        }
        return vin;
    }
}
