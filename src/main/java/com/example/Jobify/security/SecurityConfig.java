package com.example.Jobify.security;
import com.example.Jobify.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {


   private final CustomUserDetailsService userDetailsService;
   private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Security Filter Chain configured");
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth->auth
                                .requestMatchers(
                                        "/",
                                        "/index.html",
                                        "/assets/**",
                                        "/static/**",
                                        "/*.js",
                                        "/*.css",
                                        "/*.ico",
                                        "/*.png",
                                        "/*.svg",
                                        "/manifest.json"

                                ).permitAll()
                                // Publicly accessible endpoints
                                .requestMatchers(
                                        "/api/v1/auth/**"
                                ).permitAll()
//                                .requestMatchers(HttpMethod.GET,"/api/v1/job/user/dashboard").hasRole("USER")
//                                .requestMatchers(HttpMethod.GET,"/api/v1/job/{id}").hasAnyRole("ADMIN", "USER")
//                                .requestMatchers(HttpMethod.PUT,"/api/v1/job/{id}").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.DELETE,"/api/v1/job/{id}").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.GET,"/api/v1/job/dashboard").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.GET,"/api/v1/job/search").hasAnyRole("ADMIN","USER")

                                // All other requests require authentication
                                .anyRequest().authenticated()
                )
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://jobifyclient-production.up.railway.app")
                        .allowedMethods("*")
                        .allowCredentials(true); // ✅ Important for cookies
            }
        };
    }


}
