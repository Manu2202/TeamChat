package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.Connection.Database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.view.adapter.AdapterMessage;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etMessage;
    private Chat chat;
    public DBStatements db;
    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvMessages = findViewById(R.id.lvMessages);
        etMessage = findViewById(R.id.etMessage);

        db= new DBStatements(this);

        int id = getIntent().getIntExtra("chatID",0);

        chat = db.getChat(id);
        messages=db.getMessages(id);

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
  //todo: send Message implementaion
    }


    public void createEvent(View view){
        //todo: creat Event implementaion
    }
}
