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
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.fragments.FragmentMainChats;
import de.swproj.teamchat.view.fragments.FragmentMainContacts;
import de.swproj.teamchat.view.fragments.FragmentMainEvents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private ListView lvChat;
    private ListView lvEvents;
    private ListFragment chatFragment;
    private ListFragment eventFragment;
    private ListFragment contactFragment;
    private static Fragment lastSelectedFragment;
    private DBStatements db;
    private FirebaseConnection fbconnect;

    //FirebaseAuth
    private FirebaseAuth mAuth;

    private void addTestdat() {

        //löscht alle eintäge beim Start
        db.dropAll();

        db.insertUser(new User("Gott", "sdbjhdj", "Der Herr der Dinge", "Gott", "Herr"));
        db.insertUser(new User("11", "sdjhnjhdj", "Horster", "Hors", "tidiot"));
        db.insertUser(new User("abc", "sdjjunhdj", "ICH", "Man", "Derine"));
        db.insertUser(new User("Gott2", "sdbjhdj", "Der Herr der Dinge2", "Gott2", "Herr"));
        db.insertUser(new User("emusk", "sdbf", "Elon Musk", "Musk", "Elon"));
        db.insertChat(new Chat("Gugel", (getResources().getIntArray(R.array.androidcolors))[0], "123", "11"));
        db.updateChatMembers(new String[]{"Gott", "11", "abc", "Gott2"}, "123");
        db.updateChatMembers(new String[]{"Gott", "11", "abc", "Gott2"}, "3434");


        //  db.insertChat(new Chat("Labergruppe", 0xFFFB0B03, "394", "Gott"));
        db.insertChat(new Chat("Tetris esport Team", (getResources().getIntArray(R.array.androidcolors))[1], "3934", "Gott"));
        db.insertChat(new Chat("Tee Party", (getResources().getIntArray(R.array.androidcolors))[2], "3954", "Gott"));
        db.insertChat(new Chat("Buchclub", (getResources().getIntArray(R.array.androidcolors))[3], "3941", "Gott"));
        db.insertChat(new Chat("Golfclub", (getResources().getIntArray(R.array.androidcolors))[4], "3434", "Gott"));
        db.insertChat(new Chat("Fußballclub", (getResources().getIntArray(R.array.androidcolors))[5], "34", "Gott"));
        db.insertChat(new Chat("Squash-Club", (getResources().getIntArray(R.array.androidcolors))[6], "3484", "Gott"));
        db.insertChat(new Chat("Tennis-Club", (getResources().getIntArray(R.array.androidcolors))[7], "324", "Gott"));
        db.insertChat(new Chat("Eishockey-Club", (getResources().getIntArray(R.array.androidcolors))[8], "474", "Gott"));

        Date currentTime = Calendar.getInstance().getTime();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setGregorianChange(new java.sql.Date(849494994));
        Time time = new Time(currentTime.getTime());
        db.insertMessage(new Message(time, "Hallo, der horst ist da!!!", "oho", false, "11", "123"));
        time.setTime(currentTime.getTime() + 5);
        db.insertMessage(new Event(time, "Panik", "546s", true, "11", gc, "hilfe ein virus", "123", (byte) 1));
        time.setTime(currentTime.getTime() + 10);
        db.insertMessage(new Message(time, "Coolbbb bfgtf ;D", "o4454546", false, "abc", "123"));
        time.setTime(currentTime.getTime() + 10);
        db.insertMessage(new Message(time, "Cool  hu;D", "o4584846", false, "11", "123"));
        time.setTime(currentTime.getTime() + 10);
        db.insertMessage(new Message(time, "Cool  huhu;D", "o448784546", false, "Gott2", "123"));
        time.setTime(currentTime.getTime() + 10);
        db.insertMessage(new Message(time, "Cool ;D", "o454846", false, "Gott", "123"));
        time.setTime(currentTime.getTime() + 10);
        db.insertMessage(new Message(time, "Ein Huhun ;D", "o4115jn546", false, "abc", "123"));

        db.insertMessage(new Event(time, "TourdeFrance", "4546s", true, "Gott", new GregorianCalendar(2020, 10, 27, 9, 6), "hilfe ein russ", "3434", (byte) 1));
        time.setTime(currentTime.getTime() + 10);

        db.insertMessage(new Event(time, "Mars Tour", "14546s", true, "emusk", new GregorianCalendar(2020, 10, 27, 9, 6), "colonize mars with me", "123", (byte) 1));
        time.setTime(currentTime.getTime() + 1555);


        Log.d("Main TestDaten  ", db.getUser().size() + "");
        for (User user : db.getUser()) {
            Log.d("User: ", user.getAccountName() + "");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Save FCM from Notification Intent
        saveFCMtoDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = new DBStatements(this);
        fbconnect = new FirebaseConnection(db);
        //fbconnect.saveUserByID("gTiVTJ7cjORANbGUw2hpPHZfG122");

        //Save FCM from Notification Intent
        saveFCMtoDB();
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

    }

    /**
     * If app is in background notification data comes from Intent
     * Has to be handled in the Launcher Activity
     * (or other predefined Activity)
     */
    private void saveFCMtoDB() {
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            //Got new Intent extras from Notification
            String message = extras.getString("message");
            Log.d("Save FCM", "Message: "+message);

            if (message!=null && message.length()>0) {
                if(Boolean.parseBoolean(extras.getString("isInvite"))){
                    Log.d("Chat", "Got invite");
                    //Got new Invite -> Check if Chat is new
                    String chatid = extras.getString("chatid");
                    if (db.getChat(chatid)==null){
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
                            Integer.parseInt(extras.getString("status")));
                    Log.d("Save FCM Event from Intent", event.getMessage());

                    //Insert in Database
                    db.insertMessage(event);
                    }else {
                    //New Message received-------------------------------
                    Log.d("Save Fcm", "Save Message in Database");

                    Message msg = new Message(FormatHelper.formatTime(extras.getString("timestamp")),
                            message,
                            extras.getString("id"),
                            Boolean.valueOf((String) extras.get("isEvent")),
                            extras.getString("creator"),
                            extras.getString("chatid"));
                    Log.d("Save FCM Message from Intent", msg.getMessage());

                    //Insert in Database
                    db.insertMessage(msg);
                }
                //TODO: necessary to delete?
                getIntent().removeExtra("body");
            }
        }
    }

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
        //db.dropAll();


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
