package de.swproj.teamchat.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Space;
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
    private AdapterContact adapterContact;
    private ScrollView searchResultsScrollView;
    private ListView searchResults;
    private Space space;
    private ArrayList<User> searchResultUsers = new ArrayList<User>();


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



        fbconnect = new FirebaseConnection();

        // Button "Search" - Disabled until searchField has text
        searchButton = (Button)findViewById(R.id.dialog_userSearch_search_btn);
        searchButton.setOnClickListener(this);
        searchButton.setEnabled(false);

        // Cancel Button - Always the same
        cancelButton = (Button)findViewById(R.id.dialog_userSearch_cancel_btn);
        cancelButton.setOnClickListener(this);

        space = (Space)findViewById(R.id.userSearch_SPACE);
        space.setVisibility(View.INVISIBLE);

        // Search Field for username - Visible at start
        searchField = (EditText)findViewById(R.id.dialog_userSearch_name_et);
        searchField.setVisibility(View.VISIBLE);

        searchResultsScrollView = (ScrollView)findViewById(R.id.userSearch_results_scrollview);
        searchResultsScrollView.setVisibility(View.GONE);
        searchResults = (ListView)findViewById(R.id.userSearch_resultList);
        adapterContact = new AdapterContact(searchResultUsers);
        searchResults.setAdapter(adapterContact);
        setListViewHeightBasedOnItems(searchResults);

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

                DBStatements.insertUser(selectedUser);
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

                        if (DBStatements.getUserEmailExists(enteredQuery)) {
                            responseText.setText("User already in list ("
                                    + DBStatements.getUserByEmail(enteredQuery).getAccountName() + ")");
                            responseText.setVisibility(View.VISIBLE);
                        } else {
                            // Search is submitted
                            // Prepare UI
                            searchButton.setEnabled(false);
                            searchButton.setText("Searching");
                            searchButton.setTextColor(getContext().getResources().getColor(R.color.lighter_grey));
                            searchField.setVisibility(View.GONE);

                            space.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            searchField.setVisibility(View.INVISIBLE);
                            responseText.setVisibility(View.GONE);

                            if (enteredQuery.contains("@")) {
                                queryDBbyEmail(enteredQuery);
                            } else {
                                queryFirebaseByFirstname(enteredQuery);
                            }
                        }
                    }
                } else {
                    // If a user was found, the Search-Button will add all found users
                    if (searchButton.getText().equals("Add all")) {


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
                            DBStatements.insertUser(u);
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
                space.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
                searchField.setVisibility(View.GONE);
                responseText.setText("Failed to connect to server.");
                responseText.setVisibility(View.VISIBLE);
                searchButton.setText("Retry");
                searchButton.setTextColor(getContext().getResources().getColor(R.color.white));
                searchButton.setEnabled(true);
                break;

            // Firebase Server responded, but could not find User with that name in Database
            case USER_DOES_NOT_EXIST:
                space.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                responseText.setText("User unknown");
                responseText.setVisibility(View.VISIBLE);
                searchButton.setText("Search");
                searchButton.setTextColor(getContext().getResources().getColor(R.color.white));
                searchButton.setEnabled(true);
                searchField.setText("");
                searchField.setVisibility(View.VISIBLE);
                break;

            // Firebase Server successfully sent User Data to us
            case USER_WAS_FOUND:
                space.setVisibility(View.GONE);
                searchResultsScrollView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                searchField.setVisibility(View.GONE);
                setListViewHeightBasedOnItems(searchResults);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapterContact.notifyDataSetChanged();
                    }
                });
                searchButton.setText("Add all");
                searchButton.setTextColor(getContext().getResources().getColor(R.color.white));
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

                                    if (firebaseUser.getGoogleId() != FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                                        searchResultUsers.add(firebaseUser);
                                    }
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

    private void queryFirebaseByFirstname(String firstName) {
        //Check if Email exists for User in Auth------------------------------------------
        final String userFirstName = firstName;

        final ArrayList<User> firestoreResults = new ArrayList<User>();

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

                        if (firebaseUser.getGoogleId() != FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                            firestoreResults.add(firebaseUser);
                        }
                        //<--------------------------------------
                    }
                    // Removes Search Results that already exist in local database
                    for (User u: firestoreResults) {
                        if (!DBStatements.getUserEmailExists(u.getGoogleMail())) {
                            searchResultUsers.add(u);
                        }
                    }

                    if (searchResultUsers.size() > 0) {
                        reactToSearchResult(USER_WAS_FOUND);
                    }

                } else {
                    reactToSearchResult(USER_DOES_NOT_EXIST);
                    Log.d("Firebase User", "Error getting user: ", task.getException());
                }
            }
        });

    }


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }

}
