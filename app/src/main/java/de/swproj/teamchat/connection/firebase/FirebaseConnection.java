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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;
import de.swproj.teamchat.helper.FirebaseHelper;

public class FirebaseConnection {

    private FirebaseFirestore firebaseDB;


    public FirebaseConnection() {

        // Connect Firebase
        firebaseDB = FirebaseFirestore.getInstance();
    }

    public void addToFirestore(final Message message, final int type, final int action) {
        firebaseDB.collection("messages")
                .add(FirebaseHelper.convertToMap(message,type, action))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore Messages", "Message added to Firebase with ID: " + documentReference.getId());
                        message.setId(documentReference.getId());
                        DBStatements.insertMessage(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addToFirestore(message, type, action);
            }
        });
    }
    public void addToFirestore(final UserEventStatus status, final int type, final int action) {
        firebaseDB.collection("usereventstatus")
                .add(FirebaseHelper.convertToMap(status,type, action))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Firestore Messages", "UserEventStatus added with ID: " + documentReference.getId());
                        DBStatements.updateUserEventStatus(status);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addToFirestore(status, type, action);
            }
        });
    }


    public void addToFirestore(final Chat chat,  final int type, final int action, final List<String> userids) {
        firebaseDB.collection("chats").add(FirebaseHelper.convertToMap(chat,type,action,userids))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String chatid = documentReference.getId();
                        Log.d("Firestore Chat", "Chat added to Firebase with ID: " + chatid);

                        chat.setId(chatid);
                        DBStatements.insertChat(chat);
                        DBStatements.updateChatMembers(userids, chatid);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addToFirestore(chat, type, action, userids);
            }
        });
    }

    public void addToFirestore(final User user) {
        firebaseDB.collection("users").document(
                FirebaseAuth.getInstance().getCurrentUser().getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore User", "User added to Firebase");
                        DBStatements.insertUser(user);
                    }
                });
    }

    public void saveUserByID(String uID) {
        firebaseDB.collection("users").document(uID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                User firebaseUser = documentSnapshot.toObject(User.class);

                                Log.d("FirebaseUser", "Saved new User:"+
                                        firebaseUser.getAccountName() + ", " + firebaseUser.getGoogleMail());
                                DBStatements.insertUser(firebaseUser);
                            }
                        }
                    }
                });
    }

    public void saveChatbyID(final String chatid){
        firebaseDB.collection("chats").document(chatid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Chat", "DocumentSnapshot data: " + document.getData());
                        Map<String,Object> snapshot = document.getData();
                        Chat firebasechat = new Chat((String)snapshot.get("name"),
                                ((Long)snapshot.get("color")).intValue(),(String)document.getId(),
                                (String)snapshot.get("admin"));
                        Log.d("FirebaseChat", "Saved new Chat with chatid: "+chatid);
                        DBStatements.insertChat(firebasechat);
                    } else {
                        Log.d("Chat", "No such document");
                    }
                } else {
                    Log.d("Chat", "get failed with ", task.getException());
                }
            }
        });
    }

    public void saveUserbyEmail(String email){
        // Query against the collection WHERE (googleMail == given_email).
        firebaseDB.collection("users").whereEqualTo("googleMail", email).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User firebaseUser = document.toObject(User.class);

                                Log.d("FirebaseUser", firebaseUser.getAccountName() + ", " +
                                        firebaseUser.getGoogleMail());
                                DBStatements.insertUser(firebaseUser);
                            }
                        } else {
                            Log.d("Firebase User", "Error getting user: ", task.getException());
                        }
                    }
                });

    }

    public static void updateToken(final String uID, String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userid", uID);

        FirebaseFirestore.getInstance().collection("tokens").document(uID)
                .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore FCM Token", "Token added for User " + uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore FCM Token", "onFailure: Token not added");
            }
        });
    }

    public static void deleteToken(final String uID) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", FieldValue.delete());
        data.put("userid", FieldValue.delete());


        FirebaseFirestore.getInstance().collection("tokens").document(uID)
                .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Firestore FCM Token", "Token deleted for User " + uID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firestore FCM Token", "onFailure: Token not deleted");
            }
        });

    }
}
