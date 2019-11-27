package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.view.adapter.AdapterMessage;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;



import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etMessage;
    private Chat chat;
    private String chatID;
    public DBStatements db;
    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvMessages = findViewById(R.id.lvMessages);

        etMessage = findViewById(R.id.etMessage);

        db = new DBStatements(this);

        chatID = getIntent().getStringExtra("chatID");

        chat = db.getChat(chatID);

        setTitle(chat.getName());
        messages=db.getMessages(chatID);

        lvMessages.setAdapter(new AdapterMessage(messages,db,this));


        //Exclude Items from Animation
        Fade fade = new Fade();
        View deco = getWindow().getDecorView();
        fade.excludeTarget(deco, true);
        fade.excludeTarget(android.R.id.statusBarBackground,true);
        fade.excludeTarget(android.R.id.navigationBarBackground,true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        // end of exclude

    }


    public void sendMessage(View view){
     //   Time timeStamp, String message, boolean isEvent, User creator,int chatid

  //todo: send Message implementaion
    }


    public void createEvent(View view){
        //todo: creat Event implementaion
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.btn_chat_menu_newEvent:
                Intent newEventIntent = new Intent(this, EditEventActivity.class);
                newEventIntent.putExtra("ID", chatID);
                newEventIntent.putExtra("eventID", "0");
                startActivity(newEventIntent);
                break;
        }

        return true;
    }
}
