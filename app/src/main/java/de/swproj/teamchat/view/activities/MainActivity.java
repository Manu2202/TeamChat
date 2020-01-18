package de.swproj.teamchat.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.EventExpirer;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.fragments.FragmentMainChats;
import de.swproj.teamchat.view.fragments.FragmentMainContacts;
import de.swproj.teamchat.view.fragments.FragmentMainEvents;


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
        //saveFCMtoDB();
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
        //saveFCMtoDB();

        //Setup Database
        DBStatements.setDbConnection(null);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    public void onStart() {
        super.onStart();

        // DBStatements.dropAll();
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
