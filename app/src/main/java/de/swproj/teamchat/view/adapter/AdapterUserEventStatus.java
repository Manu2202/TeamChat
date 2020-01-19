package de.swproj.teamchat.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.transition.TransitionManager;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterUserEventStatus extends BaseAdapter {

    private List<UserEventStatus> userEventStatuses;


    public AdapterUserEventStatus(List<UserEventStatus> userEventStatuses) {
        this.userEventStatuses = userEventStatuses;


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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();
        UserEventStatus state = userEventStatuses.get(position);

        //Debug Logger
       // if(position+1<userEventStatuses.size())
       // Log.d("AdapteruserEvent", "Pos: "+position+"  User: "+state.getUserId()+ " nextUser: "+userEventStatuses.get(position+1).getUserId());

        if (convertView == null) {


            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lf.inflate(R.layout.listitem_usereventstatus, null, false);


            if(state.getStatus()==2) {
                cancelledUser(convertView, state);
            }

         }
        TextView tvUsername = convertView.findViewById(R.id.li_ues_tvusername);
        try {
            tvUsername.setText(DBStatements.getUser(state.getUserId()).getAccountName() + ":");
        }catch (NullPointerException e){
            tvUsername.setText("Unknown:");
        }

        TextView tvStatus = convertView.findViewById(R.id.li_ues_tvstatus);
        TextView tvReason = convertView.findViewById(R.id.li_ues_tvreason);


            switch(state.getStatus()){

                case 1: tvStatus.setTextColor(parent.getResources().getColor(R.color.save_green,null));
                        tvReason.setVisibility(View.INVISIBLE);
                        tvStatus.setText("committed");
                        break;
                case 2: tvStatus.setTextColor(parent.getResources().getColor(R.color.cancel_red,null));
                        tvReason.setVisibility(View.VISIBLE);
                        cancelledUser(convertView, state);
                        tvStatus.setText("cancelled");
                        break;
                default:
                        tvStatus.setTextColor(parent.getResources().getColor(R.color.black,null));
                        tvReason.setVisibility(View.INVISIBLE);
                        tvStatus.setText("-");
                        break;

        }
                return convertView;


    }


    private void cancelledUser(View convertView, UserEventStatus state) {
        CardView cv = convertView.findViewById(R.id.li_ues_cv);
        final TextView tvReason = convertView.findViewById(R.id.li_ues_tvreason);

        tvReason.setAlpha(0.2f);
        tvReason.setText("Reason");
        final String reason = state.getReason();

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                        CardView cv = (CardView) view;
                        TransitionManager.beginDelayedTransition(cv);
                        if (tvReason.getVisibility() == View.VISIBLE) {
                            tvReason.setVisibility(View.GONE);
                        } else {
                            tvReason.setVisibility(View.VISIBLE);
                        }

 */
                CardView cv = (CardView) view;
                TransitionManager.beginDelayedTransition(cv);
                if (tvReason.getAlpha() == 0.2f) {
                    tvReason.setAlpha(0.55f);
                    tvReason.setText("Reason: " + reason);
                } else {
                    tvReason.setAlpha(0.2f);
                    tvReason.setText("Reason");
                }


            }
        });

    }
}