package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView lvChat;
    private ListView lvEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createChat(View view){

    }
}
