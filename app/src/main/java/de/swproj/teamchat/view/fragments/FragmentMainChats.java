package de.swproj.teamchat.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.connection.firebase.services.TeamChatMessagingService;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.view.activities.ChatActivity;
import de.swproj.teamchat.view.activities.EditChatActivity;
import de.swproj.teamchat.view.activities.StartActivity;
import de.swproj.teamchat.view.adapter.AdapterChat;
import de.swproj.teamchat.view.adapter.AdapterContact;
import de.swproj.teamchat.view.viewmodels.MainChatsViewModel;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainChats extends ListFragment {

   // private ArrayList<Chat> chats;
    private MainChatsViewModel viewModel;

    private MenuItem deleteButton;
    private MenuItem cancelDeleteButton;

    private final int STD = 0;
    private final int DEL = 1;
    private int menuModus;

    // only uses one item at once so far, but may be expanded so you can delete multiple items
    private ArrayList<Chat> markedForDeletion = new ArrayList();
    private ArrayList<FrameLayout> markedMenuItems = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        menuModus = STD;
      //  Log.d("Fragments:", "In Chat Fragment" + " Chatcount " + chats.size());

        setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DBStatements.removeUpdateable(viewModel);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //instanciate LiveData
        viewModel= new MainChatsViewModel((LinkedList<Chat>) DBStatements.getChat());
        ListView list = getListView();

        final AdapterChat chatAdapter = new AdapterChat(viewModel.getLiveChats().getValue());
        setListAdapter(chatAdapter);
        // Register ViewModel in DB
        DBStatements.addUpdateable(viewModel);

        viewModel.getLiveChats().observe(this, new Observer<LinkedList<Chat>>() {
            @Override
            public void onChanged(LinkedList<Chat> chats) {
                chatAdapter.notifyDataSetChanged();
            }
        });



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Fragments:", "OnItemClick" + " menuModus = " + menuModus);
                if (menuModus == STD) {
                    Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                    Chat selectedItem = (Chat) chatAdapter.getItem(position);
                    chatIntent.putExtra("chatID", selectedItem.getId());
                    startActivityForResult(chatIntent, position);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                deleteButton.setVisible(true);
                cancelDeleteButton.setVisible(true);
                menuModus = DEL;
                if (markedForDeletion.contains(chatAdapter.getItem(position))){
                    view.findViewById(R.id.list_color_background)
                            .setBackgroundColor(ContextCompat.getColor(getContext(),
                                    R.color.background));
                    markedForDeletion.remove(chatAdapter.getItem(position));
                    markedMenuItems.remove((FrameLayout) view.findViewById(R.id.list_color_background));
                }else {
                    markedForDeletion.add(chatAdapter.getItem(position));
                    markedMenuItems.add((FrameLayout) view.findViewById(R.id.list_color_background));
                    view.findViewById(R.id.list_color_background).setBackgroundColor(Color.GRAY);
                }

                Log.d("Fragments:", "OnLongClick" + " menuModus = " + menuModus);
                return true;
            }

        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_chat_menu, menu);
        menu.findItem(R.id.btn_chat_menu_delete).setVisible(false);
        deleteButton = menu.findItem(R.id.btn_chat_menu_delete);
        cancelDeleteButton = menu.findItem(R.id.btn_cancel_delete);

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
            case R.id.btn_cancel_delete:
                // Cancel delete process - unmark all items that were marked for deletion
                AdapterChat updateViewAdapter = (AdapterChat) getListAdapter();
                for (FrameLayout fl : markedMenuItems) {
                    fl.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));
                    Log.d("Fragments:", "Item Pos 2 : " + updateViewAdapter.getItem(2).getId());
                    updateViewAdapter.notifyDataSetChanged();
                }

                Log.d("Fragments:", "Item Pos 2 : " + updateViewAdapter.getItem(2).getId());
                for (Chat c : markedForDeletion) {
                    Log.d("Fragments:", "Chat should be white now : " + c.getId());
                }

                markedMenuItems.clear();
                markedForDeletion.clear();
                cancelDeleteButton.setVisible(false);
                deleteButton.setVisible(false);
                menuModus = STD;

                break;
            case R.id.btn_chat_menu_delete:
                for (Chat c : markedForDeletion) {
                    // TODO: dbStatements deleteChat(String chatId) Function needed
                    // db.deleteChat(c.getId());
                    Log.d("Fragments:", "Chat deleted : " + c.getId());
                }
                markedMenuItems.clear();
                markedForDeletion.clear();
                cancelDeleteButton.setVisible(false);
                deleteButton.setVisible(false);
                menuModus = STD;

                break;
        }
        return true;
    }
/*
    @Override
    public void onResume() {
        super.onResume();

        // set a new Adapter -> QnD
        ArrayList<Chat> c = DBStatements.getChat();

        ListView list = getListView();


        final AdapterChat chatAdapter = new AdapterChat(c);
        setListAdapter(chatAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Fragments:", "OnItemClick (on Resume)" + " menuModus = " + menuModus);
                if (menuModus == STD) {
                    Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                    Chat selectedItem = (Chat) chatAdapter.getItem(position);
                    chatIntent.putExtra("chatID", selectedItem.getId());
                    startActivityForResult(chatIntent, position);
                }
            }
        });
    } */

}
