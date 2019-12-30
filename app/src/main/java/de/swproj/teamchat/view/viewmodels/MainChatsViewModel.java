package de.swproj.teamchat.view.viewmodels;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.ChatMembers;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

public class MainChatsViewModel extends Updateable {
    private MutableLiveData<LinkedList<Chat>> liveChats = new MutableLiveData<>();

    public MainChatsViewModel(LinkedList<Chat> chats) {
        liveChats.setValue(chats);
    }

    public MutableLiveData<LinkedList<Chat>> getLiveChats() {
        return liveChats;
    }

    private void insertLiveChat(Chat chat) {
        liveChats.getValue().add(0, chat);
        liveChats.postValue(liveChats.getValue());

    }

    private int findChat(String chatID) {
        int res = -1;
        List<Chat> chats = liveChats.getValue();
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getId().equals(chatID)) {
                return i;
            }
        }


        return res;
    }

    @Override
    public void updateObject(Chat obj) {
        if (obj != null) {
            List<Chat> chats = liveChats.getValue();
            Chat chat = chats.remove(findChat(obj.getId()));
            insertLiveChat(chat);

        }
    }


    @Override
    public void insertObject(Chat obj) {
        if (obj != null)
            insertLiveChat(obj);
    }

    @Override
    public void insertObject(Message obj) {
        if (obj != null) {
            List<Chat> chats = liveChats.getValue();
            Chat chat = chats.remove(findChat(obj.getChatid()));
            insertLiveChat(chat);

        }
    }


}
