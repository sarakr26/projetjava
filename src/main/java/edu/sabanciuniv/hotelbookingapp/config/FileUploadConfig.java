package edu.sabanciuniv.hotelbookingapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("Created upload directory: {}", uploadDir);
            }
        } catch (IOException e) {
            log.error("Could not create upload directory!", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
        String uploadAbsolutePath = "file:" + uploadDir.toString() + "/";
        
        // Add both patterns to ensure files are served correctly
        registry.addResourceHandler("/static/uploads/**", "/uploads/**")
                .addResourceLocations("classpath:/static/uploads/", uploadAbsolutePath);
        
        log.info("Configured resource handler for uploads at: {}", uploadAbsolutePath);
    }
} 