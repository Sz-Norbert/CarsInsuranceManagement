package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyExpiryLoggerServiceTest {

    @Mock
    private InsurancePolicyRepository policyRepository;

    @InjectMocks
    private PolicyExpiryLoggerService policyExpiryLoggerService;

    private InsurancePolicy expiredPolicy;
    private Car car;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setVin("WVWZZZ1JZ3W386752");

        expiredPolicy = InsurancePolicy.builder()
                .id(100L)
                .car(car)
                .provider("Carpatica Asig")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .logged(false)
                .build();
    }

    @Test
    void logExpiredPolicies_whenExpiredPoliciesExist_shouldLogAndMarkThem() {
        when(policyRepository.findExpiredUnloggedPolicies(any(LocalDate.class)))
                .thenReturn(Arrays.asList(expiredPolicy));

        policyExpiryLoggerService.logExpiredPolicies();

        verify(policyRepository).findExpiredUnloggedPolicies(any(LocalDate.class));
        verify(policyRepository).markPolicyAsLogged(100L);
    }

    @Test
    void logExpiredPolicies_whenNoExpiredPolicies_shouldNotMarkAnyAsLogged() {
        when(policyRepository.findExpiredUnloggedPolicies(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        policyExpiryLoggerService.logExpiredPolicies();

        verify(policyRepository).findExpiredUnloggedPolicies(any(LocalDate.class));
        verify(policyRepository, never()).markPolicyAsLogged(any());
    }

    @Test
    void logExpiredPolicies_whenMultipleExpiredPolicies_shouldLogAndMarkAll() {
        InsurancePolicy secondExpiredPolicy = InsurancePolicy.builder()
                .id(200L)
                .car(car)
                .provider("Uniqa Asigurari")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .logged(false)
                .build();

        List<InsurancePolicy> expiredPolicies = Arrays.asList(expiredPolicy, secondExpiredPolicy);
        when(policyRepository.findExpiredUnloggedPolicies(any(LocalDate.class)))
                .thenReturn(expiredPolicies);

        policyExpiryLoggerService.logExpiredPolicies();

        verify(policyRepository).findExpiredUnloggedPolicies(any(LocalDate.class));
        verify(policyRepository).markPolicyAsLogged(100L);
        verify(policyRepository).markPolicyAsLogged(200L);
    }

    @Test
    void logExpiredPolicies_whenRepositoryThrowsException_shouldNotMarkAsLogged() {
        when(policyRepository.findExpiredUnloggedPolicies(any(LocalDate.class)))
                .thenThrow(new RuntimeException("Database error"));

        try {
            policyExpiryLoggerService.logExpiredPolicies();
        } catch (RuntimeException e) {
        }

        verify(policyRepository).findExpiredUnloggedPolicies(any(LocalDate.class));
        verify(policyRepository, never()).markPolicyAsLogged(any());
    }

    @Test
    void logExpiredPolicies_callsRepositoryWithCurrentDate() {
        when(policyRepository.findExpiredUnloggedPolicies(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        policyExpiryLoggerService.logExpiredPolicies();

        verify(policyRepository).findExpiredUnloggedPolicies(eq(LocalDate.now()));
    }
}