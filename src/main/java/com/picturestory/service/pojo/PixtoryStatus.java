package com.picturestory.service.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bankuru on 27/8/16.
 */
public enum PixtoryStatus {
        SUBMITTED(1), EDITING(2), REJECTED(3), INAPP(4);

        private int value;

        private PixtoryStatus(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
}
