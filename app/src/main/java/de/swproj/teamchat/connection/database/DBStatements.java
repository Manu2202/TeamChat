package de.swproj.teamchat.connection.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.ChatMembers;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.view.activities.MainActivity;
import de.swproj.teamchat.view.viewmodels.Updateable;


public class DBStatements {

    private static HashSet<Updateable> updateables=new HashSet<>();
    private static DBConnection dbConnection;

    // Config Methods
    public static void addUpdateable(Updateable updateable){
        if(updateable!=null)
        updateables.add(updateable);
    }
    public static void removeUpdateable(Updateable updateable){
        updateables.remove(updateable);
    }

    public static void setDbConnection(DBConnection connection){
        dbConnection=connection;
        if(dbConnection==null)
            dbConnection=new DBConnection(MainActivity.getAppContext());
    }
    public static void dropAll(){
        dbConnection.onUpgrade(dbConnection.getWritableDatabase(),0,0);
    }

// Database Statemenst


    public static boolean insertChat(Chat chat){
    boolean insertsuccesfull = true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();

    db.beginTransaction();

    try {


        ContentValues values = new ContentValues();
        //put values

        values.put(DBCreate.COL_CHAT_ID, chat.getId());
        values.put(DBCreate.COL_CHAT_NAME, chat.getName());
        values.put(DBCreate.COL_CHAT_COLOR, chat.getColor());
        values.put(DBCreate.COL_CHAT_FK_CREATOR, chat.getAdmin());
        values.put(DBCreate.COL_CHAT_FK_LASTMESSAGE, "");



        db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);
        db.setTransactionSuccessful();

        for (Updateable u:updateables
        ) {
            u.insertObject(chat);
        }

    } catch (Exception e) {
        insertsuccesfull = false;
        Log.d("DB_Error DBStatements", "Unable to write Chat in db");
    } finally {
        db.endTransaction();
    }


    return insertsuccesfull;
}

    public static boolean insertMessage(Message message) {
            boolean insertsuccesfull=true;
      if(getChat(message.getChatid())==null){
          return false;
      }

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
            values.put(DBCreate.COL_MESSAGE_TIMESTAMP, String.valueOf(message.getTimeStampDate().getTime()));

            db.insertOrThrow(DBCreate.TABLE_MESSAGE, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error DBStatements", "Unable to write Message in DB from db");
        } finally {
            db.endTransaction();
        }
        if(insertsuccesfull){
            updateLastMessage(message);
        }

        //    Log.d("InsertEvent: ",message.isEvent()+"");
        if (message.isEvent()) {
            Event e = (Event) message;
            values = new ContentValues();


            // create new Event

            db.beginTransaction();
            try {
                values.put(DBCreate.COL_EVENT_ID, message.getId());
                values.put(DBCreate.COL_EVENT_DATE, e.getDate().getTime().getTime() + "");
                values.put(DBCreate.COL_EVENT_DESCRIPTION, e.getDescription());
               values.put(DBCreate.COL_EVENT_STATE, e.getStatus());

                db.insertOrThrow(DBCreate.TABLE_EVENT, null, values);

                db.setTransactionSuccessful();
            } catch (Exception d) {
                insertsuccesfull = false;
                Log.d("DB_Error DBStatements", "Unable to write Event in DB from db");
            } finally {
                db.endTransaction();
            }

            // if there is a new Event, add members

            if (insertsuccesfull) {

                ArrayList<String> chatMembers = getChatMembers(message.getChatid());
                db.beginTransaction();

                try {
                    for (String userId : chatMembers) {
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
                    Log.d("DB_Error DBStatements", "Unable to write EventUser in DB from db");
                } finally {
                    db.endTransaction();
                }
            }


        }
        if (insertsuccesfull) {
            for (Updateable u : updateables
            ) {
                u.insertObject(message);
            }
        }
        return insertsuccesfull;
    }

    public static boolean insertUser(User user) {
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
            for (Updateable u:updateables
            ) {
                u.insertObject(user);
            }

        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error DBStatements", "Unable to write User in db");
            // e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        if(insertsuccesfull){
            for (Updateable u:updateables
            ) {
                u.insertObject(user);
            }
        }

        return insertsuccesfull;
    }

    private static boolean insertUserEventStatus(String chatID, String userId){

        List<String> eventIDs = getEvents(chatID);

        SQLiteDatabase db = dbConnection.getWritableDatabase();

        for (String eventID:eventIDs) {

            db.beginTransaction();
            try {
                ContentValues  values = new ContentValues();
                values.put(DBCreate.COL_EVENTUSER_FK_EVENT, eventID);
                values.put(DBCreate.COL_EVENTUSER_FK_USER, userId);
                values.put(DBCreate.COL_EVENTUSER_REASON, "-");
                values.put(DBCreate.COL_EVENTUSER_STATUS, 0);
                db.insertOrThrow(DBCreate.TABLE_EVENTUSER, null, values);

                db.setTransactionSuccessful();
                db.endTransaction();
                for (Updateable u:updateables
                ) {
                    u.updateObject(new UserEventStatus(userId,eventID,0,"-"));
                }

            }catch (Exception e){
                Log.e("DB_Error DBStatements", "Unable to write USerEventstatus for Event: "+eventID);
            }

        }

        return true;
    }



    private static void updateLastMessage(Message message){

        SQLiteDatabase db = dbConnection.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBCreate.COL_CHAT_FK_LASTMESSAGE,message.getId());

        db.beginTransaction();

        int i=  db.update(DBCreate.TABLE_CHAT, values, DBCreate.COL_CHAT_ID + "=?", new String[]{message.getChatid()});
        db.setTransactionSuccessful();
        db.endTransaction();


    }

    public static boolean updateChatMembers(List<String> userIDs, String chatId) {

        boolean success;
        ContentValues values;
        List<String> actChatMembers = getChatMembers(chatId);
        List<String> newMembers = new LinkedList<>();
        newMembers.addAll(userIDs);

        SQLiteDatabase db = dbConnection.getWritableDatabase();
        db.beginTransaction();


        try {
            int i = db.delete(DBCreate.TABLE_USERCHAT, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{chatId});
            db.setTransactionSuccessful();
            db.endTransaction();
            db.beginTransaction();
            for (String s : newMembers) {
                values = new ContentValues();
                values.put(DBCreate.COL_USERCHAT_FK_CHAT, chatId);
                values.put(DBCreate.COL_USERCHAT_FK_USER, s);
                db.insertOrThrow(DBCreate.TABLE_USERCHAT, null, values);

            }
            db.setTransactionSuccessful();
            ChatMembers cm=  new ChatMembers(newMembers,chatId);
            for (Updateable u:updateables
            ) {
                u.updateObject(cm);
            }

            //Update UserEventStatus
            for (String s:actChatMembers
            ) {
                if(newMembers.contains(s)){
                    newMembers.remove(s);
                }else {
                    deleteUserEventStatus(chatId,s);
                }
            }
            for (String s:newMembers
            ) {
                insertUserEventStatus(chatId,s);
            }
            success=true;


        } catch (Exception e) {
            success = false;
            Log.d("DB_Error DBStatements", "Unable to write CHAT_USER in db");
        } finally {

            db.endTransaction();
        }

        return success;
    }

    public static void updateChat(Chat chat) {


        String chatId = chat.getId();
        boolean isNew = true;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        // check if allready exists

        db.beginTransaction();
        Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID}, DBCreate.COL_CHAT_ID + "=?", new String[]{chatId}, null, null, null, null);
        isNew = c.getCount() == 0;
        db.setTransactionSuccessful();
        db.endTransaction();

        //Start writing

        ContentValues values = new ContentValues();
        //put values
        values.put(DBCreate.COL_CHAT_ID, chatId);
        values.put(DBCreate.COL_CHAT_NAME, chat.getName());
        values.put(DBCreate.COL_CHAT_COLOR, chat.getColor());
        values.put(DBCreate.COL_CHAT_FK_CREATOR, chat.getAdmin());

        db = dbConnection.getWritableDatabase();
        db.beginTransaction();
        try {
            //create new Chat
            if (isNew) {

                Long i=   db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);
                for (Updateable u:updateables
                ) {
                    u.insertObject(chat);
                }


            }//update Chat with ID...
            else {
                int i=  db.update(DBCreate.TABLE_CHAT, values, DBCreate.COL_CHAT_ID + "=?", new String[]{chatId});
                for (Updateable u:updateables
                ) {
                    u.updateObject(chat);
                }

            }

            db.setTransactionSuccessful();
        } catch (Exception e) {

            Log.d("DB_Error DBStatements", "Unable to write CHAT in db");
        } finally {
            db.endTransaction();
        }


    }

    public static boolean updateUserEventStatus(UserEventStatus status) {
        boolean insertsuccesfull = true;

        SQLiteDatabase db = dbConnection.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();

            values.put(DBCreate.COL_EVENTUSER_STATUS, status.getStatus());
            values.put(DBCreate.COL_EVENTUSER_REASON, status.getReason());

            db.update(DBCreate.TABLE_EVENTUSER, values,DBCreate.COL_EVENTUSER_FK_EVENT+"=? AND " + DBCreate.COL_EVENTUSER_FK_USER+"=?",new String[]{status.getEventId(),status.getUserId()});

            db.setTransactionSuccessful();

        } catch (Exception e) {
            insertsuccesfull = false;
            Log.d("DB_Error DBStatements", "Unable to update Userevent status in db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        if(insertsuccesfull){
            for (Updateable u:updateables
            ) {
                u.updateObject(status);
            }
        }

        return insertsuccesfull;
    }

    public static boolean updateEvent(Event event){
        boolean res = false;

        //check existents
        Message message = getMessage(event.getId());
        if(message.isEvent()) {
            SQLiteDatabase db = dbConnection.getWritableDatabase();

            try {

                ContentValues values = new ContentValues();

                values.put(DBCreate.COL_MESSAGE_FK_CREATOR, event.getCreator());
                values.put(DBCreate.COL_MESSAGE_FK_CHATID, event.getChatid());
                values.put(DBCreate.COL_MESSAGE_ISEVENT, 1);
                values.put(DBCreate.COL_MESSAGE_MESSAGE, event.getMessage());
                values.put(DBCreate.COL_MESSAGE_ID, event.getId());
                values.put(DBCreate.COL_MESSAGE_TIMESTAMP, String.valueOf(event.getTimeStampDate().getTime()));

                db.beginTransaction();


                db.update(DBCreate.TABLE_MESSAGE,values,DBCreate.COL_MESSAGE_ID+"=?",new String[]{event.getId()});

                db.setTransactionSuccessful();
                db.endTransaction();

                db.beginTransaction();

                values = new ContentValues();


                values.put(DBCreate.COL_EVENT_DATE, event.getDate().getTime().getTime() + "");
                values.put(DBCreate.COL_EVENT_DESCRIPTION, event.getDescription());
                values.put(DBCreate.COL_EVENT_STATE, event.getStatus());

                db.update(DBCreate.TABLE_EVENT,values,DBCreate.COL_EVENT_ID+"=?",new String[]{event.getId()});

                db.setTransactionSuccessful();

                res=true;


            } catch (Exception e) {
                Log.e("DatabaseError","Unable to update Event wit id "+event.getId());
                return false;
            }
            finally {
                db.endTransaction();
            }
            for (Updateable u:updateables) {
                u.updateObject(event);
            }



        }

        return res;
    }


    public static ArrayList<UserEventStatus> getUserEventStatus(String eventId) {
        ArrayList<UserEventStatus> userEventStats = new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {
            Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_ID,DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                    DBCreate.COL_EVENTUSER_FK_EVENT + "=?", new String[]{eventId}, null, null, null);
            if (c.moveToFirst()) {

                int event = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_EVENT);
                int user = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_USER);
                int reason = c.getColumnIndex(DBCreate.COL_EVENTUSER_REASON);
                int status = c.getColumnIndex(DBCreate.COL_EVENTUSER_STATUS);

                do {
                    userEventStats.add(new UserEventStatus(c.getString(user), c.getString(event), c.getInt(status), c.getString(reason)));
                } while (c.moveToNext());

                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read UserEventStatus from db");
        } finally {
            db.endTransaction();
        }


        return userEventStats;
    }

    public static UserEventStatus getUserEventStatus(String eventId, String userId) {
        UserEventStatus state = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_EVENTUSER, new String[]{DBCreate.COL_EVENTUSER_ID,DBCreate.COL_EVENTUSER_FK_EVENT, DBCreate.COL_EVENTUSER_FK_USER, DBCreate.COL_EVENTUSER_REASON, DBCreate.COL_EVENTUSER_STATUS},
                    DBCreate.COL_EVENTUSER_FK_EVENT + "=? AND " + DBCreate.COL_EVENTUSER_FK_USER + "=?", new String[]{eventId,userId}, null, null, null);

            if (c.moveToFirst()) {
                int id = c.getColumnIndex(DBCreate.COL_EVENTUSER_ID);
                int event = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_EVENT);
                int user = c.getColumnIndex(DBCreate.COL_EVENTUSER_FK_USER);
                int reason = c.getColumnIndex(DBCreate.COL_EVENTUSER_REASON);
                int status = c.getColumnIndex(DBCreate.COL_EVENTUSER_STATUS);
                state = new UserEventStatus(c.getString(user), c.getString(event), c.getInt(status), c.getString(reason));

            }

            db.setTransactionSuccessful();


        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read UserEventStatus from User " + userId + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return state;
    }

    public static ArrayList<String> getChatMembers(String chatId) {
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
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Users in Chat from  " + chatId + "db");
        } finally {
            db.endTransaction();
        }


        return memberIds;
    }

    public static User getUser(String googleID) {
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
            Log.d("DB_Error DBStatements", "Unable to read User " + googleID + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return user;
    }

    public static User getUserByEmail(String googleMail) {
        User user = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {
            Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                    DBCreate.COL_USER_MAIL + "=?", new String[]{googleMail}, null, null, null);
            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_USER_G_ID);
                int mail = c.getColumnIndex(DBCreate.COL_USER_MAIL);
                int acc = c.getColumnIndex(DBCreate.COL_USER_ACCNAME);
                int name = c.getColumnIndex(DBCreate.COL_USER_NAME);
                int fname = c.getColumnIndex(DBCreate.COL_USER_FIRSTNAME);

                user = new User(c.getString(id), c.getString(mail), c.getString(acc), c.getString(name), c.getString(fname));
            }
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read User " + googleMail + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return user;
    }

    public static boolean getUserEmailExists(String googleMail) {
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {
            Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                    DBCreate.COL_USER_MAIL + "=?", new String[]{googleMail}, null, null, null);
            if (c.getCount() <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read User " + googleMail + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return false;
    }

    public static ArrayList<User> getUser() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_USER, new String[]{DBCreate.COL_USER_G_ID, DBCreate.COL_USER_MAIL, DBCreate.COL_USER_ACCNAME, DBCreate.COL_USER_FIRSTNAME, DBCreate.COL_USER_NAME},
                    null, null, null, null, DBCreate.COL_USER_ACCNAME+" ASC");
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
            Log.d("DB_Error DBStatements", "Unable to read Users from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return users;
    }

    public static List<User> getUsersOfChat(String chatId) {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();

        final String MY_QUERY = "SELECT * FROM " + DBCreate.getUserTable() + " a INNER JOIN " + DBCreate.TABLE_USERCHAT + " b ON a." + DBCreate.COL_USER_G_ID + "=b." + DBCreate.COL_USERCHAT_FK_USER +
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
            Log.d("DB_Error DBStatements", "Unable to read Users of Chat "+chatId+" from DB");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return users;
    }

    public static Chat getChat(String chatId) {
        Chat chat = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {

            Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_ID, DBCreate.COL_CHAT_FK_CREATOR, DBCreate.COL_CHAT_NAME, DBCreate.COL_CHAT_COLOR},
                    DBCreate.COL_CHAT_ID + "=?", new String[]{chatId}, null, null, null);
            if (c.moveToFirst()) {

                int id = c.getColumnIndex(DBCreate.COL_CHAT_ID);
                int creator = c.getColumnIndex(DBCreate.COL_CHAT_FK_CREATOR);
                int name = c.getColumnIndex(DBCreate.COL_CHAT_NAME);
                int color = c.getColumnIndex(DBCreate.COL_CHAT_COLOR);

              //     public Chat(String name, int color, String id, String admin)
                chat = new Chat(c.getString(name), c.getInt(color), c.getString(id), c.getString(creator));

            }
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Chat " + chatId + " from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }


        return chat;

    }

    public static List<Chat> getChat() {
        LinkedList<Chat> chats = new LinkedList<>();
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();

        try {

          Cursor  c= db.rawQuery("SELECT * FROM "+DBCreate.TABLE_CHAT,null);


            int id = c.getColumnIndex(DBCreate.COL_CHAT_ID);
            int creator = c.getColumnIndex(DBCreate.COL_CHAT_FK_CREATOR);
            int name = c.getColumnIndex(DBCreate.COL_CHAT_NAME);
            int color = c.getColumnIndex(DBCreate.COL_CHAT_COLOR);

            if (c.moveToFirst()) {
                do {

                    chats.add(new Chat(c.getString(name), c.getInt(color), c.getString(id), c.getString(creator)));
                }while (c.moveToNext());


            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Chats  from db");
        } finally {

            db.endTransaction();
        }


        return chats;

    }

    public static ArrayList<Message> getMessages(String chatId) {
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
                    messages.add(new Message(new Date(Long.parseLong(c.getString(timestmp))), c.getString(message), c.getString(id), (c.getInt(isEvent) == 1), c.getString(creator), chatId));

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Message of Chat "+chatId+" from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return messages;
    }

    public static Message getMessage(String messageID) {

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



               message =new Message(new Date(Long.parseLong(c.getString(timestmp))), c.getString(messageInt), c.getString(id), (c.getInt(isEvent) == 1), c.getString(creator), c.getString(chatId));


            }
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Message "+messageID+" from db");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return message;
    }

    public static Event getEvent(String messageId) {
        Message message = getMessage(messageId);
        Event event = null;

        if (message.isEvent()) {
            SQLiteDatabase db = dbConnection.getReadableDatabase();

            db.beginTransaction();
            try {

                Cursor c = db.query(DBCreate.TABLE_EVENT, new String[]{DBCreate.COL_EVENT_DATE, DBCreate.COL_EVENT_DESCRIPTION, DBCreate.COL_EVENT_STATE},
                        DBCreate.COL_EVENT_ID + "=?", new String[]{messageId}, null, null, null);


                if (c.moveToFirst()) {

                    int date = c.getColumnIndex(DBCreate.COL_EVENT_DATE);
                    int description = c.getColumnIndex(DBCreate.COL_EVENT_DESCRIPTION);
                    int state = c.getColumnIndex(DBCreate.COL_EVENT_STATE);

                    //public Event(Time timeStamp, String message, int id, boolean isEvent, String creatorID, Date date,
                    //                          String description,int chatid, int status) {


                    GregorianCalendar d = new GregorianCalendar();
                    d.setTime(new Date(Long.parseLong(c.getString(date))));
                    event = new Event(message.getTimeStampDate(), message.getMessage(), message.getId(), true, message.getCreator(),
                            d, c.getString(description), message.getChatid(), c.getInt(state));


                }
            } catch (Exception e) {
                Log.d("DB_Error DBStatements", "Unable to read Event from db");
            } finally {
                db.endTransaction();
            }

        }



        return event;
    }

    public static ArrayList<Event> getEvents() {
        //todo: make it mor efficient with INNERJOIN
         ArrayList<String> eventIDs= new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();

        db.beginTransaction();
        try {


            Cursor c = db.query(DBCreate.TABLE_EVENT, new String[]{DBCreate.COL_EVENT_ID},
                    DBCreate.COL_EVENT_STATE+"=?", new String[]{"0"}, null, null, null);
            if (c.moveToFirst()) {

                int eventId = c.getColumnIndex(DBCreate.COL_EVENT_ID);

                do {
                   eventIDs.add(c.getString(eventId));

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Users from db");
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

    public static  List<String> getEvents(String chatID){
      ArrayList<String> events = new ArrayList<>();

        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();
        try{
            Cursor c = db.query(DBCreate.TABLE_MESSAGE, new String[]{DBCreate.COL_MESSAGE_ID},DBCreate.COL_MESSAGE_ISEVENT+"=1 AND "+DBCreate.COL_MESSAGE_FK_CHATID+"=?",new String[]{chatID},null,null,null);
            if (c.moveToFirst()) {
                do {
                    events.add(c.getString(c.getColumnIndex(DBCreate.COL_MESSAGE_ID)));
                }while (c.moveToNext());
            }
            db.setTransactionSuccessful();

        }catch (Exception e){
            Log.e("DB error", "Unable to get Events from Chat "+chatID);

        }finally {
            db.endTransaction();
        }


      return events;
    }

    public static Message getLastMessage(String chatId) {
        Message message = null;
        SQLiteDatabase db = dbConnection.getReadableDatabase();
        db.beginTransaction();
        String messageID=null;

        try {

            Cursor c = db.query(DBCreate.TABLE_CHAT, new String[]{DBCreate.COL_CHAT_FK_LASTMESSAGE},
                    DBCreate.COL_CHAT_ID + "=?", new String[]{chatId}, null, null, null);
            if (c.moveToFirst()) {
                int messageIndex = c.getColumnIndex(DBCreate.COL_CHAT_FK_LASTMESSAGE);
                 messageID  = c.getString(messageIndex);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB_Error DBStatements", "Unable to read Chat " + chatId + " from db");

        } finally {
            db.endTransaction();
        }

        if(messageID!=null)
            return getMessage(messageID);
        return message;
    }


    private  static boolean deleteUserEventStatus(String chatID, String userID){
        SQLiteDatabase db = dbConnection.getWritableDatabase();

        List<String> eventIDs = getEvents(chatID);

        for (String eventId: eventIDs){
            try {
                db.delete(DBCreate.TABLE_EVENTUSER,DBCreate.COL_EVENTUSER_FK_USER+"=? AND "+DBCreate.COL_EVENT_FK_MESSAGEID+"=?",new String[]{userID,eventId});
                db.setTransactionSuccessful();
                db.endTransaction();
                for (Updateable u:updateables
                ) {
                    u.removeObject(new UserEventStatus(userID,eventId,0,"-"));

                }

            }catch (Exception e){
                Log.e("DatabaseError","Unable to delete witt id "+ userID);
                return false;
            }
        }
        db.beginTransaction();





        return true;
    }

    public static boolean deleteEvent(String eventID){
      Event event = getEvent(eventID);
      if(event==null){
          return false;
      }
      event.setStatus(2);
      updateEvent(event);

        return true;
    }

    public static  boolean deleteUser(String userID){
        boolean res = false;

        SQLiteDatabase db = dbConnection.getWritableDatabase();

        db.beginTransaction();
        try {
            db.delete(DBCreate.TABLE_USER,DBCreate.COL_USER_G_ID+"=?",new String[]{userID});
            db.setTransactionSuccessful();

            res = true;
        }catch (Exception e){
            Log.e("DatabaseError","Unable to delete User with id "+ userID);
        }finally {
            db.endTransaction();
        }


        return res;
    }

    public static boolean deleteChat(String chatID){
       Chat chat = getChat(chatID);
        SQLiteDatabase db = dbConnection.getWritableDatabase();

        if(chat!=null) {

            // Delete Chat
            db.beginTransaction();
            try {
                db.delete(DBCreate.TABLE_CHAT, DBCreate.COL_CHAT_ID + "=?", new String[]{chatID});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                return false;
            } finally {
                db.endTransaction();
            }

            //notify updateable
            for (Updateable u : updateables
            ) {
                u.removeObject(chat);
            }


            // Delete Chatmemmbers
            db.beginTransaction();
            try {
                db.delete(DBCreate.TABLE_USERCHAT, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{chatID});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                return false;
            } finally {
                db.endTransaction();
            }
            // DELETE EVENTS
            ArrayList<Event> events = getEvents();
            for (Event e : events) {
                if (e.getChatid().equals(chatID)) {
                    db.beginTransaction();
                    try {
                        db.delete(DBCreate.TABLE_EVENT, DBCreate.COL_EVENT_ID + "=?", new String[]{e.getId()});
                        db.setTransactionSuccessful();
                    } catch (Exception ex) {
                        return false;
                    } finally {
                        db.endTransaction();
                    }
                    //notify updateable
                    for (Updateable u : updateables
                    ) {
                        u.removeObject(e);
                    }

                    //DELETE USEREVENT STATES

                    db.beginTransaction();
                    try {
                        db.delete(DBCreate.TABLE_EVENTUSER, DBCreate.COL_EVENTUSER_FK_EVENT + "=?", new String[]{e.getId()});
                        db.setTransactionSuccessful();
                    } catch (Exception ex) {
                        return false;
                    } finally {
                        db.endTransaction();
                    }

                }

            }

            //DELETE MESSAGES
            db.beginTransaction();
            try {
                db.delete(DBCreate.TABLE_MESSAGE, DBCreate.COL_MESSAGE_FK_CHATID + "=?", new String[]{chatID});
                db.setTransactionSuccessful();
            } catch (Exception ex) {
                return false;
            } finally {
                db.endTransaction();
            }

        }

        return true;
    }

}
