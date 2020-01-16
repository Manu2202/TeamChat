package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import androidx.annotation.Nullable;
import de.swproj.teamchat.connection.database.DBStatements;

public class Chat implements Comparable<Chat> {
    private String id;
    private String name;
    private String admin;
    private int color;


    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Chat c = (Chat) obj;
        if(c.getId().equals(id)&&c.getAdmin().equals(admin)&&c.getName().equals(name)&&c.getColor()==color)
            return true;
        return false;
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


    @Override
    public int compareTo(Chat c) {
                      try {
                          return DBStatements.getLastMessage(id).getTimeStampDate()
                                  .compareTo(DBStatements.getLastMessage(c.getId()).getTimeStampDate());
                      }catch (NullPointerException e){
                          return 0;
                      }

    }

}
