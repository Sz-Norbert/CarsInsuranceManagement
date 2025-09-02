package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.model.Car;
import com.example.carins.model.Claim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.response.CarHistoryEvent;
import com.example.carins.web.dto.response.CarHistoryResponse;
import com.example.carins.service.interfaces.CarService;
import com.example.carins.service.interfaces.InsurancePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyService insurancePolicyService;
    private final InsurancePolicyRepository insurancePolicyRepository;


    @Override
    public List<Car> listCars() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> findById(Long carId) {
        return carRepository.findById(carId);
    }

    @Override
    public Optional<Car> findByVin(String vin) {
        return carRepository.findByVin(vin);
    }



    @Override
    public boolean isInsuranceValid(Long carId, LocalDate date) {
        return findById(carId)
                .map(car -> insurancePolicyService.isValidForDate(carId, date))
                .orElse(false);
    }

    @Override
    public Optional<CarHistoryResponse> getCarHistory(String vin) {
        return findByVin(vin).map(car -> {
            List<CarHistoryEvent> events = new ArrayList<>();
            
            for (Claim claim : car.getClaims()) {
                events.add(new CarHistoryEvent(
                    "CLAIM",
                    claim.getClaimDate(),
                    claim.getCreatedAt(),
                    claim.getDescription(),
                    claim.getAmount(),
                    claim.getProvider()
                ));
            }
            
            List<InsurancePolicy> policies = insurancePolicyRepository.findByCarId(car.getId());
            for (InsurancePolicy policy : policies) {
                events.add(new CarHistoryEvent(
                    "INSURANCE_POLICY_START",
                    policy.getStartDate(),
                    LocalDateTime.now(),
                    "Insurance policy started with " + policy.getProvider(),
                    BigDecimal.ZERO,
                    policy.getProvider()
                ));
                
                if (policy.getEndDate() != null) {
                    events.add(new CarHistoryEvent(
                        "INSURANCE_POLICY_END",
                        policy.getEndDate(),
                        LocalDateTime.now(),
                        "Insurance policy ended with " + policy.getProvider(),
                        BigDecimal.ZERO,
                        policy.getProvider()
                    ));
                }
            }
            
            events.sort(Comparator.comparing(CarHistoryEvent::getEventDate));
            
            return new CarHistoryResponse(
                car.getVin(),
                car.getMake(),
                car.getModel(),
                car.getYearOfManufacture(),
                events
            );
        });
    }


}
