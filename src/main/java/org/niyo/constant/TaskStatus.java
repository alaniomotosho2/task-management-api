package org.niyo.constant;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    CANCELED,
    ON_HOLD;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        try {
            TaskStatus.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
