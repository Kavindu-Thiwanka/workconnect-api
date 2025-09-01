package com.workconnect.api.controller;

import com.workconnect.api.dto.dashboard.WorkerDashboardDto;
import com.workconnect.api.dto.dashboard.EmployerDashboardDto;
import com.workconnect.api.dto.dashboard.ProfileCompletionDto;
import com.workconnect.api.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void getWorkerDashboard_ShouldReturnWorkerDashboardData() throws Exception {
        // Given
        WorkerDashboardDto mockDashboard = WorkerDashboardDto.builder()
                .totalApplications(5)
                .pendingApplications(2)
                .interviewsScheduled(1)
                .profileViews(25)
                .profileCompletionPercentage(75.0)
                .recentApplications(new ArrayList<>())
                .recommendedJobs(new ArrayList<>())
                .profileCompletionTips(new ArrayList<>())
                .build();

        when(dashboardService.getWorkerDashboard(anyString())).thenReturn(mockDashboard);

        // When & Then
        mockMvc.perform(get("/api/dashboard/worker"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.totalApplications").value(5))
                .andExpect(jsonPath("$.pendingApplications").value(2))
                .andExpect(jsonPath("$.interviewsScheduled").value(1))
                .andExpect(jsonPath("$.profileViews").value(25))
                .andExpect(jsonPath("$.profileCompletionPercentage").value(75.0));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = {"EMPLOYER"})
    void getEmployerDashboard_ShouldReturnEmployerDashboardData() throws Exception {
        // Given
        EmployerDashboardDto mockDashboard = EmployerDashboardDto.builder()
                .activeJobs(3)
                .totalApplications(15)
                .newApplicationsThisWeek(5)
                .totalViews(120)
                .recentApplications(new ArrayList<>())
                .activeJobPostings(new ArrayList<>())
                .build();

        when(dashboardService.getEmployerDashboard(anyString())).thenReturn(mockDashboard);

        // When & Then
        mockMvc.perform(get("/api/dashboard/employer"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.activeJobs").value(3))
                .andExpect(jsonPath("$.totalApplications").value(15))
                .andExpect(jsonPath("$.newApplicationsThisWeek").value(5))
                .andExpect(jsonPath("$.totalViews").value(120));
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void getWorkerDashboard_WithoutWorkerRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/employer"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = {"EMPLOYER"})
    void getEmployerDashboard_WithoutEmployerRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/worker"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void getWorkerProfileCompletion_ShouldReturnProfileCompletionData() throws Exception {
        // Given
        ProfileCompletionDto mockCompletion = ProfileCompletionDto.builder()
                .percentage(75.0)
                .missingFields(List.of("bio", "education"))
                .tips(List.of("Write a professional bio", "Add your education background"))
                .build();

        when(dashboardService.getWorkerProfileCompletion(anyString())).thenReturn(mockCompletion);

        // When & Then
        mockMvc.perform(get("/api/dashboard/worker/profile-completion"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.percentage").value(75.0))
                .andExpect(jsonPath("$.missingFields").isArray())
                .andExpect(jsonPath("$.tips").isArray());
    }
}
