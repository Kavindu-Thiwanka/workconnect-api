package com.workconnect.api.service.impl;

import com.cloudinary.Cloudinary;
import com.workconnect.api.service.FileUploadService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    @Value("${cloudinary.api_key}")
    private String apiKey;
    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        this.cloudinary = new Cloudinary(config);
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        try {
            // Configure upload options with proper transformation format
            Map<String, Object> uploadOptions = Map.of(
                "folder", "workconnect/profiles",
                "resource_type", "image",
                "format", "jpg",
                "transformation", "w_400,h_400,c_fill,q_auto"
            );

            Map<String, Object> uploadResult = cloudinary.uploader()
                    .upload(multipartFile.getBytes(), uploadOptions);

            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }
}
