package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.User;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mloginButton;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    //Get shared Preference File Key
    public static final String PREFERENCE_FILE_KEY = "FirebasePref_Token";
    public static final String KEY = "Token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);
        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mloginButton = (Button) findViewById(R.id.btn_login_login);
        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setTitle("Logging in");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        final String checkEmail = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mLoginProgress.dismiss();
                            //Assign Token from Shared Preference
                            String token = get_token();

                            if (!DBStatements.getUserEmailExists(checkEmail)) {
                                queryFirebaseByID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            }

                            FirebaseConnection.updateToken(FirebaseAuth.getInstance().getCurrentUser().getUid(), token);
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            mLoginProgress.hide();
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String get_token() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        return sharedPreferences.getString("Token", "--noTokenAvailable--");

    }


    private void queryFirebaseByID(String id) {
        //Check if Email exists for User in Auth------------------------------------------
        final String fireBaseID = id;

        final User user;

        //Get User from Firestore (gets saved in Database)
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("googleId", fireBaseID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult() != null) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Got User, anything related to this User has to be done inside here because its async ------->
                            User firebaseUser = document.toObject(User.class);
                            Log.d("FirebaseUser", firebaseUser.getAccountName() + ", " + firebaseUser.getFirstName());
                            DBStatements.insertUser(firebaseUser);

                        }
                    }

                    }

            }
        });

    }



}





