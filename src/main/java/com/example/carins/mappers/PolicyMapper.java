package com.example.carins.mappers;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.web.dto.response.PolicyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(source = "car.vin", target = "carVin")
    PolicyResponse toResponse(InsurancePolicy policy);
}
