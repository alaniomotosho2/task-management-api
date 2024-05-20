package org.niyo.constant;

public enum TaskPriority {
    HIGH,
    MEDIUM,
    LOW;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        try {
            TaskPriority.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

