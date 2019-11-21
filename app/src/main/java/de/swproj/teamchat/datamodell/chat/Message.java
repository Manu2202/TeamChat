package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;

public class Message {

    private Time timeStamp;
    private String message;
    private int id;
    private boolean isEvent;
    private String creator;
    private int chatid;

    public Message(Time timeStamp, String message, int id, boolean isEvent, String creatorID,int chatID) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.isEvent = isEvent;
        this.creator = creatorID;
        this.chatid=chatID;
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

    public String getCreator() {
        return creator;
    }
}
