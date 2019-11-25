package de.swproj.teamchat.Connection.database;

import android.provider.BaseColumns;

public class DBCreate {


    //Table User
    public static final String TABLE_USER = "USER";
    public static final String COL_USER_G_ID = "googleID";
    public static final String COL_USER_MAIL = "gMail";
    public static final String COL_USER_ACCNAME = "accountName";
    public static final String COL_USER_FIRSTNAME = "firstName";
    public static final String COL_USER_NAME = "Name";

    protected static String getUserTable() {
        return "CREATE TABLE " + TABLE_USER + " ("
                + COL_USER_G_ID + " TEXT PRIMARY KEY, "
                + COL_USER_MAIL + " TEXT, "
                + COL_USER_ACCNAME + " TEXT,"
                + COL_USER_FIRSTNAME + " TEXT,"
                + COL_USER_NAME + " TEXT)";
    }

    protected static String deleteUserTable() {
        return "DROP TABLE IF EXISTS " + TABLE_USER;
    }

    //Table CHAT
    public static final String TABLE_CHAT = "CHAT";
    public static final String COL_CHAT_ID = BaseColumns._ID;
    public static final String COL_CHAT_NAME = "name";
    public static final String COL_CHAT_COLOR = "color";
    public static final String COL_CHAT_FK_Creator = "fk_creator";


    protected static String getChatTable() {
        return "CREATE TABLE " + TABLE_CHAT + " ("
                + COL_CHAT_ID + " TEXT PRIMARY KEY, "
                + COL_CHAT_NAME + " TEXT, "
                + COL_CHAT_COLOR + " INTEGER, "
                + COL_CHAT_FK_Creator + " TEXT  NOT NULL REFERENCES " + TABLE_USER + ")";
    }

    protected static String deleteChatTable() {
        return "DROP TABLE IF EXISTS " + TABLE_CHAT;
    }


    //Table UserChat

    public static final String TABLE_USERCHAT = "USERCHAT";
    public static final String COL_USERCHAT_ID = BaseColumns._ID;
    public static final String COL_USERCHAT_FK_CHAT = "fk_CHAT";
    public static final String COL_USERCHAT_FK_USER = "fk_USER";

    protected static String getUserChatTable() {
        return "CREATE TABLE " + TABLE_USERCHAT + " ("
                + COL_USERCHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERCHAT_FK_CHAT + " TEXT NOT NULL REFERENCES " + TABLE_CHAT + ","
                + COL_USERCHAT_FK_USER + " TEXT NOT NULL REFERENCES " + TABLE_USER + ")";
    }

    protected static String deleteUserChatTable() {
        return "DROP TABLE IF EXISTS " + TABLE_USERCHAT;
    }


    //Table MESSAGE

    public static final String TABLE_MESSAGE = "MESSAGE";
    public static final String COL_MESSAGE_ID = BaseColumns._ID;
    public static final String COL_MESSAGE_TIMESTAMP = "timestamp";
    public static final String COL_MESSAGE_MESSAGE = "message";
    public static final String COL_MESSAGE_FK_CREATOR = "fk_Creator";
    public static final String COL_MESSAGE_FK_CHATID = "chatID";
    public static final String COL_MESSAGE_ISEVENT = "isEvent";

    protected static String getMessageTable() {
        return "CREATE TABLE " + TABLE_MESSAGE + " ("
                + COL_MESSAGE_ID + " TEXT PRIMARY KEY, "
                + COL_MESSAGE_TIMESTAMP + " TEXT,"
                + COL_MESSAGE_MESSAGE + " TEXT,"
                + COL_MESSAGE_ISEVENT + " INTEGER,"
                + COL_MESSAGE_FK_CREATOR + " TEXT NOT NULL REFERENCES " + TABLE_USER + ","
                + COL_MESSAGE_FK_CHATID + " TEXT NOT NULL REFERENCES " + TABLE_CHAT + " )";

    }

    protected static String deleteMessageTable() {
        return "DROP TABLE IF EXISTS " + TABLE_MESSAGE;
    }

    //Table EVENT

    public static final String TABLE_EVENT = "EVENT";
    public static final String COL_EVENT_ID = BaseColumns._ID;
    public static final String COL_EVENT_DATE = "date";
    public static final String COL_EVENT_DESCRIPTION = "description";
    public static final String COL_EVENT_FK_MESSAGEID = "fk_MessageID";

    protected static String getEventTable() {
        return "CREATE TABLE " + TABLE_EVENT + " ("
                + COL_EVENT_ID + " TEXT PRIMARY KEY, "
                + COL_EVENT_DATE + " TEXT, "
                + COL_EVENT_DESCRIPTION + " TEXT,"
                + COL_EVENT_FK_MESSAGEID + "TEXT NOT NULL REFERENCES " + TABLE_MESSAGE + " )";
    }

    protected static String deleteEventTable() {
        return "DROP TABLE IF EXISTS " + TABLE_EVENT;
    }


    //Table EventUser

    public static final String TABLE_EVENTUSER = "EVENTUSER";
    public static final String COL_EVENTUSER_ID = BaseColumns._ID;
    public static final String COL_EVENTUSER_FK_EVENT = "fk_EVENT";
    public static final String COL_EVENTUSER_FK_USER = "fk_USER";
    public static final String COL_EVENTUSER_STATUS = "staus";
    public static final String COL_EVENTUSER_REASON = "reason";

    protected static String getEventUserTable() {
        return "CREATE TABLE " + TABLE_EVENTUSER + " ("
                + COL_EVENTUSER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EVENTUSER_FK_EVENT + " INTEGER NOT NULL REFERENCES " + TABLE_EVENT + ","
                + COL_EVENTUSER_FK_USER + " TEXT NOT NULL REFERENCES " + TABLE_USER+","
                + COL_EVENTUSER_STATUS + " INTEGER" + ","
                + COL_EVENTUSER_REASON + " TEXT "
                + ")";
    }

    protected static String deleteEventUserTable() {
        return "DROP TABLE IF EXISTS " + TABLE_EVENTUSER;
    }


}
