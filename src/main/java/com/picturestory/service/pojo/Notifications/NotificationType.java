package com.picturestory.service.pojo.Notifications;

/**
 * Created by bankuru on 23/9/16.
 */
public enum NotificationType {

    WEB_NOTIFICATIONS(0), APP_NOTIFICATIONS(1), MIXED_NOTIFICATIONS(2);

    private int value;

    private NotificationType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}