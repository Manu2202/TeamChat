package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class User {
    //Todo googleID in UserID und googleMail in Email umbenennen
    private String googleId;
    private String googleMail;
    private String accountName;
    private String name;
    private String firstName;

    public User(String googleId, String googleMail, String accountName, String name, String firstName) {
        this.googleId = googleId;
        this.googleMail = googleMail;
        this.accountName = accountName;
        this.name = name;
        this.firstName = firstName;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getGoogleMail() {
        return googleMail;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }
}
