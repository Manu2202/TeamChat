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

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.helper.FirebaseHelper;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private String objectID;

    public FirebaseConnection(){
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }
    public String addToFirestore(Message message){
        objectID = "Noch nicht hochgeladen";
        firebaseDB.collection("messages").add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                objectID = documentReference.getId();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectID = "-1";
            }
        });
        // Return the Object ID of the entry of Firestore
        return objectID;
    }
    public String addToFirestore(Chat chat){
        objectID = "Noch nicht hochgeladen";
        firebaseDB.collection("chats").add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                objectID = documentReference.getId();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectID = "-1";
            }
        });
        // Return the Object ID of the entry of Firestore
        return objectID;
    }
    public String addToFirestore(User user){
        objectID = "Noch nicht hochgeladen";
        firebaseDB.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                objectID = documentReference.getId();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                objectID = "-1";
            }
        });
        // Return the Object ID of the entry of Firestore
        return objectID;
    }

    public ArrayList<Message> getMessages(String Chatid){
        final ArrayList<Message> messages = new ArrayList<>();
        firebaseDB.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.toObject(Event.class).isEvent()){
                                    messages.add(document.toObject(Event.class));
                                }
                                else {
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
