package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.Connection.Database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewEventActivity extends AppCompatActivity {
  private DBStatements db;
    private Event event;
    String activeUser="abc";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        db= new DBStatements(this);
        String id = getIntent().getStringExtra("eventID");

        event = db.getEvent(id);
        //todo: enumeration with Status ids (byte to string)

        UserEventStatus  userEventStatus= db.getUserEventStatus(id,activeUser);

        TextView tvCreator = findViewById(R.id.viewevent_tvcreator);
        TextView tvtime = findViewById(R.id.viewevent_tvtime);
        TextView tvName = findViewById(R.id.viewevent_tvname);
        TextView tvDate = findViewById(R.id.viewevent_tvdate);
        TextView tvDescripton = findViewById(R.id.viewevent_tvdescription);
        TextView tvStatus = findViewById(R.id.viewevent_tvstatus);

        tvCreator.setText(event.getCreator());
        tvtime.setText(event.getTimeStamp().toString());
        tvDate.setText(event.getDate().toString());
        tvName.setText(event.getMessage());
        tvDescripton.setText(event.getDescription());
        tvStatus.setText(event.getStatus());

        ArrayList<UserEventStatus> userEventStatuses = db.getUserEventStatus(id);
        ListView lvStates = findViewById(R.id.viewevent_lvstates);
        lvStates.setDivider(null);



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
}
