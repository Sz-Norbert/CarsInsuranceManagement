package com.example.carins.web;

import com.example.carins.api.ApiResponse;
import com.example.carins.facade.CarFacade;
import com.example.carins.web.dto.request.ClaimCreateRequest;
import com.example.carins.web.dto.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerWireMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarFacade carFacade;

    @Test
    void getAllCars_shouldReturnCarsList() throws Exception {
        List<CarResponse> cars = Arrays.asList(
            createCarResponse(1L, "TESTVIN1", "BMW", "X3", 2019, "Andrei Popescu", "andrei.popescu@gmail.com"),
            createCarResponse(2L, "TESTVIN2", "Mazda", "CX-5", 2022, "Diana Vasilescu", "diana.vasilescu@outlook.com")
        );
        
        ApiResponse<List<CarResponse>> mockResponse = ApiResponse.success(cars);
        
        when(carFacade.getAllCars()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].vin").value("TESTVIN1"))
                .andExpect(jsonPath("$.data[0].make").value("BMW"))
                .andExpect(jsonPath("$.data[0].model").value("X3"))
                .andExpect(jsonPath("$.data[0].ownerName").value("Andrei Popescu"))
                .andExpect(jsonPath("$.data[1].vin").value("TESTVIN2"))
                .andExpect(jsonPath("$.data[1].make").value("Mazda"))
                .andExpect(jsonPath("$.data[1].ownerName").value("Diana Vasilescu"));
    }

    @Test
    void getAllCars_whenNoCars_shouldReturnEmptyList() throws Exception {
        ApiResponse<List<CarResponse>> mockResponse = ApiResponse.success(Collections.emptyList());
        
        when(carFacade.getAllCars()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void checkInsuranceValidity_withValidData_shouldReturnValid() throws Exception {
        InsuranceValidityResponse validityResponse = new InsuranceValidityResponse(
            "TESTVIN1", "2024-06-15", true
        );
        
        ApiResponse<InsuranceValidityResponse> mockResponse = ApiResponse.success(validityResponse);
        
        when(carFacade.checkInsuranceValidity("TESTVIN1", "2024-06-15"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/TESTVIN1/insurance-valid")
                .param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.vin").value("TESTVIN1"))
                .andExpect(jsonPath("$.data.date").value("2024-06-15"))
                .andExpect(jsonPath("$.data.valid").value(true));
    }

    @Test
    void checkInsuranceValidity_withInvalidVin_shouldReturnNotFound() throws Exception {
        ApiResponse<InsuranceValidityResponse> mockResponse = ApiResponse.notFound("Car not found with VIN: ZZZ999FAKE");
        
        when(carFacade.checkInsuranceValidity("ZZZ999FAKE", "2024-06-15"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/ZZZ999FAKE/insurance-valid")
                .param("date", "2024-06-15"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Car not found with VIN: ZZZ999FAKE"));
    }

    @Test
    void checkInsuranceValidity_withNoActivePolicies_shouldReturnInvalid() throws Exception {
        InsuranceValidityResponse validityResponse = new InsuranceValidityResponse(
            "TESTVIN1", "2023-01-15", false
        );
        
        ApiResponse<InsuranceValidityResponse> mockResponse = ApiResponse.success(validityResponse);
        
        when(carFacade.checkInsuranceValidity("TESTVIN1", "2023-01-15"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/TESTVIN1/insurance-valid")
                .param("date", "2023-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(false));
    }

    @Test
    void createClaim_withValidData_shouldReturnCreated() throws Exception {
        ClaimCreationResponse claimResponse = new ClaimCreationResponse();
        ClaimResponse claim = new ClaimResponse();
        claim.setId(1L);
        claim.setCarVin("TESTVIN1");
        claim.setClaimDate(LocalDate.of(2024, 3, 15));
        claim.setDescription("Accident pe autostrada A1");
        claim.setAmount(new BigDecimal("2500.00"));
        claim.setProvider("Asirom Vienna");
        claim.setCreatedAt(LocalDateTime.now());
        
        claimResponse.setClaim(claim);
        claimResponse.setLocationUrl("/api/cars/TESTVIN1/claims/1");
        
        ApiResponse<ClaimCreationResponse> mockResponse = ApiResponse.created(claimResponse);
        
        when(carFacade.createClaimWithLocationAndHeaders(eq("TESTVIN1"), any(ClaimCreateRequest.class)))
            .thenReturn(ResponseEntity.status(201)
                .header("Location", "/api/cars/TESTVIN1/claims/1")
                .body(mockResponse));

        mockMvc.perform(post("/api/cars/TESTVIN1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validClaimRequest()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/cars/TESTVIN1/claims/1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.claim.id").value(1))
                .andExpect(jsonPath("$.data.claim.carVin").value("TESTVIN1"))
                .andExpect(jsonPath("$.data.claim.description").value("Accident pe autostrada A1"))
                .andExpect(jsonPath("$.data.claim.amount").value(2500.00));
    }

    @Test
    void createClaim_withInvalidVin_shouldReturnBadRequest() throws Exception {
        ApiResponse<ClaimCreationResponse> mockResponse = ApiResponse.badRequest("Car not found with VIN: ZZZ999FAKE");
        
        when(carFacade.createClaimWithLocationAndHeaders(eq("ZZZ999FAKE"), any(ClaimCreateRequest.class)))
            .thenReturn(ResponseEntity.status(400).body(mockResponse));

        mockMvc.perform(post("/api/cars/ZZZ999FAKE/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validClaimRequest()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Car not found with VIN: ZZZ999FAKE"));
    }

    @Test
    void createClaim_withFutureDate_shouldReturnBadRequest() throws Exception {
        ApiResponse<ClaimCreationResponse> mockResponse = ApiResponse.badRequest("Claim date cannot be in the future");
        
        when(carFacade.createClaimWithLocationAndHeaders(eq("TESTVIN1"), any(ClaimCreateRequest.class)))
            .thenReturn(ResponseEntity.status(400).body(mockResponse));

        mockMvc.perform(post("/api/cars/TESTVIN1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(futureDateClaimRequest()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Claim date cannot be in the future"));
    }

    @Test
    void getCarHistory_withValidVin_shouldReturnHistory() throws Exception {
        CarHistoryResponse historyResponse = createCarHistoryResponse();
        ApiResponse<CarHistoryResponse> mockResponse = ApiResponse.success(historyResponse);
        
        when(carFacade.getCarHistoryWithStatus("TESTVIN1"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/TESTVIN1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.vin").value("TESTVIN1"))
                .andExpect(jsonPath("$.data.make").value("BMW"))
                .andExpect(jsonPath("$.data.model").value("X3"))
                .andExpect(jsonPath("$.data.yearOfManufacture").value(2019))
                .andExpect(jsonPath("$.data.history").isArray())
                .andExpect(jsonPath("$.data.history.length()").value(2));
    }

    @Test
    void getCarHistory_withInvalidVin_shouldReturnNotFound() throws Exception {
        ApiResponse<CarHistoryResponse> mockResponse = ApiResponse.notFound("Car not found with VIN: ZZZ999FAKE");
        
        when(carFacade.getCarHistoryWithStatus("ZZZ999FAKE"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/ZZZ999FAKE/history"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Car not found with VIN: ZZZ999FAKE"));
    }

    @Test
    void getCarHistory_withNoHistory_shouldReturnEmptyEvents() throws Exception {
        CarHistoryResponse historyResponse = new CarHistoryResponse(
            "TESTVIN1", "BMW", "X3", 2019, Collections.emptyList()
        );
        ApiResponse<CarHistoryResponse> mockResponse = ApiResponse.success(historyResponse);
        
        when(carFacade.getCarHistoryWithStatus("TESTVIN1"))
            .thenReturn(mockResponse);

        mockMvc.perform(get("/api/cars/TESTVIN1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.history").isArray())
                .andExpect(jsonPath("$.data.history.length()").value(0));
    }

    // Helper methods for creating test data
    private CarResponse createCarResponse(Long id, String vin, String make, String model, 
                                        int year, String ownerName, String ownerEmail) {
        CarResponse car = new CarResponse();
        car.setId(id);
        car.setVin(vin);
        car.setMake(make);
        car.setModel(model);
        car.setYearOfManufacture(year);
        car.setOwnerName(ownerName);
        car.setOwnerEmail(ownerEmail);
        return car;
    }

    private CarHistoryResponse createCarHistoryResponse() {
        List<CarHistoryEvent> events = Arrays.asList(
            new CarHistoryEvent(
                "INSURANCE_POLICY_START",
                LocalDate.of(2024, 1, 1),
                LocalDateTime.of(2024, 1, 1, 10, 0),
                "Insurance policy started with City Insurance",
                BigDecimal.ZERO,
                "City Insurance"
            ),
            new CarHistoryEvent(
                "CLAIM",
                LocalDate.of(2024, 3, 15),
                LocalDateTime.of(2024, 3, 15, 14, 30),
                "Accident pe autostrada A1",
                new BigDecimal("2500.00"),
                "City Insurance"
            )
        );

        return new CarHistoryResponse("TESTVIN1", "BMW", "X3", 2019, events);
    }

    private String validClaimRequest() {
        return """
            {
                "claimDate": "2024-03-15",
                "description": "Accident pe autostrada A1",
                "amount": 2500.00,
                "provider": "Asirom Vienna"
            }
            """;
    }

    private String futureDateClaimRequest() {
        return """
            {
                "claimDate": "2025-12-31",
                "description": "Future accident",
                "amount": 1000.00,
                "provider": "Asirom Vienna"
            }
            """;
    }
}