package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import android.util.Log;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.Nullable;

public class Event extends Message implements Comparable<Event> {

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


    public Event(String timeStamp, String message, String id, boolean isEvent, String creator,
                 String date, String description, String chatid, int status) {
        super(timeStamp, message, id, isEvent, creator, chatid);

        this.description = description;
        this.status = status;
        StringToDate(date);
    }

    private void StringToDate(String s){
        date = new GregorianCalendar();
        String[] strings = s.split(";");
        try {
            date.set(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[2]),Integer.parseInt(strings[3]),Integer.parseInt(strings[4]));
        }catch (Exception e){
            Log.e("EventError","Unable to Pharse Date out of the DateString");
        }


    }
    public void setDateString(String s){
        StringToDate(s);
    }

    public String getDateString(){
        return   date.get(Calendar.YEAR)+";"+ date.get(Calendar.MONTH)+";"+ date.get(Calendar.DAY_OF_MONTH)+";"+ date.get(Calendar.HOUR_OF_DAY)+";"+date.get(Calendar.MINUTE);
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
        if(super.equals(obj)&& e.getDate().getTime().getTime()==date.getTime().getTime()&&e.getDescription().equals(description))
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

    public String getStatusString(){
        if(status==0){
            return "will take place";
        }else if(status==1){
            return "expired";
        }
        // status == 2
            return "called off";
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @Override
    public int compareTo(Event e) {
        return getDate().compareTo(e.getDate());
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
