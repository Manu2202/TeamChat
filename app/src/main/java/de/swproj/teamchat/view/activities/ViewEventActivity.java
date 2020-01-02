package de.swproj.teamchat.view.activities;

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

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.adapter.AdapterUserEventStatus;
import de.swproj.teamchat.view.dialogs.ReasonDialog;
import de.swproj.teamchat.view.viewmodels.ViewEventViewModel;

public class ViewEventActivity extends AppCompatActivity {


    private String activeUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // private List<UserEventStatus> userEventStates;
    //   private UserEventStatus mystate;
    private TextView tvStatus;
    private ViewEventViewModel viewModel;
    private AdapterUserEventStatus adapter;
    private FirebaseConnection fbConnection;
    private boolean actUserIsAdmin;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        id = getIntent().getStringExtra("eventID");

        viewModel = new ViewEventViewModel(DBStatements.getUserEventStatus(id, activeUser), DBStatements.getUserEventStatus(id), DBStatements.getEvent(id));

        actUserIsAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid()
                .equals(viewModel.getLiveEvent().getValue().getCreator());

        final TextView tvCreator = findViewById(R.id.viewevent_tvcreator);
        final TextView tvtime = findViewById(R.id.viewevent_tvtime);
        final TextView tvtitle = findViewById(R.id.viewevent_tvtitle);
        final TextView tvDate = findViewById(R.id.viewevent_tveveventdate);
        final TextView tvTime = findViewById(R.id.viewevent_tveventtime);
        final TextView tvDescription = findViewById(R.id.viewevent_tvdescription);
        tvStatus = findViewById(R.id.viewevent_tvstatus);

        //set Observer
        viewModel.getLiveEvent().observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                tvCreator.setText(DBStatements.getUser(event.getCreator()).getAccountName());
                tvtime.setText(FormatHelper.formatTime(event.getTimeStamp()));
                tvDate.setText(FormatHelper.formatDate(event.getDate()));
                tvTime.setText(FormatHelper.formatTime(event.getDate()));
                tvtitle.setText(event.getMessage());
                tvDescription.setText(event.getDescription());
            }
        });

        Log.d("MYLOG", "ID: " + id + " User: " + activeUser);

        viewModel.getMyLiveState().observe(this, new Observer<UserEventStatus>() {
            @Override
            public void onChanged(UserEventStatus status) {
                tvStatus.setText(status.getStatusString());
            }
        });


        ListView lvStates = findViewById(R.id.viewevent_lvstates);
        lvStates.setDivider(null);
        adapter = new AdapterUserEventStatus(viewModel.getLiveStates().getValue());
        viewModel.getLiveStates().observe(this, new Observer<List<UserEventStatus>>() {
            @Override
            public void onChanged(List<UserEventStatus> userEventStatuses) {
                adapter.notifyDataSetChanged();
            }
        });


        //registart viewModel onDB
        DBStatements.addUpdateable(viewModel);

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
        UserEventStatus mystate = viewModel.getMyLiveState().getValue();
        mystate.setReason("-");
        mystate.setStatus(1);


        Thread thread = new test(mystate);
        thread.start();//remove firebase have to do it on Success
        //todo send to other to Firebase, remove line before

      /*  String message = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0]
                + " " + mystate.getStatusString();
       */
    }

    //todo: Delete Class after firebase implementation
    class test extends Thread {
        UserEventStatus status;

        public test(UserEventStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            super.run();
            DBStatements.updateUserEventStatus(status);
        }
    }

    public void cancleDialog(View view) {
        ReasonDialog rd = new ReasonDialog(this);
        rd.show();

    }

    public void cancleState(String reason) {


        UserEventStatus mystate = viewModel.getMyLiveState().getValue();
        mystate.setReason(reason);
        mystate.setStatus(2);

        Thread thread = new test(mystate);
        thread.start();//remove firebas have to do it
        //todo send to other to Firebase, remove line before

    }


    /*
     * On Click Method to send Event directly to phone calendar
     */
    public void sendToCalendar(View view) {
        Intent calendarIntent;
        if (Build.VERSION.SDK_INT >= 14) {  // Check if SDK is high enough for extra infos
            calendarIntent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, viewModel.getLiveEvent().getValue().getDate().getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            viewModel.getLiveEvent().getValue().getDate().getTimeInMillis() + 1000 * 60 * 60 * 3)  // Default Length of Event = 3h
                    .putExtra(CalendarContract.Events.TITLE, viewModel.getLiveEvent().getValue().getMessage())
                    .putExtra(CalendarContract.Events.DESCRIPTION, viewModel.getLiveEvent().getValue().getDescription())
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        } else {
            calendarIntent = new Intent(Intent.ACTION_EDIT);
            calendarIntent.setType("vnd.android.cursor.item/event");
            calendarIntent.putExtra("beginTime", viewModel.getLiveEvent().getValue().getDate().getTimeInMillis());
            calendarIntent.putExtra("endTime",
                    viewModel.getLiveEvent().getValue().getDate().getTimeInMillis() + 1000 * 60 * 60 * 3);
            calendarIntent.putExtra("title", viewModel.getLiveEvent().getValue().getMessage());
        }
        startActivity(calendarIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_event_menu, menu);
        if (actUserIsAdmin) {  // if user is admin, set delete Button true
            menu.findItem(R.id.btn_view_event_delete).setVisible(true);
            menu.findItem(R.id.view_event_btn_edit_event).setVisible(true);
        } else {
            menu.findItem(R.id.btn_view_event_delete).setVisible(false);
            menu.findItem(R.id.view_event_btn_edit_event).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btn_view_event_delete:
                acceptDeleteDialog();
                return true;

            case R.id.view_event_btn_edit_event:
                Intent editEventIntent = new Intent(this, EditEventActivity.class);
                editEventIntent.putExtra("ID", id);
                editEventIntent.putExtra("chatID", viewModel.getLiveEvent().getValue().getChatid());

                startActivity(editEventIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method to show Accepting Dialog and handle Button
    private void acceptDeleteDialog() {
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
