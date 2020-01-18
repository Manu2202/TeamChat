package de.swproj.teamchat.datamodell.chat;

import androidx.annotation.Nullable;

public class UserEventStatus {
    private String userId;
    private String eventId;
    private int status;
    private String reason;

    public UserEventStatus(String userId, String messageId, int status, String reason) {
        this.userId = userId;
        this.eventId = messageId;
        this.status = status;
        this.reason = reason;

    }


    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserEventStatus u = (UserEventStatus) obj;
        if(u.getUserId().equals(userId ) && u.getEventId().equals(eventId)&& u.getStatus()==status&& u.getReason().equals(reason))
            return true;
        return false;
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
