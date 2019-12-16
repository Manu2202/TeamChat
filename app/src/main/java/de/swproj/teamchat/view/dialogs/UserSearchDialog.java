package de.swproj.teamchat.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;


import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.adapter.AdapterContact;

public class UserSearchDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Activity activity;
    private Button cancelButton;
    private Button searchButton;
    private EditText searchField;
    private ProgressBar progressBar;
    private TextView userIcon;
    private TextView userLName;
    private TextView userFName;
    private TextView userAccName;
    private TextView responseText;
    private User foundUser;
    private FloatingActionButton fab;
    private DBStatements dbStatements;
    private FirebaseConnection fbconnect;
    private UserSearchThread userSearchThread;

    private final int STILL_WAITING = -1;
    private final int USER_WAS_NOT_SEARCHED_FOR = 0;
    private final int USER_DOES_NOT_EXIST = 1;
    private final int USER_WAS_FOUND = 2;

    private int searchResultValue = STILL_WAITING;
    private AdapterContact adapter;


    public UserSearchDialog(Activity activity, AdapterContact adapter){
        super(activity);
        this.activity=activity;
        this.adapter = adapter;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_usersearch);
        setTitle("Search for User");

        this.dbStatements = new DBStatements(getContext());

        fbconnect = new FirebaseConnection(dbStatements);

        // Button "Search" - Disabled until searchField has text
        searchButton = (Button)findViewById(R.id.dialog_userSearch_search_btn);
        searchButton.setOnClickListener(this);
        searchButton.setEnabled(false);

        // Cancel Button - Always the same
        cancelButton = (Button)findViewById(R.id.dialog_userSearch_cancel_btn);
        cancelButton.setOnClickListener(this);

        // Search Field for username - Visible at start
        searchField = (EditText)findViewById(R.id.dialog_userSearch_name_et);
        searchField.setVisibility(View.VISIBLE);


        // As long as search field is empty, search button cannot be pressed
        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0){
                    searchButton.setEnabled(false);
                } else {
                    searchButton.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Progress Bar when a search is being started - Pops up when you start search
        progressBar = (ProgressBar)findViewById(R.id.dialog_userSearch_progressBar);
        progressBar.setVisibility(View.GONE);


        // If user was found:
        // Displays User Icon (2 Letters from name, same as in normal contactview
        userIcon = (TextView)findViewById(R.id.userSearch_user_icon);
        userIcon.setVisibility(View.GONE);

        // Displays User Last name, First name, Account name

        userLName = (TextView)findViewById(R.id.userSearch_user_lname);
        userLName.setVisibility(View.GONE);

        userFName = (TextView)findViewById(R.id.userSearch_user_fname);
        userFName.setVisibility(View.GONE);

        userAccName = (TextView)findViewById(R.id.userSearch_user_accname);
        userAccName.setVisibility(View.GONE);


        // Text when no user was found or Database collection failedl
        responseText = (TextView)findViewById(R.id.dialog_userSearch_response_tv);
        responseText.setText("Enter Username");
        responseText.setVisibility(View.VISIBLE);

        // Floating Action Button that called this Dialog
        fab = (FloatingActionButton)findViewById(R.id.userSearchFAB);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_userSearch_search_btn:
                if (foundUser == null) {
                    if(searchField.getText().toString().equals("")){
                    responseText.setText("Search Field was left empty. Please enter a username.");
                    responseText.setVisibility(View.VISIBLE);
                    } else {

                    // TODO: Extra task if you have nothing else to do:
                    // Check here if user already exists in local Database
                    // (but it looks like it will not be added if it already exists)

                        //Check if Email exists for User in Auth
                        FirebaseAuth.getInstance().fetchSignInMethodsForEmail("emailaddress@gmail.com").addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if(!task.getResult().getSignInMethods().isEmpty()){
                                    fbconnect.getUserbyEmail("fabian.dittrich98@gmail.com");
                                }
                            }
                        });


                    // Search is submitted
                    // Prepare UI
                    searchButton.setEnabled(false);
                    searchButton.setText("Searching");
                    searchField.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    responseText.setVisibility(View.GONE);

                    // Start Search Thread to connect to Firebase
                    userSearchThread = new UserSearchThread(activity, getContext(),
                            this.dbStatements, this, searchField.getText().toString());
                    new Thread(userSearchThread).start();
                    // Thread will call reactToSearchResult() once it's done.

                    }
                } else {
                    // If a user was found, the Search-Button will add the user
                    if (searchButton.getText().equals("Add")) {
                        dbStatements.insertUser(foundUser);

                        // Also loads user into current Listview and updates it
                        // (might not best way to do this)
                        // without this, User is only displayed if you leave Contact View
                        // and come back
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(foundUser);
                                adapter.notifyDataSetChanged();
                            }
                        });

                        foundUser = null;
                        dismiss();
                    }
                }

                break;
            case R.id.dialog_userSearch_cancel_btn:
                dismiss();
                break;

            default:
                break;
        }

    }

    protected void setSearchResponseValue(int responseValue){
        searchResultValue = responseValue;
    }

    protected void receiveUserFromDatabase(User user) {
        foundUser = user;
    }

    protected void reactToSearchResult() {
        // React to Response
        switch(searchResultValue) {
            // Waiting for Search Thread to timeout or Server to respond
            case STILL_WAITING:
                return;

            // Firebase Server did not respond (maybe no Internet Connection)
            case USER_WAS_NOT_SEARCHED_FOR:
                progressBar.setVisibility(View.GONE);
                searchField.setVisibility(View.GONE);
                responseText.setText("Failed to connect to server.");
                responseText.setVisibility(View.VISIBLE);
                searchButton.setText("Retry");
                searchButton.setEnabled(true);
                searchResultValue = STILL_WAITING;
                break;

            // Firebase Server responded, but could not find User with that name in Database
            case USER_DOES_NOT_EXIST:
                progressBar.setVisibility(View.GONE);
                responseText.setText("Username was not found");
                responseText.setVisibility(View.VISIBLE);
                searchButton.setText("Search");
                searchButton.setEnabled(true);
                searchField.setText("");
                searchField.setVisibility(View.VISIBLE);
                searchResultValue = STILL_WAITING;
                foundUser = null;
                break;

            // Firebase Server successfully sent User Data to us
            case USER_WAS_FOUND:
                progressBar.setVisibility(View.GONE);
                searchField.setVisibility(View.INVISIBLE);

                userLName.setText(foundUser.getName());
                userFName.setText(foundUser.getFirstName());
                userAccName.setText(foundUser.getAccountName());

                String foundUserInitials = foundUser.getFirstName().toUpperCase().charAt(0) + "" +
                        foundUser.getName().toUpperCase().charAt(0);
                userIcon.setText(foundUserInitials);
                userIcon.setVisibility(View.VISIBLE);
                userLName.setVisibility(View.VISIBLE);
                userFName.setVisibility(View.VISIBLE);
                userAccName.setVisibility(View.VISIBLE);

                searchButton.setText("Add");
                searchButton.setEnabled(true);
                break;
        }
    }


}
