package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
        convertView = inflater.inflate(R.layout.listitem_event, null, false);


        TextView tvMessage = convertView.findViewById(R.id.li_message_tvmessage);
        TextView tvTime = convertView.findViewById(R.id.li_message_tvtime);
        TextView tvUser = convertView.findViewById(R.id.li_message_tvcreator);
        tvMessage.setText(ev.getMessage());
        tvTime.setText(ev.getTimeStamp().toString());
        tvUser.setText(ev.getCreator());

        TextView tvDate = convertView.findViewById(R.id.li_event_tvdate);

        GregorianCalendar date = ev.getDate();
        String dateString = FormatHelper.formatDate(date);
        String timeString = FormatHelper.formatTime(date);


        tvDate.setText(dateString + " @" + timeString + "Uhr");


        return convertView;
    }



}
