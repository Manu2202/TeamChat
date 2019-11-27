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
import de.swproj.teamchat.helper.FirebaseHelper;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private String objectID;

    public FirebaseConnection(){
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }

    public String addToFirestore(String collectionPath, Event event){
        firebaseDB.collection(collectionPath).add(FirebaseHelper.convertToMap(event))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
        firebaseDB.collection(collectionPath).add(FirebaseHelper.convertToMap(chat))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
        firebaseDB.collection(collectionPath).add(FirebaseHelper.convertToMap(user))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
