package com.example.carins.service.impl;

import com.example.carins.exception.CarNotFoundException;
import com.example.carins.exception.PolicyValidationException;
import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.model.Owner;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsurancePolicyServiceImplTest {

    @Mock
    private InsurancePolicyRepository insurancePolicyRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private InsurancePolicyServiceImpl insurancePolicyService;

    private Car car;
    private Owner owner;
    private PolicyCreateRequest validRequest;

    @BeforeEach
    void setUp() {
        owner = new Owner("Andrei Popescu", "andrei.popescu@gmail.com");
        owner.setId(1L);

        car = new Car("WBA3B1C50CF256891", "BMW", "X3", 2019, owner);
        car.setId(1L);

        validRequest = new PolicyCreateRequest();
        validRequest.setCarVin("WBA3B1C50CF256891");
        validRequest.setProvider("City Insurance");
        validRequest.setStartDate(LocalDate.of(2024, 1, 1));
        validRequest.setEndDate(LocalDate.of(2024, 12, 31));
    }

    @Test
    void isValidForDate_withValidData_shouldReturnTrue() {
        when(insurancePolicyRepository.existsActiveOnDate(1L, LocalDate.of(2024, 6, 15)))
                .thenReturn(true);

        boolean result = insurancePolicyService.isValidForDate(1L, LocalDate.of(2024, 6, 15));

        assertTrue(result);
        verify(insurancePolicyRepository).existsActiveOnDate(1L, LocalDate.of(2024, 6, 15));
    }

    @Test
    void isValidForDate_withValidData_shouldReturnFalse() {
        when(insurancePolicyRepository.existsActiveOnDate(2L, LocalDate.of(2024, 6, 15)))
                .thenReturn(false);

        boolean result = insurancePolicyService.isValidForDate(2L, LocalDate.of(2024, 6, 15));

        assertFalse(result);
        verify(insurancePolicyRepository).existsActiveOnDate(2L, LocalDate.of(2024, 6, 15));
    }

    @Test
    void isValidForDate_withNullCarId_shouldThrowException() {
        PolicyValidationException exception = assertThrows(
                PolicyValidationException.class,
                () -> insurancePolicyService.isValidForDate(null, LocalDate.of(2024, 6, 15))
        );

        assertEquals("Car ID and date cannot be null", exception.getMessage());
        verify(insurancePolicyRepository, never()).existsActiveOnDate(any(), any());
    }

    @Test
    void isValidForDate_withNullDate_shouldThrowException() {
        PolicyValidationException exception = assertThrows(
                PolicyValidationException.class,
                () -> insurancePolicyService.isValidForDate(1L, null)
        );

        assertEquals("Car ID and date cannot be null", exception.getMessage());
        verify(insurancePolicyRepository, never()).existsActiveOnDate(any(), any());
    }

    @Test
    void createPolicyByVin_withValidRequest_shouldCreatePolicy() {
        when(carRepository.findByVin("WBA3B1C50CF256891")).thenReturn(Optional.of(car));
        
        InsurancePolicy expectedPolicy = InsurancePolicy.builder()
                .id(1L)
                .car(car)
                .provider("City Insurance")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();
        
        when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(expectedPolicy);

        InsurancePolicy result = insurancePolicyService.createPolicyByVin(validRequest);

        assertNotNull(result);
        assertEquals("City Insurance", result.getProvider());
        assertEquals(car, result.getCar());
        assertEquals(LocalDate.of(2024, 1, 1), result.getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), result.getEndDate());
        assertFalse(result.isLogged());
        
        verify(carRepository).findByVin("WBA3B1C50CF256891");
        verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
    }

    @Test
    void createPolicyByVin_withInvalidVin_shouldThrowCarNotFoundException() {
        when(carRepository.findByVin("ABC123INVALID")).thenReturn(Optional.empty());
        
        PolicyCreateRequest invalidRequest = new PolicyCreateRequest();
        invalidRequest.setCarVin("ABC123INVALID");
        invalidRequest.setProvider("City Insurance");
        invalidRequest.setStartDate(LocalDate.of(2024, 1, 1));
        invalidRequest.setEndDate(LocalDate.of(2024, 12, 31));

        CarNotFoundException exception = assertThrows(
                CarNotFoundException.class,
                () -> insurancePolicyService.createPolicyByVin(invalidRequest)
        );

        assertEquals("Car not found with VIN: ABC123INVALID", exception.getMessage());
        verify(carRepository).findByVin("ABC123INVALID");
        verify(insurancePolicyRepository, never()).save(any());
    }

    @Test
    void createPolicyByVin_withInvalidDateOrder_shouldThrowPolicyValidationException() {
        PolicyCreateRequest invalidRequest = new PolicyCreateRequest();
        invalidRequest.setCarVin("WBA3B1C50CF256891");
        invalidRequest.setProvider("City Insurance");
        invalidRequest.setStartDate(LocalDate.of(2024, 12, 31));
        invalidRequest.setEndDate(LocalDate.of(2024, 1, 1));

        PolicyValidationException exception = assertThrows(
                PolicyValidationException.class,
                () -> insurancePolicyService.createPolicyByVin(invalidRequest)
        );

        assertEquals("Start date must be before or equal to end date", exception.getMessage());
        verify(carRepository, never()).findByVin(any());
        verify(insurancePolicyRepository, never()).save(any());
    }

    @Test
    void createPolicyByVin_withInvalidDateRange_shouldThrowPolicyValidationException() {
        PolicyCreateRequest invalidRequest = new PolicyCreateRequest();
        invalidRequest.setCarVin("WBA3B1C50CF256891");
        invalidRequest.setProvider("City Insurance");
        invalidRequest.setStartDate(LocalDate.of(1899, 1, 1));
        invalidRequest.setEndDate(LocalDate.of(1899, 12, 31));

        PolicyValidationException exception = assertThrows(
                PolicyValidationException.class,
                () -> insurancePolicyService.createPolicyByVin(invalidRequest)
        );

        assertEquals("Dates must be between 1900-01-01 and 2100-12-31", exception.getMessage());
        verify(carRepository, never()).findByVin(any());
        verify(insurancePolicyRepository, never()).save(any());
    }

    @Test
    void createPolicyByVin_withFutureDateRange_shouldThrowPolicyValidationException() {
        PolicyCreateRequest invalidRequest = new PolicyCreateRequest();
        invalidRequest.setCarVin("WBA3B1C50CF256891");
        invalidRequest.setProvider("City Insurance");
        invalidRequest.setStartDate(LocalDate.of(2101, 1, 1));
        invalidRequest.setEndDate(LocalDate.of(2101, 12, 31));

        PolicyValidationException exception = assertThrows(
                PolicyValidationException.class,
                () -> insurancePolicyService.createPolicyByVin(invalidRequest)
        );

        assertEquals("Dates must be between 1900-01-01 and 2100-12-31", exception.getMessage());
        verify(carRepository, never()).findByVin(any());
        verify(insurancePolicyRepository, never()).save(any());
    }

    @Test
    void createPolicyByVin_withBoundaryDateRange_shouldCreatePolicy() {
        when(carRepository.findByVin("WBA3B1C50CF256891")).thenReturn(Optional.of(car));
        
        PolicyCreateRequest boundaryRequest = new PolicyCreateRequest();
        boundaryRequest.setCarVin("WBA3B1C50CF256891");
        boundaryRequest.setProvider("City Insurance");
        boundaryRequest.setStartDate(LocalDate.of(1900, 1, 1));
        boundaryRequest.setEndDate(LocalDate.of(2100, 12, 31));

        InsurancePolicy expectedPolicy = InsurancePolicy.builder()
                .id(1L)
                .car(car)
                .provider("City Insurance")
                .startDate(LocalDate.of(1900, 1, 1))
                .endDate(LocalDate.of(2100, 12, 31))
                .logged(false)
                .build();
        
        when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(expectedPolicy);

        InsurancePolicy result = insurancePolicyService.createPolicyByVin(boundaryRequest);

        assertNotNull(result);
        assertEquals(LocalDate.of(1900, 1, 1), result.getStartDate());
        assertEquals(LocalDate.of(2100, 12, 31), result.getEndDate());
        
        verify(carRepository).findByVin("WBA3B1C50CF256891");
        verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
    }

    @Test
    void createPolicyByVin_withSameDates_shouldCreatePolicy() {
        when(carRepository.findByVin("WBA3B1C50CF256891")).thenReturn(Optional.of(car));
        
        PolicyCreateRequest sameDateRequest = new PolicyCreateRequest();
        sameDateRequest.setCarVin("WBA3B1C50CF256891");
        sameDateRequest.setProvider("City Insurance");
        sameDateRequest.setStartDate(LocalDate.of(2024, 6, 15));
        sameDateRequest.setEndDate(LocalDate.of(2024, 6, 15));

        InsurancePolicy expectedPolicy = InsurancePolicy.builder()
                .id(1L)
                .car(car)
                .provider("City Insurance")
                .startDate(LocalDate.of(2024, 6, 15))
                .endDate(LocalDate.of(2024, 6, 15))
                .logged(false)
                .build();
        
        when(insurancePolicyRepository.save(any(InsurancePolicy.class))).thenReturn(expectedPolicy);

        InsurancePolicy result = insurancePolicyService.createPolicyByVin(sameDateRequest);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 6, 15), result.getStartDate());
        assertEquals(LocalDate.of(2024, 6, 15), result.getEndDate());
        
        verify(carRepository).findByVin("WBA3B1C50CF256891");
        verify(insurancePolicyRepository).save(any(InsurancePolicy.class));
    }
}