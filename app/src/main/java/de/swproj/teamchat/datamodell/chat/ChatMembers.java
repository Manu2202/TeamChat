package de.swproj.teamchat.datamodell.chat;

import java.util.List;

public class ChatMembers {
   private List<String> userIDs;
   private String chatID;

    public ChatMembers(List<String> userIDs, String chatID) {
        this.userIDs = userIDs;
        this.chatID = chatID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public String getChatID() {
        return chatID;
    }

}
