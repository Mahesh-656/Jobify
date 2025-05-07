package com.example.Jobify.DTO;

import com.example.Jobify.model.JobData;
import com.example.Jobify.model.JobStatus;
import com.example.Jobify.model.UserData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "applications")
public class Application {

    @Id
    private String id;

    @DBRef
    private UserData user;  // The user who applied

    @DBRef
    private JobData job;  // The job that was applied to

    private JobStatus status = JobStatus.PENDING; // Default status when user applies

    private LocalDateTime appliedAt = LocalDateTime.now(); // Automatically set when created
}
