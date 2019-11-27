package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Set;

public class EditChatActivity extends AppCompatActivity {

    private String chatId;
    private Chat chat;

    private HashMap<String, User> allUser=new HashMap<String, User>();
    private HashMap<String, User> groupMember=new HashMap<String,User>();

    private DBStatements dbStatements;
    private FirebaseConnection firebaseConnection;

    private TextInputEditText etChatName;

    private LinearLayout llUsers;
    private int listdivider=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);

        dbStatements = new DBStatements(EditChatActivity.this);
        // Connect Firebase
        firebaseConnection = new FirebaseConnection();

        etChatName = (TextInputEditText)findViewById(R.id.edit_chat_et_name);
        llUsers = findViewById(R.id.edit_chat_linear_layout);

        // Get own Intent
        Intent ownIntent = getIntent();
        chatId = ownIntent.getStringExtra("ID");

        if (!chatId.equals("0")) {
            chat = dbStatements.getChat(chatId);

            for(User user:dbStatements.getUsersOfChat(chatId)){

                groupMember.put(user.getGoogleId(),user);
            }
        }

       getAllUsers();
        generateUserViews();
    }

    private void getAllUsers(){
        for (User user:dbStatements.getUser()) {
            if(!groupMember.containsKey(user.getGoogleId()))
                Log.d("EditChat ","Users "+user.getGoogleId());
                allUser.put(user.getGoogleId(), user);
            Log.d("EditChat 2","GetUser "+allUser.get(user.getGoogleId()).getGoogleId());
        }
    }

    @SuppressLint("ResourceAsColor")
    private void generateUserViews(){

        LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View convertView = lf.inflate(R.layout.listdivider_user, null, false);

        TextView tvDividerLine = convertView.findViewById(R.id.listdivider_tvheadline);

        tvDividerLine.setText("Users in the Group:");
        tvDividerLine.setTextColor(R.color.save_green);

        llUsers.addView(convertView);


        Set<String> userIds=groupMember.keySet();
        Log.d("EditChat ","groupmembers "+userIds.size());

        for(String userId:userIds) {
           llUsers.addView(generateUserView(groupMember.get(userId),1));
        }



        convertView = lf.inflate(R.layout.listdivider_user, null, false);

        tvDividerLine = convertView.findViewById(R.id.listdivider_tvheadline);

        tvDividerLine.setText("Users not in the Group:");
        tvDividerLine.setTextColor(R.color.cancel_red);
        llUsers.addView(convertView);

        listdivider=userIds.size()+1;


      userIds=allUser.keySet();

        Log.d("EditChat ","groupmembers "+userIds.size());
        for(String userId:userIds) {
            Log.d("EditChat ","User "+userId);
            llUsers.addView(generateUserView(allUser.get(userId),2));
        }


    }



    private View generateUserView(User user, int list){
        View convertView = null;

        LayoutInflater lf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = lf.inflate(R.layout.listitem_user, null, false);

        TextView tvIcon = convertView.findViewById(R.id.li_user_icon);
        TextView tvAccName = convertView.findViewById(R.id.li_user_accname);
        TextView tvFName = convertView.findViewById(R.id.li_user_fname);
        TextView tvLName = convertView.findViewById(R.id.li_user_lname);
        Log.d("EditChatActivity","acc: "+user.getAccountName());
        tvIcon.setText(user.getAccountName().substring(0,1));
        tvAccName.setText(user.getAccountName());
        tvFName.setText(user.getFirstName());
        tvLName.setText(user.getName());

        convertView.setOnClickListener(new clicklisten(list,user.getGoogleId()));

        return convertView;



    }

    class clicklisten implements View.OnClickListener {
        private int list;
        private String userID;

        public clicklisten(int actList, String userID ) {
            list = actList;
            this.userID=userID;
        }

        @Override
        public void onClick(View view) {

            if (list == 1) {

                TransitionManager.beginDelayedTransition(llUsers);
                llUsers.removeView(view);
                llUsers.addView(view, listdivider);
                listdivider--;
                allUser.put(userID,groupMember.remove(userID));
                list = 2;
            } else {
                TransitionManager.beginDelayedTransition(llUsers);
                llUsers.removeView(view);
                llUsers.addView(view, 1);
                listdivider++;
                groupMember.put(userID,allUser.remove(userID));
                list = 1;
            }


        }

    }




    public void saveChanges(View view){
        if (chatId.equals("0")){
            //TODO: Eigener User ->ID Holen
            String dummyUserID = "ABC";
            Chat chat = new Chat(etChatName.getText().toString(), dummyUserID);

            chat.setId(firebaseConnection.addToFirestore("chat", convertToHashMap(chat)));

            dbStatements.insertChat(chat);
        }
        // TODO: Update exisiterenden Chat
    }

    private HashMap<String, Object> convertToHashMap(Chat chat){
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("Name", chat.getName());
        chatMap.put("Color", chat.getColor());
        chatMap.put("Admin", chat.getAdmin());

        return chatMap;
    }
}
