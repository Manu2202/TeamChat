package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.Connection.Database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class EditChatActivity extends AppCompatActivity {

    private String chatId;
    private Chat chat;

    private HashMap<String, User> allUser;
    private HashMap<String, User> groupMember;

    private DBStatements dbStatements;
    private FirebaseFirestore firebaseDB;

    private TextInputEditText etChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chat);

        dbStatements = new DBStatements(EditChatActivity.this);
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();

        etChatName = (TextInputEditText)findViewById(R.id.edit_chat_et_name);

        // Get own Intent
        Intent ownIntent = getIntent();
        chatId = ownIntent.getStringExtra("ID");

        if (!chatId.equals("0"))
            chat = dbStatements.getChat(chatId);

        getAllUsers();
    }

    private void getAllUsers(){
        for (User user:dbStatements.getUser()) {
            if(!groupMember.containsKey(user.getGoogleId()))
                allUser.put(user.getGoogleId(), user);
        }
    }

    public void saveChanges(View view){
        if (chatId.equals("0")){
            //TODO: Eigener User ->ID Holen
            String dummyUserID = "ABC";
            final Chat chat = new Chat(etChatName.getText().toString(), dummyUserID);

            firebaseDB.collection("chat")
                    .add(convertToHashMap(chat))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Chat", "DocumentSnapshot added with ID: " + documentReference.getId());
                            chat.setId(documentReference.getId());

                            // Push the new Chat in Local Database
                            dbStatements.updateChat(chat);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Chat", "Error adding document", e);
                        }
                    });
        }
    }

    private HashMap<String, Object> convertToHashMap(Chat chat){
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("Name", chat.getName());
        chatMap.put("Color", chat.getColor());
        chatMap.put("Admin", chat.getAdmin());

        return chatMap;
    }
}
