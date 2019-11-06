package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import android.graphics.Color;

import java.util.ArrayList;

public class Chat {
    private String name;
    private Color color;
    private ArrayList<Message> messages;
    private int id;
    ArrayList<User> currUsers;
    private User admin;

    public Chat(String name, Color color, ArrayList<Message> messages, int id, ArrayList<User> currUsers, User admin) {
        this.name = name;
        this.color = color;
        this.messages = messages;
        this.id = id;
        this.currUsers = currUsers;
        this.admin = admin;
    }
    public Chat(String name, Color color, ArrayList<Message> messages, ArrayList<User> currUsers, User admin) {
        this.name = name;
        this.color = color;
        this.messages = messages;
        this.id = 0;
        this.currUsers = currUsers;
        this.admin = admin;
    }

    public void update(){

    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public int getId() {
        return id;
    }

    public ArrayList<User> getCurrUsers() {
        return currUsers;
    }

    public User getAdmin() {
        return admin;
    }
}
