package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;
import java.util.Date;


import androidx.annotation.Nullable;

public class Message {

    private String id;
    private String creator;
    private String chatid;
    private String message;
    private Date timeStamp;
    private boolean isEvent;

    public Message(Date timeStamp, String message, String id, boolean isEvent, String creator, String chatID) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = id;
        this.isEvent = isEvent;
        this.creator = creator;
        this.chatid=chatID;
    }

    public Message(Date timeStamp, String message, boolean isEvent, String creator, String chatID) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.id = null;
        this.isEvent = isEvent;
        this.creator = creator;
        this.chatid=chatID;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message m = (Message) obj;
        if(m.getId().equals(id)&&
                m.getTimeStamp().toString().equals(timeStamp.toString())&&
                m.getMessage().equals(message) &&
                m.getCreator().equals(creator))
            return true;
        return false;
    }

    public String getChatid() {
        return chatid;
    }

    public Time getTimeStamp() {
        return new Time(timeStamp.getTime());
    }

    public Date getTmeStamp_Date() {
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

