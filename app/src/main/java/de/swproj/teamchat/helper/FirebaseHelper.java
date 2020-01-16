package de.swproj.teamchat.helper;

import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;

public class FirebaseHelper {

public static HashMap<String,String> convertToMap(Message message, String title,
                                                  boolean isInvite, boolean isEventUpdate){
    HashMap<String,String> retMap= new HashMap<>();
    if(message.isEvent()){
        Event event = (Event)message;
        retMap.put("date", event.getDateString());
        retMap.put("description",event.getDescription());
        retMap.put("status",String.valueOf(event.getStatus()));
    }
    retMap.put("chatid",message.getChatid());
    retMap.put("isInvite", Boolean.toString(isInvite));
    retMap.put("isEventUpdate", Boolean.toString(isEventUpdate));
    retMap.put("creator",message.getCreator());
    retMap.put("title",title);
    retMap.put("message",message.getMessage());
    retMap.put("isEvent",((Boolean)message.isEvent()).toString());
    retMap.put("timestamp", message.getTimeStampString());
    return retMap;
}
}
