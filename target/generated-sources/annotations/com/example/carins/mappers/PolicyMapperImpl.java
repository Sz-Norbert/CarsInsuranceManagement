package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.web.dto.response.PolicyResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-02T11:21:41+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 22.0.1 (Oracle Corporation)"
)
@Component
public class PolicyMapperImpl implements PolicyMapper {

    @Override
    public PolicyResponse toResponse(InsurancePolicy policy) {
        if ( policy == null ) {
            return null;
        }

        PolicyResponse policyResponse = new PolicyResponse();

        policyResponse.setCarVin( policyCarVin( policy ) );
        policyResponse.setId( policy.getId() );
        policyResponse.setProvider( policy.getProvider() );
        policyResponse.setStartDate( policy.getStartDate() );
        policyResponse.setEndDate( policy.getEndDate() );

        return policyResponse;
    }

    private String policyCarVin(InsurancePolicy insurancePolicy) {
        if ( insurancePolicy == null ) {
            return null;
        }
        Car car = insurancePolicy.getCar();
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
