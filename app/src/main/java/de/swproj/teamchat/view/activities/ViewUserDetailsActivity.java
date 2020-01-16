package de.swproj.teamchat.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.adapter.AdapterChat;


public class ViewUserDetailsActivity extends AppCompatActivity {


    private User user;
    private ArrayList<Chat> commonChats;

    private TextView icon;
    private TextView accName;
    private TextView forename;
    private TextView lastname;
    private TextView emailAddress;
    private TextView commonGroupsTitleText;
    private ListView commonChatsList;
    private AdapterChat adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_details);


        String id = getIntent().getStringExtra("currentContactID");
        user = DBStatements.getUser(id);

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

        commonChatsList = (ListView) findViewById(R.id.user_details_common_groups_lv);
        commonGroupsTitleText = (TextView) findViewById(R.id.user_details_shared_groups_title);

        List<Chat> allChats;
        allChats = DBStatements.getChat();

        for (Chat c : allChats) {
            if (DBStatements.getChatMembers(c.getId()).contains(user.getGoogleId())) {
                commonChats.add(c);
            }
        }


        if (commonChats.size() > 0) {
            adapter = new AdapterChat(commonChats);
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


    public void sendMailToUser(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",user.getGoogleMail(), null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "[TeamChat] ");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chats_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.btn_contact_details_menu_delete:
                DBStatements.deleteUser(user.getGoogleId());
                Log.d("Contact Detail List: ", "User deleted : " + user.getGoogleId());
                finish();
                break;
        }

        return true;
    }

}