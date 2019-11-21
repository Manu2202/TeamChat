package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.Connection.Database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EditChatActivity extends AppCompatActivity {

    private String chatId;
    private Chat chat;

    private HashMap<String, User> allUser;
    private HashMap<String, User> groupMember;

    private DBStatements dbStatements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);

        dbStatements = new DBStatements(EditChatActivity.this);

        // Get own Intent
        Intent ownIntent = getIntent();
        chatId = ownIntent.getStringExtra("ID");

        if (!chatId.equals("0"))
            chat = dbStatements.getChat(chatId);

        getAllUsers();
    }

    private void getAllUsers(){
        for (User user:dbStatements.getUser()) {
            if(!groupMember.containsKey(user.getGoogleId()))
                allUser.put(user.getGoogleId(), user);
        }
    }

    public void saveChanges(View view){

    }
}
