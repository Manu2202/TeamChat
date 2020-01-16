package de.swproj.teamchat.helper;

import java.util.HashMap;
import java.util.List;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

public class FirebaseHelper {

public static HashMap<String,String> convertToMap(Message message, int type, int action){
    HashMap<String,String> retMap= new HashMap<>();
    if(message.isEvent()){
        Event event = (Event)message;
        retMap.put("date", event.getDateString());
        retMap.put("description",event.getDescription());
        retMap.put("status",String.valueOf(event.getStatus()));
    }
    retMap.put("title",message.getMessage());
    retMap.put("chatid",message.getChatid());
    retMap.put("type",String.valueOf(type));
    retMap.put("action",String.valueOf(action));
    retMap.put("creator",message.getCreator());
    retMap.put("message",message.getMessage());
    retMap.put("isEvent",((Boolean)message.isEvent()).toString());
    retMap.put("timestamp", message.getTimeStampString());
    return retMap;
}
    public static HashMap<String,Object> convertToMap(Chat chat, int type, int action, List<String> users){
        HashMap<String,Object> retMap= new HashMap<>();
        retMap.put("admin",chat.getAdmin());
        retMap.put("id",chat.getId());
        retMap.put("name",chat.getName());
        retMap.put("color",String.valueOf(chat.getColor()));
        retMap.put("notification","You got invited to be part of "+chat.getName());
        retMap.put("type",String.valueOf(type));
        retMap.put("action",String.valueOf(action));
        StringBuilder builder = new StringBuilder();
        for (String user: users) {
            builder.append(user+";");
        }
        retMap.put("usersstring", builder.substring(0,builder.length() - 1));
        retMap.put("users",users);
        return retMap;
    }

    public static HashMap<String,Object> convertToMap(UserEventStatus status, int type, int action){
        HashMap<String,Object> retMap= new HashMap<>();
        retMap.put("eventid",status.getEventId());
        retMap.put("reason",status.getReason());
        retMap.put("status",status.getStatus());
        retMap.put("userid",status.getUserId());
        Event event = DBStatements.getEvent(status.getEventId());
        retMap.put("chatid",event.getChatid());
        retMap.put("eventname",event.getMessage());
        retMap.put("notification","User"+ DBStatements.getUser(status.getUserId()).getAccountName()+ " "+status.getStatusString());
        retMap.put("type",String.valueOf(type));
        retMap.put("action",String.valueOf(action));
        return retMap;
    }
}
