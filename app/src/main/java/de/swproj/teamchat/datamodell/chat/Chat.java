package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */



import android.graphics.Color;

import java.util.ArrayList;

public class Chat {
    private String name;
    private int color;
    private int id;
    private String admin;

    public Chat(int id, String name, int color, String admin) {
        this.name = name;
        this.color = color;
        this.id = id;
        this.admin = admin;
    }




    public void update(){

    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }


    public int getId() {
        return id;
    }



    public String getAdmin() {
        return admin;
    }
}
