package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.view.adapter.AdapterMessage;
import de.swproj.teamchat.view.viewmodels.ChatViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;


import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etMessage;
 //   private Chat chat;
    private String chatID;

    //private ArrayList<Message> messages;
    private ChatViewModel viewModel;

    private TeamChatMessagingService messagingService;
    private FirebaseConnection firebaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagingService = new TeamChatMessagingService();

        lvMessages = findViewById(R.id.lvMessages);
        etMessage = findViewById(R.id.etMessage);



        chatID = getIntent().getStringExtra("chatID");

         viewModel= new ChatViewModel(DBStatements.getChat(chatID),DBStatements.getMessages(chatID));
          final AdapterMessage adapterMessage = new AdapterMessage(viewModel.getLiveMessages().getValue(),this);
          lvMessages.setAdapter(adapterMessage);
         viewModel.getLiveChat().observe(this, new Observer<Chat>() {
             @Override
             public void onChanged(Chat chat) {
                 setTitle(chat.getName());
             }
         });

         viewModel.getLiveMessages().observe(this, new Observer<ArrayList<Message>>() {
             @Override
             public void onChanged(ArrayList<Message> messages) {
             adapterMessage.notifyDataSetChanged();
             }
         });




        firebaseConnection = new FirebaseConnection();


        //Exclude Items from Animation
        Fade fade = new Fade();
        View deco = getWindow().getDecorView();
        fade.excludeTarget(deco, true);
        fade.excludeTarget(android.R.id.statusBarBackground,true);
        fade.excludeTarget(android.R.id.navigationBarBackground,true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        // end of exclude

    }


    public void sendMessage(View view){
        String etMessageString = etMessage.getText().toString();
        // Check if the Message is empty
        if (!etMessageString.isEmpty()) {
            Message message = new Message(GregorianCalendar.getInstance().getTime(),
                    etMessageString, false,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(), chatID);
            firebaseConnection.addToFirestore(message,
                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                    false, false);
            etMessage.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.btn_chat_menu_newEvent:
                Intent newEventIntent = new Intent(this, EditEventActivity.class);
                newEventIntent.putExtra("chatID", chatID);
                newEventIntent.putExtra("ID", "0");
                startActivity(newEventIntent);
                break;

            case R.id.btn_chat_menu_editChat:
                Intent editChatIntent = new Intent(this, EditChatActivity.class);
                editChatIntent.putExtra("admin", viewModel.getLiveChat().getValue().getAdmin());
                editChatIntent.putExtra("ID", chatID);
                startActivity(editChatIntent);
                break;
        }

        return true;
    }
}
