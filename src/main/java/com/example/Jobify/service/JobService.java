package com.example.Jobify.service;

import com.example.Jobify.DTO.JobReq;
import com.example.Jobify.DTO.JobStatusDTO;
import com.example.Jobify.DTO.JobStatsDTO;
import com.example.Jobify.Repo.JobRepository;
import com.example.Jobify.Repo.UserRepository;
import com.example.Jobify.model.JobData;
import com.example.Jobify.model.JobStatus;
import com.example.Jobify.model.JobType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JobService {
    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    @Autowired
    public JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    public JobData addJob(JobReq jobReq) {
        JobData jobData = new JobData();
        jobData.setCompany(jobReq.getCompany());
        jobData.setJobLocation(jobReq.getJobLocation());
        jobData.setPosition(jobReq.getPosition());
        jobData.setJobType(JobType.valueOf(jobReq.getJobType()));
        jobData.setJobStatus(JobStatus.valueOf(jobReq.getJobStatus()));
        jobData.setUserId(jobReq.getUserId());
        jobData.setCreatedAt(LocalDateTime.now());
        jobData.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(jobData);
    }

    public Page<JobData> getJobs(Optional<Integer> pageNumber, Optional<Integer> pageSize, String sortBy) {
        Sort sort = getSort(sortBy);
        int page = pageNumber.orElse(0);  // default to 0 if not present
        int size = pageSize.orElse(30);
        Pageable pageable = PageRequest.of(page, size, sort);
        return jobRepository.findAll(pageable);
    }

    private static Sort getSort(String sortBy) {
        Sort sort=switch (sortBy.toLowerCase())
        {
            case "company" -> Sort.by(Sort.Direction.ASC, "company");
            case "a-z" -> Sort.by(Sort.Direction.ASC, "position");
            case "z-a" -> Sort.by(Sort.Direction.DESC, "position");
            default -> Sort.by(Sort.Order.desc("createdAt"));
        };
        return sort;
    }

    public JobData getJob(String id) {
        JobData job=jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return job;
    }

    public JobData updateJob(String id, JobReq jobReq) {
        JobData existingJob=jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        existingJob.setJobLocation(jobReq.getJobLocation());
        existingJob.setPosition(jobReq.getPosition());
        existingJob.setJobType(JobType.valueOf(jobReq.getJobType()));
        existingJob.setJobStatus(JobStatus.valueOf(jobReq.getJobStatus()));
        existingJob.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(existingJob);
    }

    public void deleteJob(String id) {
        JobData job=jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
         if(job!=null) {
             jobRepository.delete(job);
         }
    }

    // Search for jobs by company, position, or job location using letters
    public Page<JobData> searchJobsByLetters(String search, String jobStatus, String jobType, Optional<Integer> pageNumber, Optional<Integer> pageSize, String sortBy) {
        Sort sort1 = getSort(sortBy);
        int page = pageNumber.orElse(0);
        int size = pageSize.orElse(20);
        Pageable pageable = PageRequest.of(page, size, sort1);

        log.info("Searching with filters - search: {}, jobStatus: {}, jobType: {}", search, jobStatus, jobType);

        // If no search term is provided, search with just filters
        if (search == null || search.trim().isEmpty()) {
            if (jobStatus != null && !jobStatus.equals("all")) {
                JobStatus status = JobStatus.valueOf(jobStatus.toUpperCase());
                if (jobType != null && !jobType.equals("all")) {
                    JobType type = JobType.valueOf(jobType.toUpperCase());
                    return jobRepository.findByJobStatusAndJobType(status, type, pageable);
                }
                return jobRepository.findByJobStatus(status, pageable);
            }
            if (jobType != null && !jobType.equals("all")) {
                JobType type = JobType.valueOf(jobType.toUpperCase());
                return jobRepository.findByJobType(type, pageable);
            }
            return jobRepository.findAll(pageable);
        }

        // Search with both search term and filters
        if (jobStatus != null && !jobStatus.equals("all")) {
            JobStatus status = JobStatus.valueOf(jobStatus.toUpperCase());
            if (jobType != null && !jobType.equals("all")) {
                JobType type = JobType.valueOf(jobType.toUpperCase());
                return jobRepository.findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobStatusAndJobType(
                    search, search, search, status, type, pageable);
            }
            return jobRepository.findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobStatus(
                search, search, search, status, pageable);
        }

        if (jobType != null && !jobType.equals("all")) {
            JobType type = JobType.valueOf(jobType.toUpperCase());
            return jobRepository.findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobType(
                search, search, search, type, pageable);
        }

        return jobRepository.findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCase(
            search, search, search, pageable);
    }


    //DashBoard service
    public JobStatusDTO jobStatus(){
        JobStatusDTO jobStatusDTO=new JobStatusDTO();
        jobStatusDTO.setTotalJobs(jobRepository.count());
        jobStatusDTO.setUsers(userRepository.count());
        return jobStatusDTO;
    }

    public JobStatsDTO getJobStats(String userId) {
        JobStatsDTO statsDTO = new JobStatsDTO();
        JobStatsDTO.DefaultStats defaultStats = new JobStatsDTO.DefaultStats();

        // Get counts for each status
        defaultStats.setPending(jobRepository.countByUserIdAndJobStatus(userId, JobStatus.PENDING));
        defaultStats.setInterview(jobRepository.countByUserIdAndJobStatus(userId, JobStatus.ACCEPTED));
        defaultStats.setDeclined(jobRepository.countByUserIdAndJobStatus(userId, JobStatus.DECLINED));
        statsDTO.setDefaultStats(defaultStats);

        // Get monthly applications
        List<JobStatsDTO.MonthlyApplication> monthlyApplications = jobRepository.findMonthlyApplicationsByUserId(userId)
            .stream()
            .map(monthly -> {
                JobStatsDTO.MonthlyApplication app = new JobStatsDTO.MonthlyApplication();
                app.setDate(monthly.getDate());
                app.setCount(monthly.getCount());
                return app;
            })
            .collect(Collectors.toList());
        statsDTO.setMonthlyApplications(monthlyApplications);

        return statsDTO;
    }
}
