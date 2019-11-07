package de.swproj.teamchat.Connection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Time;

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;


public class DBStatements {
private DBConnection dbConnection;

public DBStatements (Context context){
    dbConnection = new DBConnection(context);
}



public int updateChat(Chat chat){
    int chatId=chat.getId();
    boolean isNew=true;
    SQLiteDatabase db = dbConnection.getReadableDatabase();

    // check if allready exists

    db.beginTransaction();
    Cursor c=db.query(DBCreate.TABLE_CHAT,new String []{DBCreate.COL_CHAT_ID},DBCreate.COL_CHAT_ID+"="+chatId,null,null,null,null);
   isNew= c.getCount()!=0;
   db.close();


   //Start writing

     db = dbConnection.getWritableDatabase();
    db.beginTransaction();



    ContentValues values = new ContentValues();
    //put values
    values.put(DBCreate.COL_CHAT_ID, chatId);
    values.put(DBCreate.COL_CHAT_NAME, chat.getName());
    values.put(DBCreate.COL_CHAT_COLOR, chat.getColor().toString());
    values.put(DBCreate.COL_CHAT_FK_Creator, chat.getAdmin().getGoogleId());


   try {
       //create new Chat
       if (isNew) {
          chatId = (int) db.insertOrThrow(DBCreate.TABLE_CHAT, null, values);

       }

       //update Chat with ID...
       else {
           int rows = db.update(DBCreate.TABLE_CHAT, values, DBCreate.COL_CHAT_ID + "=?",new String[]{""+chatId});


       }

   }catch (Exception e){
       Log.d("DB_Error class DBStatements:", "Unable to write CHAT in db");
   }finally {
       db.endTransaction();
   }

   db.beginTransaction();
    try{
        db.delete(DBCreate.TABLE_USERCHAT, DBCreate.COL_USERCHAT_FK_CHAT + "=?", new String[]{""+chatId}); // todo: Delete USer from Chat (Events)

        for (User u:chat.getCurrUsers()
        ) {
            values= new ContentValues();
            values.put(DBCreate.COL_USERCHAT_FK_CHAT, chatId);
            values.put(DBCreate.COL_USERCHAT_FK_USER, u.getGoogleId());
            db.insertOrThrow(DBCreate.TABLE_USERCHAT, null, values);
        }
    }catch (Exception e){
        Log.d("DB_Error class DBStatements:", "Unable to write CHAT_USER in db");
    }finally {
        db.endTransaction();
    }

   db.close();

    return chatId;
}


public boolean insertMessage (Message message){

    boolean insertsuccesfull=true;

    SQLiteDatabase db = dbConnection.getWritableDatabase();

    db.beginTransaction();

    ContentValues values = new ContentValues();
    //put values
    values.put(DBCreate.COL_MESSAGE_FK_CREATOR, message.getCreator().getGoogleId());
    values.put(DBCreate.COL_MESSAGE_FK_CHATID, message.getChatid());
    values.put(DBCreate.COL_MESSAGE_ISEVENT, message.isEvent());
    values.put(DBCreate.COL_MESSAGE_MESSAGE, message.getMessage());
    values.put(DBCreate.COL_MESSAGE_ISEVENT, message.isEvent());
    values.put(DBCreate.COL_MESSAGE_TIMESTAMP, message.getTimeStamp().getTime());



    int messageId = (int) db.insertOrThrow(DBCreate.TABLE_MESSAGE, null, values);

    if(message.isEvent()){
        Event e = (Event) message;
        values = new ContentValues();

        values.put(DBCreate.COL_EVENT_DATE, e.getDate().getTime());
        values.put(DBCreate.COL_EVENT_DESCRIPTION, e.getDescription());
        values.put(DBCreate.COL_EVENT_FK_MESSAGEID, messageId);

        int eventId = (int) db.insertOrThrow(DBCreate.TABLE_MESSAGE, null, values);



        // todo: get user from chat >> insert event user table




    }






    return insertsuccesfull;
}




}
