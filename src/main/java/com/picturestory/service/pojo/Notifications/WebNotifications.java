package com.picturestory.service.pojo.Notifications;

/**
 * Created by bankuru on 23/9/16.
 */
public enum WebNotifications {
    CONTENT_EDITED(0), CONTENT_REJECTED(1);

    private int value;

    private WebNotifications(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
