package com.picturestory.service.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bankuru on 27/8/16.
 */
public enum PixtoryStatus {
        SUBMITTED(1), EDITING(2), EDITED(3), CONTENT_SET(4), APPROVED(5), INAPP(6);

        private int value;

        private PixtoryStatus(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
}
