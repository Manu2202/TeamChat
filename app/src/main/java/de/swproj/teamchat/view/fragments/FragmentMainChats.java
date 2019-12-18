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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.view.activities.ChatActivity;
import de.swproj.teamchat.view.activities.EditChatActivity;
import de.swproj.teamchat.view.activities.StartActivity;
import de.swproj.teamchat.view.adapter.AdapterChat;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainChats extends ListFragment {
    private DBStatements db;
    private ArrayList<Chat> chats;
    private MenuItem deleteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = new DBStatements(inflater.getContext());
        chats = db.getChat();
        Log.d("Fragments:", "In Chat Fragment" + " Chatcount " + chats.size());

        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView list = getListView();

        final AdapterChat chatAdapter = new AdapterChat(chats, db);
        setListAdapter(chatAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                Chat selectedItem = (Chat) chatAdapter.getItem(position);
                chatIntent.putExtra("chatID", selectedItem.getId());
                startActivityForResult(chatIntent, position);
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                // TODO: Mark item as selected, so deleteButton can delete selected items
                // Delete Button has no function yet
                 deleteButton.setVisible(true);

                return false;
            }

        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_chat_menu, menu);
        menu.findItem(R.id.btn_chat_menu_delete).setVisible(false);
        deleteButton = menu.findItem(R.id.btn_chat_menu_delete);

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.btn_main_chat_logout:
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

            case R.id.btn_main_new_chat:

                Intent createChatIntent = new Intent(getActivity(), EditChatActivity.class);
                // ID = 0 -> new Chat
                createChatIntent.putExtra("ID", "0");
                createChatIntent.putExtra("admin", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(createChatIntent);
                break;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        // set a new Adapter -> QnD
        ArrayList<Chat> c = db.getChat();

        ListView list = getListView();


        final AdapterChat chatAdapter = new AdapterChat(c, db);
        setListAdapter(chatAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                Chat selectedItem = (Chat) chatAdapter.getItem(position);
                chatIntent.putExtra("chatID", selectedItem.getId());
                startActivityForResult(chatIntent, position);
            }
        });
    }

}
