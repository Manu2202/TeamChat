package de.swproj.teamchat.datamodell.chat;

import java.util.Collection;
import java.util.List;

public class ChatMembers {
   private Collection<String> userIDs;
   private String chatID;

    public ChatMembers(Collection<String> userIDs, String chatID) {
        this.userIDs = userIDs;
        this.chatID = chatID;
    }

    public Collection<String> getUserIDs() {
        return userIDs;
    }

    public String getChatID() {
        return chatID;
    }

}
