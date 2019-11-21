package de.swproj.teamchat.Connection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;


public class DBStatements {
private DBConnection dbConnection;

public DBStatements (Context context){
    dbConnection = new DBConnection(context);
}


public void updateChat(Chat chat){
    String chatId=chat.getId();
    boolean isNew=true;
    SQLiteDatabase db = dbConnection.getReadableDatabase();

    // check if allready exists

    db.beginTransaction();
    Cursor c=db.query(DBCreate.TABLE_CHAT,new String []{DBCreate.COL_CHAT_ID},DBCreate.COL_CHAT_ID+"=?",new String[]{chatId+""},null,null,null,null);
   isNew= c.getCount()!=0;
   db.endTransaction();


   //Start writing

     db = dbConnection.getWritableDatabase();
    db.beginTransaction();



    ContentValues values = new ContentValues();
    //put values
    values.put(DBCreate.COL_CHAT_ID, chatId);
    values.put(DBCreate.COL_CHAT_NAME, chat.getName());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        values.put(DBCreate.COL_CHAT_COLOR, chat.getColor());
    }else{
        values.put(DBCreate.COL_CHAT_COLOR, 0);
    }
    values.put(DBCreate.COL_CHAT_FK_Creator, chat.getAdmin());


   try {
       //create new Chat
       if (isNew) {
          db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);

       }

       //update Chat with ID...
       else {
         db.update(DBCreate.TABLE_CHAT, values, DBCreate.COL_CHAT_ID + "=?",new String[]{""+chatId});


       }

   }catch (Exception e){
       Log.d("DB_Error class DBStatements:", "Unable to write CHAT in db");
   }finally {
       db.endTransaction();
   }




}

public boolean updateChatMembers(String[] userIDs, int chatId){
    SQLiteDatabase db = dbConnection.getReadableDatabase();
    db.beginTransaction();

    boolean success=true;

    ContentValues values= null;
    try{
        db.delete(DBCreate.TABLE_USERCHAT, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{""+chatId}); // todo: Delete USer from Chat (Events)

        for (String s:userIDs) {
            values= new ContentValues();
            values.put(DBCreate.COL_USERCHAT_FK_CHAT, chatId);
            values.put(DBCreate.COL_USERCHAT_FK_USER, s );
            db.insertOrThrow(DBCreate.TABLE_USERCHAT, null, values);

        }
        db.setTransactionSuccessful();
    }catch (Exception e){
        success=false;
        Log.d("DB_Error class DBStatements:", "Unable to write CHAT_USER in db");
    }finally {

        db.endTransaction();
    }

    return success;
}

public boolean insertMessage (Message message){

    boolean insertsuccesfull=true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();
    ContentValues values = new ContentValues();
    int messageId =-1;
    db.beginTransaction();
try {



    //put values
    int i=0;
    if(message.isEvent())
        i=1;

    values.put(DBCreate.COL_MESSAGE_FK_CREATOR, message.getCreator());
    values.put(DBCreate.COL_MESSAGE_FK_CHATID, message.getChatid());
    values.put(DBCreate.COL_MESSAGE_ISEVENT, i);
    values.put(DBCreate.COL_MESSAGE_MESSAGE, message.getMessage());
    values.put(DBCreate.COL_MESSAGE_ID, message.getId());
    values.put(DBCreate.COL_MESSAGE_TIMESTAMP, message.getTimeStamp().getTime());

     messageId = (int) db.insertOrThrow(DBCreate.TABLE_MESSAGE, null, values);
     db.setTransactionSuccessful();
}catch (Exception e){
    insertsuccesfull=false;
    Log.d("DB_Error class DBStatements:", "Unable to write Message in DB from db");
}finally {
    db.endTransaction();
}


    if(message.isEvent()&&messageId!=-1){
        Event e = (Event) message;
        values = new ContentValues();

        int eventId=-1;

         // create new Event

        db.beginTransaction();
        try{
            values.put(DBCreate.COL_EVENT_ID, messageId);
            values.put(DBCreate.COL_EVENT_DATE, e.getDate().getTime());
            values.put(DBCreate.COL_EVENT_DESCRIPTION, e.getDescription());
            values.put(DBCreate.COL_EVENT_FK_MESSAGEID, messageId);

            eventId = (int) db.insertOrThrow(DBCreate.TABLE_EVENT, null, values);

            db.setTransactionSuccessful();
        }catch (Exception d){
            insertsuccesfull=false;
            Log.d("DB_Error class DBStatements:", "Unable to write Event in DB from db");
        }finally {
            db.endTransaction();
        }

        // if there is a newe Event, add members

      if(eventId!=-1) {

          ArrayList<String> chatMmembers = getChatMembers(message.getChatid());
          db.beginTransaction();

          try {
              for (String userId : chatMmembers
              ) {
                  values = new ContentValues();
                  values.put(DBCreate.COL_EVENTUSER_FK_EVENT, eventId);
                  values.put(DBCreate.COL_EVENTUSER_FK_USER, userId);
                  values.put(DBCreate.COL_EVENTUSER_REASON, " ");
                  values.put(DBCreate.COL_EVENTUSER_STATUS, 0);
                  db.insertOrThrow(DBCreate.TABLE_EVENTUSER, null, values);
              }
              db.setTransactionSuccessful();
          }catch (Exception ex){
              insertsuccesfull=false;
              Log.d("DB_Error class DBStatements:", "Unable to write EventUser in DB from db");
          }finally {
              db.endTransaction();
          }
      }





    }


  db.endTransaction();

    return insertsuccesfull;
}

public boolean insertUser(User user){
    boolean insertsuccesfull=true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();

    db.beginTransaction();

    try {


        ContentValues values = new ContentValues();
        //put values

        values.put(DBCreate.COL_USER_G_ID, user.getGoogleId());
        values.put(DBCreate.COL_USER_ACCNAME, user.getAccountName());
        values.put(DBCreate.COL_USER_MAIL,user.getGoogleMail());
        values.put(DBCreate.COL_USER_FIRSTNAME, user.getFirstName());
        values.put(DBCreate.COL_USER_NAME, user.getName());


        db.insertOrThrow(DBCreate.TABLE_USER, null, values);

        db.setTransactionSuccessful();

    }catch (Exception e){
        insertsuccesfull=false;
        Log.d("DB_Error class DBStatements:", "Unable to write User in db");
    }finally {
        db.endTransaction();
    }



    return insertsuccesfull;
}

public boolean updateUserEventStatus(UserEventStatus status){
    boolean insertsuccesfull=true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();

    db.beginTransaction();

    try {


        ContentValues values = new ContentValues();
        //put values

        values.put(DBCreate.COL_EVENTUSER_FK_USER, status.getUserId());
        values.put(DBCreate.COL_EVENTUSER_FK_EVENT, status.getEventId());
        values.put(DBCreate.COL_EVENTUSER_STATUS, status.getStatus());
        values.put(DBCreate.COL_EVENTUSER_REASON, status.getReason());



        db.insertOrThrow(DBCreate.TABLE_EVENTUSER, null, values);

        db.setTransactionSuccessful();

    }catch (Exception e){
        insertsuccesfull=false;
        Log.d("DB_Error class DBStatements:", "Unable to write User in db");
    }finally {
        db.endTransaction();
    }



    return insertsuccesfull;
}

public ArrayList<UserEventStatus> getUserEventStatus(int eventId){
    ArrayList<UserEventStatus> userEventStats = new ArrayList<>();

    SQLiteDatabase db = dbConnection.getReadableDatabase();

    db.beginTransaction();
    try {



        Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                DBCreate.COL_EVENTUSER_FK_EVENT+"=?", new String[]{""+eventId}, null, null, null);
        if (c.moveToFirst()) {

            int event = c.getColumnIndex(DBCreate.COL_USER_G_ID);
            int user = c.getColumnIndex(DBCreate.COL_USER_MAIL);
            int reason = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
            int status = c.getColumnIndex(DBCreate.COL_USER_NAME);

            do {

                userEventStats.add(new UserEventStatus(c.getString(user),c.getInt(event),(byte)c.getInt(status),c.getString (reason)));

            } while (c.moveToNext());

            db.setTransactionSuccessful();
        }

    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read UserEventStatus from db");
    }finally {
        db.endTransaction();
    }



    return userEventStats;
}

public UserEventStatus getUserEventStatus(int eventId, String userId) {
        UserEventStatus state = null;
    SQLiteDatabase db = dbConnection.getReadableDatabase();

    db.beginTransaction();
    try {



        Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                DBCreate.COL_EVENTUSER_FK_EVENT+"=?"+ " AND "+ DBCreate.COL_EVENTUSER_FK_USER +"=?" , new String[]{eventId+"",userId}, null, null, null);
        if (c.moveToFirst()) {

            int event = c.getColumnIndex(DBCreate.COL_USER_G_ID);
            int user = c.getColumnIndex(DBCreate.COL_USER_MAIL);
            int reason = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
            int status = c.getColumnIndex(DBCreate.COL_USER_NAME);


                state =new UserEventStatus(c.getString(user),c.getInt(event),(byte)c.getInt(status),c.getString (reason));

            }

            db.setTransactionSuccessful();


    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read UserEventStatus from User "+ userId +" from db");
    }finally {
        db.endTransaction();
    }


        return  state;
    }

public ArrayList<String> getChatMembers(int chatId){
    ArrayList<String> memberIds = new ArrayList<>();

    SQLiteDatabase db = dbConnection.getReadableDatabase();

    db.beginTransaction();
    try {
        Cursor c = db.query(DBCreate.TABLE_USERCHAT, new String[]{DBCreate.COL_USERCHAT_FK_USER,},DBCreate.COL_USERCHAT_FK_CHAT+"=?" , new String[]{chatId+""}, null, null, null);

        if (c.moveToFirst()) {
            int userId = c.getColumnIndex(DBCreate.COL_USERCHAT_FK_USER);
            do {
                memberIds.add(c.getString(userId));

            } while (c.moveToNext());
        }

    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read Users in Chat from  "+ chatId+"db");
    }finally {
        db.endTransaction();
    }


    return memberIds;
}

public User getUser(String googleID){
    User user = null;
    SQLiteDatabase db = dbConnection.getReadableDatabase();
    db.beginTransaction();

    try {

        Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                DBCreate.COL_USER_G_ID+"=?", new String[]{googleID}, null, null, null);
        if (c.moveToFirst()) {

            int id = c.getColumnIndex(DBCreate.COL_USER_G_ID);
            int mail = c.getColumnIndex(DBCreate.COL_USER_MAIL);
            int acc = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
            int name = c.getColumnIndex(DBCreate.COL_USER_NAME);
            int fname = c.getColumnIndex(DBCreate.COL_USER_FIRSTNAME);

                user =new User(c.getString(id), c.getString(mail), c.getString(acc), c.getString(name), c.getString(fname));
            }
    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read User "+googleID+" from db");
    }finally {
        db.endTransaction();
    }


    return user;
}

public List<User> getUser(){
    ArrayList<User> users= new ArrayList<>();
    SQLiteDatabase db = dbConnection.getReadableDatabase();

    db.beginTransaction();
    try {



        Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                null, null, null, null, null);
        if (c.moveToFirst()) {
            // String googleId, String googleMail, String accountName, String name, String firstName
            int id = c.getColumnIndex(DBCreate.COL_USER_G_ID);
            int mail = c.getColumnIndex(DBCreate.COL_USER_MAIL);
            int acc = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
            int name = c.getColumnIndex(DBCreate.COL_USER_NAME);
            int fname = c.getColumnIndex(DBCreate.COL_USER_FIRSTNAME);
            do {
                users.add(new User(c.getString(id), c.getString(mail), c.getString(acc), c.getString(name), c.getString(fname)));

            } while (c.moveToNext());
        }

    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
    }finally {
        db.endTransaction();
    }

    return users;
    }

public Chat getChat(int chatId){
    Chat chat = null;
    SQLiteDatabase db = dbConnection.getReadableDatabase();
    db.beginTransaction();

    try {

        Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID,DBCreate.COL_CHAT_FK_Creator, DBCreate.COL_CHAT_NAME, DBCreate.COL_CHAT_COLOR},
                DBCreate.COL_USER_G_ID+"="+chatId, null, null, null, null);
        if (c.moveToFirst()) {

            int id = c.getColumnIndex(DBCreate.COL_USER_G_ID);
            int creator = c.getColumnIndex(DBCreate.COL_USER_MAIL);
            int name = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
            int color = c.getColumnIndex(DBCreate.COL_USER_NAME);


                chat =new Chat(c.getString(id), c.getString(name), c.getInt(color),  c.getString(creator));

        }
    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to read Chat "+chatId+" from db");
    }finally {
        db.endTransaction();
    }


    return chat;

    }

public ArrayList<Message> getMessages(int chatId){
        ArrayList<Message> messages= new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {



            Cursor c = db.query(DBCreate.TABLE_MESSAGE, new String[]{DBCreate.COL_MESSAGE_ID, DBCreate.COL_MESSAGE_FK_CHATID, DBCreate.COL_MESSAGE_TIMESTAMP, DBCreate.COL_MESSAGE_FK_CREATOR, DBCreate.COL_MESSAGE_MESSAGE,DBCreate.COL_MESSAGE_ISEVENT },
                    DBCreate.COL_MESSAGE_FK_CHATID+"=?", new String[]{chatId+""}, null, null, null);
            if (c.moveToFirst()) {
                // String googleId, String googleMail, String accountName, String name, String firstName
                int id = c.getColumnIndex(DBCreate.COL_MESSAGE_ID);

                int creator = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CREATOR);
                int isEvent = c.getColumnIndex(DBCreate.COL_MESSAGE_ISEVENT);
                int message = c.getColumnIndex(DBCreate.COL_MESSAGE_MESSAGE);
                int timestmp = c.getColumnIndex(DBCreate.COL_MESSAGE_TIMESTAMP);

                do {
                    // Time timeStamp, String message, int id, boolean isEvent, User creator,int chatid
                    messages.add(new Message(Time.valueOf(c.getInt(timestmp)+""), c.getString(message), c.getInt(id), (c.getInt(isEvent)==1), c.getString(creator),c.getInt(chatId)));

                } while (c.moveToNext());
            }

        }catch (Exception e){
            Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
        }finally {
            db.endTransaction();
        }

        return messages;
    }

public Message getMessage(int messageID){

       Message message=null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {

            Cursor c = db.query(DBCreate.TABLE_MESSAGE, new String[]{DBCreate.COL_MESSAGE_ID, DBCreate.COL_MESSAGE_FK_CHATID, DBCreate.COL_MESSAGE_TIMESTAMP, DBCreate.COL_MESSAGE_FK_CREATOR, DBCreate.COL_MESSAGE_MESSAGE,DBCreate.COL_MESSAGE_ISEVENT },
                    DBCreate.COL_MESSAGE_ID+"=?", new String[]{messageID+""}, null, null, null);
            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_MESSAGE_ID);
                int chatId = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CHATID);
                int creator = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CREATOR);
                int isEvent = c.getColumnIndex(DBCreate.COL_MESSAGE_ISEVENT);
                int messageInt = c.getColumnIndex(DBCreate.COL_MESSAGE_MESSAGE);
                int timestmp = c.getColumnIndex(DBCreate.COL_MESSAGE_TIMESTAMP);


                message=new Message(Time.valueOf(c.getInt(timestmp) + ""), c.getString(messageInt), c.getInt(id), (c.getInt(isEvent) == 1), c.getString(creator), chatId);



            }
        }catch (Exception e){
            Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
        }finally {
            db.endTransaction();
        }

        return message;
    }

public Event getEvent(int messageId){
         Message message=getMessage(messageId);
         Event event =null;

         if(message.isEvent()){
             SQLiteDatabase db = dbConnection.getReadableDatabase();

             db.beginTransaction();
             try {

                 Cursor c = db.query(DBCreate.TABLE_EVENT, new String[]{DBCreate.COL_EVENT_DATE, DBCreate.COL_EVENT_DESCRIPTION },
                         DBCreate.COL_EVENT_ID+"=?", new String[]{messageId+""}, null, null, null);
                 if (c.moveToFirst()) {

                     int date = c.getColumnIndex(DBCreate.COL_EVENT_DATE);
                     int description = c.getColumnIndex(DBCreate.COL_EVENT_DESCRIPTION);


 //public Event(Time timeStamp, String message, int id, boolean isEvent, String creatorID, Date date,
   //                          String description,int chatid, Byte status) {

                     //todo: Date aus datenbank lesen

                   Date d=  new Date();
                   d.setTime(c.getInt(date));
                 event=new Event(message.getTimeStamp(),message.getMessage(),message.getId(), true, message.getCreator(),
                       d,c.getString(description), message.getChatid(),(byte)0);



                 }
             }catch (Exception e){
                 Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
             }finally {
                 db.endTransaction();
             }

         }



    return event;
}

public ArrayList<Event>getEvent(String userId){


    return null;
}

public Message getLastMessage( int chatid){

    return null;
}




}
