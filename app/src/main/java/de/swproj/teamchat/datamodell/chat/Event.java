package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.sql.Time;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;

public class Event extends Message {

    private GregorianCalendar date;
    private String description;
    private int status;

    public Event(Date timeStamp, String message, String id, boolean isEvent, String creator,
                 GregorianCalendar date, String description, String chatid, int status) {
        super(timeStamp, message, id, isEvent, creator, chatid);
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public Event(Date timeStamp, String message, boolean isEvent, String creator, GregorianCalendar date,
                 String description, String chatid, int status) {
        super(timeStamp, message, isEvent, creator, chatid);
        this.date = date;
        this.description = description;
        this.status = status;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Event e = (Event) obj;
        if(super.equals(obj)&& e.getDate().toString().equals(date.toString())&&e.getDescription().equals(description))
            return true;
        return false;
    }

    @Override
    public String getChatid() { return super.getChatid(); }

    @Override
    public Time getTimeStamp() {
        return super.getTimeStamp();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public boolean isEvent() {
        return super.isEvent();
    }

    @Override
    public String getCreator() {
        return super.getCreator();
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }
}
