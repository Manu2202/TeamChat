package de.swproj.teamchat.helper;

import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;

public class FirebaseHelper {

public static HashMap<String,String> convertToMap(Message message, String title, boolean isInvite){
    HashMap<String,String> retMap= new HashMap<>();
    if(message.isEvent()){
        Event event = (Event)message;
        retMap.put("date",FormatHelper.formatDate(event.getDate()));
        retMap.put("description",event.getDescription());
        retMap.put("status",String.valueOf(event.getStatus()));
    }
    retMap.put("chatid",message.getChatid());
    retMap.put("isInvite", ((Boolean)isInvite).toString());
    retMap.put("creator",message.getCreator());
    retMap.put("title",title);
    retMap.put("message",message.getMessage());
    retMap.put("isEvent",((Boolean)message.isEvent()).toString());
    retMap.put("timestamp",FormatHelper.formatTime(message.getTimeStamp()));
    return retMap;
}
}
