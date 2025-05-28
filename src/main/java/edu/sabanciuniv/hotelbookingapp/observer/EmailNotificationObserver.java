package edu.sabanciuniv.hotelbookingapp.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationObserver implements NotificationObserver {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void update(String message, String recipientEmail) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("krichi.2003.sara@gmail.com");
            mailMessage.setTo(recipientEmail);
            
            // Déterminer le sujet en fonction du contenu du message
            if (message.contains("Bienvenue")) {
                mailMessage.setSubject("Confirmation de création de compte - Plateforme de réservation d'hôtel");
            } else if (message.contains("booking")) {
                mailMessage.setSubject("Confirmation de réservation - Plateforme de réservation d'hôtel");
            } else {
                mailMessage.setSubject("Notification - Plateforme de réservation d'hôtel");
            }
            
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            log.info("Email envoyé avec succès à : {}", recipientEmail);
        } catch (Exception e) {
            log.error("Échec de l'envoi de l'email à {} : {}", recipientEmail, e.getMessage(), e);
        }
    }
} 