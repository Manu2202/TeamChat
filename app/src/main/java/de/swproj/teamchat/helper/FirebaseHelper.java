package de.swproj.teamchat.helper;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import java.util.ArrayList;
import java.util.HashMap;

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

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
        eventMap.put("Status", (int)event.getStatus());

        return eventMap;
    }

    public static HashMap<String, Object> convertToMap(Chat chat){
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("Name", chat.getName());
        chatMap.put("Color", chat.getColor());
        chatMap.put("Admin", chat.getAdmin());

        return chatMap;
    }

    public static HashMap<String, Object> convertToMap(User user){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("AccountName",user.getAccountName());
        userMap.put("UID",user.getGoogleId());
        userMap.put("Email",user.getGoogleMail());

        return userMap;
    }
}
