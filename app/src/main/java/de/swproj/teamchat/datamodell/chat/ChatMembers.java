package de.swproj.teamchat.datamodell.chat;

public class ChatMembers {
   private String[] userIDs;
   private String chatID;

    public ChatMembers(String[] userIDs, String chatID) {
        this.userIDs = userIDs;
        this.chatID = chatID;
    }

    public String[] getUserIDs() {
        return userIDs;
    }

    public String getChatID() {
        return chatID;
    }

}
