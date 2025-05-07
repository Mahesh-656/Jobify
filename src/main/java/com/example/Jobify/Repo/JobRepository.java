package com.example.Jobify.Repo;

import com.example.Jobify.model.JobData;
import com.example.Jobify.model.JobStatus;
import com.example.Jobify.model.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import java.util.List;

public interface JobRepository extends MongoRepository<JobData, String> {

    Page<JobData> findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCase(String company, String position, String location, Pageable pageable);

    Page<JobData> findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobStatus(
        String company, String position, String location, JobStatus jobStatus, Pageable pageable);

    Page<JobData> findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobType(
        String company, String position, String location, JobType jobType, Pageable pageable);

    Page<JobData> findByCompanyContainingIgnoreCaseOrPositionContainingIgnoreCaseOrJobLocationContainingIgnoreCaseAndJobStatusAndJobType(
        String company, String position, String location, JobStatus jobStatus, JobType jobType, Pageable pageable);

    Page<JobData> findByJobStatus(JobStatus jobStatus, Pageable pageable);
    Page<JobData> findByJobType(JobType jobType, Pageable pageable);
    Page<JobData> findByJobStatusAndJobType(JobStatus jobStatus, JobType jobType, Pageable pageable);

    long countByJobStatus(JobStatus jobStatus);

    long countByJobType(JobType jobType);

    long countByJobLocation(String location);

    // New methods for stats
    long countByUserIdAndJobStatus(String userId, JobStatus jobStatus);

    @Aggregation(pipeline = {
        "{ $match: { userId: ?0 } }",
        "{ $group: { _id: { year: { $year: '$createdAt' }, month: { $month: '$createdAt' } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id.year': -1, '_id.month': -1 } }",
        "{ $limit: 6 }",
        "{ $project: { _id: 0, date: { $concat: [{ $toString: { $month: { $dateFromParts: { year: '$_id.year', month: '$_id.month', day: 1 } } } }, ' ', { $toString: { $year: { $dateFromParts: { year: '$_id.year', month: '$_id.month', day: 1 } } } }] }, count: 1 } }"
    })
    List<MonthlyApplication> findMonthlyApplicationsByUserId(String userId);

    interface MonthlyApplication {
        String getDate();
        long getCount();
    }
}
