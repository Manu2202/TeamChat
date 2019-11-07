package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;
import java.util.Date;

public class Event extends Message {

    private Date date;
    private String description;
    private Byte status;

    public Event(Time timeStamp, String message, int id, boolean isEvent, User creator, Date date,
                 String description,int chatid, Byte status) {
        super(timeStamp, message, id, isEvent, creator, chatid);
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public Event(Time timeStamp, String message, boolean isEvent, User creator, Date date,
                 String description, Byte status) {
        super(timeStamp, message, isEvent, creator);
        this.date = date;
        this.description = description;
        this.status = status;

    }

    @Override
    public int getChatid() { return super.getChatid(); }

    @Override
    public Time getTimeStamp() {
        return super.getTimeStamp();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public boolean isEvent() {
        return super.isEvent();
    }

    @Override
    public User getCreator() {
        return super.getCreator();
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
