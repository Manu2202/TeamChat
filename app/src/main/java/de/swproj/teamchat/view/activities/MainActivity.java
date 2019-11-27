package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.Connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.adapter.AdapterChat;
import de.swproj.teamchat.view.fragments.FragmentMainChats;
import de.swproj.teamchat.view.fragments.FragmentMainContacts;
import de.swproj.teamchat.view.fragments.FragmentMainEvents;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lvChat;
    private ListView lvEvents;
    private ListFragment chatFragment;
    private ListFragment eventFragment;
    private ListFragment contactFragment;
    private static Fragment lastSelectedFragment;
    private DBStatements db;


    //FirebaseAuth
    private FirebaseAuth mAuth;

    private void  addTestdat(){

        //löscht alle eintäge beim Start
        db.dropAll();

        db.insertUser(new User("Gott","sdbjhdj","Der Herr der Dinge","Gott","Herr"));
        db.insertUser(new User("11","sdjhnjhdj","Horster","Hors","tidiot"));
        db.insertUser(new User("abc","sdjjunhdj","ICH","Man","Derine"));
        db.insertUser(new User("Gott2","sdbjhdj","Der Herr der Dinge2","Gott2","Herr"));
        db.insertChat(new Chat("test",0xFF0C00F1,"123","11"));
        db.updateChatMembers(new String[]{"Gott","11","abc","Gott2"},"123");

      //  db.insertChat(new Chat("Labergruppe", 0xFFFB0B03, "394", "Gott"));
        db.insertChat(new Chat("Tetris esport Team", 0xFFFBB400, "3934", "Gott"));
        db.insertChat(new Chat("Anonyme Alkoholiker", 0xFFB0FB03, "3954", "Gott"));
        db.insertChat(new Chat("Öffentliche Alkoholiker", 0xFF00FB71, "3941", "Gott"));
        db.insertChat(new Chat("Saufgruppe 1", 0xFF0C00F1, "3434", "Gott"));
        db.insertChat(new Chat("Saufgruppe 2", 0xFF038814, "34", "Gott"));
        db.insertChat(new Chat("Saufgruppe 3", 0xFF880E51, "3484", "Gott"));
        db.insertChat(new Chat("Saufgruppe 4", 0xFF884318, "324", "Gott"));
        db.insertChat(new Chat("Saufgruppe 5", 0xFF004888, "474", "Gott"));

        Date currentTime = Calendar.getInstance().getTime();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setGregorianChange(new java.sql.Date(849494994));
             Time time =new Time (currentTime.getTime());
        db.insertMessage(new Message(time,"Hallo, der horst ist da!!!","oho",false,"11","123"));
        time.setTime(currentTime.getTime()+5);
        db.insertMessage(new Event(time,"Panik","546s",true,"11",gc,"hilfe ein virus","123",(byte)1));
        time.setTime(currentTime.getTime()+10);
        db.insertMessage(new Message(time,"Coolbbb bfgtf ;D","o4454546",false,"abc","123"));
        time.setTime(currentTime.getTime()+10);
        db.insertMessage(new Message(time,"Cool  hu;D","o4584846",false,"11","123"));
        time.setTime(currentTime.getTime()+10);
        db.insertMessage(new Message(time,"Cool  huhu;D","o448784546",false,"Gott2","123"));
        time.setTime(currentTime.getTime()+10);
        db.insertMessage(new Message(time,"Cool ;D","o454846",false,"Gott","123"));
        time.setTime(currentTime.getTime()+10);
        db.insertMessage(new Message(time,"Ein Huhun ;D","o4115jn546",false,"abc","123"));

        db.insertMessage(new Event(time,"TourdeFrance","4546s",true,"Gott",gc,"hilfe ein russ","123",(byte)1));
        time.setTime(currentTime.getTime()+10);


        Log.d("Main TestDaten  ",db.getUser().size()+"");
        for(User user:db.getUser()){
            Log.d("User: ",user.getAccountName()+"");
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db= new DBStatements(this);
        setUpUI();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendtoStart();
        }
    }
    private void sendtoStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.btn_main_logout:
                FirebaseAuth.getInstance().signOut();
                sendtoStart();
                break;
            case R.id.btn_main_create_event:
                Intent createEventIntent = new Intent(this, EditEventActivity.class);
                // Put in a Extra to get know, which id the Event get (-> 0 = new Event)
                createEventIntent.putExtra("ID", "0");
                startActivity(createEventIntent);
                break;
            case R.id.btn_main_new_chat:
                Intent createChatIntent = new Intent(this, EditChatActivity.class);
                // ID = 0 -> new Chat
                createChatIntent.putExtra("ID", "0");
                startActivity(createChatIntent);
                break;
        }

        return true;
    }


    /*
     * Private Method to setup the fragements and the Bottom Navigation View
     */
    private void setUpUI(){
        // Set the BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_main);
        // Initialize the Listfragments only when Activity is created
        chatFragment = new FragmentMainChats();
        eventFragment = new FragmentMainEvents();
        contactFragment = new FragmentMainContacts();
        //---------------------------------------------------------------  Test
        addTestdat();



        //Set Up the first Fragment, if lastSelectedFragment is NULL, else use this
        if(lastSelectedFragment == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, chatFragment).commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_main, lastSelectedFragment).commit();
        }


        // Set Up the Listener on the bottomNav
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId()) {
                    case R.id.nav_chats:
                        selectedFragment = chatFragment;
                        break;

                    case R.id.nav_events:
                        selectedFragment = eventFragment;
                        break;

                    case R.id.nav_contacts:
                        selectedFragment = contactFragment;
                        break;
                }
                lastSelectedFragment = selectedFragment;

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_main, selectedFragment).commit();

            return true;
            }
        });
    }

}
