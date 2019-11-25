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

        DBStatements db = new DBStatements(getContext());


        // Dummy Data
        ArrayList<Chat> chats = new ArrayList<Chat>();
        chats.add(new Chat("Labergruppe", 0xFFFB0B03, "394", "Gott"));
        chats.add(new Chat("Tetris esport Team", 0xFFFBB400, "3934", "Gott"));
        chats.add(new Chat("Anonyme Alkoholiker", 0xFFB0FB03, "3954", "Gott"));
        chats.add(new Chat("Öffentliche Alkoholiker", 0xFF00FB71, "3941", "Gott"));
        chats.add(new Chat("Saufgruppe 1", 0xFF0C00F1, "3434", "Gott"));
        chats.add(new Chat("Saufgruppe 2", 0xFF038814, "3414", "Gott"));
        chats.add(new Chat("Saufgruppe 3", 0xFF880E51, "3484", "Gott"));
        chats.add(new Chat("Saufgruppe 4", 0xFF884318, "3214", "Gott"));
        chats.add(new Chat("Saufgruppe 5", 0xFF004888, "3474", "Gott"));
        chats.add(new Chat("Saufgruppe 6", 0xFF880B00, "3414", "Gott"));
        chats.add(new Chat("Saufgruppe 7", 0xFF88004D, "3484", "Gott"));
        chats.add(new Chat("Saufgruppe 8", 0xFF888800, "3214", "Gott"));
        chats.add(new Chat("Saufgruppe 9", 0xFF7F0088, "3474", "Gott"));



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
