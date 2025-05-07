package com.example.Jobify.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Jobify.DTO.LoginRequest;
import com.example.Jobify.DTO.RegisterRequest;
import com.example.Jobify.Repo.UserRepository;
import com.example.Jobify.model.Role;
import com.example.Jobify.model.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // User Registration
    public UserData register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadCredentialsException("Email already in use");
        }

        Role role = userRepository.count() == 0 ? Role.ADMIN : Role.USER;
        UserData userData = new UserData();
        userData.setEmail(registerRequest.getEmail());
        userData.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userData.setName(registerRequest.getName());
        userData.setLastName(registerRequest.getLastName());
        userData.setLocation(registerRequest.getLocation());
        userData.setRole(role);
        userData.setUrl(" ");
        userData.setPublicId(" ");
        return userRepository.save(userData);
    }

    // User Login
    public UserData login(LoginRequest loginRequest) {
        UserData userData = userRepository.findByEmail(loginRequest.getEmail());
        if (userData == null) {
            throw new UsernameNotFoundException("Email does not exist");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword()
        ));

        if (!passwordEncoder.matches(loginRequest.getPassword(), userData.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        return userData;
    }

    // User Profile Update
    public UserData updateUser(String email, String name, String lastName, String location, MultipartFile image) throws IOException {
        UserData userData = userRepository.findByEmail(email);
        if (userData == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        if (name != null) userData.setName(name);
        if (lastName != null) userData.setLastName(lastName);
        if (location != null) userData.setLocation(location);

        if (image != null && !image.isEmpty()) {
            Map<?, ?> upload = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            userData.setUrl(upload.get("secure_url").toString());
            userData.setPublicId(upload.get("public_id").toString());
        }

        return userRepository.save(userData);
    }

    public void deletePoster(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
    }
}
