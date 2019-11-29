package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

// Dies ist ein Test Kommentar

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

    public Chat(String name, String admin) {
        this.name = name;
        this.color = 000000;
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
