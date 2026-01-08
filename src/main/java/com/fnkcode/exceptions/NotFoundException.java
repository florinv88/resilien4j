package com.fnkcode.exceptions;

public class NotFoundException extends RuntimeException {

    private String reason;

    public NotFoundException(String message, String reason) {
        super(message);
        setReason(reason);
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
