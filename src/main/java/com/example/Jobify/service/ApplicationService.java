package com.example.Jobify.service;

import com.example.Jobify.DTO.Application;
import com.example.Jobify.Repo.ApplicationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepo applicationRepository;
    public List<Application> getApplicationsByUserId(String userId) {
        return applicationRepository.findByUserId(userId);
    }

}
