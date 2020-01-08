package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
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

                tv_Description.setText(event.getDescription());
                tvEventDate.setText(FormatHelper.formatDate(event.getDate()));
                tvEventTime.setText(FormatHelper.formatTime(event.getDate()));

                // Colored CardView for Events
                final CardView cv = convertView.findViewById(R.id.li_message_cv);
                int eventColor = DBStatements.getChat(message.getChatid()).getColor();

                // UserEventStatus: Not final yet - I don't know which value means the Event is completely cancelled
                if (ues.getStatus() == 0  || ues.getStatus() == 1 ) {
                    cv.setCardBackgroundColor(eventColor);
                } else {
                    // Use Chat Color but desaturate it to look greyish

                    int red = Color.red(eventColor);
                    int green = Color.green(eventColor);
                    int blue = Color.blue(eventColor);
                    float[] hsv = new float[3];
                    Color.RGBToHSV(red, green, blue, hsv);

                    // reduce saturation to 55%
                    // [0] = Hue ,  [1] = Saturation , [2] = Value
                    hsv[1] = hsv[1] * 0.55f;

                    cv.setBackgroundColor(Color.HSVToColor(hsv));

                }

                // TODO: ues.getStatus() == 2 means this user cancelled the event
                 // use whatever status number is used for "event is cancelled for everyone" or "event is in past"
                if (ues.getStatus() != 2) {
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

