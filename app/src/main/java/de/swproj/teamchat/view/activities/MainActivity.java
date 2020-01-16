package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.EventExpirer;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.fragments.FragmentMainChats;
import de.swproj.teamchat.view.fragments.FragmentMainContacts;
import de.swproj.teamchat.view.fragments.FragmentMainEvents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static Context context;
    private ListFragment chatFragment;
    private ListFragment eventFragment;
    private ListFragment contactFragment;
    private static Fragment lastSelectedFragment;
    private FirebaseConnection fbconnect;
    private EventExpirer eventExpirer;

    //FirebaseAuth
    private FirebaseAuth mAuth;


    @Override
    protected void onResume() {
        super.onResume();

        //Save FCM from Notification Intent
        saveFCMtoDB();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (eventExpirer != null) {
            eventExpirer.shutdownNow();
            eventExpirer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        fbconnect = new FirebaseConnection();

        //Save FCM from Notification Intent
        saveFCMtoDB();

        //Setup Database
        DBStatements.setDbConnection(null);

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("User-Problem", "User is NULL going back to Start");
            //TeamChatMessagingService.disableFCM();
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();

        } else {
            TeamChatMessagingService.enableFCM();
            Log.d("User-Problem", "Logged in as User" + currentUser.getDisplayName() + " with UID of:" + currentUser.getUid());

            setUpUI();
        }

        eventExpirer = new EventExpirer(7, 15);

    }


    /*
     * If app is in background notification data comes from Intent
     * Has to be handled in the Launcher Activity
     * (or other predefined Activity)
     */
    private void saveFCMtoDB() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        if (extras!=null) {
            if (FirebaseTypes.valueOf(Integer.parseInt(extras.getString("type"))) == FirebaseTypes.Message) {
                Message msg;
                if (Boolean.valueOf(extras.getString("isEvent"))) {
                    //New Event-----------------------------------------
                    msg = new Event(extras.getString("timestamp"),
                            extras.getString("message"),
                            extras.getString("id"),
                            extras.getBoolean("isEvent"),
                            extras.getString("creator"),
                            extras.getString("date"),
                            extras.getString("description"),
                            extras.getString("chatid"),
                            Integer.parseInt(extras.getString("status")));
                } else {
                    //New Message
                    msg = new Message(FormatHelper.formatTime(extras.getString("timestamp")),
                            extras.getString("message"),
                            extras.getString("id"),
                            extras.getBoolean("isEvent"),
                            extras.getString("creator"),
                            extras.getString("chatid"));
                }
                switch (FirebaseActions.valueOf(Integer.parseInt(extras.getString("action")))) {
                    case ADD:
                        DBStatements.insertMessage(msg);
                        break;
                    case UPDATE:
                        if (msg.isEvent()) DBStatements.updateEvent((Event) msg);
                        break;
                }

            } else if (FirebaseTypes.valueOf(Integer.parseInt(extras.getString("type"))) == FirebaseTypes.Chat) {

                Chat chat = new Chat(extras.getString("name"), Integer.parseInt(extras.getString("color")), extras.getString("id"), extras.getString("admin"));
                List<String> users = Arrays.asList(extras.getString("users").split(";"));
                switch (FirebaseActions.valueOf(Integer.parseInt(extras.getString("action")))) {
                    case ADD:
                        DBStatements.insertChat(chat);
                        DBStatements.updateChatMembers(users, chat.getId());
                        break;
                    case UPDATE:
                        DBStatements.updateChat(chat);
                        List<String> users_2 = Arrays.asList(extras.getString("users").split(";"));
                        DBStatements.updateChatMembers(users, chat.getId());
                        break;
                    case REMOVE:
                        DBStatements.deleteChat(chat.getId());
                        //TODO delete Chat in Firebase
                        break;
                }

            } else if (FirebaseTypes.valueOf(Integer.parseInt(extras.getString("type"))) == FirebaseTypes.EVENTSTATE) {
                UserEventStatus userEventStatus = new UserEventStatus(extras.getString("userid"),
                        extras.getString("eventid"),
                        Integer.parseInt(extras.getString("status")),
                        extras.getString("reason"));
                switch (FirebaseActions.valueOf(Integer.parseInt(extras.getString("action")))) {
                    case UPDATE:
                        DBStatements.updateUserEventStatus(userEventStatus);
                }
            }
        }
    }
        
        /*
        if (extras != null) {
            //Got new Intent extras from Notification
            String message = extras.getString("message");
            Log.d("Save FCM", "Message: "+message);
            if (message!=null && message.length()>0 && DBStatements.getMessage(extras.getString("id"))==null) {
                //Message is new and relevant
                if(Boolean.parseBoolean(extras.getString("isInvite"))){
                    Log.d("Chat", "Got invite");
                    //Got new Invite -> Check if Chat is new
                    String chatid = extras.getString("chatid");
                    if (DBStatements.getChat(chatid)==null){
                        Log.d("Chat","chat nicht vorhanden");
                        //Chat is not in Database -> Get Chat from Firestore
                        fbconnect.saveChatbyID(chatid);
                    }
                }
                if(Boolean.valueOf(extras.getString("isEvent"))) {
                    //New Event received--------------------------------
                    Log.d("Save Fcm", "Save Event in Database");

                    Event event= new Event(FormatHelper.formatTime(extras.getString("timestamp")),
                            message,
                            extras.getString("id"),
                            Boolean.valueOf(extras.getString("isEvent")),
                            extras.getString("creator"),
                            FormatHelper.formatDate(extras.getString("date")),
                            extras.getString("description"),
                            extras.getString("chatid"),
                            extras.getInt("status"));

                    //Insert in Database
                    DBStatements.insertMessage(event);
                    }else {
                    //New Message received-------------------------------
                    Log.d("Save Fcm", "Save Message in Database");

                    Message msg = new Message(FormatHelper.formatTime(extras.getString("timestamp")),
                            message,
                            extras.getString("id"),
                            Boolean.valueOf((String) extras.get("isEvent")),
                            extras.getString("creator"),
                            extras.getString("chatid"));
                 //   Log.d("Save FCM Message from Intent", msg.getMessage());

                    //Insert in Database
                    DBStatements.insertMessage(msg);
                }
                //TODO: necessary to delete?
                getIntent().removeExtra("body");
            }
        }
    }/*

    /**
     * Private Method to setup the fragements and the Bottom Navigation View
     */
    private void setUpUI() {
        // Set the BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_main);
        // Initialize the Listfragments only when Activity is created
        chatFragment = new FragmentMainChats();
        eventFragment = new FragmentMainEvents();
        contactFragment = new FragmentMainContacts();

        //---------------------------------------------------------------  Test
        //addTestdat();
       // DBStatements.dropAll();

        //Set Up the first Fragment, if lastSelectedFragment is NULL, else use this
        if (lastSelectedFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, chatFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, lastSelectedFragment).commit();
        }


        // Set Up the Listener on the bottomNav
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_chats:
                        selectedFragment = chatFragment;
                        break;

                    case R.id.nav_events:
                        selectedFragment = eventFragment;
                        break;

                    case R.id.nav_contacts:
                        selectedFragment = contactFragment;

                }
                lastSelectedFragment = selectedFragment;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, selectedFragment).commit();

                return true;
            }
        });
    }

}
