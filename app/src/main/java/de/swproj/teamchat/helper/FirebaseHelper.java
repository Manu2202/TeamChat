package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Event;

public class FirebaseHelper {

    public static HashMap<String, Object> convertToMap(Event event){
        HashMap<String, Object> eventMap = new HashMap<>();
        eventMap.put("Timestamp", event.getTimeStamp());
        eventMap.put("Titel", event.getMessage());
        eventMap.put("MessageID", event.getId());
        eventMap.put("IsEvent", event.isEvent());
        eventMap.put("CreatorID", event.getCreator());
        eventMap.put("Date", event.getDate());
        eventMap.put("Description", event.getDescription());
        eventMap.put("ChatID", event.getChatid());
        eventMap.put("Status", event.getStatus());

        return eventMap;
    }
}
