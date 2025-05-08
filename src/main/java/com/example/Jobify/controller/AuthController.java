package com.example.Jobify.controller;


import com.example.Jobify.DTO.LoginRequest;
import com.example.Jobify.DTO.RegisterRequest;
import com.example.Jobify.model.UserData;
import com.example.Jobify.security.JwtUtil;
import com.example.Jobify.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
//@CrossOrigin(origins = "https://jobifyclient-production.up.railway.app")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest registerRequest) {

        return ResponseEntity.ok().body(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserData userData = authService.login(loginRequest);
        String token = jwtUtil.generateToken(userData.getEmail());
        Cookie cookie=new Cookie("JWT",token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600);
        cookie.setSecure(true);

        response.addCookie(cookie);


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Login Successfully");
        return ResponseEntity.ok(responseMap);

    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }
}
