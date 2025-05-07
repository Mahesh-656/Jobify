package com.example.Jobify.controller;

import com.example.Jobify.DTO.Application;
import com.example.Jobify.DTO.JobStatusDTO;
import com.example.Jobify.DTO.UsercurrentDTO;
import com.example.Jobify.Repo.UserRepository;
import com.example.Jobify.model.UserData;
import com.example.Jobify.service.ApplicationService;
import com.example.Jobify.service.AuthService;
import com.example.Jobify.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class CurrentUserController {

    private final UserRepository userRepository;
    private  final ApplicationService applicationService;
    private final JobService jobService;
    private final AuthService authService;

    public CurrentUserController(UserRepository userRepository, ApplicationService applicationService, JobService jobService, AuthService authService) {
        this.userRepository = userRepository;
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserData user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UsercurrentDTO usercurrentDTO = new UsercurrentDTO();
        usercurrentDTO.setId(user.getId());
        usercurrentDTO.setEmail(user.getEmail());
        usercurrentDTO.setName(user.getName());
        usercurrentDTO.setRole(user.getRole());
        usercurrentDTO.setLocation(user.getLocation());
        usercurrentDTO.setLastName(user.getLastName());
        usercurrentDTO.setUrl(user.getUrl());
        
        // Wrap in a map to match the expected structure
        Map<String, Object> response = new HashMap<>();
        response.put("user", usercurrentDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/dashboard")
    public ResponseEntity<List<Application>> getUserApplications(Authentication authentication) {
        String userId = authentication.getName(); // assuming you store userId in JWT
        List<Application> applications = applicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }
    @GetMapping("/admin/app-stats")
//    @Secured("ROLE_ADMIN")
    public ResponseEntity<JobStatusDTO> dashboard() {

        return ResponseEntity.ok(jobService.jobStatus());
    }

    @PutMapping("/update-user/{email}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String location,
            @RequestPart(required = false) MultipartFile image) throws IOException {

        return ResponseEntity.ok(authService.updateUser(email, name, lastName, location, image));
    }

}
