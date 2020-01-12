package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import com.google.firebase.auth.FirebaseAuth;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.ColorHelper;
import de.swproj.teamchat.helper.FormatHelper;
import de.swproj.teamchat.view.activities.ViewEventActivity;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterMessage extends BaseAdapter {

    private ArrayList<Message> messages;
    private AppCompatActivity activity;

    public AdapterMessage(ArrayList<Message> messages, AppCompatActivity activity) {
        this.messages = messages;
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
        User creator = DBStatements.getUser(message.getCreator());

     //   if(convertView==null) {

            if (message.isEvent()) {

                    LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = lf.inflate(R.layout.listitem_event, null, false);

            } else {


                LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (message.getCreator().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    convertView = lf.inflate(R.layout.listitem_message2, null, false);
                } else {
                    convertView = lf.inflate(R.layout.listitem_message, null, false);
                }


      }

            TextView tvMessage = convertView.findViewById(R.id.viewevent_tvtitle);
            TextView tvTime = convertView.findViewById(R.id.viewevent_tvtime);
            TextView tvUser = convertView.findViewById(R.id.viewevent_tvcreator);
            tvMessage.setText(message.getMessage());
            tvTime.setText(FormatHelper.formatTime(message.getTimeStamp()));
            if(creator!=null)
            tvUser.setText(creator.getAccountName());


            if (message.isEvent()) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                UserEventStatus ues = DBStatements.getUserEventStatus(message.getId(), mAuth.getCurrentUser().getUid());

                Event event = DBStatements.getEvent(message.getId());
                TextView tvEventDate = convertView.findViewById(R.id.viewevent_tveveventdate);
                TextView tvEventTime = convertView.findViewById(R.id.viewevent_tveventtime);
                TextView tv_Description = convertView.findViewById(R.id.viewevent_tvdescription);
                ImageView icon_date = convertView.findViewById(R.id.li_icon_date);
                ImageView icon_time = convertView.findViewById(R.id.li_icon_time);

                tv_Description.setText(event.getDescription());
                tvEventDate.setText(FormatHelper.formatDate(event.getDate()));
                tvEventTime.setText(FormatHelper.formatTime(event.getDate()));

                // Colored CardView for Events
                final CardView cv = convertView.findViewById(R.id.li_message_cv);
                int eventColor = DBStatements.getChat(message.getChatid()).getColor();

                // UserEventStatus: Not final yet - I don't know which value means the Event is completely cancelled

                cv.setCardBackgroundColor(eventColor);

                //Calculate Contrast Ratio between Background color text. Set the text depending on the result
                String colorString = ColorHelper.cardViewColorContrast(eventColor,
                        new TextView[]{tv_Description, tvEventDate, tvUser, tvTime, tvMessage, tvEventTime});

                // Set the icons belong to the colorstring
                if (colorString.equals("#000000")) {
                    icon_date.setImageResource(R.drawable.ic_event_black_24dp);
                    icon_time.setImageResource(R.drawable.ic_access_time_black_24dp);
                } else {
                    icon_date.setImageResource(R.drawable.ic_event_white_24dp);
                    icon_time.setImageResource(R.drawable.ic_access_time_white_24dp);
                }

                if (event.getStatus() == 1 || event.getStatus() == 2) {
                    cv.setAlpha(0.55f);

                    TextView cancelledOrExpired = (TextView)convertView.findViewById(R.id.viewevent_cancelled);
                    if (event.getStatus() == 1){
                        cancelledOrExpired.setText("  Expired");
                        cancelledOrExpired.setVisibility(View.VISIBLE);
                    }

                    if (event.getStatus() == 2){
                        cancelledOrExpired.setText("Cancelled");
                        cancelledOrExpired.setVisibility(View.VISIBLE);
                    }
                }


                if (event.getStatus() == 0) {
                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity, ViewEventActivity.class);

                            Log.d("Adapter Message", "Open eventview: " + message.getId());
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

