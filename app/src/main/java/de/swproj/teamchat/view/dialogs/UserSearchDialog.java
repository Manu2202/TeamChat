package de.swproj.teamchat.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.datamodell.chat.Event;
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
    private AdapterContact adapterContact;
    private ScrollView searchResultsScrollView;
    private ListView searchResults;
   // private User foundUser;         // to be replaced by an array for multiple search results
    private ArrayList<User> searchResultUsers = new ArrayList<User>();

    private DBStatements dbStatements;
    private FirebaseConnection fbconnect;

    private final int STILL_WAITING = -1;
    private final int USER_WAS_NOT_SEARCHED_FOR = 0;
    private final int USER_DOES_NOT_EXIST = 1;
    private final int USER_WAS_FOUND = 2;

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
        setTitle("Search for User Email");

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

        searchResultsScrollView = (ScrollView)findViewById(R.id.userSearch_results_scrollview);
        searchResultsScrollView.setVisibility(View.GONE);
        searchResults = (ListView)findViewById(R.id.userSearch_resultList);
        adapterContact = new AdapterContact(searchResultUsers);
        searchResults.setAdapter(adapterContact);

        /*
        searchResultUsers.add(new User("emusk", "sdbf", "Elon Musk", "Musk", "Elon"));
        searchResultUsers.add(new User("emuskDerZweite", "sdbf", "Elon Musk der Zweite", "Musk", "Elon"));
        searchResultUsers.add(new User("emuskDerDritte", "sdbf", "Elon Musk der Dritte", "Musk", "Elon"));
        */


        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final User selectedUser = (User)adapterContact.getItem(position);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.add(selectedUser);
                        adapter.notifyDataSetChanged();
                    }
                });

                dbStatements.insertUser(selectedUser);
                searchResultUsers.remove(selectedUser);
                adapterContact.notifyDataSetChanged();

                if (searchResultUsers.size() <= 0) {
                    dismiss();
                }

            }
        });

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


        // Text when no user was found or Database collection failed
        responseText = (TextView)findViewById(R.id.dialog_userSearch_response_tv);
        responseText.setText("Enter user email address or first name");
        responseText.setVisibility(View.VISIBLE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_userSearch_search_btn:
                if (searchResultUsers.size() <= 0) {
                    if(searchField.getText().toString().equals("")){
                    responseText.setText("Enter user email address or first name");
                    responseText.setVisibility(View.VISIBLE);
                    } else {
                        String enteredQuery = searchField.getText().toString();

                        if (dbStatements.getUserEmailExists(enteredQuery)) {
                            responseText.setText("User already in list ("
                                    + dbStatements.getUserByEmail(enteredQuery).getAccountName() + ")");
                            responseText.setVisibility(View.VISIBLE);
                        } else {
                            // Search is submitted
                            // Prepare UI
                            searchButton.setEnabled(false);
                            searchButton.setText("Searching");
                            searchField.setText("");
                            progressBar.setVisibility(View.VISIBLE);
                            responseText.setText("");
                            searchField.setVisibility(View.GONE);

                            if (enteredQuery.contains("@")) {
                                queryDBbyEmail(enteredQuery);
                            } else {
                                queryFirebaseByFirstname(enteredQuery);
                            }
                        }
                    }
                } else {
                    // If a user was found, the Search-Button will add the user
                    if (searchButton.getText().equals("Add all")) {

                        // Also loads user into current Listview and updates it
                        // (might not best way to do this)
                        // without this, User is only displayed if you leave Contact View
                        // and come back

                        // Not needed when Live Refresh is active

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (User u : searchResultUsers) {
                                    adapter.add(u);
                                }
                                    adapter.notifyDataSetChanged();
                            }
                        });

                        for (User u : searchResultUsers) {
                            dbStatements.insertUser(u);
                        }

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


    protected void reactToSearchResult(int searchResultValue) {
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
                break;

            // Firebase Server responded, but could not find User with that name in Database
            case USER_DOES_NOT_EXIST:
                progressBar.setVisibility(View.GONE);
                responseText.setText("User unknown");
                responseText.setVisibility(View.VISIBLE);
                searchButton.setText("Search");
                searchButton.setEnabled(true);
                searchField.setText("");
                searchField.setVisibility(View.VISIBLE);
               // foundUser = null;
                break;

            // Firebase Server successfully sent User Data to us
            case USER_WAS_FOUND:
                searchResultsScrollView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                searchField.setVisibility(View.INVISIBLE);

                /*
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

                 */
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        for (User u : searchResultUsers) {
                            adapter.add(u);
                        }

                         */
                        adapterContact.notifyDataSetChanged();
                    }
                });
                searchButton.setText("Add all");
                searchButton.setEnabled(true);
                break;
        }
    }


    private void queryDBbyEmail(String email) {
        //Check if Email exists for User in Auth------------------------------------------
        final String userEmail = email;
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(userEmail).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().isEmpty()) { //Maybe catch Null Pointer
                    //No User found but Connection is there
                    reactToSearchResult(USER_DOES_NOT_EXIST);
                    Log.d("User Search", "User not found!");
                } else {
                    String email_to_search = userEmail;
                    //Get User from Firestore (gets saved in Database)
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("googleMail", email_to_search).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Got User, anything related to this User has to be done inside here because its async ------->

                                    User firebaseUser = document.toObject(User.class);
                                    Log.d("FirebaseUser", firebaseUser.getAccountName() + ", " + firebaseUser.getGoogleMail());
                                    // dbStatements.insertUser(firebaseUser);
                                    searchResultUsers.add(firebaseUser);
                                    //<--------------------------------------
                                }
                                searchResultUsers.add(new User("emusk", "sdbf", "Elon Musk", "Musk", "Elon"));
                                searchResultUsers.add(new User("emuskDerZweite", "sdbf", "Elon Musk der Zweite", "Musk", "Elon"));
                                searchResultUsers.add(new User("emuskDerDritte", "sdbf", "Elon Musk der Dritte", "Musk", "Elon"));

                                reactToSearchResult(USER_WAS_FOUND);
                            } else {
                                Log.d("Firebase User", "Error getting user: ", task.getException());
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("User Search", "Error in Connection to Firebase");
                //No Connection to Database
                reactToSearchResult(USER_WAS_NOT_SEARCHED_FOR);
            }
        });
        //--------------------------------------------------
    }

    private void queryFirebaseByFirstname(String firstName) {
        //Check if Email exists for User in Auth------------------------------------------
        final String userFirstName = firstName;

        // TODO: Need a Firebase interface that checks for First Name, not Email
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(userFirstName).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().isEmpty()) { //Maybe catch Null Pointer
                    //No User found but Connection is there
                    reactToSearchResult(USER_DOES_NOT_EXIST);
                    Log.d("User Search", "User not found!");
                } else {
                    String name_to_search = userFirstName;
                    //Get User from Firestore (gets saved in Database)
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("firstName", userFirstName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Got User, anything related to this User has to be done inside here because its async ------->

                                    User firebaseUser = document.toObject(User.class);
                                    Log.d("FirebaseUser", firebaseUser.getAccountName() + ", " + firebaseUser.getFirstName());
                                    // dbStatements.insertUser(firebaseUser);
                                    searchResultUsers.add(firebaseUser);
                                    //<--------------------------------------
                                }

                                reactToSearchResult(USER_WAS_FOUND);
                            } else {
                                Log.d("Firebase User", "Error getting user: ", task.getException());
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("User Search", "Error in Connection to Firebase");
                //No Connection to Database
                reactToSearchResult(USER_WAS_NOT_SEARCHED_FOR);
            }
        });
        //--------------------------------------------------
    }


}
