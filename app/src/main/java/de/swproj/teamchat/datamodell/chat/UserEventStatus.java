package de.swproj.teamchat.datamodell.chat;

public class UserEventStatus {
    private String userId;
    private int eventId;
    private int statusId;
    private byte status;
    private String reason;

    public UserEventStatus(int statusId,String userId, int messageId, byte status, String reason) {
        this.userId = userId;
        this.eventId = messageId;
        this.status = status;
        this.reason = reason;
        this.statusId=statusId;
    }

    public int getStatusId() {
        return statusId;
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

    public String getStatusString(){
        switch (status) {
            case 1:
                return "comited";
            case 2:
                return "cancled";
        }
        return "-";
    }
    public void setStatus(byte status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
