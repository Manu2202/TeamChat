package de.swproj.teamchat.connection.firebase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public FirebaseConnection(DBStatements dbStatements) {
        this.dbStatements = dbStatements;
        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }


    public void addToFirestore(final Message message) {
        firebaseDB.collection("messages").add(FirebaseHelper.convertToMap(message))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore Messages", "addToFirebase with ID: " + documentReference.getId());
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


    public void addToFirestore(final Chat chat, final List<String> userids) {
        firebaseDB.collection("chats").add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String chatid = documentReference.getId();
                        Log.d("Firestore Chat", "addToFirebase with ID: " + chatid);
                        updateUsers(chatid,userids);
                        chat.setId(chatid);
                        dbStatements.insertChat(chat);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addToFirestore(chat,userids);
            }
        });
    }

    public void addToFirestore(final User user) {
        firebaseDB.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore User", "User added to Firebase");
                dbStatements.insertUser(user);
            }
        });
    }

    public void getUserByID(String uID) {
        firebaseDB.collection("users").document(uID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        User firebaseUser = documentSnapshot.toObject(User.class);
                        Log.d("FirebaseUser", firebaseUser.getAccountName() + ", " + firebaseUser.getGoogleMail());
                        dbStatements.insertUser(firebaseUser);
                    }
                }
            }
        });
    }

    public static void updateToken(final String uID, String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userid", uID);

        FirebaseFirestore.getInstance().collection("tokens").document(uID).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore FCM Token", "onSuccess: Token added for User " + uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore FCM Token", "onFailure: Token not added");
            }
        });
    }
    public static void updateUsers(final String ChatID, List<String> users){
        Map<String, Object> data = new HashMap<>();
        data.put("users", users);

        FirebaseFirestore.getInstance().collection("chats").document(ChatID).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore Chat", "onSuccess:  Users added to Chat " + ChatID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore Chat", "onFailure: Users not added to Chat");
            }
        });
    }

    public static void deleteToken(final String uID) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", FieldValue.delete());
        data.put("userid", FieldValue.delete());


        FirebaseFirestore.getInstance().collection("tokens").document(uID).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore FCM Token", "onSuccess: Token deleted for User " + uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore FCM Token", "onFailure: Token not deleted");
            }
        });

    }
}
