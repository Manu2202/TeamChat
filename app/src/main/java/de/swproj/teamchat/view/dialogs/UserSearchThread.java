package de.swproj.teamchat.view.dialogs;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.activities.MainActivity;

public class UserSearchThread implements Runnable {

    private Activity activity;
    private Context context;
    private DBStatements db;
    private UserSearchDialog usd;
    private String username;
    private long elapsedTime = 0;
    private long startTimeMillis;
    private int userExists;

    private User foundUserFromServer;
    private final int USER_WAS_NOT_SEARCHED_FOR = 0;
    private final int USER_DOES_NOT_EXIST = 1;
    private final int USER_WAS_FOUND = 2;


    // If there is no server response after this duration, User Search will stop
    long maximumWaitingTime = 5000;

    public UserSearchThread(Activity activity, Context context, DBStatements db,
                            UserSearchDialog usd, String username) {
        this.activity = activity;
        this.context = context;
        this.db = db;
        this.usd = usd;
        this.username = username;
        startTimeMillis = System.currentTimeMillis();
        foundUserFromServer = null;
        userExists = USER_WAS_NOT_SEARCHED_FOR;
    }

    /**
     * Queries Firebase Database if a searched-for User exists in it.
     * Thread will stop if either @param maximumWaitingTime is expired before Firebase Database responds,
     * or if Firebase Database reports that the user does (or does not) exist.
     *
     * Response will be sent back to UserSearchDialog to display the correct info,
     * so the user knows what's up.
     *
     */
    @Override
    public void run() {

        synchronized (this) {
            getUserFromFireBaseServer();
            while (userExists == USER_WAS_NOT_SEARCHED_FOR && elapsedTime < maximumWaitingTime) {
                elapsedTime = System.currentTimeMillis() - startTimeMillis;
            }

            // Let our Search Dialog know what the server was doing
            // userExists sends the Dialog an int
            // userExists = USER_WAS_NOT_SEARCHED_FOR = 0 : Server was never reached
            // userExists = USER_DOES_NOT_EXIST = 1 : Server was reached, but couldn't find this Username
            // userExists = USER_WAS_FOUND = 2 : Server was reached and returned our user
            // If you want to test different userExists values, set them in getUserFromFireBaseServer(),
            // not here.
            usd.setSearchResponseValue(userExists);

            // If user was found, send it to the Dialog
            if (userExists == USER_WAS_FOUND) {
                usd.receiveUserFromDatabase(foundUserFromServer);
            }

            // Make Dialog Update its UI
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    usd.reactToSearchResult();
                }
            });

        }
    }


    synchronized void getUserFromFireBaseServer(){

     try {
        // TODO: Implement int fireBaseGetUserExists(String username) : Check Firebase Database if User exists
        // Return values:   1 = User was not found
        //                  2 = User was found
         // Please do not use boolean, or we won't know when Firebase Database Connection fails
         // or when Server returned "false"

        // userExists = fireBaseGetUserExists(username);

         // To test different userExist values:
         // userExists = 2;

     } catch (Exception e) {
         Log.e("Firebase Database Check failed", "Error when Checking if User exists (in UserSearchThread)");
         e.printStackTrace();
     }

     try {
         if (userExists == USER_WAS_FOUND) {
             // TODO: Implement User FireBaseDBgetUser(String username) : Extract User from Firebase Database
             // foundUserFromServer = FireBaseDBgetUser(username);

             // TODO: Remove this dummy user if FireBaseDBgetUser is working
             // Dummy User for testing
             foundUserFromServer = new User("Dummy User", "dummy@user.de",
                     "Dummy User", "User", "Dummi");

         }
     } catch (Exception e) {
         Log.e("Firebase Database Check failed", "Error when extracting found User to UserSearchThread");
         e.printStackTrace();
     }
    }

}
