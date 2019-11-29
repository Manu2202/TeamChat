package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.helper.FormatHelper;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterEvent extends BaseAdapter {

    private ArrayList<Event> events;
    private DBStatements db;


    public AdapterEvent(ArrayList<Event> events, DBStatements dbStatements) {
        this.events = events;
        db = dbStatements;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        Event ev = events.get(position);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_event_for_overview_menu, null, false);


        TextView tvMessage = convertView.findViewById(R.id.li_overview_eventtitle);
        TextView tvTime = convertView.findViewById(R.id.li_overview_eventtime);
        TextView tvUser = convertView.findViewById(R.id.li_overview_eventcreator);
        tvMessage.setText(ev.getMessage());
        tvTime.setText(ev.getTimeStamp().toString());

        // Display name of group this event belongs to (this is the event overview, so
        // it makes more sense here to display the group name instead of the event creator name)
        tvUser.setText(db.getChat(ev.getChatid()).getName());

        // Display event creator name
       // tvUser.setText(ev.getCreator());

        TextView tvDate = convertView.findViewById(R.id.li_overview_eventdate);

        tvDate.setText(FormatHelper.formatDateTime(ev.getDate()));


        return convertView;
    }



}
