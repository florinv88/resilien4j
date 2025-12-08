package com.fnkcode.sla;

public class SlaContext {
    private static final ThreadLocal<Long> DEADLINE = new ThreadLocal<>();

    public static void setDeadline(long deadlineMillis) {
        DEADLINE.set(deadlineMillis);
    }

    public static Long getDeadline() {
        return DEADLINE.get();
    }

    public static void clear() {
        DEADLINE.remove();
    }

    public static boolean hasTimeLeft() {
        Long deadline = DEADLINE.get();
        if (deadline == null) return true;

        return System.currentTimeMillis() < deadline;
    }
}