package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.transition.TransitionManager;
import de.swproj.teamchat.Connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterUserEventStatus extends BaseAdapter {

    private ArrayList<UserEventStatus> userEventStatuses;
    private DBStatements db;
    //todo: get authenticated user
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
        UserEventStatus state = userEventStatuses.get(position);

        if (convertView == null) {


            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = lf.inflate(R.layout.listitem_event, null, false);

            TextView tvUsername= convertView.findViewById(R.id.li_ues_tvusername);
            TextView tvStatus= convertView.findViewById(R.id.li_ues_tvstatus);

            tvUsername.setText(db.getUser(state.getUserId()).getAccountName());
            tvStatus.setText(state.getStatus());

             if(state.getStatus()==2) {
                 CardView cv = convertView.findViewById(R.id.li_ues_cv);
                 TextView tvReason = convertView.findViewById(R.id.li_ues_tvreason);
                 tvReason.setText(state.getReason());
                 cv.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         RelativeLayout rl = view.findViewById(R.id.li_ues_rlexpandlayout);
                         CardView cv = (CardView) view;
                         TransitionManager.beginDelayedTransition(cv);
                         if (rl.getVisibility() == View.VISIBLE) {
                             rl.setVisibility(View.GONE);
                         } else rl.setVisibility(View.VISIBLE);
                     }
                 });
             }





        }
                return convertView;




    }
}