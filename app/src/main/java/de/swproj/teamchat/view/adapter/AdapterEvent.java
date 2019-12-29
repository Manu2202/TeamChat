package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;

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

    private Event lastEvent;
    private int last_pos;
    private boolean evColorIsGroupColor = true;


    public AdapterEvent(ArrayList<Event> events) {
        this.events = events;
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
             ImageView icon_date = convertView.findViewById(R.id.li_icon_date);
             ImageView icon_time = convertView.findViewById(R.id.li_icon_time);

             tvTitle.setText(ev.getMessage());
             tvTime.setText(ev.getTimeStamp().toString());

             boolean YearisInPast = (Calendar.getInstance().get(Calendar.YEAR) > ev.getDate().get(Calendar.YEAR));
             boolean MonthisInPast = ((Calendar.getInstance().get(Calendar.MONTH) > ev.getDate().get(Calendar.MONTH)) && (Calendar.getInstance().get(Calendar.YEAR) == ev.getDate().get(Calendar.YEAR)));
             boolean isInPast = YearisInPast || MonthisInPast;
             // Separator
             // Will be displayed only once above Events happening on the same month
            //TODO still buggy if scrolled from bottom to top..
             boolean displaySeparator = false;
             boolean month=false,year=false;

             //!isinpast
             if (events.size() > 0) {
                 if (ev.getId().equals(events.get(0).getId())) {
                     spaceStart.setVisibility(View.VISIBLE);
                 }
                 if (lastEvent!=null){

                     if ( (ev.getDate().get(Calendar.YEAR) != lastEvent.getDate().get(Calendar.YEAR)) ||
                             (ev.getDate().get(Calendar.MONTH) != lastEvent.getDate().get(Calendar.MONTH))){
                         Log.d("Trennung zwischen",FormatHelper.getMonthfromDate(ev.getDate())+" und "+FormatHelper.getMonthfromDate(events.get(last_pos).getDate()));
                         displaySeparator = true;
                     }

                 }
                 lastEvent = ev;
                 last_pos = position;
             }

             separator.setText(FormatHelper.getMonthfromDate(ev.getDate()));

             if (displaySeparator ) {
                 separator.setVisibility(View.VISIBLE);
                 spaceBetweenSeparators.setVisibility(View.VISIBLE);
             } else {
                 separator.setVisibility(View.GONE);
             }


             // Make event cardview color the same as the color of the corresponding chat
             if (evColorIsGroupColor) {
                 CardView cardView = convertView.findViewById(R.id.li_message_cv);
                 /*if (System.currentTimeMillis() > ev.getDate().getTimeInMillis()){
                     cardView.setVisibility(View.GONE);
                 }*/
                 cardView.setCardBackgroundColor(DBStatements.getChat(ev.getChatid()).getColor());

                 //Calculate Contrast Ratio between Background color and White Text
                 double contrast_ratio = ColorUtils.calculateContrast(DBStatements.getChat(ev.getChatid()).getColor(), Color.parseColor("#FFFFFF"));

                 if (contrast_ratio < 4) {
                     //Black Text should be used
                     tvDescription.setTextColor(Color.parseColor("#000000"));
                     tvDate.setTextColor(Color.parseColor("#000000"));
                     tvGroupname.setTextColor(Color.parseColor("#000000"));
                     tvTime.setTextColor(Color.parseColor("#000000"));
                     tvTitle.setTextColor(Color.parseColor("#000000"));
                     //Black Icon should be used
                     icon_date.setImageResource(R.drawable.ic_event_black_24dp);
                     icon_time.setImageResource(R.drawable.ic_access_time_black_24dp);
                 }
             }

             // Display name of group this event belongs to (this is the event overview, so
             // it makes more sense here to display the group name instead of the event creator name)
             tvGroupname.setText(DBStatements.getChat(ev.getChatid()).getName());

             // Display event creator name
             // tvUser.setText(ev.getCreator());

             tvDescription.setText(ev.getDescription());
             tvTime.setText(FormatHelper.formatTime(ev.getDate()));
             tvDate.setText(FormatHelper.formatDate(ev.getDate()));
            return convertView;
    }

}