package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.activities.ViewEventActivity;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterMessage extends BaseAdapter {

    private ArrayList<Message> messages;
    private DBStatements db;
    String authentictedUser = "abc";
    private AppCompatActivity activity;//todo: Get Authenticated user for send and show

    public AdapterMessage(ArrayList<Message> messages, DBStatements dbStatements, AppCompatActivity activity) {
        this.messages = messages;
        db = dbStatements;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = messages.get(position);
        Context context = parent.getContext();
        User creator = db.getUser(message.getCreator());

        if(convertView==null) {

            if (message.isEvent()) {

                    LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = lf.inflate(R.layout.listitem_event, null, false);

            } else {


                LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // todo: creator mit act user vergleichen
                if (message.getCreator().equals(authentictedUser)) {
                    convertView = lf.inflate(R.layout.listitem_message, null, false);
                } else {
                    convertView = lf.inflate(R.layout.listitem_message2, null, false);
                }


            }
            Log.d("MessageAdapter Message: ", message.getMessage()+"  "+message.getCreator());
            TextView tvMessage = convertView.findViewById(R.id.li_message_tvmessage);
            TextView tvTime = convertView.findViewById(R.id.li_message_tvtime);
            TextView tvUser = convertView.findViewById(R.id.li_message_tvcreator);
            tvMessage.setText(message.getMessage());
            tvTime.setText(message.getTimeStamp().toString());
            tvUser.setText(creator.getAccountName());


            if (message.isEvent()) {
                Event event = db.getEvent(message.getId());
                TextView tvDate = convertView.findViewById(R.id.li_event_tvdate);
                //todo: fix date in DBstatemants

                tvDate.setText(FormatHelper.formatDate(event.getDate()));
                final CardView cv = convertView.findViewById(R.id.li_message_cv);


                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, ViewEventActivity.class);

                        Log.d("Adapter Message","Open eventview: " + message.getId());
                        intent.putExtra("eventID", message.getId());
                        ActivityOptionsCompat op = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, (Pair<View, String>[])
                                new Pair[]{new Pair<View, String>(cv, ViewCompat.getTransitionName(cv))});

                        activity.startActivity(intent, op.toBundle());
                    }
                });


            }
        }

            return convertView;
        }
    }

