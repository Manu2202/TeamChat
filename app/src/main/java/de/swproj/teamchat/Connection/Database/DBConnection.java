package de.swproj.teamchat.Connection.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="Omdb.db";
    public static final int DATABASE_VERSION=1;

    public DBConnection(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DBCreate.getChatTable());
        sqLiteDatabase.execSQL(DBCreate.getUserTable());
        sqLiteDatabase.execSQL(DBCreate.getUserChatTable());
        sqLiteDatabase.execSQL(DBCreate.getMessageTable());
        sqLiteDatabase.execSQL(DBCreate.getEventTable());
        sqLiteDatabase.execSQL(DBCreate.getEventUserTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBCreate.deleteChatTable());
        sqLiteDatabase.execSQL(DBCreate.deleteUserTable());
        sqLiteDatabase.execSQL(DBCreate.deleteUserChatTable());
        sqLiteDatabase.execSQL(DBCreate.deleteMessageTable());
        sqLiteDatabase.execSQL(DBCreate.deleteEventTable());
        sqLiteDatabase.execSQL(DBCreate.deleteEventUserTable());

        onCreate(sqLiteDatabase);
    }
}
