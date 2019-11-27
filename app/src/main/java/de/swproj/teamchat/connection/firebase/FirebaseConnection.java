package de.swproj.teamchat.connection.firebase;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidx.annotation.NonNull;

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private String objectID;

    public void FirebaseConnection(){
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }

    public String addToFirestore(String collectionPath, Event event){
        HashMap<String, Object> eventMap = new HashMap<>();
        eventMap.put("Timestamp", event.getTimeStamp());
        eventMap.put("Titel", event.getMessage());
        eventMap.put("MessageID", event.getId());
        eventMap.put("IsEvent", event.isEvent());
        eventMap.put("CreatorID", event.getCreator());
        eventMap.put("Date", event.getDate());
        eventMap.put("Description", event.getDescription());
        eventMap.put("ChatID", event.getChatid());
        eventMap.put("Status", event.getStatus());

        firebaseDB.collection(collectionPath).add(eventMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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
    public String addToFirestore(String collectionPath, Chat chat){
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("Name", chat.getName());
        chatMap.put("Color", chat.getColor());
        chatMap.put("Admin", chat.getAdmin());

        firebaseDB.collection(collectionPath).add(chatMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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
    public String addToFirestore(String collectionPath, User user){
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("AccountName",user.getAccountName());
        userMap.put("UID",user.getGoogleId());
        userMap.put("Email",user.getGoogleMail());

        firebaseDB.collection(collectionPath).add(userMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
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
}
