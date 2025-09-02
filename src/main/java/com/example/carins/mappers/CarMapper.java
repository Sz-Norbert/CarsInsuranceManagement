package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.web.dto.response.CarResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "owner.email", target = "ownerEmail")
    CarResponse toResponse(Car car);

    List<CarResponse> toResponseList(List<Car> cars);
}
