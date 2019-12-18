package de.swproj.teamchat.datamodell.chat;

public class UserEventStatus {
    private String userId;
    private String eventId;
    private int statusId;
    private int status;
    private String reason;

    public UserEventStatus(int statusId, String userId, String messageId, int status, String reason) {
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

    public String getEventId() {
        return eventId;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getStatusString(){
        switch (status) {
            case 1:
                return "committed";
            case 2:
                return "cancelled";
        }
        return "-";
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
