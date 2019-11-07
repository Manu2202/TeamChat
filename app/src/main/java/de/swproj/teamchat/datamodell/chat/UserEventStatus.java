package de.swproj.teamchat.datamodell.chat;

public class UserEventStatus {
    private String userId;
    private int eventId;
    private byte status;
    private String reason;

    public UserEventStatus(String userId, int messageId, byte status, String reason) {
        this.userId = userId;
        this.eventId = messageId;
        this.status = status;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public byte getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
