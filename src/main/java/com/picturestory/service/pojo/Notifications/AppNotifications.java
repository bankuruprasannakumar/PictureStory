package com.picturestory.service.pojo.Notifications;

/**
 * Created by bankuru on 23/9/16.
 */
public enum AppNotifications {

    //contributor of that content should receive
    USER_CREATED_PIXTORY(0),
    //user whose templated are unlocked should receive
    USER_TEMPLATES_UNLOCKED(1),
    //user who commented
    USER_LIKED_COMMENT(2),
    //trending pixtory
    TRENDING_PIXTORY(3);

    private int value;

    private AppNotifications(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}