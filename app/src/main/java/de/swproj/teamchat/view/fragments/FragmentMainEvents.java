package de.swproj.teamchat.view.fragments;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.view.activities.StartActivity;
import de.swproj.teamchat.view.activities.ViewEventActivity;
import de.swproj.teamchat.view.adapter.AdapterEvent;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainEvents extends ListFragment {

    private DBStatements dbStatements;
    private ArrayList<Event> eventList;
    private AdapterEvent adapterEvent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dbStatements = new DBStatements(getContext());
        eventList = dbStatements.getEvents();
        adapterEvent = new AdapterEvent(eventList, dbStatements);
        Log.d("Fragments:", "In Event Fragment");


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView list = getListView();
        //Todo: Add divider for each individual group
        //int[] colors = {0xFF000000, 0xFF000000, 0xFF000000};
        //list.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        //list.setDividerHeight(10);
        setListAdapter(adapterEvent);

        setHasOptionsMenu(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent eventIntent = new Intent(getActivity(), ViewEventActivity.class);
                Event selectedItem = (Event)adapterEvent.getItem(position);
                eventIntent.putExtra("eventID", selectedItem.getId());
                startActivityForResult(eventIntent, position);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_event_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.btn_main_event_logout:
                //delete Token from uID
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseConnection.deleteToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                TeamChatMessagingService.disableFCM();
                FirebaseAuth.getInstance().signOut();
                Intent startIntent = new Intent(getActivity(), StartActivity.class);
                startActivity(startIntent);
                getActivity().finish();
                break;
        }
        return true;
    }
}
