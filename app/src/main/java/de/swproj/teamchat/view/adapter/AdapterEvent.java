package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.helper.ColorHelper;
import de.swproj.teamchat.helper.FormatHelper;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterEvent extends BaseAdapter {

    private ArrayList<Event> events;
    private ArrayList<EventSeparator> eventsAndSeparators;

    private boolean evColorIsGroupColor = true;


    public AdapterEvent(ArrayList<Event> events) {
        this.events = events;
        prepareList();
    }

    /**
     * Sorts Events by Date (most recent first)
     * Prepares a list which determines whether the separator (title with Date) should be displayed or not
     * Doing it here means we only have to do it once for the entire list, and we avoid a display bug
     */
    private void prepareList() {
        if (events.size() > 0) {
            Collections.sort(this.events);
            eventsAndSeparators = new ArrayList<>();
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
        Event ev = eventsAndSeparators.get(position).getEv();
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
             //!isinpast


        if (eventsAndSeparators.get(position).needsSeparator()) {
            separator.setText(FormatHelper.getMonthfromDate(eventsAndSeparators.get(position).getEv().getDate()));
            separator.setVisibility(View.VISIBLE);
            if (position != 0) {
                // Larger space between Events on different months
                spaceBetweenSeparators.setVisibility(View.VISIBLE);
            } else {
                // Small space before the very first Event in the List
                spaceStart.setVisibility(View.VISIBLE);
            }
        } else {
            // If Events happen in the same month and year, there is no Date title or separating space between them
            separator.setVisibility(View.GONE);
            spaceBetweenSeparators.setVisibility(View.GONE);
        }

             // Make event cardview color the same as the color of the corresponding chat
             if (evColorIsGroupColor) {
                 CardView cardView = convertView.findViewById(R.id.li_message_cv);
                 cardView.setCardBackgroundColor(DBStatements.getChat(ev.getChatid()).getColor());

                 //Calculate Contrast Ratio between Background color and White Text
                 String colorString = ColorHelper.cardViewColorContrast(
                         DBStatements.getChat(ev.getChatid()).getColor());

                 tvDescription.setTextColor(Color.parseColor(colorString));
                 tvDate.setTextColor(Color.parseColor(colorString));
                 tvGroupname.setTextColor(Color.parseColor(colorString));
                 tvTime.setTextColor(Color.parseColor(colorString));
                 tvTitle.setTextColor(Color.parseColor(colorString));

                 // Set the icons belong to the colorstring
                 if (colorString.equals("#FFFFFF")) {
                     icon_date.setImageResource(R.drawable.ic_event_black_24dp);
                     icon_time.setImageResource(R.drawable.ic_access_time_black_24dp);
                 } else {
                     icon_date.setImageResource(R.drawable.ic_event_white_24dp);
                     icon_time.setImageResource(R.drawable.ic_access_time_white_24dp);
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