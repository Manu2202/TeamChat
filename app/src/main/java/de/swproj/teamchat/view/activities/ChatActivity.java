package de.swproj.teamchat.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.helper.EventExpirer;
import de.swproj.teamchat.view.adapter.AdapterMessage;
import de.swproj.teamchat.view.viewmodels.ChatViewModel;

public class ChatActivity extends AppCompatActivity {

    private ListView lvMessages;
    private EditText etMessage;

    private String chatID;

    private AdapterMessage adapterMessage;

    private ChatViewModel viewModel;

    private TeamChatMessagingService messagingService;
    private FirebaseConnection firebaseConnection;

    private EventExpirer eventExpirer;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (eventExpirer != null) {
            eventExpirer.shutdownNow();
            eventExpirer = null;
        }

        DBStatements.removeUpdateable(viewModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagingService = new TeamChatMessagingService();

        lvMessages = findViewById(R.id.lvMessages);
        etMessage = findViewById(R.id.etMessage);


        chatID = getIntent().getStringExtra("chatID");

        viewModel = new ChatViewModel(DBStatements.getChat(chatID), DBStatements.getMessages(chatID));


        DBStatements.addUpdateable(viewModel);

        adapterMessage = new AdapterMessage(viewModel.getLiveMessages().getValue(), this);
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
                scrollListViewToBottom();
            }
        });


        firebaseConnection = new FirebaseConnection();

        eventExpirer = new EventExpirer(5, 20);


        //Exclude Items from Animation
        Fade fade = new Fade();
        View deco = getWindow().getDecorView();
        fade.excludeTarget(deco, true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        // end of exclude

    }

    private void scrollListViewToBottom() {
        lvMessages.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lvMessages.setSelection(adapterMessage.getCount() - 1);
            }
        });
    }

    public void sendMessage(View view) {
        //hide Keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        String etMessageString = etMessage.getText().toString();
        // Check if the Message is empty
        if (!etMessageString.isEmpty()) {
            Message message = new Message(GregorianCalendar.getInstance().getTime(),
                    etMessageString, false,
                    FirebaseAuth.getInstance().getCurrentUser().getUid(), chatID);
            firebaseConnection.addToFirestore(message, FirebaseTypes.Message.getValue(), FirebaseActions.ADD.getValue());
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
    protected void onPause() {
        super.onPause();

        if (eventExpirer != null) {
            eventExpirer.shutdownNow();
            eventExpirer = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
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

            case R.id.btn_chat_menu_leaveChat:
                acceptLeaveChatDialog();
        }

        return true;
    }

    // Method to show Accepting Dialog and handle Button
    private void acceptLeaveChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.leaveChat);
        builder.setMessage(R.string.acceptLeaveChat);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO: Verlassen der Gruppe und Update an Gruppen Mitglieder

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();


        alert.show();
    }
}
