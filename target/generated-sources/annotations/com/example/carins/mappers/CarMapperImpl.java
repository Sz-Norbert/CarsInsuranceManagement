package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.model.Owner;
import com.example.carins.web.dto.response.CarResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-02T15:47:59+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class CarMapperImpl extends CarMapper {

    @Override
    public CarResponse toResponse(Car car) {
        if ( car == null ) {
            return null;
        }

        CarResponse.CarResponseBuilder carResponse = CarResponse.builder();

        carResponse.ownerName( carOwnerName( car ) );
        carResponse.ownerEmail( carOwnerEmail( car ) );
        carResponse.id( car.getId() );
        carResponse.vin( car.getVin() );
        carResponse.make( car.getMake() );
        carResponse.model( car.getModel() );
        carResponse.yearOfManufacture( car.getYearOfManufacture() );

        carResponse.isInsuranceValid( isInsuranceValidForToday(car) );

        return carResponse.build();
    }

    @Override
    public List<CarResponse> toResponseList(List<Car> cars) {
        if ( cars == null ) {
            return null;
        }

        List<CarResponse> list = new ArrayList<CarResponse>( cars.size() );
        for ( Car car : cars ) {
            list.add( toResponse( car ) );
        }

        return list;
    }

    private String carOwnerName(Car car) {
        if ( car == null ) {
            return null;
        }
        Owner owner = car.getOwner();
        if ( owner == null ) {
            return null;
        }
        String name = owner.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String carOwnerEmail(Car car) {
        if ( car == null ) {
            return null;
        }
        Owner owner = car.getOwner();
        if ( owner == null ) {
            return null;
        }
        String email = owner.getEmail();
        if ( email == null ) {
            return null;
        }
        return email;
    }
}
