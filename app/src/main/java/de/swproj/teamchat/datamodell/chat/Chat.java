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
    private int id;
    private String admin;

    public Chat(int id, String name, Color color, String admin) {
        this.name = name;
        this.color = color;
        this.id = id;
        this.admin = admin;
    }

    public Chat(int id, String name, String admin) {
        this.name = name;
        this.color=null;
        this.id = id;
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



    public int getId() {
        return id;
    }



    public String getAdmin() {
        return admin;
    }
}
