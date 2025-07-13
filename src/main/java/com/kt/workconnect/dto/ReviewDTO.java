package com.kt.workconnect.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class ReviewDTO {
    private UUID jobId;
    private UUID revieweeId;
    private Integer rating;
    private String comment;
}
