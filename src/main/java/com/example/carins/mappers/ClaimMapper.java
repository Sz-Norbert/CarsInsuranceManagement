package com.example.carins.mappers;

import com.example.carins.model.Claim;
import com.example.carins.web.dto.response.ClaimResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClaimMapper {

    @Mapping(source = "car.vin", target = "carVin")
    ClaimResponse toResponse(Claim claim);
}
