package de.swproj.teamchat.helper;

import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;

public class FirebaseHelper {

    /*
     * Static Helper Method to put all Data in Strings
     * -> need it to send the class via Firebase (only accepting Strings)
     */
    public static HashMap<String, String> convertToMap(Message message) {
        HashMap<String, String> retMap = new HashMap<>();
        if (message.isEvent()) {
            Event event = (Event) message;
            retMap.put("date", FormatHelper.formatDate(event.getDate()));
            retMap.put("description", event.getDescription());
            retMap.put("status", String.valueOf(event.getStatus()));
        }
        retMap.put("chatid", message.getChatid());
        retMap.put("creator", message.getCreator());
        retMap.put("message", message.getMessage());
        retMap.put("isEvent", ((Boolean) message.isEvent()).toString());
        retMap.put("timestamp", FormatHelper.formatTime(message.getTimeStamp()));
        return retMap;
    }
}
