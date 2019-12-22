package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

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
    private Event lastEvent;
    private boolean evColorIsGroupColor = false;


    public AdapterEvent(ArrayList<Event> events, DBStatements dbStatements) {
        this.events = events;
        db = dbStatements;
        Collections.sort(this.events);
    }

    public void toggleEventColorToGroupColor() {
        evColorIsGroupColor = !evColorIsGroupColor;
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

        TextView separator = convertView.findViewById(R.id.listitem_event_separator);
        Space spaceStart = convertView.findViewById(R.id.eventview_space1);
        spaceStart.setVisibility(View.GONE);
        Space spaceBetweenSeparators = convertView.findViewById(R.id.eventview_space2);
        spaceBetweenSeparators.setVisibility(View.GONE);
        TextView tvTitle = convertView.findViewById(R.id.viewevent_tvtitle);
        TextView tvDate = convertView.findViewById(R.id.viewevent_tveveventdate);
        TextView tvTime = convertView.findViewById(R.id.viewevent_tveventtime);
        TextView tvGroupname = convertView.findViewById(R.id.viewevent_tvcreator);
        TextView tvDescription = convertView.findViewById(R.id.viewevent_tvdescription);
        tvTitle.setText(ev.getMessage());
        tvTime.setText(ev.getTimeStamp().toString());


        // Separator
        // Will be displayed only once above Events happening on the same day
        boolean displaySeparator = false;
        if (events.size() > 0) {
            if ( ev.getId().equals(events.get(0).getId()) ) {
                spaceStart.setVisibility(View.VISIBLE);
                displaySeparator = true;

            } else if ( ev.getDate().get(Calendar.YEAR) != lastEvent.getDate().get(Calendar.YEAR) ||
                    ev.getDate().get(Calendar.MONTH) != lastEvent.getDate().get(Calendar.MONTH) ||
                    ev.getDate().get(Calendar.DAY_OF_MONTH) != lastEvent.getDate().get(Calendar.DAY_OF_MONTH)
            ) {
                displaySeparator = true;
            }

            lastEvent = ev;
        }

        separator.setText(FormatHelper.formatDate(ev.getDate()));
        if (displaySeparator) {
            separator.setVisibility(View.VISIBLE);
            if ( !ev.getId().equals(events.get(0).getId()) ) {
                spaceBetweenSeparators.setVisibility(View.VISIBLE);
            }
        } else {
            separator.setVisibility(View.GONE);
        }


        // Make event cardview color the same as the color of the corresponding chat
        if (evColorIsGroupColor) {
            CardView cardView = convertView.findViewById(R.id.li_message_cv);
            cardView.setCardBackgroundColor(db.getChat(ev.getChatid()).getColor());
        }

        // Display name of group this event belongs to (this is the event overview, so
        // it makes more sense here to display the group name instead of the event creator name)
        tvGroupname.setText(db.getChat(ev.getChatid()).getName());

        // Display event creator name
       // tvUser.setText(ev.getCreator());

        tvDescription.setText(ev.getDescription());
        tvTime.setText(FormatHelper.formatTime(ev.getDate()));
        tvDate.setText(FormatHelper.formatDate(ev.getDate()));


        return convertView;
    }

}