package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.R;
import de.swproj.teamchat.view.fragments.FragmentMainChats;
import de.swproj.teamchat.view.fragments.FragmentMainContacts;
import de.swproj.teamchat.view.fragments.FragmentMainEvents;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ListView lvChat;
    private ListView lvEvents;
    private ListFragment chatFragment;
    private ListFragment eventFragment;
    private ListFragment contactFragment;
    private static Fragment lastSelectedFragment;

    //FirebaseAuth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                // TODO: Übergabe, von wo aus Activity aufgerufen wird
                startActivity(createEventIntent);
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
