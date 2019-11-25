package de.swproj.teamchat.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.Connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.view.activities.ChatActivity;
import de.swproj.teamchat.view.adapter.AdapterChat;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainChats extends ListFragment {
 private DBStatements db;
 private ArrayList<Chat> chats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          db = new DBStatements(inflater.getContext());
        chats=db.getChat();

        //TODO: Den Adapter erzeugen und mit Werten füllen und auf ListFragment setzen
        Log.d("Fragments:", "In Chat Fragment"+" Chatcount "+chats.size());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView list = getListView();
     //   String[] s = new String[]{"Hier könnte Ihre Werbung stehen!", "Wählen Sie die 62 70 8"};
     //   ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.dummy_list_item, R.id.textView, s);
        //  setListAdapter(adapter);







        final AdapterChat chatAdapter = new AdapterChat(chats, db);
        setListAdapter(chatAdapter);

//TODO: Test ClickListener once ChatActivity can be tested
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                Chat selectedItem = (Chat)chatAdapter.getItem(position);
                chatIntent.putExtra("chatID", selectedItem.getId());
                startActivityForResult(chatIntent, position);
            }
        });
    }
}
