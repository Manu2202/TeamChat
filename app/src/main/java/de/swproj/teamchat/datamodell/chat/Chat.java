package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import android.content.res.Resources;

import java.util.Random;

import de.swproj.teamchat.R;

public class Chat {
    private String name;
    private int color;
    private String id;
    private String admin;

    public Chat(String name, int color, String id, String admin) {
        this.name = name;
        this.color = color;
        this.id = id;
        this.admin = admin;

    }

    public Chat(String name, String admin, int color) {
        this.name = name;
        this.color = color;
        this.id = "0";
        this.admin = admin;
    }



    public void setId(String id) {
        this.id = id;
    }

    public void update() {
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

}
