package de.swproj.teamchat.connection.database;

import android.content.Context;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class DBStatementsTest {
    DBStatements db;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Chat> chats = new ArrayList<>();
    ArrayList<Event> events= new ArrayList<>();
    ArrayList<Message>messages= new ArrayList<>();

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
        for (int i=0;i<5;i++){
            Message m = new Message(new Date(System.currentTimeMillis()+i*30000), "Test Nachricht"+i, "ghdfghdtrgfx"+i, false, users.get(i).getGoogleId(), chats.get(1).getId());
            messages.add(m);
            db.insertMessage(m);
        }
        for (int i=0;i<3;i++){
             Event e = new Event(new Date(System.currentTimeMillis()+(i+5)*30000), "TourdeFrance"+i, "ergbu34gfgfr7v4zd7u"+i,
                     true, users.get(i).getGoogleId(), new GregorianCalendar(2020+i, i, 13+i, 9+i, 6+i),
                     "Keine Panik "+i,  chats.get(1).getId(), (byte) 1);
             events.add(e);
             db.insertMessage(e);
        }


    }

    @After
    public void dropDB(){
        db.dropAll();
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
        Date time = GregorianCalendar.getInstance().getTime();;
        Message m = new Message(time, "Test Nachricht insert", "ghdzuhzfghdtrgfx", false, users.get(1).getGoogleId(), chats.get(1).getId());
       boolean b= db.insertMessage(m);
        Message m2 = db.getMessage(m.getId());
        assertThat(m, equalTo(m2));


        time = new Time(System.currentTimeMillis());

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
        UserEventStatus ues = db.getUserEventStatus(events.get(1).getId(), users.get(1).getGoogleId());
        ues.setReason("Blabla");
        ues.setStatus(2);
        db.updateUserEventStatus(ues);
        UserEventStatus ues2= db.getUserEventStatus(events.get(1).getId(), users.get(1).getGoogleId());

        assertThat(ues,equalTo(ues2));
    }

    @Test
    public void getUserEventStatus() {

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
        Date time = GregorianCalendar.getInstance().getTime();;

        Event e=  new Event(time, "TourdeFrance", "ergbu34gfgfr74bgv4zd7u",
                true, users.get(2).getGoogleId(), new GregorianCalendar(2020, 10, 27, 9, 6),
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