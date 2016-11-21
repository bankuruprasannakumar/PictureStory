package com.picturestory.service.pojo.Notifications;

/**
 * Created by bankuru on 23/9/16.
 */
public enum MixedNotifications {
    //contributor recives noti..
    USER_LIKED_CONTENT(0),
    //contributor receives noti...
    USER_COMMENTED_COMMENT(1),
    //contributor receives noti..
    COMMENT_IN_APP(2);

    private int value;

    private MixedNotifications(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
