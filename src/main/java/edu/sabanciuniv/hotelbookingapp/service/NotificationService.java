package edu.sabanciuniv.hotelbookingapp.service;

import edu.sabanciuniv.hotelbookingapp.observer.NotificationObserver;
import edu.sabanciuniv.hotelbookingapp.observer.NotificationSubject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationService implements NotificationSubject {
    
    private final List<NotificationObserver> observers = new ArrayList<>();
    
    @Override
    public void registerObserver(NotificationObserver observer) {
        observers.add(observer);
        log.info("Registered new notification observer");
    }
    
    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
        log.info("Removed notification observer");
    }
    
    @Override
    public void notifyObservers(String message, String recipientEmail) {
        log.info("Notifying observers about: {}", message);
        for (NotificationObserver observer : observers) {
            observer.update(message, recipientEmail);
        }
    }
    
    public void notifyBookingConfirmation(String recipientEmail, String bookingId) {
        String message = String.format("Your booking (ID: %s) has been confirmed. Thank you for choosing our service!", bookingId);
        notifyObservers(message, recipientEmail);
    }
    
    public void notifyNewAccount(String recipientEmail, String username) {
        String message = String.format(
            "Bienvenue %s!\n\n" +
            "Votre compte a été créé avec succès sur notre plateforme de réservation d'hôtel.\n\n" +
            "Vous pouvez maintenant :\n" +
            "- Effectuer des réservations\n" +
            "- Gérer votre profil\n" +
            "- Consulter l'historique de vos réservations\n\n" +
            "Si vous avez des questions, n'hésitez pas à nous contacter.\n\n" +
            "Cordialement,\n" +
            "L'équipe de réservation d'hôtel",
            username
        );
        notifyObservers(message, recipientEmail);
    }
} 