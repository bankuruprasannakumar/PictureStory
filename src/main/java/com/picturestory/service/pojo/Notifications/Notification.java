package com.picturestory.service.pojo.Notifications;

/**
 * Created by bankuru on 23/9/16.
 */
public class Notification {
    private String notificationMessage;
    private int notificationId;
    private int notificationType;
    private long time;

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }
}
