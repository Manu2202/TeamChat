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
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBConnection;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
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
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        //Log.d("Messaging Service, Message FROM", remoteMessage.getFrom());

        sendNotification(notification, data);
        save_message(notification, data);
    }

    /**
     * If app is in foreground, notification data comes from onMessageReceived
     */
    private void save_message(RemoteMessage.Notification notification, Map<String, String> data) {

        Log.d("IS EVENT", "Ist es ein Event?:"+Boolean.valueOf(data.get("isEvent")));
        if (notification.getBody() != null && notification.getBody().length() > 0 && DBStatements.getMessage(data.get("id")) == null) {
            if (Boolean.parseBoolean(data.get("isInvite"))) {
                Log.d("Chat", "Got invite");
                //Got new Invite -> Check if Chat is new
                String chatid = data.get("chatid");
                if (DBStatements.getChat(chatid) == null) {
                    Log.d("Chat", "Chat nicht vorhanden");
                    //Chat is not in Database -> Get Chat from Firestore
                    fbconnect.saveChatbyID(chatid);
                }
            }
            if (Boolean.valueOf(data.get("isEvent"))) {
                //New Event-----------------------------------------
                Event event = new Event(FormatHelper.formatTime(data.get("timestamp")),
                        notification.getBody(),
                        data.get("id"),
                        Boolean.valueOf(data.get("isEvent")),
                        data.get("creator"),
                        FormatHelper.formatDate(data.get("date")),
                        data.get("description"),
                        data.get("chatid"),
                        Integer.parseInt(data.get("status")));
                //Log.d("Save FCM Event from onMessageReceived", event.getMessage() +
                //"Status:" + event.getStatus());
                //Save in Database
                DBStatements.insertMessage(event);

            } else if (Boolean.valueOf(data.get("isEventUpdate"))) {
                String userID = data.get("creator");
                String eventID = data.get("id");
                UserEventStatus userEventStatus = DBStatements.getUserEventStatus(eventID, userID);
                String status = notification.getBody();
                status = status.split(" ")[1];
                int intStatus = 0;
                if (status.equals("committed"))
                    intStatus = 1;
                else if (status.equals("cancelled"))
                    intStatus = 2;
                userEventStatus.setStatus(intStatus);

                DBStatements.updateUserEventStatus(userEventStatus);
            }
            else {
            //New Message
            Message msg = new Message(FormatHelper.formatTime(data.get("timestamp")),
                    notification.getBody(),
                    data.get("id"),
                    Boolean.valueOf(data.get("isEvent")),
                    data.get("creator"),
                    data.get("chatid"));
                //Log.d("Save FCM Message from onMessageReceived", msg.getMessage());
            //Save in Database
                DBStatements.insertMessage(msg);
        }
        }
    }


    private void save_token(final String token) {
        //Get shared Preference
        SharedPreferences sharedPref = this.getSharedPreferences(
                PREFERENCE_FILE_KEY, MODE_PRIVATE);
        //Write
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Token", token);
        editor.apply();
    }

    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        //Bundle bundle = new Bundle();
        Log.d("Message", "Got new Notification with mesage" + notification.getBody());
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
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
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