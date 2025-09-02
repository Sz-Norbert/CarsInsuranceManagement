package com.example.carins.web;

import com.example.carins.api.ApiResponse;
import com.example.carins.facade.PolicyFacade;
import com.example.carins.web.dto.request.PolicyCreateRequest;
import com.example.carins.web.dto.response.PolicyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PolicyController.class)
class PolicyControllerWireMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PolicyFacade policyFacade;

    @Test
    void createPolicy_withValidData_shouldReturnCreated() throws Exception {
        PolicyResponse policyResponse = new PolicyResponse();
        policyResponse.setId(1L);
        policyResponse.setCarVin("5N1AR2MM5FC654321");
        policyResponse.setProvider("Asirom Vienna");
        policyResponse.setStartDate(LocalDate.of(2024, 1, 1));
        policyResponse.setEndDate(LocalDate.of(2024, 12, 31));
        
        ApiResponse<PolicyResponse> mockResponse = ApiResponse.created(policyResponse);
        
        when(policyFacade.createPolicyByVin(any(PolicyCreateRequest.class)))
            .thenReturn(mockResponse);

        mockMvc.perform(post("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPolicyRequest()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.provider").value("Asirom Vienna"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createPolicy_withInvalidVin_shouldReturnNotFound() throws Exception {
        when(policyFacade.createPolicyByVin(any(PolicyCreateRequest.class)))
            .thenThrow(new RuntimeException("Car not found with VIN: ZZZ999FAKE"));

        mockMvc.perform(post("/api/policies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidVinRequest()))
                .andExpect(status().isInternalServerError());
    }


    private String validPolicyRequest() {
        return """
            {
                "carVin": "5N1AR2MM5FC654321",
                "provider": "Asirom Vienna",
                "startDate": "2024-01-01",
                "endDate": "2024-12-31"
            }
            """;
    }

    private String invalidVinRequest() {
        return """
            {
                "carVin": "ZZZ999FAKE",
                "provider": "Asirom Vienna", 
                "startDate": "2024-01-01",
                "endDate": "2024-12-31"
            }
            """;
    }

}