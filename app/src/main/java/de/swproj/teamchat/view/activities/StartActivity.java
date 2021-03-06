package de.swproj.teamchat.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shobhitpuri.custombuttons.GoogleSignInButton;
//import com.shobhitpuri.custombuttons.GoogleSignInButton;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.User;

import static de.swproj.teamchat.view.activities.LoginActivity.PREFERENCE_FILE_KEY;

public class StartActivity extends AppCompatActivity {
    // For Google SignIn //
    //GoogleSignInButton signInButton;
    GoogleSignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 0;

    private FirebaseAuth mAuth;

    private FirebaseConnection fbconnect = new FirebaseConnection();

    /////////////////////

    private Button btn_reg;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();


        ///////// Google Sign In ///////////

        signInButton = findViewById(R.id.google_sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Register per Mail
        btn_reg = (Button)findViewById(R.id.btn_start_reg);
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        //Log in per Mail
        btn_login = (Button)findViewById(R.id.btn_start_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google SignIn", "Google sign in failed", e);
                // ...
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Google SignIn", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Assign Token from Shared Preference
                            String token=get_token();
                            FirebaseConnection.updateToken(FirebaseAuth.getInstance().getCurrentUser().getUid(),token);


                            //Check if User is New -> if true add to Database
                            if (task.getResult().getAdditionalUserInfo().isNewUser()){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String forname_lastname[];
                                if (user.getDisplayName().contains(" ")){
                                    forname_lastname = user.getDisplayName().split(" ");
                                }
                                else{
                                    forname_lastname= new String[]{user.getDisplayName(),""};
                                }

                                fbconnect.addToFirestore(new User(user.getUid(),user.getEmail(),user.getDisplayName(),forname_lastname[1],forname_lastname[0]));
                            } else {
                                if (!DBStatements.getUserEmailExists(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                       queryFirebaseByID(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                }
                            }




                            //Send to MainActivity
                            Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google SignIn", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(StartActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    private String get_token(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        return sharedPreferences.getString("Token","--noTokenAvailable--");

    }


    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

    }

    //---------------------------------------------------------------------------------------------
    /*
     * Temp method to drop manually every item from your local Database
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.btn_start_menu_dropall:
                DBStatements.dropAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void queryFirebaseByID(String id) {
        //Check if Email exists for User in Auth------------------------------------------
        final String fireBaseID = id;

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