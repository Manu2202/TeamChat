package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;

import android.os.Bundle;
import android.view.View;

public class EditChatActivity extends AppCompatActivity {

    private int chatId;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);
    }

    public void saveChanges(View view){

    }
}
