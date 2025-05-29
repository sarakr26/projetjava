package edu.sabanciuniv.hotelbookingapp.service;

import edu.sabanciuniv.hotelbookingapp.model.Photo;
import edu.sabanciuniv.hotelbookingapp.model.Hotel;
import edu.sabanciuniv.hotelbookingapp.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class PhotoService {
    
    @Value("${upload.path}")
    private String uploadPath;
    
    private final PhotoRepository photoRepository;
    
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }
    
    public List<Photo> savePhotos(List<MultipartFile> photos, Hotel hotel) throws IOException {
        List<Photo> savedPhotos = new ArrayList<>();
        
        try {
            // Get absolute path
            Path absolutePath = Paths.get(uploadPath).toAbsolutePath();
            log.info("Using absolute upload path: {}", absolutePath);
            
            // Create base upload directory if it doesn't exist
            if (!Files.exists(absolutePath)) {
                Files.createDirectories(absolutePath);
                log.info("Created base upload directory: {}", absolutePath);
            }
            
            // Create hotel-specific directory
            Path hotelPath = absolutePath.resolve(String.valueOf(hotel.getId()));
            Files.createDirectories(hotelPath);
            log.info("Created hotel directory: {}", hotelPath);
            
            for (MultipartFile photo : photos) {
                if (photo != null && !photo.isEmpty()) {
                    try {
                        // Generate unique filename
                        String originalFilename = photo.getOriginalFilename();
                        if (originalFilename == null) {
                            log.warn("Received photo with null filename, skipping");
                            continue;
                        }
                        
                        String extension = StringUtils.getFilenameExtension(originalFilename);
                        if (extension == null) {
                            log.warn("No file extension found for file: {}, skipping", originalFilename);
                            continue;
                        }
                        
                        String newFilename = UUID.randomUUID().toString() + "." + extension;
                        
                        // Save file
                        Path filePath = hotelPath.resolve(newFilename);
                        Files.copy(photo.getInputStream(), filePath);
                        log.info("Saved photo: {}", filePath);
                        
                        // Create and save photo entity
                        Photo photoEntity = new Photo();
                        photoEntity.setHotel(hotel);
                        // Store relative path in database
                        String relativePath = "/static/uploads/hotels/" + hotel.getId() + "/" + newFilename;
                        photoEntity.setPhotoUrl(relativePath);
                        savedPhotos.add(photoRepository.save(photoEntity));
                        log.info("Saved photo entity to database: {}", photoEntity.getPhotoUrl());
                    } catch (Exception e) {
                        log.error("Error processing photo: {}", e.getMessage(), e);
                        throw new IOException("Failed to process photo: " + e.getMessage());
                    }
                }
            }
            
            return savedPhotos;
        } catch (Exception e) {
            log.error("Error in savePhotos: {}", e.getMessage(), e);
            throw new IOException("Failed to save photos: " + e.getMessage());
        }
    }
} 