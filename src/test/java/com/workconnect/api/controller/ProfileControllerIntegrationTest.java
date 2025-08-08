package com.workconnect.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workconnect.api.dto.WorkerProfileDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class ProfileControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void updateWorkerProfile_WithValidRequest_ShouldSucceed() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create a valid WorkerProfileDto request
        WorkerProfileDto validRequest = new WorkerProfileDto();
        validRequest.setFirstName("John");
        validRequest.setLastName("Doe");
        validRequest.setPhoneNumber("+1234567890");
        validRequest.setLocation("New York, NY");
        validRequest.setBio("Experienced software developer");
        validRequest.setExperience("5 years of full-stack development");
        validRequest.setEducation("Bachelor's in Computer Science");
        validRequest.setAvailability("full-time");
        validRequest.setSkills(Set.of("Java", "Spring Boot", "React", "JavaScript"));

        String requestJson = objectMapper.writeValueAsString(validRequest);
        
        System.out.println("=== CORRECT REQUEST FORMAT ===");
        System.out.println(requestJson);
        System.out.println("===============================");

        mockMvc.perform(put("/api/profiles/me/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void updateWorkerProfile_WithMalformedRequest_ShouldReturn400() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test with malformed JSON (missing quotes)
        String malformedJson = "{ firstName: John, lastName: Doe }";

        mockMvc.perform(put("/api/profiles/me/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void updateWorkerProfile_WithInvalidFieldLength_ShouldReturn400() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Test with field that exceeds validation constraints
        WorkerProfileDto invalidRequest = new WorkerProfileDto();
        invalidRequest.setFirstName("A".repeat(100)); // Exceeds 50 character limit
        invalidRequest.setLastName("Doe");

        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(put("/api/profiles/me/worker")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
