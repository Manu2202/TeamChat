package de.swproj.teamchat.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import de.swproj.teamchat.Connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.view.activities.ChatActivity;
import de.swproj.teamchat.view.activities.ViewEventActivity;
import de.swproj.teamchat.view.adapter.AdapterChat;
import de.swproj.teamchat.view.adapter.AdapterEvent;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainEvents extends ListFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO: Den Adapter erzeugen und mit Werten füllen und auf ListFragment setzen
        Log.d("Fragments:", "In Event Fragment");


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView list = getListView();
        String[] s = new String[]{"Hier steht was so abgeht", "Richtig Stabil Bruder", "Bruder muss groß", "xD"};
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.dummy_list_item, R.id.textView, s);
   //     setListAdapter(adapter);


        ArrayList<Event> events = new ArrayList<Event>();

        Byte test = 1;
        Time tstamp = new Time(33434);

        // Dummy Data (causes Crash)
      /*
        events.add(new Event(tstamp, "Tetris WM", "123", true, "Ich", new Date(12345),
                "Description", "01", test));
        events.add(new Event(tstamp, "Tetris WM 2", "124", true, "Blabla", new Date(12345),
                "Description", "01", test));
        events.add(new Event(tstamp, "Tetris WM 3", "125", true, "Egal", new Date(12345),
                "Description", "01", test));
        events.add(new Event(tstamp, "Tetris WM 4", "126", true, "Creator", new Date(12345),
                "Description", "01", test));
        events.add(new Event(tstamp, "Tetris WM 5", "127", true, "Jemand anders", new Date(12345),
                "Description", "01", test));

*/
        final AdapterEvent eventAdapter = new AdapterEvent(events, new DBStatements(getContext()));
        setListAdapter(eventAdapter);

        //TODO: Test ClickListener once Dummy Data + ViewEventActivity can be tested
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent eventIntent = new Intent(getActivity(), ViewEventActivity.class);
                Event selectedItem = (Event)eventAdapter.getItem(position);
                eventIntent.putExtra("eventID", selectedItem.getId());
                startActivityForResult(eventIntent, position);
            }
        });
    }
}
