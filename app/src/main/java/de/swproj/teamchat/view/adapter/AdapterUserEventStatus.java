package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import de.swproj.teamchat.Connection.Database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.view.activities.ViewEventActivity;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterUserEventStatus extends BaseAdapter {

    private ArrayList<UserEventStatus> userEventStatuses;
    private DBStatements db;
    String authentictedUser = "abc";


    public AdapterUserEventStatus(ArrayList<UserEventStatus> userEventStatuses, DBStatements dbStatements) {
        this.userEventStatuses = userEventStatuses;
        db = dbStatements;

    }

    @Override
    public int getCount() {
        return userEventStatuses.size();
    }

    @Override
    public Object getItem(int position) {
        return userEventStatuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if (convertView == null) {


            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lf.inflate(R.layout.listitem_event, null, false);


        }
                return convertView;



    }
}