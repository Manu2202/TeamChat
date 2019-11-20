package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;

import android.os.Bundle;
import android.view.View;

public class EditEventActivity extends AppCompatActivity {

    private Event event;
    private int msgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
    }

    public void onClickSaveChanges(View view){

    }

    public void onClickCancel(View view){

    }
}
