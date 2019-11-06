package de.swproj.teamchat.Connection.Database;

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
             //   + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_G_ID + " TEXT PRIMARY KEY, "
                + COL_USER_MAIL + " TEXT, "
                + COL_USER_ACCNAME+ " TEXT,"
                + COL_USER_FIRSTNAME+ " TEXT,"
                + COL_USER_NAME + " TEXT)";
    }

    protected static String deleteUserTable() {
        return  "DROP TABLE IF EXISTS "+TABLE_USER;
    }

    //Table CHAT
    public static final String TABLE_CHAT = "CHAT";
    public static final String COL_CHAT_ID= BaseColumns._ID;
    public static final String COL_CHAT_NAME = "name";
    public static final String COL_CHAT_COLOR = "color";
    public static final String COL_CHAT_FK_Creator = "fk_creator";


    protected static String getChatTable() {
        return "CREATE TABLE " + TABLE_CHAT + " ("
                  + COL_CHAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CHAT_NAME + " TEXT, "
                + COL_CHAT_COLOR + " TEXT, "
                + COL_CHAT_FK_Creator + " TEXT  NOT NULL REFERENCES "+ TABLE_USER+")";
    }

    protected static String deleteChatTable() {
        return  "DROP TABLE IF EXISTS "+TABLE_CHAT;
    }


    //Table UserChat

    public static final String TABLE_USERCHAT ="USERCHAT";
    public static final String COL_USERCHAT_FK_CHAT="fk_CHAT";
    public static final String COL_USERCHAT_FK_USER="fk_USER";

    protected static String getUserChatTable(){
        return "CREATE TABLE "+ TABLE_USERCHAT+" ("
                + BaseColumns._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERCHAT_FK_CHAT + " INTEGER NOT NULL REFERENCES " + TABLE_CHAT + ","
                + COL_USERCHAT_FK_USER + " STRING NOT NULL REFERENCES " + TABLE_USER + ")";
    }

    protected static String deleteUserChatTable() {
        return  "DROP TABLE IF EXISTS "+TABLE_USERCHAT;
    }
}
