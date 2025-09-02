package com.example.carins.repo;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.model.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InsurancePolicyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    private Car car;
    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner("Diana Vasilescu", "diana.vasilescu@outlook.com");
        entityManager.persistAndFlush(owner);

        car = new Car("JM1BK32F781234567", "Mazda", "CX-5", 2022, owner);
        entityManager.persistAndFlush(car);
    }

    @Test
    void existsActiveOnDate_withActivePolicy_shouldReturnTrue() {
        InsurancePolicy activePolicy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(activePolicy);

        boolean result = insurancePolicyRepository.existsActiveOnDate(car.getId(), LocalDate.of(2024, 6, 15));

        assertTrue(result);
    }

    @Test
    void existsActiveOnDate_withExpiredPolicy_shouldReturnFalse() {
        InsurancePolicy expiredPolicy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(expiredPolicy);

        boolean result = insurancePolicyRepository.existsActiveOnDate(car.getId(), LocalDate.of(2024, 6, 15));

        assertFalse(result);
    }

    @Test
    void existsActiveOnDate_withFuturePolicy_shouldReturnFalse() {
        InsurancePolicy futurePolicy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(futurePolicy);

        boolean result = insurancePolicyRepository.existsActiveOnDate(car.getId(), LocalDate.of(2024, 6, 15));

        assertFalse(result);
    }

    @Test
    void existsActiveOnDate_onStartDate_shouldReturnTrue() {
        InsurancePolicy policy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2024, 6, 15))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(policy);

        boolean result = insurancePolicyRepository.existsActiveOnDate(car.getId(), LocalDate.of(2024, 6, 15));

        assertTrue(result);
    }

    @Test
    void existsActiveOnDate_onEndDate_shouldReturnTrue() {
        InsurancePolicy policy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 15))
                .logged(false)
                .build();
        entityManager.persistAndFlush(policy);

        boolean result = insurancePolicyRepository.existsActiveOnDate(car.getId(), LocalDate.of(2024, 6, 15));

        assertTrue(result);
    }

    @Test
    void findExpiredUnloggedPolicies_shouldReturnOnlyExpiredUnloggedPolicies() {
        InsurancePolicy expiredUnlogged = InsurancePolicy.builder()
                .car(car)
                .provider("Prima Asig")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .logged(false)
                .build();

        InsurancePolicy expiredLogged = InsurancePolicy.builder()
                .car(car)
                .provider("Omniasig")
                .startDate(LocalDate.of(2022, 1, 1))
                .endDate(LocalDate.of(2022, 12, 31))
                .logged(true)
                .build();

        InsurancePolicy activeUnlogged = InsurancePolicy.builder()
                .car(car)
                .provider("Euroins")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();

        entityManager.persistAndFlush(expiredUnlogged);
        entityManager.persistAndFlush(expiredLogged);
        entityManager.persistAndFlush(activeUnlogged);

        List<InsurancePolicy> result = insurancePolicyRepository.findExpiredUnloggedPolicies(LocalDate.of(2024, 6, 15));

        assertEquals(1, result.size());
        assertEquals("Prima Asig", result.get(0).getProvider());
        assertFalse(result.get(0).isLogged());
    }

    @Test
    void findExpiredUnloggedPolicies_withNoExpiredPolicies_shouldReturnEmptyList() {
        InsurancePolicy activePolicy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(activePolicy);

        List<InsurancePolicy> result = insurancePolicyRepository.findExpiredUnloggedPolicies(LocalDate.of(2024, 6, 15));

        assertTrue(result.isEmpty());
    }

    @Test
    void markPolicyAsLogged_shouldUpdateLoggedField() {
        InsurancePolicy policy = InsurancePolicy.builder()
                .car(car)
                .provider("Romanian Assurance")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .logged(false)
                .build();
        entityManager.persistAndFlush(policy);

        insurancePolicyRepository.markPolicyAsLogged(policy.getId());
        entityManager.flush();
        entityManager.clear();

        InsurancePolicy updatedPolicy = entityManager.find(InsurancePolicy.class, policy.getId());
        assertTrue(updatedPolicy.isLogged());
    }

    @Test
    void findByCarId_shouldReturnPoliciesForSpecificCar() {
        Car secondCar = new Car("JTDKN3DU4E0123456", "Toyota", "Prius", 2023, owner);
        entityManager.persistAndFlush(secondCar);

        InsurancePolicy policy1 = InsurancePolicy.builder()
                .car(car)
                .provider("Generali")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();

        InsurancePolicy policy2 = InsurancePolicy.builder()
                .car(car)
                .provider("BCR Asigurari")
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 12, 31))
                .logged(false)
                .build();

        InsurancePolicy policy3 = InsurancePolicy.builder()
                .car(secondCar)
                .provider("Asirom")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .logged(false)
                .build();

        entityManager.persistAndFlush(policy1);
        entityManager.persistAndFlush(policy2);
        entityManager.persistAndFlush(policy3);

        List<InsurancePolicy> result = insurancePolicyRepository.findByCarId(car.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getCar().getId().equals(car.getId())));
    }
}