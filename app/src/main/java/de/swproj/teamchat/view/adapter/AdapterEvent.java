package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.swproj.teamchat.Connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;


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
            TextView tvUser = convertView.findViewById(R.id.li_message_tvuser);
            tvMessage.setText(ev.getMessage());
            tvTime.setText(ev.getTimeStamp().toString());
            tvUser.setText(ev.getCreator());

            final Event event = db.getEvent(ev.getId());
            TextView tvDate = convertView.findViewById(R.id.li_event_tvdate);
            // tvDate.setText(event.getDate().toString());
            tvDate.setText("19.11.2019");



            return convertView;
    }

}
