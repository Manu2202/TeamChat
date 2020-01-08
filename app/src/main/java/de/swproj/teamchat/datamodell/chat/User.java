package de.swproj.teamchat.datamodell.chat;

/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

import androidx.annotation.Nullable;

public class User {
    public User() {

    }

    //Todo googleID in UserID und googleMail in Email umbenennen
    private String googleId;
    private String googleMail;
    private String accountName;
    private String name;
    private String firstName;

    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User u = (User) obj;
        if (googleId.equals(u.googleId) && googleMail.equals(u.googleMail) && accountName.equals(u.accountName))
            return true;
        return false;
    }

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
