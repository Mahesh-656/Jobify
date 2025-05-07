package com.example.Jobify.security;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(false);

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600)
                .resourceChain(false);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .mediaType("css", MediaType.valueOf("text/css"))
                .mediaType("js", MediaType.valueOf("application/javascript")) // application/javascript
                .mediaType("html", MediaType.valueOf("text/html")) // text/html
                .mediaType("json", MediaType.valueOf("application/json")) // application/json
                .mediaType("png", MediaType.valueOf("image/png")) // image/png
                .mediaType("svg", MediaType.valueOf("image/svg+xml")) // image/svg+xml
                .mediaType("ico", MediaType.valueOf("image/x-icon")) // image/x-icon
                .defaultContentType(MediaType.APPLICATION_JSON); // Default to application/json
    }
}
