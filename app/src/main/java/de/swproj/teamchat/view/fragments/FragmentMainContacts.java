package de.swproj.teamchat.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.view.activities.StartActivity;
import de.swproj.teamchat.view.activities.ViewUserDetailsActivity;
import de.swproj.teamchat.view.adapter.AdapterContact;
import de.swproj.teamchat.view.dialogs.UserSearchDialog;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainContacts extends ListFragment {


    private ArrayList<User> users;
    private FloatingActionButton fab;
    AdapterContact adapterContact;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create DBStatements and get all User

        Log.d("Fragments:", "In Contact Fragment");

        setHasOptionsMenu(true);

        View v = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.contactfragment_floatingactionbutton, container, false);

        parent.addView(v, 0);
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView list = getListView();
        // Create the adapter
        adapterContact = new AdapterContact(users);
        setListAdapter(adapterContact);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewUserDetailsActivity.class);

                User selectedUser = (User)adapterContact.getItem(position);
                intent.putExtra("currentContactID", selectedUser.getGoogleId());

                // TODO: Cool Transition Effect

                startActivityForResult(intent, position);
            }
        });


        fab = (FloatingActionButton) getView().findViewById(R.id.userSearchFAB);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UserSearchDialog userSearchDialog = new UserSearchDialog(getActivity(), adapterContact);
                userSearchDialog.show();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_contacts_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.btn_main_contacts_logout:
                //delete Token from uID
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseConnection.deleteToken(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                TeamChatMessagingService.disableFCM();
                FirebaseAuth.getInstance().signOut();
                Intent startIntent = new Intent(getActivity(), StartActivity.class);
                startActivity(startIntent);
                getActivity().finish();
                break;
        }
        return true;
    }


    public void updateMainContactsList() {
        if (adapterContact != null) {
            adapterContact.notifyDataSetChanged();
        }
    }



}
