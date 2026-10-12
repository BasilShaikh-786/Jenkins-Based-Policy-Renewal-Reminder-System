package com.policy.renewal.service.notification;

import com.policy.renewal.model.ReminderRecord;

/**
 * NotificationResult — value object encapsulating the outcome of a notification attempt.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class NotificationResult {

    private final boolean success;
    private final String status;
    private final String channel;
    private final String message;
    private final String errorMessage;

    public NotificationResult(boolean success, String status, String channel, String message, String errorMessage) {
        this.success = success;
        this.status = status;
        this.channel = channel;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public static NotificationResult simulated(String message) {
        return new NotificationResult(
                true,
                ReminderRecord.STATUS_SIMULATED,
                ReminderRecord.CHANNEL_SIMULATION,
                message,
                null
        );
    }

    public static NotificationResult sent(String message) {
        return new NotificationResult(
                true,
                ReminderRecord.STATUS_SENT,
                ReminderRecord.CHANNEL_EMAIL,
                message,
                null
        );
    }

    public static NotificationResult failure(String channel, String errorMessage) {
        return new NotificationResult(
                false,
                ReminderRecord.STATUS_FAILED,
                channel,
                null,
                errorMessage
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "NotificationResult{" +
                "success=" + success +
                ", status='" + status + '\'' +
                ", channel='" + channel + '\'' +
                ", message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
