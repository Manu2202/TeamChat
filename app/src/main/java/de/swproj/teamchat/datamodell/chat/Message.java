package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;
import java.sql.Timestamp;

public class Message {

    private Time timeStamp;
    private String message;
    private String id;
    private boolean isEvent;
    private String creator;
    private String chatid;

    public Message(Time timeStamp, String message, String id, boolean isEvent, String creator, String chatID) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.isEvent = isEvent;
        this.creator = creator;
        this.chatid=chatID;
    }

    public Message(Time timeStamp, String message, boolean isEvent, String creator, String chatID) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = null;
        this.isEvent = isEvent;
        this.creator = creator;
        this.chatid=chatID;
    }

    public String getChatid() {
        return chatid;
    }

    public Time getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public String getCreator() {
        return creator;
    }

    public void setId(String id) {
        this.id = id;
    }
}

