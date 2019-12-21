package de.swproj.teamchat.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.adapter.AdapterChat;
import de.swproj.teamchat.view.adapter.AdapterMessage;


public class ViewUserDetailsActivity extends AppCompatActivity {

    private DBStatements db;
    private User user;
    private String activeUser;
    private ArrayList<Chat> commonChats;

    private TextView icon;
    private TextView accName;
    private TextView forename;
    private TextView lastname;
    private TextView emailAddress;
    private TextView commonGroupsTitleText;

    private ListView commonChatsList;

    private Button sendEmail;
    private Button removeUser;

    private AdapterChat adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);

        db = new DBStatements(this);
        activeUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String id = getIntent().getStringExtra("currentContactID");
        user = db.getUser(id);

        icon = (TextView) findViewById(R.id.user_details_icon);
        accName = (TextView) findViewById(R.id.user_details_accname);
        forename = (TextView) findViewById(R.id.user_details_fname);
        lastname = (TextView) findViewById(R.id.user_details_lname);
        emailAddress = (TextView) findViewById(R.id.user_details_email);

        icon.setText(user.getFirstName().toUpperCase().charAt(0) + "" + user.getName().toUpperCase().charAt(0));
        accName.setText(user.getAccountName());
        forename.setText(user.getFirstName());
        lastname.setText(user.getName());
        emailAddress.setText(user.getGoogleMail());

        commonChats = new ArrayList<Chat>();

        sendEmail = (Button) findViewById(R.id.user_details_send_email_btn);
        removeUser = (Button) findViewById(R.id.user_details_remove_user);
        commonChatsList = (ListView) findViewById(R.id.user_details_common_groups_lv);
        commonGroupsTitleText = (TextView) findViewById(R.id.user_details_shared_groups_title);

        /////////////////////////////////////////////////////////////////
        // TODO: dbStatement  ArrayList<Chat>getChatsContainingUser(String userId)
        // returns ArrayList of Chats containing the currently selected user

//        commonChats = db.getChatsContainingUser(user.getGoogleId());

        // If this db method exists, you can delete this section

//        db.insertChat(new Chat("DUMMY CHAT ", 0xFF004888, "9999000", user.getGoogleId()));
//       db.updateChatMembers(new String[]{user.getGoogleId()}, "9999000");

        ArrayList<Chat> allChats;
        allChats = db.getChat();

        for (Chat c : allChats) {
            if (db.getChatMembers(c.getId()).contains(user.getGoogleId())) {
                commonChats.add(c);
            }
        }
        ///////////////////////////////////////////////////////////////////


        if (commonChats.size() > 0) {
            adapter = new AdapterChat(commonChats, db);
            commonChatsList.setAdapter(adapter);

            commonChatsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Contact Detail List: ", "ListView onClickListener on Chat: " + position);
                    Intent chatIntent = new Intent(ViewUserDetailsActivity.this, ChatActivity.class);
                    Chat selectedItem = (Chat) adapter.getItem(position);
                    chatIntent.putExtra("chatID", selectedItem.getId());
                    startActivityForResult(chatIntent, position);
                }
            });


        } else {
            commonChatsList.setVisibility(View.GONE);
            commonGroupsTitleText.setVisibility(View.GONE);
        }


    }


    public void removeUserFromContacts(View view) {
        // TODO: Maybe ask user for confirmation
        // TODO: dbStatements deleteUser(String userID) Function needed
        // db.deleteUser(user.getGoogleId());

        Log.d("Contact Detail List: ", "User deleted : " + user.getGoogleId());
        finish();
    }


    public void sendMailToUser(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",user.getGoogleMail(), null));

        intent.putExtra(Intent.EXTRA_SUBJECT, "[TeamChat] ");
       // intent.putExtra(Intent.EXTRA_TEXT, "Message Test");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));

    }
}
