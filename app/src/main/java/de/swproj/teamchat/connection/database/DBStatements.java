package de.swproj.teamchat.connection.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;


public class DBStatements {
    private DBConnection dbConnection;

    public void dropAll(){
        dbConnection.onUpgrade(dbConnection.getWritableDatabase(),0,0);
    }

    public DBStatements(Context context) {
        dbConnection = new DBConnection(context);
    }

    public void updateChat(Chat chat) {
        //todo: BUG fix

        String chatId = chat.getId();
        boolean isNew = true;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        // check if allready exists

        db.beginTransaction();
        Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID}, DBCreate.COL_CHAT_ID + "=?", new String[]{chatId}, null, null, null, null);
        isNew = c.getCount() == 0;
        db.setTransactionSuccessful();
        db.endTransaction();

  Log.d("UpdateChat","Is new "+isNew);
        //Start writing




        ContentValues values = new ContentValues();
        //put values
        values.put(DBCreate.COL_CHAT_ID, chatId);
        values.put(DBCreate.COL_CHAT_NAME, chat.getName());
        values.put(DBCreate.COL_CHAT_COLOR, chat.getColor());
        values.put(DBCreate.COL_CHAT_FK_Creator, chat.getAdmin());

        db = dbConnection.getWritableDatabase();
        db.beginTransaction();
        try {
            //create new Chat
            if (isNew) {
                Log.d("UpdateChat","Is new "+isNew);
             Long i=   db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);

                Log.d("UpdateChat","Insert: "+i);

            }//update Chat with ID...
            else {

                db.update(DBCreate.TABLE_CHAT, values, DBCreate.COL_CHAT_ID + "=?", new String[]{"" + chatId});

            }

        } catch (Exception e) {

            Log.d("DB_Error class DBStatements:", "Unable to write CHAT in db");
        } finally {
            db.endTransaction();
        }


    }

    public boolean insertChat(Chat chat){
    boolean insertsuccesfull = true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();

    db.beginTransaction();

    try {


        ContentValues values = new ContentValues();
        //put values

        values.put(DBCreate.COL_CHAT_ID, chat.getId());
        values.put(DBCreate.COL_CHAT_NAME, chat.getName());
        values.put(DBCreate.COL_CHAT_COLOR, chat.getColor());
        values.put(DBCreate.COL_CHAT_FK_Creator, chat.getAdmin());



        db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);

        db.setTransactionSuccessful();

    } catch (Exception e) {
        insertsuccesfull = false;
        Log.d("DB_Error class DBStatements:", "Unable to write Chat in db");
    } finally {
        db.endTransaction();
    }


    return insertsuccesfull;
}

    public boolean updateChatMembers(String[] userIDs, String chatId) {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        boolean success = true;

        ContentValues values = null;
        try {
            db.delete(DBCreate.TABLE_USERCHAT, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{"" + chatId}); // todo: Delete USer from Chat (Events)

            for (String s : userIDs) {
                values = new ContentValues();
                values.put(DBCreate.COL_USERCHAT_FK_CHAT, chatId);
                values.put(DBCreate.COL_USERCHAT_FK_USER, s);
                db.insertOrThrow(DBCreate.TABLE_USERCHAT, null, values);

            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            success = false;
            Log.d("DB_Error class DBStatements:", "Unable to write CHAT_USER in db");
        } finally {

            db.endTransaction();
        }

        return success;
    }

    public boolean insertMessage(Message message) {

        boolean insertsuccesfull = true;

        SQLiteDatabase db = dbConnection.getWritableDatabase();
        ContentValues values = new ContentValues();

        db.beginTransaction();
        try {


            //put values
            int i = 0;
            if (message.isEvent())
                i = 1;

            values.put(DBCreate.COL_MESSAGE_FK_CREATOR, message.getCreator());
            values.put(DBCreate.COL_MESSAGE_FK_CHATID, message.getChatid());
            values.put(DBCreate.COL_MESSAGE_ISEVENT, i);
            values.put(DBCreate.COL_MESSAGE_MESSAGE, message.getMessage());
            values.put(DBCreate.COL_MESSAGE_ID, message.getId());
            values.put(DBCreate.COL_MESSAGE_TIMESTAMP, message.getTimeStamp().getTime());

            db.insertOrThrow(DBCreate.TABLE_MESSAGE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error class DBStatements:", "Unable to write Message in DB from db");
        } finally {
            db.endTransaction();
        }

        Log.d("InsertEVent: ",message.isEvent()+"");
        if (message.isEvent()) {
            Event e = (Event) message;
            values = new ContentValues();


            // create new Event

            db.beginTransaction();
            try {
                values.put(DBCreate.COL_EVENT_ID, message.getId());
                values.put(DBCreate.COL_EVENT_DATE, e.getDate().getTime().getTime()+"");
                values.put(DBCreate.COL_EVENT_DESCRIPTION, e.getDescription());
              //  values.put(DBCreate.COL_EVENT_FK_MESSAGEID, message.getId());

                db.insertOrThrow(DBCreate.TABLE_EVENT, null, values);

                db.setTransactionSuccessful();
            } catch (Exception d) {
                insertsuccesfull = false;
                Log.d("DB_Error class DBStatements:", "Unable to write Event in DB from db");
            } finally {
                db.endTransaction();
            }

            // if there is a newe Event, add members

            if (null == null) {

                ArrayList<String> chatMmembers = getChatMembers(message.getChatid());
                db.beginTransaction();

                try {
                    for (String userId : chatMmembers
                    ) {
                        values = new ContentValues();
                        values.put(DBCreate.COL_EVENTUSER_FK_EVENT, message.getId());
                        values.put(DBCreate.COL_EVENTUSER_FK_USER, userId);
                        values.put(DBCreate.COL_EVENTUSER_REASON, "-");
                        values.put(DBCreate.COL_EVENTUSER_STATUS, 0);
                        db.insertOrThrow(DBCreate.TABLE_EVENTUSER, null, values);
                    }
                    db.setTransactionSuccessful();
                } catch (Exception ex) {
                    insertsuccesfull = false;
                    Log.d("DB_Error class DBStatements:", "Unable to write EventUser in DB from db");
                } finally {
                    db.endTransaction();
                }
            }


        }




        return insertsuccesfull;
    }

    public boolean insertUser(User user) {
        boolean insertsuccesfull = true;

        SQLiteDatabase db = dbConnection.getWritableDatabase();

        db.beginTransaction();

        try {


            ContentValues values = new ContentValues();
            //put values

            values.put(DBCreate.COL_USER_G_ID, user.getGoogleId());
            values.put(DBCreate.COL_USER_ACCNAME, user.getAccountName());
            values.put(DBCreate.COL_USER_MAIL, user.getGoogleMail());
            values.put(DBCreate.COL_USER_FIRSTNAME, user.getFirstName());
            values.put(DBCreate.COL_USER_NAME, user.getName());


            db.insertOrThrow(DBCreate.TABLE_USER, null, values);

            db.setTransactionSuccessful();

        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error class DBStatements:", "Unable to write User in db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return insertsuccesfull;
    }

    public boolean updateUserEventStatus(UserEventStatus status) {
        boolean insertsuccesfull = true;

        SQLiteDatabase db = dbConnection.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            values.put(DBCreate.COL_EVENTUSER_STATUS, status.getStatus());
            values.put(DBCreate.COL_EVENTUSER_REASON, status.getReason());

            db.update(DBCreate.TABLE_EVENTUSER, values,DBCreate.COL_EVENTUSER_ID+"=?",new String[]{status.getStatusId()+""});

            db.setTransactionSuccessful();

        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error class DBStatements:", "Unable to update Userevent status in db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return insertsuccesfull;
    }

    public ArrayList<UserEventStatus> getUserEventStatus(String eventId) {
        ArrayList<UserEventStatus> userEventStats = new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_ID,DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                    DBCreate.COL_EVENTUSER_FK_EVENT + "=?", new String[]{"" + eventId}, null, null, null);
            if (c.moveToFirst()) {
                int id = c.getColumnIndex(DBCreate.COL_EVENTUSER_ID);
                int event = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_EVENT);
                int user = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_USER);
                int reason = c.getColumnIndex(DBCreate.COL_EVENTUSER_REASON);
                int status = c.getColumnIndex(DBCreate.COL_EVENTUSER_STATUS);

                do {

                    userEventStats.add(new UserEventStatus(c.getInt(id),c.getString(user), c.getInt(event), c.getInt(status), c.getString(reason)));

                } while (c.moveToNext());

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read UserEventStatus from db");
        } finally {
            db.endTransaction();
        }


        return userEventStats;
    }
    public UserEventStatus getUserEventStatus(String eventId, String userId) {
        UserEventStatus state = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_ID,DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                    DBCreate.COL_EVENTUSER_FK_EVENT + "=? AND " + DBCreate.COL_EVENTUSER_FK_USER + "=?", new String[]{eventId,userId}, null, null, null);
            Log.d("getUserEventStatus","Cursor count: "+c.getCount());
            if (c.moveToFirst()) {
                int id = c.getColumnIndex(DBCreate.COL_EVENTUSER_ID);
                int event = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_EVENT);
                int user = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_USER);
                int reason = c.getColumnIndex(DBCreate.COL_EVENTUSER_REASON);
                int status = c.getColumnIndex(DBCreate.COL_EVENTUSER_STATUS);

                state = new UserEventStatus(c.getInt(id),c.getString(user), c.getInt(event), c.getInt(status), c.getString(reason));

            }

            db.setTransactionSuccessful();


        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read UserEventStatus from User " + userId + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return state;
    }

    public ArrayList<String> getChatMembers(String chatId) {
        ArrayList<String> memberIds = new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {
            Cursor c = db.query(DBCreate.TABLE_USERCHAT, new String[]{DBCreate.COL_USERCHAT_FK_USER,}, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{chatId + ""}, null, null, null);

            if (c.moveToFirst()) {
                int userId = c.getColumnIndex(DBCreate.COL_USERCHAT_FK_USER);
                do {
                    memberIds.add(c.getString(userId));

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Users in Chat from  " + chatId + "db");
        } finally {
            db.endTransaction();
        }


        return memberIds;
    }

    public User getUser(String googleID) {
        User user = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {

            Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                    DBCreate.COL_USER_G_ID + "=?", new String[]{googleID}, null, null, null);
            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_USER_G_ID);
                int mail = c.getColumnIndex(DBCreate.COL_USER_MAIL);
                int acc = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
                int name = c.getColumnIndex(DBCreate.COL_USER_NAME);
                int fname = c.getColumnIndex(DBCreate.COL_USER_FIRSTNAME);

                user = new User(c.getString(id), c.getString(mail), c.getString(acc), c.getString(name), c.getString(fname));
            }
        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read User " + googleID + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return user;
    }

    public ArrayList<User> getUser() {
        ArrayList<User> users = new ArrayList<>();
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

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return users;
    }

    public List<User> getUsersOfChat(String chatId) {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        final String MY_QUERY = "SELECT * FROM "+DBCreate.getUserTable() +" a INNER JOIN "+DBCreate.TABLE_USERCHAT+" b ON a."+ DBCreate.COL_USER_G_ID +"=b."+DBCreate.COL_USERCHAT_FK_USER+
               " WHERE b."+DBCreate.COL_USERCHAT_FK_CHAT+"=?";
        db.beginTransaction();
        try {


            Cursor c = db.rawQuery(MY_QUERY, new String[]{chatId});
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

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Users of Chat "+chatId+" from DB");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return users;
    }

    public Chat getChat(String chatId) {
        Chat chat = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {

            Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID, DBCreate.COL_CHAT_FK_Creator, DBCreate.COL_CHAT_NAME, DBCreate.COL_CHAT_COLOR},
                    DBCreate.COL_CHAT_ID + "=?", new String[]{chatId}, null, null, null);
            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_CHAT_ID);
                int creator = c.getColumnIndex(DBCreate.COL_CHAT_FK_Creator);
                int name = c.getColumnIndex(DBCreate.COL_CHAT_NAME);
                int color = c.getColumnIndex(DBCreate.COL_CHAT_COLOR);

              //     public Chat(String name, int color, String id, String admin)
                chat = new Chat(c.getString(name), c.getInt(color), c.getString(id), c.getString(creator));

            }
        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Chat " + chatId + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return chat;

    }


    public ArrayList<Chat> getChat() {
        ArrayList<Chat> chats = new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {

            Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID, DBCreate.COL_CHAT_FK_Creator, DBCreate.COL_CHAT_NAME, DBCreate.COL_CHAT_COLOR},
                    null, null, null, null, null);
            c= db.rawQuery("SELECT * FROM "+DBCreate.TABLE_CHAT,null);
            Log.d("GetChat","Cursor count "+  c.getCount());


            int id = c.getColumnIndex(DBCreate.COL_CHAT_ID);
            int creator = c.getColumnIndex(DBCreate.COL_CHAT_FK_Creator);
            int name = c.getColumnIndex(DBCreate.COL_CHAT_NAME);
            int color = c.getColumnIndex(DBCreate.COL_CHAT_COLOR);

            if (c.moveToFirst()) {
                do {
                    Log.d("GetChat","Schleife");
                    chats.add(new Chat(c.getString(name), c.getInt(color), c.getString(id), c.getString(creator)));
                }while (c.moveToNext());


            }
        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Chats  from db");
        } finally {
            db.endTransaction();
        }


        return chats;

    }

    public ArrayList<Message> getMessages(String chatId) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_MESSAGE, new String[]{DBCreate.COL_MESSAGE_ID, DBCreate.COL_MESSAGE_FK_CHATID, DBCreate.COL_MESSAGE_TIMESTAMP, DBCreate.COL_MESSAGE_FK_CREATOR, DBCreate.COL_MESSAGE_MESSAGE, DBCreate.COL_MESSAGE_ISEVENT},
                    DBCreate.COL_MESSAGE_FK_CHATID + "=?", new String[]{chatId + ""}, null, null, null);

            Log.d("getMassages","Cursor count "+c.getCount());
            if (c.moveToFirst()) {
                // String googleId, String googleMail, String accountName, String name, String firstName
                int id = c.getColumnIndex(DBCreate.COL_MESSAGE_ID);

                int creator = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CREATOR);
                int isEvent = c.getColumnIndex(DBCreate.COL_MESSAGE_ISEVENT);
                int message = c.getColumnIndex(DBCreate.COL_MESSAGE_MESSAGE);
                int timestmp = c.getColumnIndex(DBCreate.COL_MESSAGE_TIMESTAMP);

                do {
                    // Time timeStamp, String message, int id, boolean isEvent, User creator,int chatid
                    messages.add(new Message(new Time(c.getInt(timestmp)), c.getString(message), c.getString(id), (c.getInt(isEvent) == 1), c.getString(creator), chatId));

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Message of Chat "+chatId+" from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return messages;
    }

    public Message getMessage(String messageID) {

        Message message = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {

            Cursor c = db.query(DBCreate.TABLE_MESSAGE, new String[]{DBCreate.COL_MESSAGE_ID, DBCreate.COL_MESSAGE_FK_CHATID, DBCreate.COL_MESSAGE_TIMESTAMP, DBCreate.COL_MESSAGE_FK_CREATOR, DBCreate.COL_MESSAGE_MESSAGE, DBCreate.COL_MESSAGE_ISEVENT},
                    DBCreate.COL_MESSAGE_ID + "=?", new String[]{messageID}, null, null, null);

           // Cursor c = db.rawQuery("SELECT * FROM " +DBCreate.TABLE_MESSAGE+" WHERE "+ DBCreate.COL_MESSAGE_ID+"=?;",new String[]{messageID});



            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_MESSAGE_ID);
                int chatId = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CHATID);
                int creator = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CREATOR);
                int isEvent = c.getColumnIndex(DBCreate.COL_MESSAGE_ISEVENT);
                int messageInt = c.getColumnIndex(DBCreate.COL_MESSAGE_MESSAGE);
                int timestmp = c.getColumnIndex(DBCreate.COL_MESSAGE_TIMESTAMP);


                message = new Message(new Time(c.getInt(timestmp)), c.getString(messageInt), c.getString(id), (c.getInt(isEvent) == 1), c.getString(creator), c.getString(chatId));


            }
        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Message "+messageID+" from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return message;
    }

    public Event getEvent(String messageId) {
        Message message = getMessage(messageId);
        Event event = null;

        if (message.isEvent()) {
            SQLiteDatabase db = dbConnection.getReadableDatabase();

            db.beginTransaction();
            try {

         //       Cursor c = db.query(DBCreate.TABLE_EVENT, new String[]{DBCreate.COL_EVENT_DATE, DBCreate.COL_EVENT_DESCRIPTION},
           //             DBCreate.COL_EVENT_ID + "=?", new String[]{messageId + ""}, null, null, null);
                Cursor c = db.rawQuery("SELECT * FROM "+ DBCreate.TABLE_EVENT,null);//" WHERE "+DBCreate.COL_EVENT_ID+"=? ;",new String[]{messageId});

                Log.d("Get Message ", "counter for id "+ messageId+" coutn: "+c.getCount());

                if (c.moveToFirst()) {

                    int date = c.getColumnIndex(DBCreate.COL_EVENT_DATE);
                    int description = c.getColumnIndex(DBCreate.COL_EVENT_DESCRIPTION);


                    //public Event(Time timeStamp, String message, int id, boolean isEvent, String creatorID, Date date,
                    //                          String description,int chatid, int status) {


                    GregorianCalendar d = new GregorianCalendar();
                    d.setTime(new Date(Long.parseLong(c.getString(date))));
                    event = new Event(message.getTimeStamp(), message.getMessage(), message.getId(), true, message.getCreator(),
                            d, c.getString(description), message.getChatid(), 0);


                }
            } catch (Exception e) {
                Log.d("DB_Error class DBStatements:", "Unable to read Event from db");
            } finally {
                db.endTransaction();
            }

        }



        return event;
    }

    public ArrayList<Event> getEvents() {
         ArrayList<String> eventIDs= new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_EVENT, new String[]{DBCreate.COL_EVENT_ID},
                    null, null, null, null, null);
            if (c.moveToFirst()) {

                int eventId = c.getColumnIndex(DBCreate.COL_EVENT_ID);

                do {
                   eventIDs.add(c.getString(eventId));

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d("DB_Error class DBStatements:", "Unable to read Users from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

         ArrayList<Event> events = new ArrayList<>();

        for (String s:eventIDs
             ) {
            events.add(getEvent(s));
        }


        return events;
    }

    public Message getLastMessage(String chatId) {
        Message message = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try{
            Cursor c = db.rawQuery("SELECT * FROM " +DBCreate.TABLE_MESSAGE+" WHERE "+ DBCreate.COL_MESSAGE_FK_CHATID+"=? AND "+DBCreate.COL_MESSAGE_TIMESTAMP+
                    "=(SELECT MAX("+ DBCreate.COL_MESSAGE_TIMESTAMP+") FROM "+DBCreate.TABLE_MESSAGE+"" + " WHERE "+DBCreate.COL_MESSAGE_FK_CHATID+");"
                    ,new String[] {chatId} );
    //     Cursor c=  db.query(DBCreate.TABLE_MESSAGE, null, DBCreate.COL_MESSAGE_TIMESTAMP+"=(" +
      //            "SELECT MAX("+ DBCreate.COL_MESSAGE_TIMESTAMP+" FROM "+DBCreate.TABLE_MESSAGE+" WHERE "+DBCreate.COL_MESSAGE_FK_CHATID+")" +
       //           ") AND "+DBCreate.COL_MESSAGE_FK_CHATID+"=?", new String[]{chatid}, null, null, null);
           Log.d("getLasMessage", "Cursor count "+c.getCount()+"  ChatID "+chatId);
               if(c.moveToFirst()){
                   int id = c.getColumnIndex(DBCreate.COL_MESSAGE_ID);
                   int chatID = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CHATID);
                   int creator = c.getColumnIndex(DBCreate.COL_MESSAGE_FK_CREATOR);
                   int isEvent = c.getColumnIndex(DBCreate.COL_MESSAGE_ISEVENT);
                   int messageInt = c.getColumnIndex(DBCreate.COL_MESSAGE_MESSAGE);
                   int timestmp = c.getColumnIndex(DBCreate.COL_MESSAGE_TIMESTAMP);


                   message = new Message(new Time(c.getInt(timestmp)), c.getString(messageInt), c.getString(id), (c.getInt(isEvent) == 1), c.getString(creator), c.getString(chatID));
               }
        }catch (Exception e){
            Log.d("DB_Error class DBStatements:", "Unable to read LastMessage from db");
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }


        return message;
    }



}
