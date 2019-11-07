package de.swproj.teamchat.datamodell.chat;

public class UserEventStatus {
    private String userId;
    private String messageId;
    private byte status;
    private String reason;

    public UserEventStatus(String userId, String messageId, byte status, String reason) {
        this.userId = userId;
        this.messageId = messageId;
        this.status = status;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public byte getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }
}
