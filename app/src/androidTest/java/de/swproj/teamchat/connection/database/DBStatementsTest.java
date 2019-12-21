package de.swproj.teamchat.connection.database;

import android.content.Context;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class DBStatementsTest {
    DBStatements db;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Chat> chats = new ArrayList<>();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = new DBStatements(context);
        db.dropAll();
        for (int i=0;i<10;i++){
            User u = new User("hduwuf3ihu4nkn8u83hr"+i, "test"+i+"@mail.de", "TestAcc"+i, "Mustermann"+i, "Max"+i);
            users.add(u);
            db.insertUser(u);
        }
        for (int i=0;i<5;i++){
            Chat c =new Chat("TestChat"+i, i, "gf23uficfg37829"+i, users.get(i).getGoogleId());
            chats.add(c);
            db.insertChat(c);
        }
        for (int i=0;i<10;i++){
            String[] us = new String[users.size()];
            for (int j = 0; j<us.length;j++) {
                us[j]=users.get(j).getGoogleId();
            }
            db.updateChatMembers(us,chats.get(1).getId());

        }



    }


    @Test
    public void updateChat() {
    }

    @Test
    public void insertChat() {
       Chat c = chats.get(0);
        db.insertChat(c);
        Chat c2 = db.getChat(c.getId());

        assertThat(c, equalTo(c2));
    }

    @Test
    public void updateChatMembers() {
        String[] userList = new String[]{users.get(0).getGoogleId(),users.get(2).getGoogleId(),users.get(1).getGoogleId()};
        db.updateChatMembers(userList,chats.get(0).getId());
        ArrayList<String> members =db.getChatMembers(chats.get(0).getId());
        assertThat(members.get(0), equalTo(userList[0]));
        assertThat(members.get(1), equalTo(userList[1]));
        assertThat(members.get(2), equalTo(userList[2]));
    }

    @Test
    public void insertMessage() {
        Time time = new Time(System.currentTimeMillis());
        Message m = new Message(time, "Test Nachricht 1", "ghdfghdtrgfx", false, users.get(1).getGoogleId(), chats.get(1).getId());
        db.insertMessage(m);
        Message m2 = db.getMessage(m.getId());
        assertThat(m, equalTo(m2));




        time = new Time(System.currentTimeMillis()+20000);

    }

    @Test
    public void insertUser() {

        User u = users.get(0);
        db.insertUser(u);
        User u2 = db.getUser(u.getGoogleId());

        assertThat(u, equalTo(u2));

    }

    @Test
    public void updateUserEventStatus() {
    }

    @Test
    public void getUserEventStatus() {
    }

    @Test
    public void getUserEventStatus1() {
    }

    @Test
    public void getChatMembers() {
    }

    @Test
    public void getUser() {
    }

    @Test
    public void getUserByEmail() {
    }

    @Test
    public void getUserEmailExists() {
    }



    @Test
    public void getUsersOfChat() {
    }



    @Test
    public void getMessages() {
    }



    @Test
    public void getEvent() {
        Time time = new Time(System.currentTimeMillis());

        Event e=  new Event(time, "TourdeFrance", "ergbu34zr74bgv4zd7u",
                true, users.get(1).getGoogleId(), new GregorianCalendar(2020, 10, 27, 9, 6),
                "hilfe ein russ",  chats.get(1).getId(), (byte) 1);


        db.insertMessage(e);
        Event e2 = db.getEvent(e.getId());
        assertThat(e, equalTo(e2));




        time = new Time(System.currentTimeMillis()+20000);
    }

    @Test
    public void getEvents() {
    }

    @Test
    public void getLastMessage() {
    }
}