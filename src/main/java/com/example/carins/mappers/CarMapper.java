package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.web.dto.response.CarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "owner.email", target = "ownerEmail")
    @Mapping(target = "isInsuranceValid", ignore = true)
    CarResponse toResponse(Car car);

    List<CarResponse> toResponseList(List<Car> cars);
}
