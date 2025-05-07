package com.example.Jobify.Repo;

import com.example.Jobify.DTO.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepo extends MongoRepository<Application, String> {
    List<Application> findByUserId(String userId);


}
