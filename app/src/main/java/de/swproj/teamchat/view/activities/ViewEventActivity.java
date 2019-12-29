package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.adapter.AdapterUserEventStatus;
import de.swproj.teamchat.view.dialogs.ReasonDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ViewEventActivity extends AppCompatActivity {

    private Event event;
    private String activeUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<UserEventStatus> userEventStates;
    private UserEventStatus mystate;
    private TextView tvStatus;
    private AdapterUserEventStatus adapter;
    private FirebaseConnection fbConnection;
    private boolean actUserIsAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);


        String id = getIntent().getStringExtra("eventID");

        event = DBStatements.getEvent(id);

        actUserIsAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid()
                .equals(event.getCreator());

        TextView tvCreator = findViewById(R.id.viewevent_tvcreator);
        TextView tvtime = findViewById(R.id.viewevent_tvtime);
        TextView tvtitle = findViewById(R.id.viewevent_tvtitle);
        TextView tvDate = findViewById(R.id.viewevent_tveveventdate);
        TextView tvTime = findViewById(R.id.viewevent_tveventtime);
        TextView tvDescription = findViewById(R.id.viewevent_tvdescription);
        tvStatus = findViewById(R.id.viewevent_tvstatus);

        tvCreator.setText(DBStatements.getUser(event.getCreator()).getAccountName());
        tvtime.setText(FormatHelper.formatTime(event.getTimeStamp()));
        tvDate.setText(FormatHelper.formatDate(event.getDate()));
        tvTime.setText(FormatHelper.formatTime(event.getDate()));
        tvtitle.setText(event.getMessage());
        tvDescription.setText(event.getDescription());
        Log.d("MYLOG","ID: "+id+" User: "+ activeUser);

        mystate = DBStatements.getUserEventStatus(id, activeUser);
        if(mystate==null){
            mystate= new UserEventStatus("abc","4546s",0,"def");
        }
        //Log.d("getUserEventStatus",mystate.getReason());
        tvStatus.setText(mystate.getStatusString());


        //Get EventStatus and Print it in the List
        userEventStates = DBStatements.getUserEventStatus(id);

        //Log.d("ViewEventActivity", "State objects: " + userEventStates.size() + "  " + userEventStates.get(0).getUserId() + "  " + userEventStates.get(1).getUserId());
        ListView lvStates = findViewById(R.id.viewevent_lvstates);
        lvStates.setDivider(null);
        adapter = new AdapterUserEventStatus(userEventStates);

        lvStates.setAdapter(adapter);


        //Exclude Items from Animation
        Fade fade = new Fade();
        View deco = getWindow().getDecorView();
        fade.excludeTarget(deco, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        // end of exclude

    }

    public void commit(View view) {
        mystate.setReason("-");
        mystate.setStatus(1);
        DBStatements.updateUserEventStatus(mystate);
        tvStatus.setText(mystate.getStatusString());

        repaintMyState(mystate);

        String message = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0]
                + " " + mystate.getStatusString();

        fbConnection.addToFirestore(new Message(GregorianCalendar.getInstance().getTime(),
                message, false,
                FirebaseAuth.getInstance().getCurrentUser().getUid(), event.getChatid()),
                event.getMessage(), false, true);
    }

    private void repaintMyState(UserEventStatus state) {
        int i = 0;
        boolean b = true;
        while (i < userEventStates.size() && b) {
            if (userEventStates.get(i).getUserId().equals(activeUser)) {
                userEventStates.set(i, state);
                b = false;
            }
            i++;
        }
        adapter.notifyDataSetChanged();
    }

    public void cancleDialog(View view) {
        ReasonDialog rd = new ReasonDialog(this);
        rd.show();
        //todo: send state to server

        // sendToFB: eventTitle + userEventStatus
    }

    public void cancleState(String reason) {
        Log.d("ViewEvent dialog", "Reason: " + reason);
    }


    /*
     * On Click Method to send Event directly to phone calendar
     */
    public void sendToCalendar(View view){
        Intent calendarIntent;
        if (Build.VERSION.SDK_INT >= 14) {  // Check if SDK is high enough for extra infos
            calendarIntent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDate().getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            event.getDate().getTimeInMillis() + 1000 * 60 * 60 * 3)  // Default Length of Event = 3h
                    .putExtra(CalendarContract.Events.TITLE, event.getMessage())
                    .putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        } else {
            calendarIntent = new Intent(Intent.ACTION_EDIT);
            calendarIntent.setType("vnd.android.cursor.item/event");
            calendarIntent.putExtra("beginTime", event.getDate().getTimeInMillis());
            calendarIntent.putExtra("endTime",
                    event.getDate().getTimeInMillis() + 1000 * 60 * 60 * 3);
            calendarIntent.putExtra("title", event.getMessage());
        }
        startActivity(calendarIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_event_menu, menu);
        if(actUserIsAdmin)  // if user is admin, set delete Button true
            menu.findItem(R.id.btn_view_event_delete).setVisible(true);
        else
            menu.findItem(R.id.btn_view_event_delete).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.btn_view_event_delete:
                acceptDeleteDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to show Accepting Dialog and handle Button
    private void acceptDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Event");
        builder.setMessage("Are you sure you want to delete this event?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO: Löschen des Events und senden einer Nachricht an andere User

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

}
