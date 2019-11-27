package de.swproj.teamchat.connection.firebase;

/*
 * Created by Manuel Lanzinger on 27. November 2019.
 * For the project: TeamChat.
 */

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidx.annotation.NonNull;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;
    private String objectID;

    public FirebaseConnection(){
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }

    public String addToFirestore(String collectionPath, HashMap<String, Object> objectData){

        firebaseDB.collection(collectionPath).add(objectData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
