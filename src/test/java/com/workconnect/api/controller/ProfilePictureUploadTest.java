package com.workconnect.api.controller;

import com.workconnect.api.service.FileUploadService;
import com.workconnect.api.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfilePictureUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private FileUploadService fileUploadService;

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void uploadProfilePicture_WithValidImage_ShouldSucceed() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        String expectedImageUrl = "https://cloudinary.com/test-image.jpg";
        when(fileUploadService.uploadFile(any())).thenReturn(expectedImageUrl);

        // When & Then
        mockMvc.perform(multipart("/api/profiles/me/picture")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.imageUrl").value(expectedImageUrl));
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void uploadProfilePicture_WithEmptyFile_ShouldReturn400() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/api/profiles/me/picture")
                .file(emptyFile)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please select a file to upload"));
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void uploadProfilePicture_WithNonImageFile_ShouldReturn400() throws Exception {
        // Given
        MockMultipartFile textFile = new MockMultipartFile(
                "file",
                "document.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/profiles/me/picture")
                .file(textFile)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only image files are allowed"));
    }

    @Test
    @WithMockUser(username = "worker@test.com", roles = {"WORKER"})
    void uploadProfilePicture_WithLargeFile_ShouldReturn400() throws Exception {
        // Given - Create a file larger than 5MB
        byte[] largeFileContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile largeFile = new MockMultipartFile(
                "file",
                "large-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                largeFileContent
        );

        // When & Then
        mockMvc.perform(multipart("/api/profiles/me/picture")
                .file(largeFile)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File size must be less than 5MB"));
    }

    @Test
    void uploadProfilePicture_WithoutAuthentication_ShouldReturn401() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/profiles/me/picture")
                .file(file))
                .andExpect(status().isUnauthorized());
    }
}
