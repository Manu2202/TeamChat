package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessage;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    public void sendMessage(View view){

    }

    public void sendEvent(Event event){

    }

    public void createEvent(View view){

    }
}
