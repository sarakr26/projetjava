package edu.sabanciuniv.hotelbookingapp.observer;

public interface NotificationSubject {
    void registerObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(String message, String recipientEmail);
} 