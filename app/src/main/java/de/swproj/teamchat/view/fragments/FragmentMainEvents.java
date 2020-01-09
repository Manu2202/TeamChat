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
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import androidx.lifecycle.Observer;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.view.activities.StartActivity;
import de.swproj.teamchat.view.activities.ViewEventActivity;
import de.swproj.teamchat.view.adapter.AdapterEvent;
import de.swproj.teamchat.view.adapter.EventSeparator;
import de.swproj.teamchat.view.viewmodels.EventListViewModel;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainEvents extends ListFragment {

    private AdapterEvent adapterEvent;
    private EventListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DBStatements.removeUpdateable(viewModel);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Convert EVENT List to EVENTSEPERATOR List
        List<EventSeparator> eventList =prepareList(DBStatements.getEvents());


        viewModel = new EventListViewModel(eventList);
        adapterEvent = new AdapterEvent(viewModel.getLiveEvents().getValue());
        // Get the ListView out of the fragment
        ListView list = getListView();

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        setListAdapter(adapterEvent);

        // Register ViewModel in DBStatements
        DBStatements.addUpdateable(viewModel);

        viewModel.getLiveEvents().observe(this, new Observer<List<EventSeparator>>() {
            @Override
            public void onChanged(List<EventSeparator> eventSeparators) {
                adapterEvent.notifyDataSetChanged();
            }
        });

        setHasOptionsMenu(true);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent eventIntent = new Intent(getActivity(), ViewEventActivity.class);
                EventSeparator selectedItem = (EventSeparator)adapterEvent.getItem(position);
                eventIntent.putExtra("eventID", selectedItem.getEv().getId());
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

    private List<EventSeparator> prepareList(List<Event> events) {
        List<EventSeparator> eventsAndSeparators = new LinkedList<>();
        if (events.size() > 0) {
            Collections.sort(events);
            EventSeparator prevEvSep = null;

            for (Event ev : events) {
                if (prevEvSep != null) {
                    if ((ev.getDate().get(Calendar.YEAR) != prevEvSep.getEv().getDate().get(Calendar.YEAR)) ||
                            (ev.getDate().get(Calendar.MONTH) != prevEvSep.getEv().getDate().get(Calendar.MONTH))) {
                        prevEvSep = new EventSeparator(true, ev);
                    } else {
                        prevEvSep = new EventSeparator(false, ev);
                    }
                } else {
                    prevEvSep = new EventSeparator(true, ev);
                }

                eventsAndSeparators.add(prevEvSep);
            }
        }

        return  eventsAndSeparators;
    }
}
