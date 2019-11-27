package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import java.util.HashSet;

public class Chat {
    private String name;
    private int color;
    private String id;
    private String admin;
    private HashSet<String> chatMember;

    public Chat(String name, int color, String id, String admin){
        this.name = name;
        this.color = color;
        this.id = id;
        this.admin = admin;

        // Initialize the chatMember Set
        chatMember = new HashSet<String>();
        // Add Admin as first member of the Chat
        chatMember.add(admin);
    }

    public Chat(String name, String admin) {
        this.name = name;
        this.color = 000000;
        this.id = "0";
        this.admin = admin;

        // Initialize the chatMember Set
        chatMember = new HashSet<String>();
        // Add Admin as first member of the Chat
        chatMember.add(admin);
    }


    public void setId(String id) {
        this.id = id;
    }

    public void update(){

    }

    public String getName() {
        return name;
    }


    public int getColor() {
        return color;
    }


    public String getId() {
        return id;
    }


    public String getAdmin() {
        return admin;
    }


    public HashSet<String> getChatMember() {
        return chatMember;
    }


    public void addChatMember(String userID){
        chatMember.add(userID);
    }

    public boolean isMemberOfChat(String userID){
        return chatMember.contains(userID);
    }
}
