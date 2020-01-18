package de.swproj.teamchat.connection.firebase.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.activities.MainActivity;

import static de.swproj.teamchat.view.activities.LoginActivity.PREFERENCE_FILE_KEY;

public class TeamChatMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_NAME = "FCM";
    private static final String CHANNEL_DESC = "Firebase Cloud Messaging";
    private int numMessages = 0;

    FirebaseConnection fbconnect = new FirebaseConnection();

    @Override
    public void onNewToken(final String token) {
        Log.d("Messaging Service", "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseConnection.updateToken(FirebaseAuth.getInstance().getCurrentUser().getUid(), token);
        }
        //Store Token in Shared Prefs. If User is logged back in we can continue.
        save_token(token);
    }

    public static void enableFCM() {
        // Enable FCM via enable Auto-init service which generate new token and receive in FCMService
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public static void disableFCM() {
        // Disable auto init
        FirebaseMessaging.getInstance().setAutoInitEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Remove InstanceID initiate to unsubscribe all topic
                    // TODO: May be a better way to use FirebaseMessaging.getInstance().unsubscribeFromTopic()
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Message received", "Message received");
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        //Log.d("Messaging Service, Message FROM", remoteMessage.getFrom());

        sendNotification(data.get("title"),data.get("body"));
        save_message(data);
    }

    /**
     * If app is in foreground, notification data comes from onMessageReceived
     */
    private void save_message(Map<String, String> data) {
        Log.d("FB Debug Type", "Type = " + data.get("type"));
            if (FirebaseTypes.valueOf(Integer.parseInt(data.get("type"))) == FirebaseTypes.Message) {
                Message msg;
                if (Boolean.valueOf(data.get("isEvent"))) {
                    //New Event-----------------------------------------
                    msg = new Event(data.get("timestamp"),
                            data.get("message"),
                            data.get("id"),
                            Boolean.valueOf(data.get("isEvent")),
                            data.get("creator"),
                            data.get("date"),
                            data.get("description"),
                            data.get("chatid"),
                            Integer.parseInt(data.get("status")));
                } else {
                    //New Message
                    msg = new Message(data.get("timestamp"),
                            data.get("message"),
                            data.get("id"),
                            Boolean.valueOf(data.get("isEvent")),
                            data.get("creator"),
                            data.get("chatid"));
                }
                switch (FirebaseActions.valueOf(Integer.parseInt(data.get("action")))) {
                    case ADD:
                        DBStatements.insertMessage(msg);
                        break;
                    case UPDATE:
                        if (msg.isEvent()) DBStatements.updateEvent((Event) msg);
                        break;
                }

            } else if (FirebaseTypes.valueOf(Integer.parseInt(data.get("type"))) == FirebaseTypes.Chat) {

                Chat chat = new Chat(data.get("name"), Integer.parseInt(data.get("color")), data.get("id"), data.get("admin"));
                List<String> users = Arrays.asList(data.get("users").split(";"));
                List<String> missingUserIDs= new ArrayList<>();
                switch (FirebaseActions.valueOf(Integer.parseInt(data.get("action")))) {
                    case ADD:
                        DBStatements.insertChat(chat);
                        Log.d("TMS", "Add Chat and missing Users");
                        for (String userID: users) {
                            if (DBStatements.getUser(userID)==null){
                                Log.d("Add missing User", "User "+userID+ "missing");
                                missingUserIDs.add(userID);
                            }
                        }
                        //Get missing User IDs from Firebase
                        fbconnect.saveUserByIDs(missingUserIDs);
                        DBStatements.updateChatMembers(users,chat.getId());
                        break;
                    case UPDATE:
                        DBStatements.updateChat(chat);
                        Log.d("TMS", "Update Chat and missing Users");
                        for (String userID: users) {
                            Log.d("TMS","is User in database?"+(DBStatements.getUser(userID)==null));
                            if (DBStatements.getUser(userID)==null){
                                Log.d("Add missing User", "User "+userID+ "missing");
                                missingUserIDs.add(userID);
                            }
                        }
                        //Get missing User IDs from Firebase

                        fbconnect.saveUserByIDs(missingUserIDs);
                        DBStatements.updateChatMembers(users,chat.getId());
                        break;
                    case REMOVE:
                        DBStatements.deleteChat(chat.getId());
                        //TODO delete Chat in Firebase
                        break;
                }

            } else if (FirebaseTypes.valueOf(Integer.parseInt(data.get("type"))) == FirebaseTypes.EVENTSTATE) {
                UserEventStatus userEventStatus = new UserEventStatus(data.get("userid"),
                        data.get("eventid"),
                        Integer.parseInt(data.get("status")),
                        data.get("reason"));
                switch (FirebaseActions.valueOf(Integer.parseInt(data.get("action")))){
                    case UPDATE:
                        DBStatements.updateUserEventStatus(userEventStatus);
                }
            }
    }


        private void save_token ( final String token){
            //Get shared Preference
            SharedPreferences sharedPref = this.getSharedPreferences(
                    PREFERENCE_FILE_KEY, MODE_PRIVATE);
            //Write
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Token", token);
            editor.apply();
        }

        private void sendNotification (String title, String body){
            //Bundle bundle = new Bundle();
            Log.d("Message", "Got new Notification with message" + body);
            //bundle.putString("body", notification.getBody());

            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtras(bundle);

            // Creates an Intent for the Activity
            Intent pendingIntent = new Intent(this, MainActivity.class);
            // Sets the Activity to start in a new, empty task
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Creates the PendingIntent
            PendingIntent notifyPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            pendingIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "noti Builder"/*getString(/*R.string.notification_channel_id)*/)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.win))
                    .setContentIntent(notifyPendingIntent)
                    .setContentInfo("Hello")
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setColor(getColor(R.color.colorAccent))
                    .setLights(Color.RED, 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setNumber(++numMessages)
                    .setSmallIcon(R.drawable.ic_access_time_white_24dp);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "NotiChannelID"/*getString(R.string.notification_channel_id)*/, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(CHANNEL_DESC);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            //Todo Notification ID?
            assert notificationManager != null;
            notificationManager.notify(0, notificationBuilder.build());
        }
    }