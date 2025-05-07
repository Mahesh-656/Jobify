package com.example.Jobify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "jobs")
public class JobData {
    @Id
    private String id;
    private String company;
    private String position;
    private JobStatus jobStatus;
    private JobType jobType;
    private String jobLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userId;
}
