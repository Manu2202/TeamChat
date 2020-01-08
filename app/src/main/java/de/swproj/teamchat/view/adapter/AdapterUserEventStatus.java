package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
                CardView cv = convertView.findViewById(R.id.li_ues_cv);
                final TextView tvReason = convertView.findViewById(R.id.li_ues_tvreason);
                tvReason.setText(state.getReason());
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CardView cv = (CardView) view;
                        TransitionManager.beginDelayedTransition(cv);
                        if (tvReason.getVisibility() == View.VISIBLE) {
                            tvReason.setVisibility(View.GONE);
                        } else tvReason.setVisibility(View.VISIBLE);
                    }
                });
            }

         }
        TextView tvUsername = convertView.findViewById(R.id.li_ues_tvusername);
        tvUsername.setText(DBStatements.getUser(state.getUserId()).getAccountName() + ":");
        TextView tvStatus = convertView.findViewById(R.id.li_ues_tvstatus);

            switch(state.getStatus()){

                case 1: tvStatus.setTextColor(parent.getResources().getColor(R.color.save_green,null));
                        tvStatus.setText("zugesagt");
                        break;
                case 2:
                    tvStatus.setTextColor(parent.getResources().getColor(R.color.cancel_red, null));
                        tvStatus.setText("abgesagt");
                        break;
                default: tvStatus.setText("-");
                         break;

        }
                return convertView;




    }
}