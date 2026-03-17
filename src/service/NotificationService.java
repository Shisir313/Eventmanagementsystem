package service;

/**
 * Simple notification service stub. In a real system this would send emails/SMS.
 */
public class NotificationService {

    /**
     * Send a notification (console output for demo).
     */
    public void sendNotification(String message) {
        System.out.println("[Notification] " + message);
    }
}
