package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;

public abstract class Message {

    private Time timeStamp;
    private String message;
    private int id;
    private boolean isEvent;
    private User creator;
    private int chatid;

    public Message(Time timeStamp, String message, int id, boolean isEvent, User creator,int chatid) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.isEvent = isEvent;
        this.creator = creator;
        this.chatid=chatid;
    }
    public Message(Time timeStamp, String message, boolean isEvent, User creator) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = 0;
        this.isEvent = isEvent;
        this.creator = creator;

    }

    public int getChatid() {
        return chatid;
    }

    public Time getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public User getCreator() {
        return creator;
    }
}
