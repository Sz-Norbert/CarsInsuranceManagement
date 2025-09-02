package com.example.carins.mappers;

import com.example.carins.model.Car;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.web.dto.response.CarResponse;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CarMapper {

    @Resource
    @Getter
    protected CarService carService;

    @Mapping(source = "owner.name", target = "ownerName")
    @Mapping(source = "owner.email", target = "ownerEmail")
    @Mapping(target = "isInsuranceValid", expression = "java(isInsuranceValidForToday(car))")
    public abstract CarResponse toResponse(Car car);

    public abstract List<CarResponse> toResponseList(List<Car> cars);

    protected Boolean isInsuranceValidForToday(Car car) {
        if (car == null || car.getId() == null) {
            return null;
        }
        try {
            return getCarService().isInsuranceValid(car.getId(), LocalDate.now());
        } catch (Exception e) {
            return null;
        }
    }
}
