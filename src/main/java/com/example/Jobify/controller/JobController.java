package com.example.Jobify.controller;


import com.example.Jobify.DTO.JobReq;
import com.example.Jobify.DTO.JobStatusDTO;
import com.example.Jobify.DTO.JobStatsDTO;
import com.example.Jobify.Repo.UserRepository;
import com.example.Jobify.model.JobData;
import com.example.Jobify.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.example.Jobify.model.UserData;


import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/v1/job")

public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;


    public JobController(JobService jobService, UserRepository userRepository) {
        this.jobService = jobService;
        this.userRepository = userRepository;

    }

    @PostMapping
    public ResponseEntity<JobData> addJob(@RequestBody JobReq jobReq, Authentication authentication) {
        // Get the current user's email from authentication
        String email = authentication.getName();
        // Get the user from repository to get their ID
        UserData user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // Set the userId in the request
        jobReq.setUserId(user.getId());
        return new ResponseEntity<>(jobService.addJob(jobReq), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<JobData>> getJobs(Optional<Integer> page, Optional<Integer> size, @RequestParam(defaultValue = "newest") String sortBy) {
        log.info("getJobs");
        Page<JobData> jobs = jobService.getJobs(page, size, sortBy);
        log.info("Fetched jobs: {}", jobs.getContent());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobData> getJob(@PathVariable String id) {
        log.info("getJob {}", id);
        return ResponseEntity.ok(jobService.getJob(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobData> updateJob(@PathVariable String id, @RequestBody JobReq jobReq) {
        log.info("updateJob");
        return ResponseEntity.ok(jobService.updateJob(id,jobReq));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        log.info("deleteJob {}", id);
        jobService.deleteJob(id);
        return ResponseEntity.ok().body("Deleted Successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobData>> searchJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String jobStatus,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") Optional<Integer> page,
            @RequestParam(defaultValue = "15") Optional<Integer> size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        
        log.info("Search params - search: {}, jobStatus: {}, jobType: {}, sortBy: {}", 
            search, jobStatus, jobType, sortBy);
            
        Page<JobData> jobs = jobService.searchJobsByLetters(search, jobStatus, jobType, page, size, sortBy);
        log.info("Found {} jobs", jobs.getTotalElements());
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<JobStatsDTO> getJobStats(@PathVariable String userId) {
        log.info("Getting job stats for user: {}", userId);
        JobStatsDTO stats = jobService.getJobStats(userId);
        return ResponseEntity.ok(stats);
    }
}
