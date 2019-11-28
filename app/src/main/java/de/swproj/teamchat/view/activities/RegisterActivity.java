package de.swproj.teamchat.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.User;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button btn_create;
    private FirebaseConnection fbconnect;
    private DBStatements dbStatements;
    //Firebase Auth
    private FirebaseAuth mAuth;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        dbStatements = new DBStatements(this);
        fbconnect = new FirebaseConnection(dbStatements);


        mName=(TextInputLayout)findViewById(R.id.reg_display_name);
        mEmail=(TextInputLayout)findViewById(R.id.reg_email);
        mPassword=(TextInputLayout)findViewById(R.id.reg_password);
        btn_create=(Button)findViewById(R.id.btn_reg_create);

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(display_name)|| !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name,email,password);
                }

            }
        });
    }

    private void register_user(final String display_name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mRegProgress.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            fbconnect.addToFirestore(new User(user.getUid(),user.getEmail(),user.getDisplayName(),user.getDisplayName(),user.getDisplayName()));
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Firebase", "createUserWithEmail:success");
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            mRegProgress.hide();
                            // If sign in fails, display a message to the user.
                            Log.w("Firebase", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
