package edu.sabanciuniv.hotelbookingapp.config;

import edu.sabanciuniv.hotelbookingapp.observer.EmailNotificationObserver;
import edu.sabanciuniv.hotelbookingapp.service.NotificationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NotificationConfig {
    
    private final NotificationService notificationService;
    private final EmailNotificationObserver emailNotificationObserver;
    
    @PostConstruct
    public void init() {
        // Register the email notification observer
        notificationService.registerObserver(emailNotificationObserver);
    }
} 