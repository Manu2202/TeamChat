package de.swproj.teamchat.connection.firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.helper.FirebaseHelper;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private DBStatements dbStatements;
    private ArrayList<Message> messages;

    public FirebaseConnection(DBStatements dbStatements) {
        this.dbStatements = dbStatements;
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }

    public void addToFirestore(final Message message) {
        firebaseDB.collection("messages").add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                        message.setId(documentReference.getId());
                        dbStatements.insertMessage(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               addToFirestore(message);
            }
        });
    }


    public void addToFirestore(final Chat chat) {
        firebaseDB.collection("chats").add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                        chat.setId(documentReference.getId());
                        dbStatements.insertChat(chat);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addToFirestore(chat);
            }
        });
    }


    public void addToFirestore(User user) {
        firebaseDB.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                        // TODO: Muss was gemacht werden? User braucht keine ID aus Firebase?!
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public ArrayList<Message> getMessages(String Chatid) {
        messages = new ArrayList<>();
        firebaseDB.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.toObject(Event.class).isEvent()) {
                                    messages.add(document.toObject(Event.class));
                                } else {
                                    messages.add(document.toObject(Message.class));
                                }
                            }
                        } else {
                            Log.w("Firebase", "Error getting documents.", task.getException());
                        }
                    }
                });
        return messages;
    }

}
