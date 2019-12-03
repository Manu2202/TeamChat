package de.swproj.teamchat.connection.firebase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private DBStatements dbStatements;

    public FirebaseConnection(DBStatements dbStatements ) {
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
    public void addToFirestore(final User user) {
        firebaseDB.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase", "User added to Firebase");
                dbStatements.insertUser(user);
            }
        });
        /*firebaseDB.collection("users").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firebase", "addToFirebase with ID: " + documentReference.getId());
                        dbStatements.insertUser(user);
                        // TODO: Muss was gemacht werden? User braucht keine ID aus Firebase?!
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
         */
    }
    public static void updateToken(final String uID, String token){
        Log.d("Firebase Connection","Updating Token...");
        Map<String, Object> data = new HashMap<>();
            data.put("token",token);

        FirebaseFirestore.getInstance().collection("users").document(uID).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase Connection", "onSuccess: Token added for User "+ uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase Connection", "onFailure: Token not added");
            }
        });
    }
    public static void deleteToken(final String uID){
        Log.d("Firebase Connection","Deleting Token...");
        Map<String, Object> data = new HashMap<>();
        data.put("token", FieldValue.delete());

        FirebaseFirestore.getInstance().collection("users").document(uID).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firebase Connection", "onSuccess: Token deleted for User "+ uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase Connection", "onFailure: Token not deleted");
            }
        });

    }
}
