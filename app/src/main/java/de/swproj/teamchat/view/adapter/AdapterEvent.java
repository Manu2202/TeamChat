package de.swproj.teamchat.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.helper.ColorHelper;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.activities.ViewEventActivity;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterEvent extends BaseAdapter {


    private List<EventSeparator> eventsAndSeparators;
    private Activity activity;


    public AdapterEvent(List<EventSeparator> events, Activity a) {
        eventsAndSeparators = events;
        activity=a;

    }

    /**
     * Sorts Events by Date (most recent first)
     * Prepares a list which determines whether the separator (title with Date) should be displayed or not
     * Doing it here means we only have to do it once for the entire list, and we avoid a display bug
     */


    @Override
    public int getCount() {
        return eventsAndSeparators.size();
    }

    @Override
    public Object getItem(int position) {
        return eventsAndSeparators.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        final Event ev = eventsAndSeparators.get(position).getEv();
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

        // Separator
        // Will be displayed only once above Events happening on the same month


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
        final CardView cardView = convertView.findViewById(R.id.li_message_cv);
        cardView.setCardBackgroundColor(DBStatements.getChat(ev.getChatid()).getColor());

        //Calculate Contrast Ratio between Background color text. Set the text depending on the result
        String colorString = ColorHelper.cardViewColorContrast(
                DBStatements.getChat(ev.getChatid()).getColor(),
                new TextView[]{tvDescription, tvDate, tvGroupname, tvTime, tvTitle});


        // Set the icons belong to the colorstring
        if (colorString.equals("#000000")) {
            icon_date.setImageResource(R.drawable.ic_event_black_24dp);
            icon_time.setImageResource(R.drawable.ic_access_time_black_24dp);
        } else {
            icon_date.setImageResource(R.drawable.ic_event_white_24dp);
            icon_time.setImageResource(R.drawable.ic_access_time_white_24dp);
        }


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ViewEventActivity.class);

                Log.d("Adapter Message", "Open eventview: " + ev.getId());
                intent.putExtra("eventID", ev.getId());
                ActivityOptionsCompat op = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, (Pair<View, String>[])
                        new Pair[]{new Pair<View, String>(cardView, ViewCompat.getTransitionName(cardView))});

                activity.startActivity(intent, op.toBundle());
            }
        });


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